package tora.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ecai on 6/21/2016.
 */


public enum TokenType {
    KEYWORD,
    IDENTIFIER,
    CLASS,
    NUMBER,
    BOOLEAN,
    NULL,
    STRING,
    PUNCTUATION,
    OPERATOR,
    COMMENT,
    ERROR,
    EOF;

    //Keywords taken from ecma-262 6 11.6.2.1, excluding class which is its own token
    private static final String[] KEYWORDS = {"break", "do", "in", "typeof",
            "case", "else", "instanceof", "var",
            "catch", "export", "new", "void",
            "extends", "return", "while",
            "const", "finally", "super", "with",
            "continue", "for", "switch", "yield",
            "debugger", "function", "this",
            "default", "if", "throw",
            "delete", "import", "try"};
    private static final Set<String> KEYWORDS_SET = new HashSet<String>(Arrays.asList(KEYWORDS));

    //Taken from mozilla expressions and operators guide
    private static String[] OPERATORS = {"=", "+=", "-=", "*=", "/=", "%=", "**=", "<<=", ">>=", ">>>=", "&=", "^=",
            "|=", //Assignment operators
            "==", "!=", "===", "!==", ">", ">=", "<", "<=", //Comparator operators
            "%", "++", "--", "-", "+", "**", "/", "*", //Arithmetic operators
            "&", "|", "^", "~", "<<", ">>", ">>>", //Bitwise operators
            "&&", "||", "!",  //Logical operators
            "?", ":"}; //Ternary operators
    private static final Set<String> OPERATORS_SET = new HashSet<String>(Arrays.asList(OPERATORS));

    //Rules for identifier names from emca-262 section 11.6.1
    public static boolean startsIdentifier(char ch) {
        return String.valueOf(ch).matches("[a-zA-Z$_]");
    }

    public static boolean partOfIdentifier(char ch) {
        return String.valueOf(ch).matches("[0-9a-zA-Z$_]");
    }

    public static boolean isKeyword(String word) {
        return KEYWORDS_SET.contains(word);
    }

    public static boolean isNull(String word) {
        return word.equals("null");
    }

    public static boolean isBoolean(String word) {
        return word.equals("false") || word.equals("true");
    }

    public static boolean isClass(String word) {
        return word.equals("class");
    }

    public static boolean isPunctuation(char ch) {
        return "(){}[].,;".indexOf(ch) >= 0;
    }

    public static boolean isPartOfOperator(char ch) {
        return "=+-*/%<>&^|!~?:".indexOf(ch) >= 0;
    }

    public static boolean isOperator(String word) {
        return OPERATORS_SET.contains(word);
    }

    public static boolean isHexCh(char ch) {
        return String.valueOf(ch).matches("[0-9a-fA-F]");
    }

    public static boolean isDigit(char ch) {
        return String.valueOf(ch).matches("[0-9]");
    }

    public static boolean isBinary(char ch) {
        return ch == '0' || ch == '1';
    }

    public static boolean isOctal(char ch) {
        return String.valueOf(ch).matches("[0-7]");
    }


}
