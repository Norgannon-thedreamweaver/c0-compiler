package miniplc0java.analyser;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolEntry {
    String name;
    Object value;

    boolean isInitialized;
    long stackOffset;
    SymbolType symbolType;
    IdentType identType;

    boolean isParam;
    int param_cnt;
    SymbolTable paramTable;
    SymbolTable localTable;

    ArrayList<Instruction> instructions;

    public SymbolEntry(int stackOffset){
        this.name=null;
        this.value=0;
        this.isInitialized = false;
        this.stackOffset = stackOffset;
        this.symbolType=null;
        this.identType=null;
        this.isParam = false;
        this.param_cnt=0;
        this.paramTable = null;
        this.localTable =null;
        this.instructions=null;
    }

    public SymbolEntry(String name,Object value,boolean isInitialized,long stackOffset,SymbolType symbolType,
                       IdentType identType,boolean isParam,int param_cnt,SymbolTable paramTable,
                       SymbolTable localTable,ArrayList<Instruction> instructions){
        this.name=name;
        this.value=value;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.symbolType=symbolType;
        this.identType=identType;
        this.isParam = isParam;
        this.param_cnt=param_cnt;
        this.paramTable = paramTable;
        this.localTable =localTable;
        this.instructions=instructions;
    }

    public SymbolEntry(String name,Object value,boolean isInitialized,long stackOffset,SymbolType symbolType,IdentType identType){
        this.name=name;
        this.value=value;
        this.isInitialized = isInitialized;
        this.stackOffset = stackOffset;
        this.symbolType=symbolType;
        this.identType=identType;
        this.isParam = false;
        this.param_cnt=0;
        this.paramTable = null;
        this.localTable =null;
        this.instructions=null;
    }

    public boolean isConstant(){
        return this.symbolType == SymbolType.CONST;
    }

    public void decParamIndex(){
        LinkedHashMap<String, SymbolEntry> table=this.paramTable.getSymbolTable();
        for(SymbolEntry entry : table.values()) {
            entry.decStackOffset();
        }
        this.paramTable.getNextOffset().decOffset();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public long getStackOffset() {
        return stackOffset;
    }

    public void decStackOffset(){
        this.stackOffset--;
    }

    public void setStackOffset(long stackOffset) {
        this.stackOffset = stackOffset;
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(SymbolType symbolType) {
        this.symbolType = symbolType;
    }

    public IdentType getIdentType() {
        return identType;
    }

    public void setIdentType(IdentType identType) {
        this.identType = identType;
    }

    public boolean isParam() {
        return isParam;
    }

    public void setParam(boolean param) {
        isParam = param;
    }

    public int getParam_cnt() {
        return param_cnt;
    }

    public void setParam_cnt(int param_cnt) {
        this.param_cnt = param_cnt;
    }

    public SymbolTable getParamTable() {
        return paramTable;
    }

    public void setParamTable(SymbolTable paramTable) {
        this.paramTable = paramTable;
    }

    public SymbolTable getLocalTable() {
        return localTable;
    }

    public void setLocalTable(SymbolTable localTable) {
        this.localTable = localTable;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }
}
