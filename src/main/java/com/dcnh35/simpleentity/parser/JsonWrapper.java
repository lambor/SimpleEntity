package com.dcnh35.simpleentity.parser;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.VariableElement;

import com.dcnh35.simpleentity.annotations.BooleanState;
import com.dcnh35.simpleentity.annotations.EntitiesConfig;
import com.dcnh35.simpleentity.annotations.EntityConfig;
import com.dcnh35.simpleentity.exception.IllegalJsonStringException;
import com.dcnh35.simpleentity.writer.JavaWriter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import static com.dcnh35.simpleentity.util.StringRegex.*;
import static com.dcnh35.simpleentity.util.StringUtil.upperFirstChar;

public class JsonWrapper implements JavaWriter.Codable {
	
	//the field value (JSON string) annotated by @EntityConfig
	private String jsonString;

	//the field name annotated by @EntityConfig
	private String fieldName; 
	
	//local EntityConfig annotation
	private EntityConfig localConfig;

	//global EntitiesConfig annotation
	private EntitiesConfig globalConfig;

	// store the parsed JSON string
	private Entity entity; 
	
	// entity class for output
	private EntityClass entityClass; 

	private String patternName;


	public JsonWrapper(VariableElement element, EntitiesConfig global)
			throws IllegalJsonStringException, IllegalArgumentException {
		this.jsonString = shrinkJsonString((String) element.getConstantValue());
		this.fieldName = element.getSimpleName().toString();

		this.localConfig = element.getAnnotation(EntityConfig.class);
		this.globalConfig = global;

		this.patternName = getPatternName();
		String sourceString = getPatternString(patternName);
		this.entity = JsonStringAnalyze.analyze(sourceString); //parse JSON string to entity
	}



	@SuppressWarnings("unused")
	public JsonWrapper(String fieldName, String fieldValue, EntityConfig local, EntitiesConfig global)
			throws IllegalJsonStringException, IllegalArgumentException {
		this.jsonString = shrinkJsonString(fieldValue);
		this.fieldName = fieldName;
		
		this.localConfig = local;
		this.globalConfig = global;
		
		String patternName = getPatternName();
		String sourceString = getPatternString(patternName);
		this.entity = JsonStringAnalyze.analyze(sourceString);
	}
	
	/***
	 * get the needed part of jsonString according to patternName
	 * @param patternName
	 * @return
	 * @throws IllegalJsonStringException
	 */
	private String getPatternString(String patternName) throws IllegalArgumentException, IllegalJsonStringException {
		String sourceString = this.jsonString;
		if (!patternName.equals("")) {
			String regex = patternName + "\":(?=[\\{\\[])";
			String[] slices = sourceString.split(regex);
			if (slices.length != 2) {
				throw new IllegalArgumentException("patternName is invalid");
			}

			char first = slices[1].charAt(0);
			char last = first == '{' ? '}':']';
			int stackSize = 0;
			int lastIndex = -1;
			for(int index=0;index<slices[1].toCharArray().length;index++) {
				char c = slices[1].toCharArray()[index];
				if(c == first) stackSize ++;
				else if(c == last) stackSize --;
				if(stackSize == 0) {
					lastIndex = index;
					break;
				}
			}
			if(stackSize != 0) throw new IllegalJsonStringException("the json string pointed by patternName is illegal");

			sourceString = slices[1].substring(0, lastIndex + 1);
//			System.out.println(sourceString);
		}
		return sourceString;
	}
		
	/**
	 * get the pattern name defined by annotations. pattern name points to the needed part of json string.
	 * @return
	 */
	public String getPatternName() {
		return localConfig.patternName().equals("") ? globalConfig.patternName() : localConfig.patternName();
	}
	
	/**
	 * the entity class is serializable or not. defined by annotations. 
	 */
	public boolean isSerializable() {
		if (localConfig.serializable().equals(BooleanState.True))
			return true;
		else if (localConfig.serializable().equals(BooleanState.False))
			return false;
		else {
			return globalConfig.serializable().equals(BooleanState.True);
		}
	}

	private List<String[]> namePairs;
	private List<String[]> replaceNamePairs() {
		if (namePairs != null)
			return namePairs;
		List<String[]> _namePairs = new ArrayList<>();
		for (String names : localConfig.replaceFieldNames())
			_namePairs.add(names.split(":"));
		namePairs = _namePairs;
		return _namePairs;
	}

	private String getReplaceName(String oldName) {
		List<String[]> _namePairs = replaceNamePairs();
		for (String[] pair : _namePairs) {
			if (pair.length == 2 && pair[0].equals(oldName))
				return pair[1];
		}
		return "";
	}

	private String packageName() {
		return localConfig.packageName().equals("") ? globalConfig.packageName() : localConfig.packageName();
	}

	private String className() {
		if (!"".equals(localConfig.entityName()))
			return localConfig.entityName();
		else
			return upperFirstChar(fieldName) + upperFirstChar(getPatternName());
	}

//	private String upperFirstChar(String str) {
//		if("".equals(str)) return "";
//		String first = str.substring(0, 1);
//		String last = str.substring(1);
//		return first.toUpperCase() + last;
//	}

	public boolean isPublic() {
		if (localConfig.isPublic().equals(BooleanState.True))
			return true;
		else if (localConfig.isPublic().equals(BooleanState.False))
			return false;
		else {
			return globalConfig.isPublic().equals(BooleanState.True);
		}
	}


	private EntityClass entity2Class(Entity entity, String className,boolean fieldPublic) {

		// if first level entity type is list ,then start with next level entity
		if(entity.type.equals(EntityType.LIST) && entity.types.size() == 1)
			return entity2Class(entity.types.get(-1),className,fieldPublic);

		EntityClass clazz = new EntityClass(className);

//		System.out.println("metaDatas size:" + entity.metaDatas.size());
		for (int i = 0; i < entity.metaDatas.size(); i++) {

			String fieldMetaData = entity.metaDatas.get(i);

			String _fieldName = fieldMetaData.split(":")[0];
			_fieldName = _fieldName.substring(1, _fieldName.length()-1); //trim the quote mark


			TypeName _fieldType;
			if (fieldMetaData.matches("\"[a-zA-Z0-9_-]+\":$")) {
				// _fieldType = ClassName.get(getPackageName(),getClassName(),
				// upperFirstChar(_fieldName));
//				System.out.println("[1]" + fieldMetaData);
				Entity nextEntity = entity.types.get(i);
				_fieldType = getNextLevelType(nextEntity, _fieldName);
				clazz.innerClasses.add(entity2Class(nextEntity, upperFirstChar(_fieldName),true));
			} else {
//				System.out.println("[2]" + fieldMetaData);
				String[] fieldValue = fieldMetaData.split("(?<=(\")):");
				_fieldType = getBasicFieldType(fieldValue[1]);
			}
			// boolean _isPublic = entityClass == null ? isPublic() : true;
			EntityField field = new EntityField(_fieldName, _fieldType, fieldPublic);

			String newName = getReplaceName(_fieldName);
			if(!newName.equals("")) field.setSerizableName(_fieldName,newName);
			clazz.fields.add(field);
		}

		return clazz;
	}

	private TypeName getBasicFieldType(String string) {
		if (isLongNumber(string))
			return ClassName.get(Long.class);
		else if (isDoubleNumber(string))
			return ClassName.get(Double.class);
		else if (isString(string))
			return ClassName.get(String.class);
		else
			throw new IllegalArgumentException("not a basic field. value : " + string);
	}

	private static final ClassName list = ClassName.get("java.util", "List");

	private TypeName getNextLevelType(Entity nextEntity, String innerClassName) {
		if (nextEntity.isClass()) {
			String _packageName = getPackageName();
			String _outterClassName = getClassName();
			String _innerClassName = upperFirstChar(innerClassName);
//			System.out.println("next is class");
			return ClassName.get(_packageName, _outterClassName, _innerClassName);
		} else { // LIST : basic type list , entity type list
			if (nextEntity.metaDatas.size()>0 && isIllegalData(nextEntity.metaDatas.get(0))){
//				System.out.println("next is basic list");
				return ParameterizedTypeName.get(list, getBasicFieldType(nextEntity.metaDatas.get(0)));
			}
			else{
//				System.out.println("next is class list");
				return ParameterizedTypeName.get(list, getNextLevelType(nextEntity.types.get(-1), innerClassName));
			}
		}
	}

	@Override
	public EntityClass getEntityClass() {
		if (entityClass == null) {
			entityClass = entity2Class(entity, getClassName(),isPublic());
		}
		return entityClass;
	}

	private String packageName = "";
	@Override
	public String getPackageName() {
		if ("".equals(packageName))
			packageName = packageName();
		return packageName;
	}

	private String className = "";
	public String getClassName() {
		if ("".equals(className))
			className = className();
		return className;
	}

	/**
	 * shrink the string type value to "" and wipe out the whitespace
	 * @param jsonString
	 * @return
     */
	static String shrinkJsonString(String jsonString) {
		jsonString = jsonString.replaceAll("\\s*","");
		return jsonString.replaceAll("(?<=([:\\[]))(\".*?\")(?=[,\\]\\}])","\"\"");
	}

}
