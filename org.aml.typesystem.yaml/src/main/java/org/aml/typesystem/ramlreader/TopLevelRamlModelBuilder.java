package org.aml.typesystem.ramlreader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.BuiltIns;
import org.aml.typesystem.TypeOps;
import org.aml.typesystem.beans.ISimpleFacet;
import org.aml.typesystem.meta.FacetRegistry;
import org.aml.typesystem.meta.TypeInformation;
import org.aml.typesystem.meta.facets.Annotation;
import org.aml.typesystem.meta.facets.FacetDeclaration;
import org.aml.typesystem.meta.facets.XMLFacet;
import org.aml.typesystem.meta.restrictions.FacetRestriction;
import org.aml.typesystem.meta.restrictions.IRangeRestriction;
import org.aml.typesystem.meta.restrictions.RestrictionsList;
import org.raml.v2.api.loader.CompositeResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.loader.UrlResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.RamlHeader.InvalidHeaderException;
import org.raml.v2.internal.impl.commons.nodes.AnnotationNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationTypeNode;
import org.raml.v2.internal.impl.commons.nodes.CustomFacetDefinitionNode;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.FacetNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.ArrayTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.UnionTypeExpressionNode;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYBooleanNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYFloatingNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYIntegerNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYNullNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYObjectNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYStringNode;

public class TopLevelRamlModelBuilder {

	private static final String REQUIRED = "required";
	private static final String FACETS = "facets";
	private static final String PROPERTIES = "properties";
	private static final String USES = "uses";
	private static final String TYPES = "types";
	private static final String SCHEMAS = "schemas";
	
	private static final String ANNOTATIONTYPES = "annotationTypes";

	protected Map<Object, LibraryImpl> ramlGraph = new HashMap<>();

	RamlHeader header;

	public LibraryImpl buildUsesMaps(Node node) {
		if (ramlGraph.containsKey(id(node))) {
			return ramlGraph.get(id(node));
		}
		LibraryImpl result = new LibraryImpl(node);
		ramlGraph.put(id(node), result);
		Optional<Node> value = getValue(node, USES);
		value.ifPresent(x -> x.getChildren().forEach(l -> {
			LibraryNode used = (LibraryNode) l;
			String namespace = used.getName();
			Node c = used.getContextNode();
			if (c instanceof LibraryLinkNode) {
				LibraryLinkNode link = (LibraryLinkNode) c;
				Node libraryContent = link.getRefNode();
				LibraryImpl impl = buildUsesMaps(libraryContent);
				result.usesMap.put(namespace, impl);
			}
		}));
		value = getValue(node, TYPES);
		value.ifPresent(x -> x.getChildren().forEach(l -> {
			KeyValueNode t = (KeyValueNode) l;
			
			// result.topLevelTypes.put(t.getName(), BuiltIns.NOTHING);
			result.typeDecls.put(((StringNode)t.getKey()).getLiteralValue(),  t.getValue());
		}));
		value = getValue(node, SCHEMAS);
		value.ifPresent(x -> x.getChildren().forEach(l -> {
			KeyValueNode t = (KeyValueNode) l;
			// result.topLevelTypes.put(t.getName(), BuiltIns.NOTHING);
			result.typeDecls.put(((StringNode)t.getKey()).getLiteralValue(),  t.getValue());
		}));
		value = getValue(node, ANNOTATIONTYPES);
		value.ifPresent(x -> x.getChildren().forEach(l -> {
			AnnotationTypeNode t = (AnnotationTypeNode) l;
			result.atypeDecls.put(t.getName(), (TypeDeclarationNode) t.getValue());

		}));
		return result;
	}

	protected void buildTypes(TopLevelRamlImpl node) {
		node.usesMap.values().forEach(x -> buildTypes(x));
		node.typeDecls.keySet().forEach(x -> resolveType(node, x));
		node.atypeDecls.keySet().forEach(x -> buildType(node, x, node.atypeDecls.get(x), true));
		for (AbstractType t : node.types()) {
			finishAnnotationBinding(node, t);
		}
		for (AbstractType t : node.annotationTypes()) {
			finishAnnotationBinding(node, t);
		}
		for (Node q : node.original.getChildren()) {
			if (q instanceof AnnotationNode) {
				AnnotationNode aNode = (AnnotationNode) q;
				Node value = aNode.getValue();
				Object val = toObject(value);
				String literalValue = aNode.getKey().getLiteralValue();
				Annotation as = new Annotation(literalValue.substring(1, literalValue.length() - 1), val, null);
				bindAnnotation(node, as);
				node.annotations.add(as);
			}
		}
	}

	private void finishAnnotationBinding(TopLevelRamlImpl node, AbstractType t) {
		for (TypeInformation i : t.declaredMeta()) {
			if (i instanceof Annotation) {
				Annotation a = (Annotation) i;
				bindAnnotation(node, a);
			}
			if (i instanceof IRangeRestriction) {
				IRangeRestriction mm = (IRangeRestriction) i;
				finishAnnotationBinding(node, mm.range());
			}
		}
	}

	private void bindAnnotation(TopLevelRamlImpl node, Annotation a) {
		String name = a.getName();
		String namespace = null;
		int indexOf = name.indexOf('.');
		if (indexOf != -1) {
			namespace = name.substring(0, indexOf);
			name = name.substring(indexOf + 1);
		}
		TopLevelRamlImpl ti = node;
		if (namespace != null) {
			ti = node.usesMap.get(namespace);
		}
		if (ti == null) {
			return;
		}
		AbstractType type = ti.annotationTypes().getType(name);
		if (type != null) {
			a.setAnnotationType(type);
		} else {
			System.err.println("Error");
		}
	}

	private AbstractType buildSuperType(TopLevelRamlImpl node, TypeExpressionNode tn) {
		if (tn instanceof ArrayTypeExpressionNode) {
			ArrayTypeExpressionNode at = (ArrayTypeExpressionNode) tn;
			return TypeOps.array(buildSuperType(node, at.of()));
		}
		if (tn instanceof UnionTypeExpressionNode) {
			UnionTypeExpressionNode un = (UnionTypeExpressionNode) tn;
			List<TypeExpressionNode> of = un.of();
			Stream<AbstractType> map = of.stream().map(t -> buildSuperType(node, t)).filter(x->!x.isNill());
			Object[] array = map.toArray();
			int count = array.length;
			if (array.length==1){
				AbstractType abstractType = (AbstractType) array[0];
				if (abstractType.isNumber()||abstractType.isBoolean()){
					abstractType=TypeOps.derive("", abstractType);
					abstractType.setNullable(true);
				}
				return abstractType;
			}
			return TypeOps.union("", Arrays.asList(array).toArray(new AbstractType[count]));
		}
		if (tn instanceof NativeTypeExpressionNode) {
			return BuiltIns.getBuiltInTypes().getType(tn.getTypeExpressionText());
		}
		if (tn instanceof NamedTypeExpressionNode) {
			NamedTypeExpressionNode namedNode = (NamedTypeExpressionNode) tn;
			String nameSpace = null;
			if (!namedNode.getChildren().isEmpty()) {
				nameSpace = ((LibraryRefNode) namedNode.getChildren().get(0)).getRefName();
			}
			String typeName = namedNode.getLiteralValue();
			if (nameSpace != null) {
				AbstractType resolveType = resolveType(node.usesMap.get(nameSpace), typeName);
				return resolveType;
			}
			AbstractType resolveType = resolveType(node, typeName);
			if (resolveType==null&&typeName.equals("date")){
				return BuiltIns.DATE;
			}
			return resolveType;
		}
		if (tn instanceof TypeDeclarationNode) {
			// should not pass here;
			throw new IllegalStateException();
		}
		if (tn instanceof ExternalSchemaTypeExpressionNode) {
			ExternalSchemaTypeExpressionNode en = (ExternalSchemaTypeExpressionNode) tn;
			// TODO FIX ME
			return TypeOps.deriveExternal(en.getLiteralValue(),en.getLiteralValue());
		}
		throw new IllegalStateException();
	}

	private AbstractType resolveType(TopLevelRamlImpl topLevelRamlImpl, String typeName) {
		if (topLevelRamlImpl.topLevelTypes.hasDeclaration(typeName)) {
			return topLevelRamlImpl.topLevelTypes.getType(typeName);
		}
		AbstractType abstractType = buildType(topLevelRamlImpl, typeName);
		return abstractType;
	}

	private AbstractType buildType(TopLevelRamlImpl topLevelRamlImpl, String typeName) {
		Node ts = findDeclaration(topLevelRamlImpl, typeName);
		return buildType(topLevelRamlImpl, typeName, ts, true);
	}

	protected AbstractType buildType(TopLevelRamlImpl topLevelRamlImpl, String typeName, Node tn, boolean register) {
		AbstractType innerBuild = innerBuild(topLevelRamlImpl, typeName, tn, register);
		if (innerBuild!=null&&!innerBuild.isBuiltIn()){
			innerBuild.setSource(topLevelRamlImpl);
		}
		return innerBuild;
	}

	private AbstractType innerBuild(TopLevelRamlImpl topLevelRamlImpl, String typeName, Node tn, boolean register) {
		if (tn instanceof ArrayNode) {
			ArrayList<AbstractType>ts=new ArrayList<>();
			List<Node> children = tn.getChildren();
			for (Node n:children){
				AbstractType buildType = buildType(topLevelRamlImpl, typeName, n, false);
				ts.add(buildType);
			}
			
			return TypeOps.union(typeName, ts.toArray(new AbstractType[ts.size()]));
		}
		if (tn instanceof NullNode) {
			AbstractType result = TypeOps.derive(typeName, BuiltIns.STRING);
			if (tn.getParent() instanceof AnnotationTypeNode) {
				if (register) {
					topLevelRamlImpl.annotationTypes.registerType(result);
				}
				result.setAnnotation(true);
			} else {
				if (register) {
					topLevelRamlImpl.topLevelTypes.registerType(result);
				}
			}
			return result;
		}
		if (tn instanceof StringNode) {
			// RAML 0.8 global schema declaration
			StringNode sr = (StringNode) tn;
			AbstractType deriveExternal = TypeOps.deriveExternal(typeName,sr.getLiteralValue());
			if (register){
				topLevelRamlImpl.topLevelTypes.registerType(deriveExternal);
			}
			return deriveExternal;
		}
		if (tn instanceof SYObjectNode) {
			// RAML 0.8
			SYObjectNode sr = (SYObjectNode) tn;
			AbstractType superType08 = getSuperType08(sr, topLevelRamlImpl);
			return TypeOps.derive(typeName,superType08);
		}
		if (tn instanceof TypeDeclarationNode) {
			TypeDeclarationNode ts = (TypeDeclarationNode) tn;
			List<TypeExpressionNode> baseTypes = ts.getBaseTypes();
			ArrayList<AbstractType> superTypes = new ArrayList<>();
			for (TypeExpressionNode n : baseTypes) {
				AbstractType superType = buildSuperType(topLevelRamlImpl, n);
				superTypes.add(superType);
			}
			AbstractType result = TypeOps.derive(typeName, superTypes.toArray(new AbstractType[superTypes.size()]));
			if (ts.getParent() instanceof AnnotationTypeNode) {
				if (register) {
					topLevelRamlImpl.annotationTypes.registerType(result);
				}
				result.setAnnotation(true);
			} else {
				if (register) {
					topLevelRamlImpl.topLevelTypes.registerType(result);
				}
			}
			List<Node> facets = ts.getChildren();
			for (Node node : facets) {
				if (node instanceof FacetNode) {
					FacetNode n = (FacetNode) node;

					if (n.getName().equals(PROPERTIES)) {
						Node value = n.getValue();
						List<Node> ps = value.getChildren();
						parseProperties(topLevelRamlImpl, register, result, ps, false);
						continue;
					}

					Node value = n.getValue();
					if (value instanceof SimpleTypeNode<?>) {
						SimpleTypeNode<?> stn = (SimpleTypeNode<?>) value;
						FacetRestriction<?> build = RestrictionsList.build(n.getName(), stn.getValue());
						if (build != null) {
							result.addMeta(build);
						} else {
							TypeInformation facet = FacetRegistry.facet(n.getName());
							if (facet != null && facet instanceof ISimpleFacet) {
								ISimpleFacet fs = (ISimpleFacet) facet;
								fs.setValue(toObject(value));
								result.addMeta(facet);
							}
						}
					} else {
						Object object = toObject(value);
						if (object != null) {
							FacetRestriction<?> build = RestrictionsList.build(n.getName(), object);
							result.addMeta(build);
						}
					}
				} else {
					KeyValueNode kv = (KeyValueNode) node;
					SimpleTypeNode b = (SimpleTypeNode) kv.getKey();
					String literalValue = b.getLiteralValue();
					if (literalValue.equals(REQUIRED)) {
						Object object = toObject(kv.getValue());
						if (object instanceof Boolean) {
							Boolean bl = (Boolean) object;
							if (!bl) {
								result.setOptional(true);
							}
						}
					}
					if (kv instanceof AnnotationNode) {
						Node value = kv.getValue();
						Object val = toObject(value);
						Annotation as = new Annotation(literalValue.substring(1, literalValue.length() - 1), val, null);
						result.addMeta(as);
					}
					if (literalValue.equals(FACETS)) {
						Node value = kv.getValue();
						List<Node> ps = value.getChildren();
						for (Node p : ps) {
							CustomFacetDefinitionNode pn = (CustomFacetDefinitionNode) p;
							TypeDeclarationNode td = (TypeDeclarationNode) pn.getValue();
							AbstractType buildType = buildType(topLevelRamlImpl, "", td, register);
							FacetDeclaration fd = new FacetDeclaration(pn.getFacetName(), buildType);
							result.addMeta(fd);
						}
						continue;
					}
					TypeInformation facet = FacetRegistry.facet(literalValue);
					if (facet instanceof ISimpleFacet) {
						ISimpleFacet sf = (ISimpleFacet) facet;
						Node value = kv.getValue();
						if (value instanceof ObjectNode) {
							ObjectNode on = (ObjectNode) value;
							if (on.getChildren().size() == 1) {
								Node n2 = on.getChildren().get(0);
								if (n2 instanceof KeyValueNode) {
									KeyValueNode kv2 = (KeyValueNode) n2;
									if (kv2.getValue() instanceof SimpleTypeNode<?>) {
										SimpleTypeNode<?> vl = (SimpleTypeNode<?>) kv2.getValue();
										sf.setValue(vl.getValue());
										result.addMeta(facet);
									}
								}
							}
						} else {
							Object object = toObject(value);
							if (object != null) {
								sf.setValue(object);
								result.addMeta(facet);
							}
						}
					}
					if (facet instanceof XMLFacet) {
						Object object = toObject(kv.getValue());
						XMLFacet xml = (XMLFacet) facet;
						if (object instanceof HashMap<?, ?>) {
							HashMap<String, Object> rs = (HashMap<String, Object>) object;
							xml.setName((String) rs.get("name"));
							if (rs.containsKey("attribute")) {
								xml.setAttribute((Boolean) rs.get("attribute"));
							}
							if (rs.containsKey("wrapped")) {
								xml.setWrapped(((Boolean) rs.get("wrapped")));
							}
							if (rs.containsKey("namespace")) {
								xml.setNamespace((((String) rs.get("namespace"))));
							}
							if (rs.containsKey("prefix")) {
								xml.setNamespace((((String) rs.get("prefix"))));
							}
							if (rs.containsKey("order")) {
								xml.setOrder(((List) rs.get("order")));
							}
						}
						result.addMeta(xml);
					}
				}
			}
			return result;
		}
		return null;
	}

	private void parseProperties(TopLevelRamlImpl topLevelRamlImpl, boolean register, AbstractType result,
			List<Node> ps, boolean useNames) {
		for (Node p : ps) {
			KeyValueNode pn = (KeyValueNode) p;
			Node value = pn.getValue();
			String name = getPropertyName(pn);
			AbstractType buildType = buildType(topLevelRamlImpl, useNames?name:"", value, register);
			boolean required = isRequired(pn);

			
			if (name.startsWith("/") && name.endsWith("/") && name.length() != 1) {
				if (name.length() != 2) {
					result.declareMapProperty(name.substring(1, name.length() - 1), buildType);
				} else {
					result.declareAdditionalProperty(buildType);
				}
			} else {
				result.declareProperty(name, buildType, !required);
			}
		}
	}
	
	public boolean isRequired(KeyValueNode kv)
    {
        final StringNode key = (StringNode) kv.getKey();
        return getRequiredNode(kv) instanceof BooleanNode ? ((BooleanNode) getRequiredNode(kv)).getValue() : !key.getValue().endsWith("?");
    }
	 public String getPropertyName(KeyValueNode pn)
	    {
	        final StringNode key = (StringNode) pn.getKey();
	        final String keyValue = key.getValue();
	        if (getRequiredNode(pn) == null)
	        {
	            // If required field is set then the ? should be ignored
	            return keyValue.endsWith("?") ? keyValue.substring(0, keyValue.length() - 1) : keyValue;
	        }
	        else
	        {
	            return keyValue;
	        }
	    }

	    private Node getRequiredNode(KeyValueNode v)
	    {
	    	if (v.getValue() instanceof ArrayNode){
	    		return null;
	    	}
	        return v.getValue().get("required");
	    }
	


	private AbstractType getSuperType08(Node na,TopLevelRamlImpl raml) {
		for (Node n : na.getChildren()) {
			if (n instanceof KeyValueNode) {
				KeyValueNode k = (KeyValueNode) n;
				Node key = k.getKey();
				if (key instanceof StringNode) {
					StringNode sn = (StringNode) key;
					String literalValue = sn.getLiteralValue();
					Object vl = toObject(k.getValue());
					String typeName = ""+vl;
					if (literalValue.equals("schema")) {
						if (raml.topLevelTypes.hasDeclaration(typeName)){
							return raml.topLevelTypes.getType(typeName);
						}
						return TypeOps.deriveExternal("", typeName);
					}
					if (literalValue.equals("formParameters")){
						AbstractType derive = TypeOps.derive("" , BuiltIns.OBJECT);
						List<Node> children = k.getValue().getChildren();
						parseProperties(raml, false, derive, children, true);
						return derive;
					}
					if (literalValue.equals("type")) {
						return TypeOps.derive("", BuiltIns.getBuiltInTypes().getType(typeName));
					}
				}
			}
		}
		return BuiltIns.STRING;
	}

	Object toObject(Node n) {
		if (n instanceof SimpleTypeNode<?>) {
			SimpleTypeNode<?> si = (SimpleTypeNode<?>) n;
			return si.getValue();
		}
		if (n instanceof SYObjectNode) {
			SYObjectNode mm = (SYObjectNode) n;
			LinkedHashMap<String, Object> vals = new LinkedHashMap<>();
			mm.getChildren().forEach(x -> {
				KeyValueNode kv = (KeyValueNode) x;
				SimpleTypeNode key = (SimpleTypeNode) kv.getKey();
				String pName = key.getLiteralValue();
				vals.put(pName, toObject(kv.getValue()));

			});
			return vals;
		}
		if (n instanceof SYBooleanNode) {
			SYBooleanNode b = (SYBooleanNode) n;
			return b.getValue();
		}
		if (n instanceof SYFloatingNode) {
			SYFloatingNode b = (SYFloatingNode) n;
			return b.getValue().doubleValue();
		}
		if (n instanceof SYIntegerNode) {
			SYIntegerNode b = (SYIntegerNode) n;
			return b.getValue();
		}
		if (n instanceof SYNullNode) {
			return null;
		}
		if (n instanceof SYStringNode) {
			return ((SYStringNode) n).getValue();
		}
		if (n instanceof SYArrayNode) {
			SYArrayNode an = (SYArrayNode) n;
			ArrayList<Object> result = new ArrayList<>();
			an.getChildren().forEach(x -> result.add(toObject(x)));
			return result;
		}
		return n;
	}

	private Node findDeclaration(TopLevelRamlImpl topLevelRamlImpl, String typeName) {
		return topLevelRamlImpl.typeDecls.get(typeName);
	}

	public TopLevelRamlImpl build(InputStream c, ResourceLoader loader, String path) {
		String string = StreamUtils.toString(c);
		return build(string, loader, path);
	}

	public TopLevelRamlImpl build(Node node, RamlHeader header) {
		TopLevelRamlImpl n = buildUsesMaps(node);
		buildTypes(n);
		if (header.getFragment() != RamlFragment.Library) {
			ApiImpl impl = new ApiImpl(n);
			return impl;
		}
		return n;
	}

	private Object id(Node node) {
		return node;
	}

	Optional<Node> getValue(Node n, String path) {
		Node r = n.get(path);
		return Optional.ofNullable(r);
	}

	public TopLevelRamlImpl build(String ramlBuffer, ResourceLoader loader, String readerLocation) {
		CompositeResourceLoader rs = new CompositeResourceLoader(loader, new UrlResourceLoader());
		Node build = new RamlBuilder().build(ramlBuffer, loader, readerLocation);
		RamlHeader header = null;
		try {
			header = RamlHeader.parse(ramlBuffer);
		} catch (InvalidHeaderException e) {
			return null;
		}
		return build(build, header);
	}
}