package miniplc0java.optimizer;

import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;

import java.util.*;

public class ReturnCheck {
    LinkedHashMap<Integer, Instruction> instructions = new LinkedHashMap<>();
    ArrayList<Integer> entry=new ArrayList<>();
    ArrayList<BasicBlock> blocks=new ArrayList<>();

    public ReturnCheck(ArrayList<Instruction> instructions){
        for(int i=0;i<instructions.size();i++){
            this.instructions.put(i,instructions.get(i));
        }
    }

    public boolean check(){
        this.findEntry();
        Collections.sort(entry);
        for(int i=0;i<entry.size()-1;i++){
            BasicBlock block=new BasicBlock();
            for(int j=entry.get(i);j<entry.get(i+1);j++){
                block.addInstruction(j,instructions.get(j));
            }
            blocks.add(block);
        }
        for(BasicBlock block:blocks){
            for(Integer jmp:block.jmpTo()){
                BasicBlock blockTo=getBlockByIndex(jmp);
                if(blockTo!=null){
                    block.next.add(blockTo);
                    blockTo.from.add(block);
                }
            }
        }

        for(BasicBlock block:blocks){
            if(!block.isDead()&&block.isLeaf()&&!block.hasRet())
                return false;
        }
        return true;
    }

    public void findEntry(){
        addEntry(0);
        for(Map.Entry<Integer, Instruction> i : instructions.entrySet()) {
            if(i.getValue().getOpt()== Operation.BR||i.getValue().getOpt()== Operation.BR_TRUE||i.getValue().getOpt()== Operation.BR_FALSE){
                addEntry(i.getKey()+1);
                addEntry(i.getKey()+1+(int)i.getValue().getX());
            }
            if(i.getValue().getOpt()== Operation.RET){
                addEntry(i.getKey()+1);
            }
        }
        addEntry(instructions.size());
    }
    public void addEntry(Integer i){
        if(!entry.contains(i))
            entry.add(i);
    }
    public BasicBlock getBlockByIndex(int index){
        for(BasicBlock block:blocks){
            if(block.hasIndex(index))
                return block;
        }
        return null;
    }
}
