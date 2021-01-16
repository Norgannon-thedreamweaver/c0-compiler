package miniplc0java.generator;

import miniplc0java.analyser.*;
import miniplc0java.instruction.Instruction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Generator {
    DataOutputStream output;
    Analyser analyser;
    int magic=0x72303b3e;
    int version=1;
    List<String> stdlib= Arrays.asList("getint","getdouble","getchar","putint","putdouble","putchar","putstr","putln");

    public Generator(DataOutputStream output, Analyser analyser){
        this.output=output;
        this.analyser=analyser;
    }

    public void generateBin() throws IOException{
        output.writeInt(this.magic);
        output.writeInt(this.version);
        generateGlobals();
        generateFunctions();
    }

    private void generateGlobals() throws IOException {
        int count=analyser.globalTable.getSize();
        output.writeInt(count);
        System.out.println("global size:"+count);
        LinkedHashMap<String, SymbolEntry> table=analyser.globalTable.getSymbolTable();
        for(SymbolEntry entry : table.values()) {
            System.out.println(entry.getName());
            output.writeBoolean(entry.isConstant());
            if(entry.getSymbolType()== SymbolType.FN){
                output.writeInt(((String)entry.getName()).length());
                output.write(((String) entry.getName()).getBytes());
            }
            else if(entry.getIdentType()== IdentType.INT||entry.getIdentType()==IdentType.DOUBLE){
                output.writeInt(8);
                output.writeLong((Long) entry.getValue());
            }
            else if(entry.getIdentType()==IdentType.STRING){
                output.writeInt(((String)entry.getValue()).length());
                output.write(((String) entry.getValue()).getBytes());
            }
        }
    }

    private void generateFunctions() throws IOException{
        int count=analyser.funcTable.getSize();
        output.writeInt(count);
        System.out.println("function size:"+count);

        LinkedHashMap<String, SymbolEntry> table=analyser.funcTable.getSymbolTable();
        for(SymbolEntry entry : table.values()) {
            if(stdlib.contains(entry.getName()))
                continue;
            System.out.print("fn "+entry.getName()+" "+entry.getStackOffset()+" "+entry.getIdentType());
            output.writeInt((int) entry.getStackOffset());
            if(entry.getIdentType() == IdentType.VOID){
                output.writeInt(0);
            }
            else if(entry.getIdentType() ==IdentType.INT||entry.getIdentType() ==IdentType.DOUBLE){
                output.writeInt(1);
            }

            output.writeInt(entry.getParam_cnt());
            System.out.print(" "+entry.getParam_cnt());

            SymbolTable localTable=entry.getLocalTable();
            output.writeInt(localTable.getSize());
            System.out.println(" "+localTable.getSize());

            ArrayList<Instruction> instructions=entry.getInstructions();
            output.writeInt(instructions.size());
            generateInstruction(instructions);
        }
    }
    private void generateInstruction(ArrayList<Instruction> instructions) throws IOException {
        for(int i=0;i<instructions.size();i++){
            output.write(instructions.get(i).toBytes());
            System.out.println(i+":"+instructions.get(i).toString());
        }
    }
}
