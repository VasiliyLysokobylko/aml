#Java Classes to RAML types conversion

#Programmatic conversion

Look in [`org.aml.java2raml.Java2Raml`](https://github.com/OnPositive/aml/blob/master/org.aml.java2raml/src/main/java/org/aml/java2raml/Java2Raml.java) and [`org.aml.java2raml.Config`](https://github.com/OnPositive/aml/blob/master/org.aml.java2raml/src/main/java/org/aml/java2raml/Config.java) classes to get the idea of how you may use `org.aml.java2raml` to convert java class definitions to RAML programmatically

#Using as maven plugin

Simply add plugin configuration to your maven build: 
```xml
<plugin>
	<version>0.0.1</version>
	<groupId>com.onpositive.aml</groupId>
	<artifactId>java2raml-maven-plugin</artifactId>
	<configuration>
	   <packagesToProcess>
		<value>com.some.package.to.process</value><!-- replace this with your own package names  -->
		<value>com.another.package.to.process</value>
	  </packagesToProcess>
	</configuration>
        <executions>
         <execution>
            <goals>
                <goal>generateRaml</goal>
            </goals>
            <phase>compile</phase>
         </execution>
       </executions>
</plugin>
```

The RAML definition will be processed and the code will be generated when running `mvn compile` or `mvn package`.

####Eclipse usage

When developing in Eclipse, you must manage lifecycle mapping. For this purpose your `pom.xml` must have following child element in `pluginExecutions` section:
``` xml
<pluginExecution>
	<pluginExecutionFilter>
		<groupId>org.raml.plugins</groupId>
		<artifactId>jaxrs-raml-maven-plugin</artifactId>
		<versionRange>1.3.4</versionRange>
		<goals>
			<goal>generate-raml</goal>
		</goals>
	</pluginExecutionFilter>
	<action>
		<execute>
			<runOnIncremental>false</runOnIncremental>
		</execute>
	</action>
</pluginExecution>
```


#Configuration Options

##Input and output related options

* `packagesToProcess` - list of java package names to process and convert to RAML library (Required)
* `outputFile` - path where to store generated raml library. Defaults to `${project.build.directory}/generated-sources/raml/types.raml`
* `classMasksToIgnore` - list of regular expressions. If class name matches to expression this class is excluded from generation
* `ignoreUnreferencedAnnotationDeclarations` - set it to true if you want to ignore annotation type declarations in the processed packages.


##Conversion Style Related options

* `memberMode` - allows to configure what kind of Java type members should be converted to RAML Type properties. Allowed values: FIELDS , PROPERTIES .Defaults to FIELDS
* `optionalityMode` - allows to configure how Java Object types should be represented by default with respect to `required` property facet and `nill` type. Allowed values: PRIMITIVES_ARE_REQUIRED , EVERYTHING_IS_REQUIRED , OBJECTS_ARE_NULLABLE. Defaults to: EVERYTHING_IS_REQUIRED
* `annotationsBehavior` - this parameter allows to configure how Java annotations should be represented. Allowed values: IGNORE_ALL_EXCEPT_EXPLICIT_PACKAGES , GENERATE_ALL. Defaults to IGNORE_ALL_EXCEPT_EXPLICIT_PACKAGES.
* `annotationPackages` - this parameter allows to set a list of packages which contains declarations of annotations which should be converted to RAML.


##Annotation Profiles

Java annotations may oftenly affect on desired RAML types representation of Java types. This effects depends from used frameworks as well from developer preference and style. 

We support customization of this effects with `Annotation Profiles` feature which allows you to customize conversion behavior depending from set of Java member annotations. Annotations Profile are  xml configuration files which contains mappings of Java annotation parameters to RAML types facets as well as providing control on nullability, optionality, name and presense of generated RAML property in containing type.

The following snippet shows a built-in annotation profile which we use to customize generation of members depending from JAXB annotations present.

```xml
<annotations>
	<annotation name="javax.xml.bind.annotation.XmlElement" skipFromRaml="true">
		<member name="required" target="required"/>
		<member name="name" target="xml.name"/>
		<member name="namespace" target="xml.namespace"/>
		<member name="defaultValue" target="default"/>
		<member name="nillable" target="nullable"/>			
	</annotation>
	<annotation name="javax.xml.bind.annotation.XmlAttribute" skipFromRaml="true">
		<member name="required" target="required"/>
		<member name="name" target="xml.name"/>
		<member name="namespace" target="xml.namespace"/>
		<member target="xml.attribute" value="true"/>		
	</annotation>
	<annotation name="javax.xml.bind.annotation.XmlTransient" skipFromRaml="true">
		<member value="true" target="skipMember"/>	
	</annotation>		
	<annotation name="javax.xml.bind.annotation.XmlRootElement" skipFromRaml="true">
		<member name="name" target="xml.name"/>
		<member name="namespace" target="xml.namespace"/>
	</annotation>
	<annotation name="javax.xml.bind.annotation.XmlElementWrapper" skipFromRaml="true">
		<member target="xml.wrapped" value="true"/>
	</annotation>	
</annotations>
```
In addition to built in  RAML facets annotation profiles has following special targets:
* `skipMember` - allows to skip member depending from annotation presense or member value
* `required` - allows to configure if property is required depending from annotation presense or annotation member value
* `nullable` - allows to configure if resulting property type should allow `null` value
* `propName` - allows to configure resulting property name depending from member value

At this moment project contains following annotation  profiles which are applied by default:
  * `jaxb` - profile which customizes generation behavior depending from JAXB annotations (`javax.xml.bind`)
  * `javax.validation` - this profile converts `javax.validation` annotation to corresponding RAML types constraints
  * `lang` - settings to always ignore built-in java annotations

##Annotation Profiles related configuration parameters

* `annotationProfiles` - this parameter allows to path list of files containing profiles for a custom handling of Java annotations. Alternatively you may pass names of built in annotation profiles to it. 
* `ignoreDefaultAnnotationProfiles` - turns of default annotation profiles. By default following built in profiles are turned on `javax.validaton`, `jaxb`, `lang`

##Extensions

Some times you need to do further customization of the generation process in this case you may pass a set of classNames into `extensions` plugin parameter. This classes should implement one of our extension interface, provide default constructor and be available on plugin class path.

#Limitations

* At this moment only annotations with runtime retention policy will be represented in generated raml library.
* conversion to date types as well as to file type is not supported yet.


#Examples

There are following example projects at this moment:

* [Simple Example](/examples/org.aml.example.simple) 
* [Java Annotations moving to RAML](https://github.com/OnPositive/aml/tree/master/examples/org.aml.example.ramlannotations)
* [Using Annotation Profile to configure optionality and default values](https://github.com/OnPositive/aml/tree/master/examples/org.aml.example.customannotationprofile)  

To run example project you should go to project directory and execute `mvn compile` from command line, raml library will be generated to `target/generated-sources/raml/types.raml` 
