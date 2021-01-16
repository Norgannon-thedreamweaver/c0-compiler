package miniplc0java.tokenizer;

import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();

        // 跳过之前的所有空白字符
        skipSpaceCharacters();
        Token ret;

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }

        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            ret= lexNumber();
        } else if (Character.isAlphabetic(peek)|| peek=='_') {
            ret= lexIdentOrKeyword();
        } else if(peek=='\''){
            ret= lexChar();
        } else if(peek=='\"'){
            ret= lexString();
        } else {
            ret= lexOperatorOrUnknown();
        }

        if(ret.getTokenType()==TokenType.COMMENT)
            return nextToken();
        return ret;
    }

    private Token lexNumber() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 解析存储的字符串为无符号整数
        // 解析成功则返回无符号整数类型的token，否则返回编译错误
        //
        // Token 的 Value 应填写数字的值
        Pos start = it.currentPos();
        char peek = it.peekChar();
        StringBuilder numberPart = new StringBuilder();
        StringBuilder floatPart = new StringBuilder();
        String signPart = "";
        StringBuilder exponentPart = new StringBuilder();

        while(Character.isDigit(peek)){//一直读数字
            numberPart.append(it.nextChar());
            peek = it.peekChar();
        }
        if(peek=='.'){
            it.nextChar();
            peek = it.peekChar();
            if(!Character.isDigit(peek))
                throw new TokenizeError(ErrorCode.InvalidInput, start);
            while(Character.isDigit(peek)){//一直读数字
                floatPart.append(it.nextChar());
                peek = it.peekChar();
            }
            if(peek == 'e' || peek == 'E'){
                it.nextChar();
                peek = it.peekChar();
                if(peek == '-'){
                    it.nextChar();
                    signPart="-";
                }
                else if(peek == '+'){
                    it.nextChar();
                    signPart="+";
                }
                else
                    signPart="+";
                peek = it.peekChar();
                if(!Character.isDigit(peek))
                    throw new TokenizeError(ErrorCode.InvalidInput, start);
                while(Character.isDigit(peek)){//一直读数字
                    floatPart.append(it.nextChar());
                    peek = it.peekChar();
                }
                String str= numberPart.toString()+"."+floatPart.toString()+"e"+signPart+exponentPart.toString();
                double d = Double.parseDouble(str);
                return new Token(TokenType.DOUBLE_LITERAL,d,start,it.currentPos());
            }
            else{
                String str= numberPart.toString()+"."+floatPart.toString();
                double d = Double.parseDouble(str);
                return new Token(TokenType.DOUBLE_LITERAL,d,start,it.currentPos());
            }
        }
        else if(peek == 'e' || peek == 'E'){
            it.nextChar();
            peek = it.peekChar();
            if(peek == '-'){
                it.nextChar();
                signPart="-";
            }
            else if(peek == '+'){
                it.nextChar();
                signPart="+";
            }
            else
                signPart="+";
            peek = it.peekChar();
            if(!Character.isDigit(peek))
                throw new TokenizeError(ErrorCode.InvalidInput, start);
            while(Character.isDigit(peek)){//一直读数字
                floatPart.append(it.nextChar());
                peek = it.peekChar();
            }
            String str= numberPart.toString()+"."+floatPart.toString()+"e"+signPart+exponentPart.toString();
            double d = Double.parseDouble(str);
            return new Token(TokenType.DOUBLE_LITERAL,d,start,it.currentPos());
        }
        else{
            Integer i = Integer.valueOf(numberPart.toString());
            return new Token(TokenType.UINT_LITERAL,i,start,it.currentPos());
        }
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        // 请填空：
        // 直到查看下一个字符不是数字或字母为止:
        // -- 前进一个字符，并存储这个字符
        //
        // 尝试将存储的字符串解释为关键字
        // -- 如果是关键字，则返回关键字类型的 token
        // -- 否则，返回标识符
        //
        // Token 的 Value 应填写标识符或关键字的字符串
        Pos start=it.currentPos();
        StringBuilder str=new StringBuilder();
        while(Character.isAlphabetic(it.peekChar()) || Character.isDigit(it.peekChar()) || it.peekChar() == '_'){
            str.append(it.nextChar());
        }
        String s=str.toString();

        if(s.equals("fn"))
            return new Token(TokenType.FN_KW,s,start,it.currentPos());
        else if(s.equals("let"))
            return new Token(TokenType.LET_KW,s,start,it.currentPos());
        else if(s.equals("const"))
            return new Token(TokenType.CONST_KW,s,start,it.currentPos());
        else if(s.equals("as"))
            return new Token(TokenType.AS_KW,s,start,it.currentPos());
        else if(s.equals("while"))
            return new Token(TokenType.WHILE_KW,s,start,it.currentPos());
        else if(s.equals("if"))
            return new Token(TokenType.IF_KW,s,start,it.currentPos());
        else if(s.equals("else"))
            return new Token(TokenType.ELSE_KW,s,start,it.currentPos());
        else if(s.equals("return"))
            return new Token(TokenType.RETURN_KW,s,start,it.currentPos());
        else if(s.equals("break"))
            return new Token(TokenType.BREAK_KW,s,start,it.currentPos());
        else if(s.equals("continue"))
            return new Token(TokenType.CONTINUE_KW,s,start,it.currentPos());
        else
            return new Token(TokenType.IDENT,s,start,it.currentPos());
    }
    private Token lexChar() throws TokenizeError{
        Pos start=it.currentPos();
        it.nextChar();
        char peek=it.peekChar();
        if(peek=='\\'){
            char escape=lexEscapeSequence();
            if(it.nextChar()=='\'')
                return new Token(TokenType.CHAR_LITERAL, escape, start, it.currentPos());
            throw new TokenizeError(ErrorCode.InvalidInput, start);
        }
        else if(peek!='\'' && peek!='\n' && peek!='\t' && peek!='\r'){
            char c=it.nextChar();
            if(it.nextChar()=='\'')
                return new Token(TokenType.CHAR_LITERAL, c, start, it.currentPos());
            throw new TokenizeError(ErrorCode.InvalidInput, start);
        }
        throw new TokenizeError(ErrorCode.InvalidInput, start);
    }

    private Token lexString() throws TokenizeError{
        Pos start=it.currentPos();
        StringBuilder str=new StringBuilder();
        it.nextChar();
        char peek=it.peekChar();
        while(peek!='\"'){
            if(peek=='\\'){
                str.append(lexEscapeSequence());
            }
            else if(peek!='\'' && peek!='\n' && peek!='\t' && peek!='\r'){
                str.append(it.nextChar());
            }
            else
                throw new TokenizeError(ErrorCode.InvalidInput, start);
            peek=it.peekChar();
        }
        it.nextChar();
        String s=str.toString();
        return new Token(TokenType.STRING_LITERAL, s, start, it.currentPos());
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        Pos start=it.currentPos();
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.PLUS, '+', start, it.currentPos());
            case '-':
                if(it.peekChar()=='>'){
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", start, it.currentPos());
                }
                return new Token(TokenType.MINUS, '-', start, it.currentPos());
            case '*':
                return new Token(TokenType.MUL, '*', start, it.currentPos());
            case '/':
                if(it.peekChar()=='/'){
                    while(it.nextChar()!='\n');
                    return new Token(TokenType.COMMENT, "//", start, it.currentPos());
                }
                return new Token(TokenType.DIV, '/', start, it.currentPos());
            case '=':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", start, it.currentPos());
                }
                return new Token(TokenType.ASSIGN, '=', start, it.currentPos());
            case '!':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.NEQ, "!=", start, it.currentPos());
                }
                throw new TokenizeError(ErrorCode.InvalidInput, start);
            case '<':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", start, it.currentPos());
                }
                return new Token(TokenType.LT, '<', start, it.currentPos());
            case '>':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", start, it.currentPos());
                }
                return new Token(TokenType.GT, '>', start, it.currentPos());
            case '(':
                return new Token(TokenType.L_PAREN, '(', start, it.currentPos());
            case ')':
                return new Token(TokenType.R_PAREN, ')', start, it.currentPos());
            case '{':
                return new Token(TokenType.L_BRACE, '{', start, it.currentPos());
            case '}':
                return new Token(TokenType.R_BRACE, '}', start, it.currentPos());
            case ',':
                return new Token(TokenType.COMMA, ',', start, it.currentPos());
            case ':':
                return new Token(TokenType.COLON, ':', start, it.currentPos());
            case ';':
                return new Token(TokenType.SEMICOLON, ';', start, it.currentPos());

            default:
                // 不认识这个输入，摸了
                throw new TokenizeError(ErrorCode.InvalidInput, start);
        }
    }

    private char lexEscapeSequence() throws TokenizeError{
        Pos start=it.currentPos();
        it.nextChar();// \
        switch(it.nextChar()){
            case '\\':
                return '\\';
            case '\"':
                return '\"';
            case '\'':
                return '\'';
            case 'n':
                return '\n';
            case 't':
                return '\t';
            case 'r':
                return '\r';
            default:
                throw new TokenizeError(ErrorCode.InvalidInput, start);
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
