package miniplc0java.optimizer;


import miniplc0java.analyser.SymbolEntry;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BasicBlock {
    LinkedHashMap<Integer, Instruction> instructions = new LinkedHashMap<>();
    boolean isLeaf=false;
    boolean hasReturn=false;
    ArrayList<BasicBlock> next=new ArrayList<>();
    ArrayList<BasicBlock> from=new ArrayList<>();

    public void addInstruction(int index,Instruction instruction){
        instructions.put(index,instruction);
    }

    public boolean hasIndex(int index){
        for(Integer i:instructions.keySet()){
            if(i == index)
                return true;
        }
        return false;
    }
    public boolean isLeaf(){
        return next.size() == 0;
    }

    public boolean isDead(){
        return from.size() == 0;
    }

    public boolean hasRet(){
        for(Instruction i:instructions.values()){
            if(i.getOpt()== Operation.RET)
                return true;
        }
        return false;
    }
    public ArrayList<Integer> jmpTo(){
        ArrayList<Integer> list=new ArrayList<>();
        for(Map.Entry<Integer, Instruction> i : instructions.entrySet()){
            Instruction ins=i.getValue();
            if(ins.getOpt()== Operation.BR)
                list.add((int)ins.getX()+1+i.getKey());
            if(ins.getOpt()== Operation.BR_FALSE||ins.getOpt()== Operation.BR_TRUE)
                list.add(1+i.getKey());
        }
        return list;
    }
}
