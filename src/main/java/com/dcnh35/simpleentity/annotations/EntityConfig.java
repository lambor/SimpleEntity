package com.dcnh35.simpleentity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
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
