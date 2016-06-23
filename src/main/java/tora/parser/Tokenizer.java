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
        put("static",TokenType.KEYWORD );
        put("new",TokenType.KEYWORD );
        put("void",TokenType.KEYWORD );
        put("this",TokenType.KEYWORD );
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

    private Token next(){
        removeWhiteSpace();
        trackPosition();
        switch (ch) {
            case '0':
                return consumeBase2Number();
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return consumeDecNumber();
            case '"':
                return consumeString();
            case '{':
                nextChar();
                return newToken(TokenType.LBRACE, "{");
            case '}':
                nextChar();
                return newToken(TokenType.RBRACE, "}");
            case ':':
                nextChar();
                return newToken(TokenType.COLON, ":");
            case ',':
                nextChar();
                return newToken(TokenType.COMMA, ",");
            case '(':
                nextChar();
                return newToken(TokenType.LPAREN, "(");
            case ')':
                nextChar();
                return newToken(TokenType.RPAREN, ")");
            case '[':
                nextChar();
                return newToken(TokenType.LSQUARE, "[");
            case ']':
                nextChar();
                return newToken(TokenType.RSQUARE, "]");
            case '\0':
                nextChar();
                return newToken(TokenType.EOF, "END");
            case ';':
                nextChar();
                return newToken(TokenType.SEMICOLON, ";");
            default:
                return consumeOther();

        }
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

    private Token consumeBase2Number() {
        Double val = 0.0;

        StringBuffer buff = new StringBuffer();
        while(('0' <= ch && ch <= '9') || (ch >= 'a' && ch <= 'h')
                || (ch >= 'A' && ch <= 'H') || ch == 'x' ||
                ch == 'X' ||ch == 'o' ||ch == 'O'){
            buff.append(ch);
            nextChar();
        }
        String base2Val = buff.toString();
        String firstTwoLetters = base2Val.substring(0,2);

        if(firstTwoLetters.equals("0b") || firstTwoLetters.equals("0B")) {
            val = Long.parseLong( base2Val.substring(2), 2) / 1.0;
        } else if(firstTwoLetters.equals("0o") || firstTwoLetters.equals("0O")){
            val = Long.parseLong( base2Val.substring(2), 8) / 1.0;
        } else if(firstTwoLetters.equals("0x") || firstTwoLetters.equals("0X")){
            val = Long.parseLong( base2Val.substring(2), 16) / 1.0;
        } else if(base2Val.matches("[0-7]*")) {
            val = Long.parseLong( base2Val, 8) / 1.0;
        }

        return newNumberToken(TokenType.NUMBER, base2Val, val);
    }

    private Token consumeDecNumber(){
        StringBuffer buff = new StringBuffer();
        Boolean readSeperator = false;
        while(('0' <= ch && ch <= '9') || (!readSeperator && ch == '.')) {

            if(ch == '.') {
                readSeperator = true;
            }
            buff.append(ch);
            nextChar();
        }

        Double val = Double.parseDouble(buff.toString());

        return newNumberToken(TokenType.NUMBER, buff.toString(), val);

    }

    private void removeWhiteSpace(){
        while(ch == ' ' || ch == '\n') { nextChar();}
    }

    private Token consumeOperator() {
        StringBuffer buff = new StringBuffer();

        while((ch <= '?' && ch >= '<') || ch == '|' || ch == '&' || ch == '/' || ch == '+' ||
                ch == '=' || ch =='-' || ch == '%'|| ch == '*' || ch == '.'){
            buff.append(ch);
            nextChar();
        }
        return newToken(TokenType.OPERATOR, buff.toString());

    }

    private Token consumeWord() {

        StringBuffer buff = new StringBuffer();

        while((ch >= 'a' && ch <= 'z')||(ch >= 'A' && ch <= 'Z')|| ch == '$' || ch == '_' || (ch >= '0' && ch <= '9')){
            buff.append(ch);
            nextChar();
        }
        TokenType type = keywordMap.get(buff.toString());
        if(type == null) {
            type = TokenType.SYMBOL;
        }

        return newToken(type, buff.toString());
    }

    private Token consumeOther(){
        if((ch <= '?' && ch >= '<') || ch == '|' || ch == '&' || ch == '/' || ch == '+' ||
                ch == '=' || ch =='-' || ch == '%' || ch == '*' ||ch =='.') {
            return consumeOperator();
        } else {
            return consumeWord();
        }
    }

    private Token newToken(TokenType type, String tokenValue){
        return new Token(type, tokenValue, _posLine, _posCol, _posOffset, 0);
    }
    private Token newNumberToken(TokenType type, String tokenValue, Double num){
        return new Token(type, tokenValue, _posLine, _posCol, _offset, num);
    }

    private int _posLine;
    private int _posCol;
    private int _posOffset;

    private void trackPosition() {
        _posCol = _column;
        _posLine = _line;
        _posOffset = _offset;
    }

    private char nextChar(){

        if(_offset < _string.length()) {
            ch = _string.charAt(_offset);
            if(ch == '\n') {
                _line++;
                _column = -1;

            }
            _offset++;
            _column++;
        } else {
            ch = '\0';
        }
        return ch;
    }
    public ArrayList<Token> tokenize(){
        ArrayList<Token> list = new ArrayList<Token>();
        nextChar();
        trackPosition();
        Token token = next();

        while(token.getTokenType() != TokenType.EOF) {
            list.add(token);
            token = next();
        }
        return list;
    }
}
