package miniplc0java.instruction;

import java.nio.ByteBuffer;
import java.util.Objects;

public class Instruction {
    private Operation opt;
    long x;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0;
    }

    public Instruction(Operation opt, Long x) {
        this.opt = opt;
        this.x = x;
    }

    public Instruction() {
        this.opt = Operation.NOP;
        this.x = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public byte[] toBytes() {

        switch (this.opt) {
            case NOP:
            case POP:
            case DUP:
            case LOAD_8:
            case LOAD_16:
            case LOAD_32:
            case LOAD_64:
            case STORE_8:
            case STORE_16:
            case STORE_32:
            case STORE_64:
            case ALLOC:
            case FREE:
            case ADD_I:
            case SUB_I:
            case MUL_I:
            case DIV_I:
            case ADD_F:
            case SUB_F:
            case MUL_F:
            case DIV_F:
            case DIV_U:
            case SHL:
            case SHR:
            case AND:
            case OR:
            case XOR:
            case NOT:
            case CMP_I:
            case CMP_U:
            case CMP_F:
            case NEG_I:
            case NEG_F:
            case ITOF:
            case FTOI:
            case SHRL:
            case SET_LT:
            case SET_GT:
            case RET:
            case SCAN_I:
            case SCAN_C:
            case SCAN_F:
            case PRINT_I:
            case PRINT_C:
            case PRINT_F:
            case PRINT_S:
            case PRINTLN:
                return new byte[]{this.opt.toByte()};

            case PUSH:
                ByteBuffer byteBuffer9=ByteBuffer.allocate(9);
                byteBuffer9.put(this.opt.toByte());
                byteBuffer9.putLong(this.x);
                return byteBuffer9.array();
            
            case POP_N:
            case LOCA:
            case ARGA:
            case GLOBA:
            case STACKALLOC:
            case BR:
            case BR_FALSE:
            case BR_TRUE:
            case CALL:
            case CALLNAME:
                ByteBuffer byteBuffer5=ByteBuffer.allocate(5);
                byteBuffer5.put(this.opt.toByte());
                byteBuffer5.putInt((int)this.x);
                return byteBuffer5.array();

            default:
                return new byte[]{this.opt.toByte()};
        }
    }


    @Override
    public String toString() {
        switch (this.opt) {
            case NOP:
            case POP:
            case DUP:
            case LOAD_8:
            case LOAD_16:
            case LOAD_32:
            case LOAD_64:
            case STORE_8:
            case STORE_16:
            case STORE_32:
            case STORE_64:
            case ALLOC:
            case FREE:
            case ADD_I:
            case SUB_I:
            case MUL_I:
            case DIV_I:
            case ADD_F:
            case SUB_F:
            case MUL_F:
            case DIV_F:
            case DIV_U:
            case SHL:
            case SHR:
            case AND:
            case OR:
            case XOR:
            case NOT:
            case CMP_I:
            case CMP_U:
            case CMP_F:
            case NEG_I:
            case NEG_F:
            case ITOF:
            case FTOI:
            case SHRL:
            case SET_LT:
            case SET_GT:
            case RET:
            case SCAN_I:
            case SCAN_C:
            case SCAN_F:
            case PRINT_I:
            case PRINT_C:
            case PRINT_F:
            case PRINT_S:
            case PRINTLN:
                return String.format("%s", this.opt);


            case PUSH:
            case POP_N:
            case LOCA:
            case ARGA:
            case GLOBA:
            case STACKALLOC:
            case BR:
            case BR_FALSE:
            case BR_TRUE:
            case CALL:
            case CALLNAME:
                return String.format("%s %s", this.opt, this.x);
            default:
                return "PANIC";
        }
    }
}
