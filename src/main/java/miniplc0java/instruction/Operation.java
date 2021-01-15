package miniplc0java.instruction;

public enum Operation {
    NOP,
    PUSH,
    POP,
    POP_N,
    DUP,
    LOCA,
    ARGA,
    GLOBA,
    LOAD_8,
    LOAD_16,
    LOAD_32,
    LOAD_64,
    STORE_8,
    STORE_16,
    STORE_32,
    STORE_64,
    ALLOC,
    FREE,
    STACKALLOC,
    ADD_I,
    SUB_I,
    MUL_I,
    DIV_I,
    ADD_F,
    SUB_F,
    MUL_F,
    DIV_F,
    DIV_U,
    SHL,
    SHR,
    AND,
    OR,
    XOR,
    NOT,
    CMP_I,
    CMP_U,
    CMP_F,
    NEG_I,
    NEG_F,
    ITOF,
    FTOI,
    SHRL,
    SET_LT,
    SET_GT,
    BR,
    BR_FALSE,
    BR_TRUE,
    CALL,
    RET,
    CALLNAME,
    SCAN_I,
    SCAN_C,
    SCAN_F,
    PRINT_I,
    PRINT_C,
    PRINT_F,
    PRINT_S,
    PRINTLN,
    PANIC;
    

    public byte toByte(){
        switch (this) {
            case NOP:
                return (byte)0x0;
            case PUSH:
                return (byte)0x01;
            case POP:
                return (byte)0x02;
            case POP_N:
                return (byte)0x03;
            case DUP:
                return (byte)0x04;
            case LOCA:
                return (byte)0xA;
            case ARGA:
                return (byte)0xB;
            case GLOBA:
                return (byte)0xC;
            case LOAD_8:
                return (byte)0x10;
            case LOAD_16:
                return (byte)0x11;
            case LOAD_32:
                return (byte)0x12;
            case LOAD_64:
                return (byte)0x13;
            case STORE_8:
                return (byte)0x14;
            case STORE_16:
                return (byte)0x15;
            case STORE_32:
                return (byte)0x16;
            case STORE_64:
                return (byte)0x17;
            case ALLOC:
                return (byte)0x18;
            case FREE:
                return (byte)0x19;
            case STACKALLOC:
                return (byte)0x1A;
            case ADD_I:
                return (byte)0x20;
            case SUB_I:
                return (byte)0x21;
            case MUL_I:
                return (byte)0x22;
            case DIV_I:
                return (byte)0x23;
            case ADD_F:
                return (byte)0x24;
            case SUB_F:
                return (byte)0x25;
            case MUL_F:
                return (byte)0x26;
            case DIV_F:
                return (byte)0x27;
            case DIV_U:
                return (byte)0x28;
            case SHL:
                return (byte)0x29;
            case SHR:
                return (byte)0x2A;
            case AND:
                return (byte)0x2B;
            case OR:
                return (byte)0x2C;
            case XOR:
                return (byte)0x2D;
            case NOT:
                return (byte)0x2E;
            case CMP_I:
                return (byte)0x30;
            case CMP_U:
                return (byte)0x31;
            case CMP_F:
                return (byte)0x32;
            case NEG_I:
                return (byte)0x34;
            case NEG_F:
                return (byte)0x35;
            case ITOF:
                return (byte)0x36;
            case FTOI:
                return (byte)0x37;
            case SHRL:
                return (byte)0x38;
            case SET_LT:
                return (byte)0x39;
            case SET_GT:
                return (byte)0x3A;
            case BR:
                return (byte)0x41;
            case BR_FALSE:
                return (byte)0x42;
            case BR_TRUE:
                return (byte)0x43;
            case CALL:
                return (byte)0x48;
            case RET:
                return (byte)0x49;
            case CALLNAME:
                return (byte)0x4A;
            case SCAN_I:
                return (byte)0x50;
            case SCAN_C:
                return (byte)0x51;
            case SCAN_F:
                return (byte)0x52;
            case PRINT_I:
                return (byte)0x54;
            case PRINT_C:
                return (byte)0x55;
            case PRINT_F:
                return (byte)0x56;
            case PRINT_S:
                return (byte)0x57;
            case PRINTLN:
                return (byte)0x58;
            default:
                return (byte)0xFE;
        }
    }
}
