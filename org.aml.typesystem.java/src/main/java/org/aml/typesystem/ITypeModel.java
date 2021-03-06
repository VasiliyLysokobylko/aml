package org.aml.typesystem;

/**
 *
 * Model of the type
 *
 * @author kor
 * @version $Id: $Id
 */
public interface ITypeModel extends IBasicModel, IGenericElement{

	
	/**
	 * <p>getMethods.</p>
	 *
	 * @return methods declared in this type
	 */
	IMethodModel[] getMethods();
	
	/**
	 * <p>getFields.</p>
	 *
	 * @return an array of {@link org.aml.typesystem.IFieldModel} objects.
	 */
	IFieldModel[] getFields();
	
	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getFullyQualifiedName();
	
	/**
	 * <p>getSuperClass.</p>
	 *
	 * @return a {@link org.aml.typesystem.ITypeModel} object.
	 */
	ITypeModel getSuperClass();
	
	
	/**
	 * <p>getImplementedInterfaces</p>
	 *
	 * @return an array of {@link org.aml.typesystem.ITypeModel} objects.
	 */
	ITypeModel[] getImplementedInterfaces();
	
	
	/**
	 * <p>resolveClass</p>
	 *
	 * @param qualifiedName class qualified name
	 * @return class model object
	 */
	ITypeModel resolveClass(String qualifiedName);
	
	
	/**
	 * <p>isCollection.</p>
	 *
	 * @return a boolean.
	 */
	boolean isCollection();
	
	/**
	 * <p>getComponentType.</p>
	 *
	 * @return a {@link org.aml.typesystem.ITypeModel} object.
	 */
	ITypeModel getComponentType();

	/**
	 * <p>isEnum.</p>
	 *
	 * @return a boolean.
	 */
	boolean isEnum();
	
	/**
	 * <p>isAnnotation.</p>
	 *
	 * @return a boolean.
	 */
	boolean isAnnotation();

	/**
	 * <p>getPackageName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	String getPackageName();
}
