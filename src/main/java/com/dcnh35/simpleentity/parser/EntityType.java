package com.dcnh35.simpleentity.parser;

import static com.dcnh35.simpleentity.util.StringRegex.*;

/**
 * Created by lambor on 16-4-21.
 */
enum EntityType {

    LIST,CLASS,NORMAL;

    static EntityType getEntityType(String sourceString) {
        sourceString = sourceString.trim();
        if(sourceString.startsWith(CLASS_PREFIX)) return EntityType.CLASS;
        else if(sourceString.startsWith(LIST_PREFIX)) return EntityType.LIST;
        else return EntityType.NORMAL;
    }

    static EntityType getEndType(String sourceString) {
        sourceString = sourceString.trim();
        if(sourceString.endsWith(CLASS_SUFFIX)) return EntityType.CLASS;
        else if(sourceString.endsWith(LIST_SUFFIX)) return EntityType.LIST;
        else return EntityType.NORMAL;
    }
}