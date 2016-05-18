# SimpleEntity
A simple helper for json parser. It can generate entity java files base on JSON strings.

### Feature
You can generate JSON Entity Java Files simply from Json Strings with annotation @EntityConfig.  
e.g.  
```java
@EntitiesConfig 
class EntitiesClass{
    // Don't worry about escaping quotation marks.
    // Android Studio automaticly escapes quotation marks in a String.
    // So you just copy JSON string into a couple of quotation marks.
    @EntityConfig
    static final String MyEntity = "{\"a\":\"123\"}"; 
}
```

and run Gradle Task
```
gradle compileEntities
```

then you will get `MyEntity.java` in your java source directory.
```java
// dcnh always love 35
// you can rename package name. just read on
package com.dcnh.love35; 

import java.lang.String;

public class MyFirstEntity {
  public String a;
}
```



### Usage

##### Android Studio

**0x00** use gradle plugin [SimpleEntityHelper](https://github.com/lambor/SimpleEntityHelper)  
```
//root project build.gradle
buildscript {
  dependencies {
        ...
        classpath 'com.dcnh35:simpleentityhelper:1.2'
  }
}
```


**0x01** use SimpleEntity
```
//module build.gradle

dependencies {
    ...
    compile 'com.dcnh35:simpleentity:1.5'
}

apply plugin: 'simpleentityhelper'

simpleEntityHelper {
    jsonStringClass = "com.example.MyClass" //which class holds json strings
}
```

**0x02** write your "MyClass". the class name is equal to `simpleEntityHelper.jsonStringClass`
```java
//e.g. the content of com.example.MyClass
package com.example;

@EntitiesConfig
class MyClass{

    //must be static final String field.
    @EntityConfig
    static final String MyFirstEntity = "{\"a\":\"123\"}"; 

}
```

**0x03** run gradle task 
```
gradle compileEntities
```
and you will get the Entity class you want in your java source directory.

```java
//the content of generated Entity
package com.dcnh.love35;

import java.lang.String;

public class MyFirstEntity {
  public String a;
}
```

##### MAVEN

not support simple entity helper plugin.  
```xml
<dependency>
  <groupId>com.dcnh35</groupId>
  <artifactId>simpleentity</artifactId>
  <version>1.1</version>
  <type>pom</type>
</dependency>
```
You have to **move** the entity files to your source directory if you want to edit them after generate.  
And you must **turn SimpleEntity off** by `@EntitiesConfig(...,switchGenerate = false)` in case that compile conflict exception occurs


### Detail

**0x00** @EntitiesConfig Annotation
```java
public @interface EntitiesConfig {
    /**
     * which same pattern of the JSON String fields you want to generate
     * e.g.  
     *  Field strings are formated like "{"a":"123","b":{"c":"123","d":123}}" and patternName assigned with ' patternName = "b " ' , it will generate entity from "{c:"123",d:123}" 
     */
    String patternName() default "";
    
    /**
     * the entities implement Serializable Interface or not
     */
    BooleanState serializable() default BooleanState.False;

    /** the fields ' access is public or not */
    BooleanState isPublic() default BooleanState.True;

    /** the entities ' packageName */
    String packageName() default "com.dcnh.love35";

    /** skip generate process or not*/
    /** you don't need to care about this, helper plugin will do this for you*/
    boolean skipGenerate() default true;
}
```

**0x01** @EntityConfig Annotation
```java
public @interface EntityConfig {

    /** implements serializable interface or not*/
    BooleanState serializable() default BooleanState.Default;

    /** public access fields or not */
    BooleanState isPublic() default BooleanState.Default;

    /** which part of json string you want to generate, like EntitiesConfig 's patternName */
    String patternName() default "";

    /** the entity class name you want to set. equal to the generated entity java file name */
    String entityName() default "";

    /** the serializable name feature of GSON, it's not complete because i don't want to add gson dependency */
    String[] replaceFieldNames() default {" : "}; // oldName:newName

    /** the generated entity class's packagename */
    String packageName() default "";

    /** skip this entity generate process or not*/
    boolean skip() default false;
}
```
