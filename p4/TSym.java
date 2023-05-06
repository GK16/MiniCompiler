import java.util.*;

public class TSym {
    private String type;
    private SymTable symTable;
    private StructDeclNode struct;

    public TSym(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String name() {
        return type;
    }

    public StructDeclNode getStruct() {
        return this.struct;
    }

    public void setStruct(StructDeclNode struct) {
        this.struct = struct;
    }
}

class FuncSym extends TSym {
    private List<String> _param;
    private String _type;

    public FuncSym(String _type, List<String> _param) {
        super("function");
        this._param = _param;
        this._type = _type;
    }

    public int getParamNum() {
        return _param.size();
    }

    public String name() {
        String params = String.join(", ", _param);
        if (params.length() == 0) {
            params = "void";
        }
        return params + " -> " + _type;
    }
}