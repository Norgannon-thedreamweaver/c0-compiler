package miniplc0java.analyser;

public enum SymbolType {
    FN,
    CONST,
    LET;

    @Override
    public String toString() {
        switch (this) {
            case FN:
                return "fn";
            case CONST:
                return "const";
            case LET:
                return "let";
            default:
                return "invalid SymbolType";
        }
    }
}
