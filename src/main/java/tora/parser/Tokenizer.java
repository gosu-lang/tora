package tora.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tora.parser.TokenType;
import java.util.regex.*;

public class Tokenizer
{
    static public class Token {


        private TokenType _tokenType;
        private String _value;
        private int _lineNumber;
        private int _column;
        private int _offset;
        private double _num;

        public Token(TokenType type, String value, int lineNumber, int column, int offset,double num ){
            _value = value;
            _tokenType = type;
            _lineNumber = lineNumber;
            _offset = offset;
            _column = column;
            _num = num;
        }
        public int getColumn() {return _column;}
        public int getLineNumber () {return _lineNumber;}
        public int getOffset () {return _offset;}
        public double getTokenNumberValue(){return _num;}
        public String getTokenStringValue(){return _value;}
        public TokenType getTokenType(){return _tokenType;}
    }

    Map<String,TokenType> keywordMap = new HashMap<String,TokenType>() {{
        put("false",TokenType.FALSE );
        put("true",TokenType.TRUE );
        put("null",TokenType.NULL );
        put("function",TokenType.FUNCTION );
        put("class",TokenType.CLASS );
        put("get",TokenType.PROPERTY );
        put("set",TokenType.PROPERTY );
        put("for",TokenType.CONTROL );
        put("if",TokenType.CONTROL );
        put("while",TokenType.CONTROL );
        put("do",TokenType.CONTROL );
        put("continue",TokenType.CONTROL );
        put("return",TokenType.CONTROL );
        put("try",TokenType.CONTROL );
        put("catch",TokenType.CONTROL );
        put("switch",TokenType.CONTROL );
        put("break",TokenType.CONTROL );
        put("case",TokenType.CONTROL );
        put("else",TokenType.CONTROL );
        put("in",TokenType.CONTROL );
        put("throw",TokenType.CONTROL );
        put("in",TokenType.KEYWORD );
        put("default",TokenType.KEYWORD );
        put("enum",TokenType.KEYWORD );
        put("instanceof",TokenType.KEYWORD );
        put("super",TokenType.KEYWORD );
        put("var",TokenType.KEYWORD );
        put("delete",TokenType.KEYWORD );
        put("eval",TokenType.KEYWORD );
        put("new",TokenType.KEYWORD );
        put("void",TokenType.KEYWORD );
        put("default",TokenType.KEYWORD );
        put("import",TokenType.RESERVED );
        put("extends",TokenType.RESERVED );
        put("case",TokenType.RESERVED );
        put("package",TokenType.RESERVED );
        put("import",TokenType.RESERVED );

    }};
    private String _string;
    private int _offset;
    private int _line;
    private int _column;
    private char ch;
    private int _errorCount;

    public Tokenizer(String string) {
        _string = string;
        _offset = 0;
        _line = 1;
        _column = 0;

    }
    public String getString() {
        return _string;
    }

    public static TokenType getTokenType(Token toke){
        return toke.getTokenType();
    }

    public static String getTokenString(Token toke){
        return toke.getTokenStringValue();
    }

    public static double getTokenValue(Token toke) {
        return toke.getTokenNumberValue();
    }
    private Token next(){
        removeWhiteSpace();
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return consumeNumber();
            case '"':
                return consumeString();
            case '{':
                return newToken(TokenType.LBRACE, "{");
            case '}':
                return newToken(TokenType.RBRACE, "}");
            case ':':
                return newToken(TokenType.COLON, ":");
            case ',':
                return newToken(TokenType.COMMA, ",");
            case '(':
                return newToken(TokenType.LPAREN, "(");
            case ')':
                return newToken(TokenType.RPAREN, ")");
            case '[':
                return newToken(TokenType.LSQUARE, "[");
            case ']':
                return newToken(TokenType.RSQUARE, "]");
            case '\0':
                return newToken(TokenType.EOF, "END");
            case ';':
                return newToken(TokenType.SEMICOLON, ";");
            default:
                return consumeOther();

        }
    }

    private char nextChar(){

        if(_offset < _string.length()) {
            ch = _string.charAt(_offset);
            if(ch == '\n') {
                _line++;
                _column = 0;
            }
            _offset++;
            _column++;
        } else {
            ch = '\0';
        }
        return ch;
    }
    private Token consumeString(){
        String str = "";
        String val = Character.toString(ch);
        String charactersRegex = "^[ ;:\"\\]";
        nextChar();
        while(ch != '"' && ch != '\0'){
            str = str + ch;
            nextChar();
        }
        nextChar();


        str = "\"" + str + "\"";
        return newToken(TokenType.STRING, str);
    }
    private Token consumeNumber(){

        Pattern numberPattern = Pattern.compile("[1-9][0-9]*(\\.)?[0-9]*e?\\-?\\+?[0-9]*");
        Matcher numberMatcher = numberPattern.matcher(_string);
        numberMatcher.find(_offset-1);

        Pattern hexPattern = Pattern.compile("0x?b?o?X?B?O?[0-9a-f]*");
        Matcher hexMatcher = hexPattern.matcher(_string);
        Boolean result = hexMatcher.find(_offset-1);
        String tokenName = "";

        Double val = 0.0;
        if(result && hexMatcher.start() == _offset-1){ //Hex/ binary/ or other
            tokenName = hexMatcher.group();

            String firstTwoLetters = hexMatcher.group().substring(0,2);
            if(firstTwoLetters.equals("0b") || firstTwoLetters.equals("0B")) {
                val = Long.parseLong( hexMatcher.group().substring(2), 2) / 1.0;
            } else if(firstTwoLetters.equals("0o") || firstTwoLetters.equals("0O")){
                val = Long.parseLong( hexMatcher.group().substring(2), 8) / 1.0;
            } else if(firstTwoLetters.equals("0x") || firstTwoLetters.equals("0X")){
                val = Long.parseLong( hexMatcher.group().substring(2), 16) / 1.0;
            } else if(hexMatcher.group().matches("[0-7]*")) {
                val = Long.parseLong( hexMatcher.group(), 8) / 1.0;
            }

        } else { //Decimal
            tokenName = numberMatcher.group();
            val = Double.parseDouble(numberMatcher.group());
        }

        for (int i = 0; i < tokenName.length() - 1; i++) {
            nextChar();
        }
        return newNumberToken(TokenType.NUMBER, tokenName, val);

    }

    private void removeWhiteSpace(){
        while(ch == ' ') { nextChar();}
    }


    private Token consumeOther(){
        Pattern symbolPattern = Pattern.compile("[A-Za-z0-9$_]*");
        Matcher symbolMatcher = symbolPattern.matcher(_string);
        symbolMatcher.find(_offset-1);
        Pattern operatorPattern = Pattern.compile("[/\\*<>\\?%+=\\-\\.]*");
        Matcher operatorMatcher = operatorPattern.matcher(_string);
        operatorMatcher.find(_offset -1);

        String tokenName = "";

        if(symbolMatcher.group().equals("")) { //Operator
            tokenName = operatorMatcher.group();
            for (int i = 0; i < tokenName.length() - 1; i++) {
                nextChar();
            }
            return newToken(TokenType.OPERATOR, tokenName);

        } else { // Symbol

            tokenName = symbolMatcher.group();
            for (int i = 0; i < tokenName.length() - 1; i++) {
                nextChar();
            }

            TokenType type = keywordMap.get(tokenName);
            if(type == null) {
                type = TokenType.SYMBOL;
            }
            return newToken(type, tokenName);
        }
    }

    private Token newToken(TokenType type, String tokenValue){
        return new Token(type, tokenValue, _line, _column, _offset +1, 0);
    }
    private Token newNumberToken(TokenType type, String tokenValue, Double num){
        return new Token(type, tokenValue, _line, _column, _offset+1, num);
    }

    public ArrayList<Token> tokenize(){
        ArrayList<Token> list = new ArrayList<Token>();
        nextChar();

        Token token = next();
        while(token.getTokenType() != TokenType.EOF) {
            list.add(token);
            nextChar();
            token = next();
        }
        return list;
    }
}
