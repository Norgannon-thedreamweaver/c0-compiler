package compiler.optimizer;


import compiler.analyser.SymbolEntry;
import compiler.instruction.Instruction;
import compiler.instruction.Operation;

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
        boolean ret_flag=false;
        boolean br_flag=false;
        int index=-1;
        for(Map.Entry<Integer, Instruction> i : instructions.entrySet()){
            Instruction ins=i.getValue();
            index=i.getKey();
            if(ins.getOpt()== Operation.BR){
                br_flag=true;
                if(!list.contains((int)ins.getX()+1+index))
                    list.add((int)ins.getX()+1+index);
            }
            else if(ins.getOpt()== Operation.BR_FALSE||ins.getOpt()== Operation.BR_TRUE){
                br_flag=true;
                if(!list.contains(1+index))
                    list.add(1+index);
            }
            else if(ins.getOpt()== Operation.RET)
                ret_flag=true;
        }
        if(!ret_flag && !br_flag && index!=-1){
            list.add(1+index);
        }
        return list;
    }

    public void printInstructions(){
        ArrayList<Integer> list=new ArrayList<>();
        for(Map.Entry<Integer, Instruction> i : instructions.entrySet()){
            System.out.println(i.getKey()+":"+i.getValue().toString());
        }
    }
}
