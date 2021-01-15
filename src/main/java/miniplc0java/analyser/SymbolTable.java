package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

import java.util.LinkedHashMap;

public class SymbolTable {
    private LinkedHashMap<String, SymbolEntry> symbolTable = new LinkedHashMap<>();
    private SymbolTable lastTable;
    private int nextOffset;

    public SymbolTable(int nextOffset){
        this.lastTable=null;
        this.nextOffset=nextOffset;
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


    public int getNextVariableOffset() {
        return this.nextOffset++;
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

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }
}
