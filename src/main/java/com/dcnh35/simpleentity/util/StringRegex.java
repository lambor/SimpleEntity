package com.dcnh35.simpleentity.util;

/**
 * Created by lambor on 16-4-21.
 */
public class StringRegex {

    public static String STRING_PREFIX = "\"";
    public static String CLASS_PREFIX = "{";
    public static String LIST_PREFIX = "[";

    public static String STRING_SUFFIX = STRING_PREFIX;
    public static String CLASS_SUFFIX = "}";
    public static String LIST_SUFFIX = "]";

    public static String WHITESPACE_REGX = "\\s*";

    public static String COMMA = ",";
    public static String COMMA_WITH_WHITESPACES =WHITESPACE_REGX + COMMA + WHITESPACE_REGX;

    public static String STRING_SPLITER = STRING_SUFFIX + COMMA_WITH_WHITESPACES;
    public static String CLASS_SPLITER = CLASS_SUFFIX + COMMA_WITH_WHITESPACES;
    public static String LIST_SPLITER = LIST_SUFFIX + COMMA_WITH_WHITESPACES;

    static String whitespace_chars =  ""       /* dummy empty string for homogeneity */
            + "\\u0009" // CHARACTER TABULATION
            + "\\u000A" // LINE FEED (LF)
            + "\\u000B" // LINE TABULATION
            + "\\u000C" // FORM FEED (FF)
            + "\\u000D" // CARRIAGE RETURN (CR)
            + "\\u0020" // SPACE
            + "\\u0085" // NEXT LINE (NEL)
            + "\\u00A0" // NO-BREAK SPACE
            + "\\u1680" // OGHAM SPACE MARK
            + "\\u180E" // MONGOLIAN VOWEL SEPARATOR
            + "\\u2000" // EN QUAD
            + "\\u2001" // EM QUAD
            + "\\u2002" // EN SPACE
            + "\\u2003" // EM SPACE
            + "\\u2004" // THREE-PER-EM SPACE
            + "\\u2005" // FOUR-PER-EM SPACE
            + "\\u2006" // SIX-PER-EM SPACE
            + "\\u2007" // FIGURE SPACE
            + "\\u2008" // PUNCTUATION SPACE
            + "\\u2009" // THIN SPACE
            + "\\u200A" // HAIR SPACE
            + "\\u2028" // LINE SEPARATOR
            + "\\u2029" // PARAGRAPH SEPARATOR
            + "\\u202F" // NARROW NO-BREAK SPACE
            + "\\u205F" // MEDIUM MATHEMATICAL SPACE
            + "\\u3000" // IDEOGRAPHIC SPACE
            ;
    /* A \s that actually works for Java’s native character set: Unicode */
    public static String  WHITESPACES = "["  + whitespace_chars + "]*";
    /* A \S that actually works for  Java’s native character set: Unicode */
    public static String NOT_WHITESPACES = "[^" + whitespace_chars + "]";



    public static boolean isLongNumber(String data) {
        return data.matches("[0-9]+");
    }

    public static boolean isDoubleNumber(String data) {
        return data.matches("[0-9]+\\.[0-9]+");
    }

    public static boolean isString(String data) {
        return data.matches("\".*\"$");
    }

    public static boolean isIllegalData(String data) {
        return isLongNumber(data) || isDoubleNumber(data) || isString(data);
    }
}
