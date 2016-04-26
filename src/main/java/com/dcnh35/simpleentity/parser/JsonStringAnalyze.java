package com.dcnh35.simpleentity.parser;

import java.util.Stack;


import com.dcnh35.simpleentity.exception.IllegalJsonStringException;

import static com.dcnh35.simpleentity.util.StringRegex.*;

/** split json string and store the slices into entity tree.*/
class JsonStringAnalyze {

//	private static final String regex = "(?<=([:\\d\\}\\]\"\\{\\[]))(,)";
	private static final String regex = "(,)(?=[,\"\\]\\}\\[\\{])";


	/**
	 * get result entity.
	 * @param sourceString must be shrinked json string.
	 * @return the parsed entity
	 * @throws IllegalJsonStringException
     */
	static Entity analyze(String sourceString) throws IllegalJsonStringException {

		//split the json string
		/**  { ... }  [ ... ] => ,{ ... ,} ,[ ... ,]  */
		sourceString = sourceString.replaceAll("(?=[\\{\\[\\}\\]])", ",");
//		System.out.println(sourceString);
//		System.out.println("****************************");


		/**  ,{ ... ,}  ,[ ... ,] => ,{, ... ,}, ,[, ... ,],  */
		sourceString = sourceString.replaceAll("(?<=([\\{\\[\\}\\]]))", ",");
//		System.out.println(sourceString);
//		System.out.println("----------------------------");

		 /**  }, => }  */
		sourceString = sourceString.replaceAll(",$","");
//		sourceString = sourceString.substring(0,sourceString.length()-1);

		/**  ,{, ... ,} =>  "{" "..." "..." "}" ""  */
		String[] slices = sourceString.split(regex);

		System.out.println("出错请贴出下面的输出信息/please issues me with information below");
		System.out.println("=========================");
		for(String slice:slices) {
			System.out.println(slice);
		}
		System.out.println("=========================");
		
		return analyzeSlices(slices);
	}
	
	
	private static Entity analyzeSlices(String[] slices) throws IllegalJsonStringException {

		/** check if json string starts with '{' or '[' */
		for(String firstSlice:slices) {
			if("".equals(firstSlice)) continue;
			if(!firstSlice.equals(CLASS_PREFIX) && !firstSlice.equals(LIST_PREFIX))
				throw new IllegalJsonStringException("json string not starts with { or [");
			else
				break;
		}

		Stack<Entity> stack = new Stack<>();
		Entity entity = null;
		Entity rootEntity = null;

		boolean nextEntity = false;

		/** create the entity tree storing the information of json string slices */
		for(String slice : slices) {
			if("".equals(slice)) continue;

			if(slice.equals(CLASS_PREFIX) || slice.equals(LIST_PREFIX)) {

				Entity old = entity;
				entity = Entity.newInstance(EntityType.getEntityType(slice));
				stack.push(old);
				if(old!=null) { old.putEntity(old.metaDatas.size()-1, entity);}
				else rootEntity = entity;
				nextEntity = false;
//				System.out.println("~~~~1~~~~~ meta: " + slice +entity);

			}else if(nextEntity){

				throw new IllegalJsonStringException("not enter next entity");

			}else if(slice.equals(CLASS_SUFFIX) || slice.equals(LIST_SUFFIX)) {

				if(!entity.type.equals(EntityType.getEndType(slice)))
					throw new IllegalJsonStringException("wrong format json string");
				entity = stack.pop();
//				System.out.println("~~~~2~~~~~ meta: " + slice+ entity);

			} else if(slice.matches("\"[a-zA-Z0-9_-]+\":.*")) {

				if(!entity.type.equals(EntityType.CLASS)) throw new IllegalJsonStringException("not match type");
				entity.addData(slice);
				if(slice.endsWith(":")) nextEntity = true;
//				System.out.println("~~~~3~~~~~ meta: " + slice +entity );

			} else if(isIllegalData(slice)) {

				if(!entity.type.equals(EntityType.LIST)) throw new IllegalJsonStringException("not match type");
				entity.addData(slice);
//				System.out.println("~~~~4~~~~~ meta: " + slice +entity);

			} else 
				throw new IllegalJsonStringException("not find the match pattern, metaString : " + slice);
			
		}
		
		if(!stack.isEmpty()) {
			throw new IllegalJsonStringException("not enough end");
		}
		
		return rootEntity;
	}
	
	



}

