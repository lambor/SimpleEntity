package com.dcnh35.simpleentity.parser;

import java.util.*;


class Entity {

	static int curId = 0;

	// store next level entity
	HashMap<Integer,Entity> types = new HashMap<>();

	// store leaf data string
	List<String> metaDatas = new ArrayList<>();

	EntityType type ; //list or class

	// the id of Entity
	int id;

	private Entity(EntityType type) {
		if(type.equals(EntityType.NORMAL)) throw new IllegalArgumentException("Entity's type cannot be NORMAL");
		this.type = type;
		id = curId++;
	}

	static Entity newInstance(EntityType type) {
		return new Entity(type);
	}


	void putEntity(int index,Entity entity) {
		types.put(index,entity);
	}

	@SuppressWarnings("unused")
	public void setDatas (List<String> datas) {
		metaDatas = datas;
	}
	
	void addData (String data) {
		metaDatas.add(data);
	}

	@SuppressWarnings("unused")
	boolean isLeaf() {
		return types.isEmpty();
	}

	
	@Override
	public String toString() {
		return "\r\nmetaDatas:"+metaDatas
				+ "\r\ntype:" + (type.equals(EntityType.CLASS)?"class":"list")
				+ "\r\nid:" + id
				+ "\r\nnext:"+ getIds(types.values());
	}

	private String getIds(Collection<Entity> entities) {
		String result = "";
		for(Entity entity:entities)
			result += entity.id+",";
		return result;
	}
	
	boolean isClass() {
		return EntityType.CLASS.equals(type);
	}

	@SuppressWarnings("unused")
	boolean isList() {
		return EntityType.LIST.equals(type);
	}
}
