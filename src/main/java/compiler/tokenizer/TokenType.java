package compiler.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /** fn */
    FN_KW,
    /** let */
    LET_KW,
    /** const */
    CONST_KW,
    /** as */
    AS_KW,
    /** while */
    WHILE_KW,
    /** if */
    IF_KW,
    /** else */
    ELSE_KW,
    /** return */
    RETURN_KW,
    /** break */
    BREAK_KW,
    /** continue */
    CONTINUE_KW,
    /** 无符号整数 */
    UINT_LITERAL,
    /** 字符串常量 */
    STRING_LITERAL,
    /** 浮点数常量 */
    DOUBLE_LITERAL,
    /** 字符常量 */
    CHAR_LITERAL,
    /** 标识符 */
    IDENT,
    /** + */
    PLUS,
    /** - */
    MINUS,
    /** * */
    MUL,
    /** / */
    DIV,
    /** = */
    ASSIGN,
    /** == */
    EQ,
    /** != */
    NEQ,
    /** < */
    LT,
    /** > */
    GT,
    /** <= */
    LE,
    /** >= */
    GE,
    /** ( */
    L_PAREN,
    /** ) */
    R_PAREN,
    /** { */
    L_BRACE,
    /** } */
    R_BRACE,
    /** -> */
    ARROW,
    /** , */
    COMMA,
    /** : */
    COLON,
    /** ; */
    SEMICOLON,
    /** 注释 */
    COMMENT ,
    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case None:
                return "NullToken";
            case FN_KW:
                return "fn";
            case LET_KW:
                return "let";
            case CONST_KW:
                return "const";
            case AS_KW:
                return "as";
            case WHILE_KW:
                return "while";
            case IF_KW:
                return "if";
            case ELSE_KW:
                return "else";
            case RETURN_KW:
                return "return";
            case BREAK_KW:
                return "break";
            case CONTINUE_KW:
                return "continue";
            case UINT_LITERAL:
                return "uint";
            case STRING_LITERAL:
                return "string";
            case DOUBLE_LITERAL:
                return "double";
            case CHAR_LITERAL:
                return "char";
            case IDENT:
                return "ident";
            case PLUS:
                return "plus";
            case MINUS:
                return "minus";
            case MUL:
                return "mul";
            case DIV:
                return "div";
            case ASSIGN:
                return "assign";
            case EQ:
                return "equal";
            case NEQ:
                return "nequal";
            case LT:
                return "less";
            case GT:
                return "greater";
            case LE:
                return "lessequal";
            case GE:
                return "greaterequal";
            case L_PAREN:
                return "lparen";
            case R_PAREN:
                return "rparen";
            case L_BRACE:
                return "lbrace";
            case R_BRACE:
                return "rbrace";
            case ARROW:
                return "arrow";
            case COMMA:
                return "comma";
            case COLON:
                return "colon";
            case SEMICOLON:
                return "semicolon";
            case COMMENT:
                return "comment";
            case EOF:
                return "eof";
            default:
                return "InvalidToken";
        }
    }
}
