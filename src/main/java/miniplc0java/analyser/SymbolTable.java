package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.util.LinkedHashMap;

public class SymbolTable {
    private LinkedHashMap<String, SymbolEntry> symbolTable = new LinkedHashMap<>();
    private SymbolTable lastTable;
    private SymbolOffset nextOffset;

    public SymbolTable(SymbolOffset nextOffset){
        this.lastTable=null;
        this.nextOffset= nextOffset;
    }
    public SymbolTable(Integer nextOffset){
        this.lastTable=null;
        this.nextOffset= new SymbolOffset(nextOffset);
    }

    public void addSymbol(SymbolEntry cur_func,SymbolEntry sym, Pos curPos)throws AnalyzeError {
        if(this.symbolTable.containsKey(sym.getName()))
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        if(cur_func.getLocalTable().equals(this) && cur_func.getParamTable().getSymbolTable().containsKey(sym.getName()))
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        this.symbolTable.put(sym.getName(), sym);
    }
    public void addSymbol(SymbolEntry sym, Pos curPos)throws AnalyzeError {
        if(this.symbolTable.containsKey(sym.getName()))
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        this.symbolTable.put(sym.getName(), sym);
    }

    public SymbolEntry findSymbolNoRe(String name,Pos curPos) throws AnalyzeError {
        SymbolEntry entry= this.symbolTable.get(name);
        if(entry!=null)
            return entry;
        return null;
    }


    public Integer getNextVariableOffset() {
        SymbolOffset offset=this.getNextOffset();
        return offset.incOffset();
    }

    public boolean isStart(){
        return this.lastTable == null;
    }

    public int getSize(){
        return symbolTable.size();
    }

    public LinkedHashMap<String, SymbolEntry> getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(LinkedHashMap<String, SymbolEntry> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public SymbolTable getLastTable() {
        return lastTable;
    }

    public void setLastTable(SymbolTable lastTable) {
        this.lastTable = lastTable;
    }

    public SymbolOffset getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(SymbolOffset nextOffset) {
        this.nextOffset = nextOffset;
    }
}
