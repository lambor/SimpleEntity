package com.dcnh35.simpleentity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface EntitiesConfig {
	/**
	 * which same pattern of the JSON String fields you want to generate
	 * e.g.  
	 * 	Field strings are formated like "{"a":"123","b":{"c":"123","d":123}}" and patternName assigned with ' patternName = "b " ' , it will generate entity from "{c:"123",d:123}" 
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

	/** skip the generate process or not*/
	boolean skipGenerate() default true;
}
