package org.aml.raml2java;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.aml.raml2java.JavaGenerationConfig.MultipleInheritanceStrategy;
import org.aml.typesystem.ramlreader.TopLevelRamlImpl;
import org.aml.typesystem.ramlreader.TopLevelRamlModelBuilder;
import org.junit.Test;
import org.raml.v2.api.loader.ClassPathResourceLoader;

import junit.framework.TestCase;

public class BasicTests extends CompilerTestCase {

	@Test
	public void test0() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at0.raml"),
				new ClassPathResourceLoader(), "at0.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Author");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Author");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 2);
	}

	@Test
	public void test1() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at1.raml"),
				new ClassPathResourceLoader(), "at1.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Author");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Author");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 2);
		for (Method m : class1.getDeclaredMethods()) {
			TestCase.assertTrue(m.getDefaultValue().equals(""));
		}
	}

	@Test
	public void test2() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at2.raml"),
				new ClassPathResourceLoader(), "at2.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Author");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Author");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 2);
		for (Method m : class1.getDeclaredMethods()) {
			if (m.getName().equals("Name")) {
				TestCase.assertTrue(m.getDefaultValue().equals("Name"));
			}
		}
	}

	@Test
	public void test3() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at3.raml"),
				new ClassPathResourceLoader(), "at3.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Author");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Author");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 2);
		for (Method m : class1.getDeclaredMethods()) {
			if (m.getName().equals("Name")) {
				TestCase.assertTrue(m.getDefaultValue().equals("Name"));
			}
			if (m.getName().equals("books")) {
				TestCase.assertTrue(
						m.getReturnType().isArray() && m.getReturnType().getComponentType() == String.class);
				TestCase.assertTrue(m.getDefaultValue() != null);
			}
		}
	}

	@Test
	public void test4() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at4.raml"),
				new ClassPathResourceLoader(), "at4.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Author");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Author");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 2);
		for (Method m : class1.getDeclaredMethods()) {
			if (m.getName().equals("Name")) {
				TestCase.assertTrue(m.getDefaultValue().equals("Name"));
			}
			if (m.getName().equals("books")) {
				TestCase.assertTrue(
						m.getReturnType().isArray() && m.getReturnType().getComponentType() == String.class);
				TestCase.assertTrue(m.getDefaultValue() == null);
			}
		}
	}

	@Test
	public void test5() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at5.raml"),
				new ClassPathResourceLoader(), "at5.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.HumanName");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.HumanName");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test6() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at6.raml"),
				new ClassPathResourceLoader(), "at6.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.ImportantItem");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.ImportantItem");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 0);
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test7() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at7.raml"),
				new ClassPathResourceLoader(), "at7.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.AuthorWithKind");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.AuthorWithKind");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		try {
			Method method = class1.getMethod("kind");
			TestCase.assertTrue(method.getReturnType().isEnum());
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test8() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at8.raml"),
				new ClassPathResourceLoader(), "at8.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.AuthorWithKind");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.AuthorWithKind");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		try {
			Method method = class1.getMethod("kind");
			TestCase.assertTrue(method.getReturnType().isEnum());
			TestCase.assertTrue(method.getReturnType().getSimpleName().equals("Kind"));
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test9() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at9.raml"),
				new ClassPathResourceLoader(), "at9.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.KindAnnotated");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.KindAnnotated");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		try {
			Method method = class1.getMethod("value");
			TestCase.assertTrue(method.getReturnType().isEnum());
			TestCase.assertTrue(method.getReturnType().getSimpleName().equals("Kind"));
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	@Test
	public void test10() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at10.raml"),
				new ClassPathResourceLoader(), "at10.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Kind");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.Kind");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		try {
			Method method = class1.getMethod("value");
			TestCase.assertTrue(method.getReturnType().isEnum());
			TestCase.assertTrue(method.getReturnType().getSimpleName().equals("Kind2"));
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test11() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/at11.raml"),
				new ClassPathResourceLoader(), "at11.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.KindAnnotated");
		TestCase.assertEquals(compileAndTest.size(), 1);
		Class<?> class1 = compileAndTest.get("org.aml.test.KindAnnotated");
		TestCase.assertTrue(class1.isAnnotation());
		TestCase.assertTrue(class1.getDeclaredMethods().length == 1);
		try {
			Method method = class1.getMethod("value");
			TestCase.assertTrue(method.getReturnType().isEnum());
			TestCase.assertTrue(method.getReturnType().getSimpleName().equals("Kind"));
			TestCase.assertTrue(method.getDefaultValue()!=null);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test12() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t1.raml"),
				new ClassPathResourceLoader(), "t1.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==4);
			beanInfo = Introspector.getBeanInfo(class1.getSuperclass());
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==3);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	@Test
	public void test13() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t2.raml"),
				new ClassPathResourceLoader(), "t2.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==6);
			beanInfo = Introspector.getBeanInfo(class1.getSuperclass());
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==5);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	@Test
	public void test14() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t3.raml"),
				new ClassPathResourceLoader(), "t3.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==7);
			beanInfo = Introspector.getBeanInfo(class1.getSuperclass());
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==5);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	@Test
	public void test15() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t4.raml"),
				new ClassPathResourceLoader(), "t4.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==7);			
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}	
	@Test
	public void test16() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t5.raml"),
				new ClassPathResourceLoader(), "t5.raml");
		JavaWriter wr = new JavaWriter();
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==7);			
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	@Test
	public void test17() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t5.raml"),
				new ClassPathResourceLoader(), "t5.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==7);
			beanInfo = Introspector.getBeanInfo(class1.getSuperclass());
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==5);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}	
	
	@Test
	public void test18() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t6.raml"),
				new ClassPathResourceLoader(), "t6.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==7);
			beanInfo = Introspector.getBeanInfo(class1.getSuperclass());
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==5);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test19() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t7.raml"),
				new ClassPathResourceLoader(), "t7.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==3);
			boolean hasInnerType=false;
			for (PropertyDescriptor d:beanInfo.getPropertyDescriptors()){
				if (d.getName().equals("innerType")){
					hasInnerType=true;
					Class<?> propertyType = d.getPropertyType();
					BeanInfo b=Introspector.getBeanInfo(propertyType);
					TestCase.assertEquals(b.getPropertyDescriptors().length, 3);
				}
			}
			TestCase.assertTrue(hasInnerType);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test20() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t8.raml"),
				new ClassPathResourceLoader(), "t8.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==3);
			boolean hasInnerType=false;
			for (PropertyDescriptor d:beanInfo.getPropertyDescriptors()){
				if (d.getName().equals("innerType")){
					hasInnerType=true;
					Class<?> propertyType = d.getPropertyType();
					BeanInfo b=Introspector.getBeanInfo(propertyType);
					TestCase.assertEquals(b.getPropertyDescriptors().length, 4);
				}
			}
			TestCase.assertTrue(hasInnerType);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test21() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t9.raml"),
				new ClassPathResourceLoader(), "t9.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==3);
			boolean hasInnerType=false;
			for (PropertyDescriptor d:beanInfo.getPropertyDescriptors()){
				if (d.getName().equals("innerType")){
					hasInnerType=true;
					Class<?> propertyType = d.getPropertyType();
					BeanInfo b=Introspector.getBeanInfo(propertyType);
					TestCase.assertEquals(b.getPropertyDescriptors().length, 3);
				}
			}
			TestCase.assertTrue(hasInnerType);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}
	
	@Test
	public void test22() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t9.raml"),
				new ClassPathResourceLoader(), "t9.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(class1);
			TestCase.assertTrue(beanInfo.getPropertyDescriptors().length==3);
			boolean hasInnerType=false;
			for (PropertyDescriptor d:beanInfo.getPropertyDescriptors()){
				if (d.getName().equals("innerType")){
					hasInnerType=true;
					Class<?> propertyType = d.getPropertyType();
					BeanInfo b=Introspector.getBeanInfo(propertyType);
					TestCase.assertEquals(b.getPropertyDescriptors().length, 3);
				}
			}
			TestCase.assertTrue(hasInnerType);
		} catch (IntrospectionException e) {
			TestCase.assertTrue(false);
		}
		//TestCase.assertTrue(class1.getDeclaredMethods()[0].getName().equals("value"));
	}

//	
	@Test
	public void test23() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t10.raml"),
				new ClassPathResourceLoader(), "t10.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Important"));
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test24() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t11.raml"),
				new ClassPathResourceLoader(), "t11.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Label"));
			
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test25() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t12.raml"),
				new ClassPathResourceLoader(), "t12.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Order"));
			Object invoke = annotations[0].getClass().getMethod("value").invoke(annotations[0]);
			TestCase.assertEquals(invoke, 2);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalAccessException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		} catch (InvocationTargetException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test26() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t13.raml"),
				new ClassPathResourceLoader(), "t13.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Skip"));
			Object invoke = annotations[0].getClass().getMethod("value").invoke(annotations[0]);
			TestCase.assertEquals(invoke, true);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalAccessException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		} catch (InvocationTargetException e) {
			TestCase.assertTrue(false);
		}

	}
	@Test
	public void test27() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t14.raml"),
				new ClassPathResourceLoader(), "t14.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Version"));
			Object invoke = annotations[0].getClass().getMethod("majour").invoke(annotations[0]);
			TestCase.assertEquals(invoke, 3.0);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalAccessException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		} catch (InvocationTargetException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test28() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t15.raml"),
				new ClassPathResourceLoader(), "t15.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Version"));
			double[] invoke = (double[]) annotations[0].getClass().getMethod("value").invoke(annotations[0]);
			TestCase.assertEquals(invoke[0], 3.0);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalAccessException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		} catch (InvocationTargetException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test29() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t16.raml"),
				new ClassPathResourceLoader(), "t16.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.Manager");
		Class<?> class1 = compileAndTest.get("org.aml.test.Manager");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getName");
			Annotation[] annotations = declaredMethod.getAnnotations();
			TestCase.assertTrue(annotations.length==1);
			TestCase.assertTrue(annotations[0].annotationType().getSimpleName().equals("Version"));
			double[] invoke = (double[]) annotations[0].getClass().getMethod("q").invoke(annotations[0]);
			TestCase.assertEquals(invoke[0], 3.0);
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalAccessException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		} catch (InvocationTargetException e) {
			TestCase.assertTrue(false);
		}

	}
	
	@Test
	public void test30() {
		TopLevelRamlImpl build = new TopLevelRamlModelBuilder().build(BasicTests.class.getResourceAsStream("/t17.raml"),
				new ClassPathResourceLoader(), "t17.raml");
		JavaWriter wr = new JavaWriter();
		wr.getConfig().setMultipleInheritanceStrategy(MultipleInheritanceStrategy.MIX_IN);
		wr.setDefaultPackageName("org.aml.test");
		wr.write(build);
		HashMap<String, Class<?>> compileAndTest = compileAndTest(wr.getModel(), "org.aml.test.OkStatus");
		Class<?> class1 = compileAndTest.get("org.aml.test.OkStatus");
		//TestCase.assertTrue(class1.getSuperclass().getSimpleName().equals("Person"));
		try {
			Method declaredMethod = class1.getDeclaredMethod("getGeo");
			Class<?> returnType = declaredMethod.getReturnType();
			System.out.println(returnType);
			try {
				Field declaredField = returnType.getDeclaredField("address");
				String simpleName = declaredField.getType().getSimpleName();
				TestCase.assertTrue(simpleName.equals("OkStatusgeoaddress"));				
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			TestCase.assertTrue(false);
		} catch (SecurityException e) {
			TestCase.assertTrue(false);
		} catch (IllegalArgumentException e) {
			TestCase.assertTrue(false);
		}

	}
}

