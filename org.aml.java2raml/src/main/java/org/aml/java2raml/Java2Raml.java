package org.aml.java2raml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.aml.typesystem.ITypeModel;
import org.aml.typesystem.java.AcceptAllAnnotations;
import org.aml.typesystem.java.AcceptAllAnnotationsFromPackages;
import org.aml.typesystem.java.AllObjectsAreNullable;
import org.aml.typesystem.java.AllObjectsAreOptional;
import org.aml.typesystem.java.AllRequired;
import org.aml.typesystem.java.BeanPropertiesFilter;
import org.aml.typesystem.java.FieldMemberFilter;
import org.aml.typesystem.java.IAnnotationFilter;
import org.aml.typesystem.java.IMemberFilter;
import org.aml.typesystem.java.IPropertyNameBuilder;
import org.aml.typesystem.java.ITypeNamingConvention;
import org.aml.typesystem.java.JavaTypeBuilder;
import org.aml.typesystem.java.NoneFilter;
import org.aml.typesystem.java.OptionalityNullabilityChecker;
import org.aml.typesystem.java.TypeBuilderConfig;
import org.aml.typesystem.reflection.ReflectionType;
import org.aml.typesystem.yamlwriter.RamlWriter;

/**
 * <p>Java2Raml class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Java2Raml {

	private JavaTypeBuilder builder = new JavaTypeBuilder();
	private RamlWriter writer = new RamlWriter();

	/**
	 * <p>add.</p>
	 *
	 * @param model a {@link org.aml.typesystem.ITypeModel} object.
	 */
	public void add(ITypeModel model) {
		builder.getType(model);
	}

	/**
	 * <p>add.</p>
	 *
	 * @param model a {@link java.lang.Class} object.
	 */
	public void add(Class<?> model) {
		builder.getType(new ReflectionType(model));
	}

	/**
	 * <p>flush.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String flush() {
		return writer.store(builder.getRegistry(), builder.getAnnotationTypeRegistry());
	}

	/**
	 * <p>getTypeBuilderConfig.</p>
	 *
	 * @return a {@link org.aml.typesystem.java.TypeBuilderConfig} object.
	 */
	public TypeBuilderConfig getTypeBuilderConfig() {
		return builder.getConfig();
	}

	/**
	 * <p>processConfigToString.</p>
	 *
	 * @param classLoader a {@link java.lang.ClassLoader} object.
	 * @param cfg a {@link org.aml.java2raml.Config} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String processConfigToString(ClassLoader classLoader, Config cfg) {
		Collection<ITypeModel> gather = new ClassLoaderBasedCollector(classLoader).gather(cfg);

		switch (cfg.memberMode) {
		case FIELDS:
			builder.getConfig().setMemberFilter(new FieldMemberFilter());
			break;
		case PROPERTIES:
			builder.getConfig().setMemberFilter(new BeanPropertiesFilter());
		case NONE:
			builder.getConfig().setMemberFilter(new NoneFilter());
		}
		switch (cfg.optinalityMode) {
		case EVERYTHING_IS_REQUIRED:
			builder.getConfig().setCheckNullable(new AllRequired());
			break;
		case PRIMITIVES_ARE_REQUIRED:
			builder.getConfig().setCheckNullable(new AllObjectsAreOptional());
		case OBJECTS_ARE_NULLABLE:
			builder.getConfig().setCheckNullable(new AllObjectsAreNullable());
		}
		switch (cfg.annotationsBehavior) {
		case GENERATE_ALL:
			builder.getConfig().setAnnotationsFilter(new AcceptAllAnnotations());
			break;
		case IGNORE_ALL_EXCEPT_EXPLICIT_PACKAGES:
			builder.getConfig().setAnnotationsFilter(new AcceptAllAnnotationsFromPackages(cfg.annotationPackages));
		default:
			break;
		}
		if (cfg.ignoreDefaultProfiles){
			builder.getConfig().getAnnotationsProcessingConfig().clear();
		}
		
		for (Object o:cfg.extensions){
			if (o instanceof IAnnotationFilter){
				builder.getConfig().setAnnotationsFilter((IAnnotationFilter) o);
			}
			if (o instanceof IMemberFilter){
				builder.getConfig().setMemberFilter((IMemberFilter) o);
			}
			if (o instanceof OptionalityNullabilityChecker){
				builder.getConfig().setCheckNullable(((OptionalityNullabilityChecker) o));
			}
			if (o instanceof ITypeNamingConvention){
				builder.getConfig().setNamingConvention(((ITypeNamingConvention) o));
			}
			if (o instanceof IPropertyNameBuilder){
				builder.getConfig().setPropertyNameBuilder(((IPropertyNameBuilder) o));
			}
		}
		for (String s:cfg.annotationProfiles){
			File f=new File(s);
			if (f.isAbsolute()){
				try {
					FileInputStream str = new FileInputStream(f);
					builder.getConfig().getAnnotationsProcessingConfig().append(str);
					str.close();
				} catch (FileNotFoundException e) {
					cfg.log.log(e.getMessage());
				} catch (IOException e) {
					cfg.log.log(e.getMessage());
					
				}
			}
			else{
				builder.getClass().getClassLoader().getResourceAsStream("/"+s+".xml");
			}
		}
		for (ITypeModel m : gather) {
			add(m);
		}
		return flush();
	}
}
