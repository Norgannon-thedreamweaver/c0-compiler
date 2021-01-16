package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;

    public SymbolTable funcTable;
    public SymbolTable globalTable;

    public SymbolEntry _start=null;
    public SymbolEntry main=null;
    /** 当前偷看的 token */
    Token peekedToken = null;

    public Analyser(Tokenizer tokenizer) throws AnalyzeError {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
        SymbolOffset shareInt= new SymbolOffset(0);
        funcTable=new SymbolTable(shareInt);
        globalTable=new SymbolTable(shareInt);

        this._start=new SymbolEntry("_start","_start",false,globalTable.getNextVariableOffset(),
                SymbolType.FN,IdentType.VOID,false,0,new SymbolTable(0),new SymbolTable(0),this.instructions);
        funcTable.addSymbol(_start,null);
        globalTable.addSymbol(_start,null);

        /*
        SymbolEntry entry;
        entry=new SymbolEntry("getint","getint",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.INT);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("getdouble","getdouble",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.DOUBLE);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("getchar","getchar",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.INT);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("putint","putint",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.VOID);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("putdouble","putdouble",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.VOID);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("putchar","putchar",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.VOID);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("putstr","putstr",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.VOID);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        entry=new SymbolEntry("putln","putln",true,globalTable.getNextVariableOffset(),SymbolType.FN,IdentType.VOID);
        funcTable.addSymbol(entry,null);
        globalTable.addSymbol(entry,null);
        */
        //System.out.println("func:"+funcTable.getNextOffset().getOffset());
        //System.out.println("global:"+globalTable.getNextOffset().getOffset());
    }

    public void analyse() throws CompileError {
        analyseProgram();
    }

    /**
     * 查看下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     * 
     * @param tt
     * @return
     * @throws TokenizeError
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * 
     * @param tt 类型
     * @return 如果匹配则返回这个 token，否则返回 null
     * @throws TokenizeError
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     * 
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    private void analyseProgram() throws CompileError {
        analyseMain();
        expect(TokenType.EOF);
    }

    private void analyseMain() throws CompileError {
        while(true){
            if(check(TokenType.CONST_KW)||check(TokenType.LET_KW)){
                analyseDeclaration(this._start,globalTable);
            }
            else if(check(TokenType.FN_KW)){
                analyseFunction();
            }
            else
                break;
        }
    }
    private void analyseDeclaration(SymbolEntry cur_func,SymbolTable varTable) throws CompileError{
        if(check(TokenType.CONST_KW)){
            analyseConstDeclaration(cur_func,varTable);
        }
        else if(check(TokenType.LET_KW)){
            analyseLetDeclaration(cur_func,varTable);
        }
        else{
            throw new ExpectedTokenError(List.of(TokenType.CONST_KW,TokenType.LET_KW), next());
        }

    }
    private void analyseConstDeclaration(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        expect(TokenType.CONST_KW);
        Token name=expect(TokenType.IDENT);
        IdentType type;
        expect(TokenType.COLON);

        Token ty=expect(TokenType.IDENT);
        if(ty.getValue().toString().equals("int")){
            type=IdentType.INT;
        }
        else if(ty.getValue().toString().equals("double")){
            type=IdentType.DOUBLE;
        }
        else{
            System.out.println("read ty:"+ty.getValue());
            throw new AnalyzeError(ErrorCode.ExpectTY, peekedToken.getStartPos());
        }
        expect(TokenType.ASSIGN);

        SymbolEntry symbol=new SymbolEntry(name.getValue().toString(),0L,true,varTable.getNextVariableOffset(),SymbolType.CONST,type);

        if(varTable.isStart()){
            cur_func.getInstructions().add(new Instruction(Operation.GLOBA,symbol.getStackOffset()));
        }
        else{
            cur_func.getInstructions().add(new Instruction(Operation.LOCA,symbol.getStackOffset()));
        }

        IdentType expressionType=analyseExpression(cur_func,varTable);
        if(expressionType!=type){
            throw new AnalyzeError(ErrorCode.InvalidType,name.getStartPos());
        }
        cur_func.getInstructions().add(new Instruction(Operation.STORE_64));
        expect(TokenType.SEMICOLON);

        varTable.addSymbol(cur_func,symbol,name.getStartPos());
    }
    private void analyseLetDeclaration(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        expect(TokenType.LET_KW);
        Token name=expect(TokenType.IDENT);
        IdentType type;
        expect(TokenType.COLON);

        Token ty=expect(TokenType.IDENT);
        if(ty.getValue().toString().equals("int")){
            type=IdentType.INT;
        }
        else if(ty.getValue().toString().equals("double")){
            type=IdentType.DOUBLE;
        }
        else{
            System.out.println("read ty:"+ty.getValue());
            throw new AnalyzeError(ErrorCode.ExpectTY, peekedToken.getStartPos());
        }
        SymbolEntry symbol=new SymbolEntry(name.getValue().toString(),0L,false,varTable.getNextVariableOffset(),SymbolType.LET,type);

        if(check(TokenType.ASSIGN)){
            expect(TokenType.ASSIGN);
            symbol.setInitialized(true);
            if(varTable.isStart()){
                cur_func.getInstructions().add(new Instruction(Operation.GLOBA,symbol.getStackOffset()));
            }
            else{
                cur_func.getInstructions().add(new Instruction(Operation.LOCA,symbol.getStackOffset()));
            }

            IdentType expressionType=analyseExpression(cur_func,varTable);
            if(expressionType!=type){
                throw new AnalyzeError(ErrorCode.InvalidType,name.getStartPos());
            }
            cur_func.getInstructions().add(new Instruction(Operation.STORE_64));
        }
        expect(TokenType.SEMICOLON);
        varTable.addSymbol(cur_func,symbol,name.getStartPos());
    }

    private void analyseFunction() throws CompileError {
        SymbolEntry symbol=new SymbolEntry(globalTable.getNextVariableOffset());
        symbol.setSymbolType(SymbolType.FN);
        symbol.setInitialized(true);

        ArrayList<Instruction> cur_instructions=new ArrayList<>();
        symbol.setInstructions(cur_instructions);


        SymbolTable paramTable=new SymbolTable(1);
        symbol.setParamTable(paramTable);
        paramTable.setLastTable(globalTable);

        SymbolTable localTable=new SymbolTable(0);
        symbol.setLocalTable(localTable);
        localTable.setLastTable(paramTable);

        expect(TokenType.FN_KW);
        var name = expect(TokenType.IDENT);
        symbol.setName(name.getValue().toString());
        IdentType type;

        expect(TokenType.L_PAREN);
        if(check(TokenType.CONST_KW)||check(TokenType.IDENT)){
            analyseParamList(symbol);
        }
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        switch (expect(TokenType.IDENT).getValue().toString()) {
            case "int":
                type = IdentType.INT;
                break;
            case "double":
                type = IdentType.DOUBLE;
                break;
            case "void":
                type = IdentType.VOID;
                break;
            default:
                throw new AnalyzeError(ErrorCode.ExpectTY, peekedToken.getStartPos());
        }
        symbol.setIdentType(type);
        if(name.getValue().toString().equals("main")){
            this.main=symbol;
            if(type==IdentType.VOID){
                _start.getInstructions().add(new Instruction(Operation.STACKALLOC, 0L));
            }
            else {
                _start.getInstructions().add(new Instruction(Operation.STACKALLOC,1L));
            }
            _start.getInstructions().add(new Instruction(Operation.CALL,symbol.getStackOffset()));
        }
        globalTable.addSymbol(symbol,name.getStartPos());
        funcTable.addSymbol(symbol,name.getStartPos());
        analyseBlockStatement(symbol,localTable,false,-1);
        if(type==IdentType.VOID||symbol.getInstructions().get(symbol.getInstructions().size()-1).getOpt()!=Operation.RET){
            symbol.getInstructions().add(new Instruction(Operation.RET));
        }
    }

    private void analyseParamList(SymbolEntry cur_func) throws CompileError{
        SymbolEntry symbol=new SymbolEntry(cur_func.getParamTable().getNextVariableOffset());

        if(nextIf(TokenType.CONST_KW)!=null){
            symbol.setSymbolType(SymbolType.CONST);
        }
        else{
            symbol.setSymbolType(SymbolType.LET);
        }
        var name = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        IdentType type;
        switch (expect(TokenType.IDENT).getValue().toString()) {
            case "int":
                type = IdentType.INT;
                break;
            case "double":
                type = IdentType.DOUBLE;
                break;
            default:
                throw new AnalyzeError(ErrorCode.ExpectTY, peekedToken.getStartPos());
        }

        symbol.setName(name.getValue().toString());
        symbol.setInitialized(true);
        symbol.setIdentType(type);
        symbol.setParam(true);
        cur_func.setParam_cnt(cur_func.getParamTable().getSymbolTable().size()+1);

        cur_func.getParamTable().addSymbol(symbol,name.getStartPos());
        if(nextIf(TokenType.COMMA)!=null){
            analyseParamList(cur_func);
        }
    }

    private Instruction[] analyseBlockStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError{
        ArrayList<Instruction> brList=new ArrayList<>();
        expect(TokenType.L_BRACE);
        while(check(TokenType.IF_KW)||check(TokenType.WHILE_KW)||check(TokenType.RETURN_KW)
                ||check(TokenType.SEMICOLON)||check(TokenType.MINUS)||check(TokenType.IDENT)
                ||check(TokenType.LET_KW)||check(TokenType.CONST_KW)
                ||check(TokenType.CONTINUE_KW)||check(TokenType.BREAK_KW)
                ||check(TokenType.L_BRACE)) {
            Instruction[] br=analyseStatement(cur_func,varTable,isWhile,startOffset);
            if(br!=null){
                brList.addAll(Arrays.asList(br));
            }
        }
        expect(TokenType.R_BRACE);
        return brList.toArray(new Instruction[0]);
    }
    private Instruction[] analyseStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError {
        if(check(TokenType.IF_KW)){
            return analyseIfStatement(cur_func,varTable,isWhile,startOffset);
        }
        else if(check(TokenType.WHILE_KW)){
            analyseWhileStatement(cur_func,varTable,isWhile,startOffset);
            return null;
        }
        else if(check(TokenType.RETURN_KW)){
            analyseReturnStatement(cur_func,varTable);
            return null;
        }
        else if(check(TokenType.IDENT)){
            analyseIdentStatement(cur_func,varTable);
            return null;
        }
        else if(check(TokenType.LET_KW)||check(TokenType.CONST_KW)){
            analyseDeclaration(cur_func,varTable);
            return null;
        }
        else if(check(TokenType.CONTINUE_KW)){
            analyseContinueStatement(cur_func,varTable,isWhile,startOffset);
            return null;
        }
        else if(check(TokenType.BREAK_KW)){
            return new Instruction[]{analyseBreakStatement(cur_func,varTable,isWhile,startOffset)};
        }
        else if(check(TokenType.L_BRACE)){
            SymbolTable blockTable=new SymbolTable(cur_func.getLocalTable().getNextOffset());
            blockTable.setLastTable(varTable);
            return analyseBlockStatement(cur_func,blockTable,isWhile,-1);
        }
        else if(check(TokenType.SEMICOLON)){
            expect(TokenType.SEMICOLON);
            return null;
        }
        else{
            throw new ExpectedTokenError(List.of(TokenType.IF_KW,TokenType.WHILE_KW,TokenType.RETURN_KW,
                    TokenType.SEMICOLON,TokenType.IDENT, TokenType.LET_KW,TokenType.CONST_KW,TokenType.CONTINUE_KW,TokenType.BREAK_KW),next());
        }
    }

    private Instruction[] analyseIfStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError {
        expect(TokenType.IF_KW);

        IdentType type=analyseBooleanExpression(cur_func, varTable);
        cur_func.getInstructions().add(new Instruction(Operation.BR_TRUE,1L));

        Instruction if_1=new Instruction(Operation.BR);
        cur_func.getInstructions().add(if_1);
        int offset_1=cur_func.getInstructions().size();

        SymbolTable if_blockTable=new SymbolTable(cur_func.getLocalTable().getNextOffset());
        if_blockTable.setLastTable(varTable);
        Instruction[] if_br =analyseBlockStatement(cur_func,if_blockTable,isWhile,startOffset);
        List<Instruction> brList = new ArrayList<>(Arrays.asList(if_br));

        Instruction if_2=new Instruction(Operation.BR, 0L);
        cur_func.getInstructions().add(if_2);
        int offset_2=cur_func.getInstructions().size();

        if_1.setX(offset_2-offset_1);

        if(check(TokenType.ELSE_KW)){
            expect(TokenType.ELSE_KW);
            if (check(TokenType.L_BRACE)) {
                SymbolTable else_blockTable=new SymbolTable(cur_func.getLocalTable().getNextOffset());
                else_blockTable.setLastTable(varTable);
                if_br=analyseBlockStatement(cur_func,else_blockTable,isWhile,startOffset);
                brList.addAll(Arrays.asList(if_br));
            } else if (check(TokenType.IF_KW)) {
                if_br=analyseIfStatement(cur_func,varTable,isWhile,startOffset);
                brList.addAll(Arrays.asList(if_br));
            }
            int offset_3=cur_func.getInstructions().size();
            if_2.setX(offset_3-offset_2);
        }
        return brList.toArray(new Instruction[0]);
    }

    private void analyseWhileStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError {
        expect(TokenType.WHILE_KW);

        Instruction while_1=new Instruction(Operation.BR,0L);
        cur_func.getInstructions().add(while_1);
        int offset_1=cur_func.getInstructions().size();

        IdentType type=analyseBooleanExpression(cur_func,varTable);
        cur_func.getInstructions().add(new Instruction(Operation.BR_TRUE,1L));

        Instruction while_2=new Instruction(Operation.BR,0L);
        cur_func.getInstructions().add(while_2);
        int offset_2=cur_func.getInstructions().size();

        SymbolTable blockTable=new SymbolTable(cur_func.getLocalTable().getNextOffset());
        blockTable.setLastTable(varTable);
        Instruction[] break_br =analyseBlockStatement(cur_func,blockTable,true,offset_1);

        Instruction while_3=new Instruction(Operation.BR);
        cur_func.getInstructions().add(while_3);
        int offset_3=cur_func.getInstructions().size();

        for (Instruction br:break_br){
            br.setX(offset_3- br.getX());
        }

        while_3.setX(offset_1-offset_3);
        while_2.setX(offset_3-offset_2);
    }

    private void analyseContinueStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError{
        if(!isWhile)
            throw new AnalyzeError(ErrorCode.ContinueOutOfWhile,peek().getStartPos());
        if(startOffset==-1)
            throw new AnalyzeError(ErrorCode.InvalidJMPOffset,peek().getStartPos());
        expect(TokenType.CONTINUE_KW);
        expect(TokenType.SEMICOLON);

        Instruction br=new Instruction(Operation.BR);
        cur_func.getInstructions().add(br);
        br.setX(startOffset-cur_func.getInstructions().size());
    }
    private Instruction analyseBreakStatement(SymbolEntry cur_func,SymbolTable varTable,boolean isWhile,int startOffset) throws CompileError {
        if(!isWhile)
            throw new AnalyzeError(ErrorCode.BreakOutOfWhile,peek().getStartPos());
        expect(TokenType.BREAK_KW);
        expect(TokenType.SEMICOLON);

        Instruction br=new Instruction(Operation.BR);
        cur_func.getInstructions().add(br);
        br.setX(cur_func.getInstructions().size());
        return br;
    }
    private void analyseReturnStatement(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        expect(TokenType.RETURN_KW);
        if(cur_func.getIdentType()!=IdentType.VOID){
            cur_func.getInstructions().add(new Instruction(Operation.ARGA, 0L));
            IdentType type=analyseExpression(cur_func,varTable);
            cur_func.getInstructions().add(new Instruction(Operation.STORE_64));
            if(type!=cur_func.getIdentType()){
                System.out.println("InvalidReturnType:"+type.toString()+" "+cur_func.getIdentType().toString());
                throw new AnalyzeError(ErrorCode.InvalidReturnType,peek().getStartPos());
            }
        }
        expect(TokenType.SEMICOLON);
        cur_func.getInstructions().add(new Instruction(Operation.RET));
    }

    private void analyseIdentStatement(SymbolEntry cur_func,SymbolTable varTable) throws CompileError{
        var name=expect(TokenType.IDENT);
        if(check(TokenType.L_PAREN)){
            analyseFn(cur_func,varTable,name);
        }
        else if(check(TokenType.ASSIGN)){
            SymbolEntry entry=getIdent(cur_func,varTable,name);
            if(entry.isConstant()){
                throw new AnalyzeError(ErrorCode.AssignToConstant,name.getStartPos());
            }
            expect(TokenType.ASSIGN);
            entry.setInitialized(true);
            IdentType type=analyseExpression(cur_func, varTable);
            if(type!=entry.getIdentType()){
                throw new AnalyzeError(ErrorCode.InvalidAssignment,name.getStartPos());
            }
            cur_func.getInstructions().add(new Instruction(Operation.STORE_64));
        }
        else{
            throw new ExpectedTokenError(List.of(TokenType.IDENT,TokenType.ASSIGN) ,next());
        }
        expect(TokenType.SEMICOLON);
    }
    private IdentType analyseBooleanExpression(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        IdentType type=analyseExpression(cur_func, varTable);
        IdentType subtype;
        if(check(TokenType.LT)){
            expect(TokenType.LT);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            cur_func.getInstructions().add(new Instruction(Operation.SET_LT));
            return IdentType.BOOLEAN;
        }
        else if(check(TokenType.LE)){
            expect(TokenType.LE);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            cur_func.getInstructions().add(new Instruction(Operation.SET_GT));
            cur_func.getInstructions().add(new Instruction(Operation.NOT));
            return IdentType.BOOLEAN;
        }
        else if(check(TokenType.GT)){
            expect(TokenType.GT);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            cur_func.getInstructions().add(new Instruction(Operation.SET_GT));
            return IdentType.BOOLEAN;
        }
        else if(check(TokenType.GE)){
            expect(TokenType.GE);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            cur_func.getInstructions().add(new Instruction(Operation.SET_LT));
            cur_func.getInstructions().add(new Instruction(Operation.NOT));
            return IdentType.BOOLEAN;
        }
        else if(check(TokenType.NEQ)){
            expect(TokenType.NEQ);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            return IdentType.BOOLEAN;
        }
        else if(check(TokenType.EQ)){
            expect(TokenType.EQ);
            subtype=analyseExpression(cur_func, varTable);
            if(subtype!=type)
                throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());

            if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_I));
            }
            else if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.CMP_F));
            }
            cur_func.getInstructions().add(new Instruction(Operation.NOT));
            return IdentType.BOOLEAN;
        }
        return type;

    }

    private IdentType analyseExpression(SymbolEntry cur_func,SymbolTable varTable) throws CompileError{
        IdentType type=analyseTerm(cur_func,varTable);
        while(check(TokenType.MINUS)||check(TokenType.PLUS)){
            if(nextIf(TokenType.MINUS)!=null){
                IdentType subtype=analyseTerm(cur_func,varTable);
                if(subtype!=type){
                    throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());
                }
                if(type==IdentType.DOUBLE){
                    cur_func.getInstructions().add(new Instruction(Operation.SUB_F));
                }
                else if(type==IdentType.INT){
                    cur_func.getInstructions().add(new Instruction(Operation.SUB_I));
                }
            }
            else if(nextIf(TokenType.PLUS)!=null){
                IdentType subtype=analyseTerm(cur_func,varTable);
                if(subtype!=type){
                    throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());
                }
                if(type==IdentType.DOUBLE){
                    cur_func.getInstructions().add(new Instruction(Operation.ADD_F));
                }
                else if(type==IdentType.INT){
                    cur_func.getInstructions().add(new Instruction(Operation.ADD_I));
                }
            }
        }
        return type;
    }
    private IdentType analyseTerm(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        IdentType type=analyseFactor(cur_func,varTable);
        while(check(TokenType.MUL)||check(TokenType.DIV)){
            if(nextIf(TokenType.MUL)!=null){
                IdentType subtype=analyseFactor(cur_func,varTable);
                if(subtype!=type){
                    throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());
                }
                if(type==IdentType.DOUBLE){
                    cur_func.getInstructions().add(new Instruction(Operation.MUL_F));
                }
                else if(type==IdentType.INT){
                    cur_func.getInstructions().add(new Instruction(Operation.MUL_I));
                }
            }
            else if(nextIf(TokenType.DIV)!=null){
                IdentType subtype=analyseFactor(cur_func,varTable);
                if(subtype!=type){
                    throw new AnalyzeError(ErrorCode.InvalidExpressionType,peek().getStartPos());
                }
                if(type==IdentType.DOUBLE){
                    cur_func.getInstructions().add(new Instruction(Operation.DIV_F));
                }
                else if(type==IdentType.INT){
                    cur_func.getInstructions().add(new Instruction(Operation.DIV_I));
                }
            }
        }
        return type;
    }

    private IdentType analyseFactor(SymbolEntry cur_func,SymbolTable varTable) throws CompileError {
        int negate = 0;
        IdentType type;
        while (check(TokenType.MINUS) ||check(TokenType.PLUS)) {
            if(nextIf(TokenType.MINUS)!=null){
                negate = negate+1;
                // 计算结果需要被 0 减
            }
            else{
                nextIf(TokenType.PLUS);
            }
        }

        if (check(TokenType.IDENT)) {
            Token name=expect(TokenType.IDENT);
            if(check(TokenType.L_PAREN)){
                type=analyseFn(cur_func,varTable,name);
            }
            else{
                SymbolEntry entry;
                entry=getIdent(cur_func,varTable,name);
                cur_func.getInstructions().add(new Instruction(Operation.LOAD_64));
                type=entry.getIdentType();
            }
        } else if (check(TokenType.UINT_LITERAL)) {
            Token name=expect(TokenType.UINT_LITERAL);
            cur_func.getInstructions().add(new Instruction(Operation.PUSH,(long) (Integer) name.getValue()));
            type=IdentType.INT;

        } else if (check(TokenType.DOUBLE_LITERAL)) {
            Token name=expect(TokenType.DOUBLE_LITERAL);
            cur_func.getInstructions().add(new Instruction(Operation.PUSH,((Double)name.getValue()).longValue()));
            type=IdentType.DOUBLE;

        }else if(check(TokenType.STRING_LITERAL)){
            Token name=expect(TokenType.STRING_LITERAL);
            SymbolEntry symbol=new SymbolEntry(name.getValue().toString(),name.getValue().toString(),
                    true,globalTable.getNextVariableOffset(),SymbolType.CONST,IdentType.STRING);
            globalTable.addSymbol(symbol,name.getStartPos());
            cur_func.getInstructions().add(new Instruction(Operation.PUSH,symbol.getStackOffset()));
            type=IdentType.STRING;
        }
        else if(check(TokenType.CHAR_LITERAL)){
            Token name=expect(TokenType.CHAR_LITERAL);
            cur_func.getInstructions().add(new Instruction(Operation.PUSH,((Integer)name.getValue()).longValue()));
            type=IdentType.INT;
        }
        else if (check(TokenType.L_PAREN)) {
            expect(TokenType.L_PAREN);
            type=analyseBooleanExpression(cur_func,varTable);
            expect(TokenType.R_PAREN);
        } else {
            // 都不是，摸了
            throw new ExpectedTokenError(List.of(TokenType.IDENT, TokenType.UINT_LITERAL, TokenType.L_PAREN), next());
        }

        while(check(TokenType.AS_KW)){
            expect(TokenType.AS_KW);
            Token ty=expect(TokenType.IDENT);
            if(ty.getValue().toString().equals("int") && type==IdentType.DOUBLE){
                type=IdentType.INT;
                cur_func.getInstructions().add(new Instruction(Operation.FTOI));
            }
            else if(ty.getValue().toString().equals("double") && type==IdentType.INT){
                type=IdentType.DOUBLE;
                cur_func.getInstructions().add(new Instruction(Operation.ITOF));
            }
            else if(!ty.getValue().toString().equals(type.toString())){
                throw new AnalyzeError(ErrorCode.InvalidAS,ty.getStartPos());
            }
        }

        if(negate % 2 == 1){
            if(type==IdentType.DOUBLE){
                cur_func.getInstructions().add(new Instruction(Operation.NEG_F));
            }
            else if(type==IdentType.INT){
                cur_func.getInstructions().add(new Instruction(Operation.NEG_I));
            }
        }
        return type;
    }

    private IdentType analyseFn(SymbolEntry cur_func,SymbolTable varTable,Token name) throws CompileError {
        IdentType type=null;
        String funcName=name.getValue().toString();
        if(funcName.equals("getint")||funcName.equals("getdouble")||funcName.equals("getchar")){
            //SymbolEntry entry=funcTable.findSymbolNoRe(funcName,name.getStartPos());
            expect(TokenType.L_PAREN);
            expect(TokenType.R_PAREN);
            //cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC, 1L));
            //cur_func.getInstructions().add(new Instruction(Operation.CALLNAME,entry.getStackOffset()));
            if(funcName.equals("getint")){
                cur_func.getInstructions().add(new Instruction(Operation.SCAN_I));
            }
            else if(funcName.equals("getchar")){
                cur_func.getInstructions().add(new Instruction(Operation.SCAN_C));
            }
            else {
                cur_func.getInstructions().add(new Instruction(Operation.SCAN_F));
            }

            if(funcName.equals("getint")||funcName.equals("getchar")){
                type=IdentType.INT;
            }
            else {
                type=IdentType.DOUBLE;
            }
        }
        else if(funcName.equals("putint")||funcName.equals("putchar")||funcName.equals("putstr")){
            //SymbolEntry entry=funcTable.findSymbolNoRe(funcName.toString(),name.getStartPos());
            //cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC,0L));
            expect(TokenType.L_PAREN);
            IdentType ty=analyseExpression(cur_func,varTable);
            if(ty!=IdentType.INT && ty!=IdentType.STRING)
                throw new AnalyzeError(ErrorCode.InvalidFuncTY,peek().getStartPos());
            expect(TokenType.R_PAREN);

            //cur_func.getInstructions().add(new Instruction(Operation.CALLNAME,entry.getStackOffset()));
            if(funcName.equals("putint"))
                cur_func.getInstructions().add(new Instruction(Operation.PRINT_I));
            else if(funcName.equals("putchar"))
                cur_func.getInstructions().add(new Instruction(Operation.PRINT_C));
            else
                cur_func.getInstructions().add(new Instruction(Operation.PRINT_S));

            type=IdentType.VOID;
        }
        else if(funcName.equals("putdouble")){
            //SymbolEntry entry=funcTable.findSymbolNoRe(funcName,name.getStartPos());
            //cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC,0L));
            expect(TokenType.L_PAREN);
            IdentType ty=analyseExpression(cur_func,varTable);
            if(ty!=IdentType.DOUBLE)
                throw new AnalyzeError(ErrorCode.InvalidFuncTY,peek().getStartPos());
            expect(TokenType.R_PAREN);

            //cur_func.getInstructions().add(new Instruction(Operation.CALLNAME,entry.getStackOffset()));
            cur_func.getInstructions().add(new Instruction(Operation.PRINT_F));
            type=IdentType.VOID;
        }
        else if(funcName.equals("putln")){
            //SymbolEntry entry=funcTable.findSymbolNoRe(funcName,name.getStartPos());
            expect(TokenType.L_PAREN);
            expect(TokenType.R_PAREN);
            //cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC, 0L));
            //cur_func.getInstructions().add(new Instruction(Operation.CALLNAME,entry.getStackOffset()));
            cur_func.getInstructions().add(new Instruction(Operation.PRINTLN));
            type=IdentType.VOID;
        }
        else{
            SymbolEntry entry=funcTable.findSymbolNoRe(funcName,name.getStartPos());
            if(entry.getIdentType()==IdentType.VOID)
                cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC, 0L));
            else if(entry.getIdentType()==IdentType.INT || entry.getIdentType()==IdentType.DOUBLE)
                cur_func.getInstructions().add(new Instruction(Operation.STACKALLOC, 1L));

            expect(TokenType.L_PAREN);
            for(int i=0;i<entry.getParam_cnt();i++){
                analyseExpression(cur_func, varTable);
                if(i!=entry.getParam_cnt()-1)
                    expect(TokenType.COMMA);
            }
            expect(TokenType.R_PAREN);
            cur_func.getInstructions().add(new Instruction(Operation.CALL,entry.getStackOffset()));
            type=entry.getIdentType();
        }
        return type;
    }
    public static SymbolEntry findSymbolInLocal(SymbolTable para,SymbolTable glo,SymbolTable local,String name, Pos curPos) throws AnalyzeError {
        SymbolEntry entry;

        while(local!=null && !local.equals(para) && !local.equals(glo) ){
            entry = local.getSymbolTable().get(name);
            if(entry!=null)
                return entry;
            local=local.getLastTable();
        }
        return null;
    }
    public SymbolEntry getIdent(SymbolEntry cur_func,SymbolTable varTable,Token name) throws AnalyzeError {
        SymbolEntry entry;
        entry=findSymbolInLocal(cur_func.getParamTable(),globalTable,varTable,name.getValue().toString(),name.getStartPos());
        if(entry!=null){
            cur_func.getInstructions().add(new Instruction(Operation.LOCA,entry.stackOffset));
        }
        else if(cur_func.getParamTable()!=null){
            entry=cur_func.getParamTable().findSymbolNoRe(name.getValue().toString(),name.getStartPos());
            if(entry!=null){
                cur_func.getInstructions().add(new Instruction(Operation.ARGA,entry.stackOffset));
            }
            else{
                entry=globalTable.findSymbolNoRe(name.getValue().toString(),name.getStartPos());
                if(entry!=null)
                    cur_func.getInstructions().add(new Instruction(Operation.GLOBA,entry.stackOffset));
                else
                    throw new AnalyzeError(ErrorCode.NotDeclared, name.getStartPos());
            }
        }
        else{
            entry=globalTable.findSymbolNoRe(name.getValue().toString(),name.getStartPos());
            if(entry!=null)
                cur_func.getInstructions().add(new Instruction(Operation.GLOBA,entry.stackOffset));
            else
                throw new AnalyzeError(ErrorCode.NotDeclared, name.getStartPos());
        }
        return entry;
    }
}
