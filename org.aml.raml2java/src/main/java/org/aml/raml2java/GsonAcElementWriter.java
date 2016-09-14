package org.aml.raml2java;

import org.aml.typesystem.AbstractType;
import org.aml.typesystem.acbuilder.AcElement;
import org.aml.typesystem.acbuilder.AcElementKind;
import org.aml.typesystem.acbuilder.AcScheme;
import org.aml.typesystem.acbuilder.CompositeAcElement;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStatement;

public class GsonAcElementWriter extends GenericAcAdapterWriter{

	
	public GsonAcElementWriter() {
		super("/gsonAdapter.tpl");
	}

	protected void contribute(CompositeAcElement ac,JBlock block,AbstractType target,JavaWriter writer){
		JExpression expression=null;
		switch (ac.getFamily()) {
		case BOOLEAN:
			expression=JExpr.direct("vl.isJsonPrimitive()&&vl.getAsJsonPrimitive().isBoolean()");
			break;
		case ARRAY:
			expression=JExpr.direct("vl.isJsonArray()");
			break;
		case NUMBER:
			expression=JExpr.direct("vl.isJsonPrimitive()&&vl.getAsJsonPrimitive().isNumber()");
			break;
		case STRING:
			expression=JExpr.direct("vl.isJsonPrimitive()&&vl.getAsJsonPrimitive().isString()");
			break;
		case OBJECT:
			expression=JExpr.direct("vl.isJsonObject()");
			break;
		default:
			break;
		}
		JBlock _then = block._if(expression)._then();
		for (AcElement x:ac.getChildren()){
			if (x.kind()==AcElementKind.PROPERTY_EXISTANCE){
				_then=_then._if(JExpr.direct("vl.getAsJsonObject().has(\""+x.getProperty()+"\")"))._then();
			}
		};
		_then.add(new JStatement() {
			
			@Override
			public void state(JFormatter f) {
				String name = getName(target, writer);
				f.p("union.set"+Character.toUpperCase(name.charAt(0))+name.substring(1)+"(gson.fromJson(vl,"+getJavaTypeName(target, writer)+".class));");
				f.nl();
				f.p("return union;");
				f.nl();
			}
		});
	}

	@Override
	protected JBlock generateAc(AcScheme scheme,JavaWriter writer) {
		JBlock impl=new JBlock();
		for (AbstractType t:scheme.getSchemes().keySet()){
			contribute(scheme.getSchemes().get(t), impl, t,writer);
		}
		return impl;
	}
}