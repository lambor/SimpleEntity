package com.dcnh35.simpleentity.util;

/**
 * Created by lambor on 16-5-14.
 */
public class StringUtil {
    public static String upperFirstChar(String str) {
        if("".equals(str)) return "";
        String first = str.substring(0, 1);
        String last = str.substring(1);
        return first.toUpperCase() + last;
    }

    public static String lowerFirstChar(String str) {
        if("".equals(str)) return "";
        String first = str.substring(0, 1);
        String last = str.substring(1);
        return first.toLowerCase() + last;
    }
}
