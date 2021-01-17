package compiler.analyser;

public enum IdentType {
    INT,
    DOUBLE,
    STRING,
    VOID,
    BOOLEAN;

    @Override
    public String toString() {
        switch (this) {
            case INT:
                return "int";
            case DOUBLE:
                return "double";
            case STRING:
                return "string";
            case VOID:
                return "void";
            case BOOLEAN:
                return "boolean";
            default:
                return "invalid IdentType";
        }
    }
}
