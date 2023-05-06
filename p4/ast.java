import java.io.*;
import java.util.*;

// **********************************************************************
// The ASTnode class defines the nodes of the abstract-syntax tree that
// represents a C-- program.
//
// Internal nodes of the tree contain pointers to children, organized
// either in a list (for nodes that may have a variable number of
// children) or as a fixed set of fields.
//
// The nodes for literals and ids contain line and character number
// information; for string literals and identifiers, they also contain a
// string; for integer literals, they also contain an integer value.
//
// Here are all the different kinds of AST nodes and what kinds of children
// they have.  All of these kinds of AST nodes are subclasses of "ASTnode".
// Indentation indicates further subclassing:
//
//     Subclass            Children
//     --------            ----
//     ProgramNode         DeclListNode
//     DeclListNode        linked list of DeclNode
//     DeclNode:
//       VarDeclNode       TypeNode, IdNode, int
//       FnDeclNode        TypeNode, IdNode, FormalsListNode, FnBodyNode
//       FormalDeclNode    TypeNode, IdNode
//       StructDeclNode    IdNode, DeclListNode
//
//     FormalsListNode     linked list of FormalDeclNode
//     FnBodyNode          DeclListNode, StmtListNode
//     StmtListNode        linked list of StmtNode
//     ExpListNode         linked list of ExpNode
//
//     TypeNode:
//       IntNode           -- none --
//       BoolNode          -- none --
//       VoidNode          -- none --
//       StructNode        IdNode
//
//     StmtNode:
//       AssignStmtNode      AssignNode
//       PostIncStmtNode     ExpNode
//       PostDecStmtNode     ExpNode
//       ReadStmtNode        ExpNode
//       WriteStmtNode       ExpNode
//       IfStmtNode          ExpNode, DeclListNode, StmtListNode
//       IfElseStmtNode      ExpNode, DeclListNode, StmtListNode,
//                                    DeclListNode, StmtListNode
//       WhileStmtNode       ExpNode, DeclListNode, StmtListNode
//       RepeatStmtNode      ExpNode, DeclListNode, StmtListNode
//       CallStmtNode        CallExpNode
//       ReturnStmtNode      ExpNode
//
//     ExpNode:
//       IntLitNode          -- none --
//       StrLitNode          -- none --
//       TrueNode            -- none --
//       FalseNode           -- none --
//       IdNode              -- none --
//       DotAccessNode       ExpNode, IdNode
//       AssignNode          ExpNode, ExpNode
//       CallExpNode         IdNode, ExpListNode
//       UnaryExpNode        ExpNode
//         UnaryMinusNode
//         NotNode
//       BinaryExpNode       ExpNode ExpNode
//         PlusNode
//         MinusNode
//         TimesNode
//         DivideNode
//         AndNode
//         OrNode
//         EqualsNode
//         NotEqualsNode
//         LessNode
//         GreaterNode
//         LessEqNode
//         GreaterEqNode
//
// Here are the different kinds of AST nodes again, organized according to
// whether they are leaves, internal nodes with linked lists of children, or
// internal nodes with a fixed number of children:
//
// (1) Leaf nodes:
//        IntNode,   BoolNode,  VoidNode,  IntLitNode,  StrLitNode,
//        TrueNode,  FalseNode, IdNode
//
// (2) Internal nodes with (possibly empty) linked lists of children:
//        DeclListNode, FormalsListNode, StmtListNode, ExpListNode
//
// (3) Internal nodes with fixed numbers of children:
//        ProgramNode,     VarDeclNode,     FnDeclNode,     FormalDeclNode,
//        StructDeclNode,  FnBodyNode,      StructNode,     AssignStmtNode,
//        PostIncStmtNode, PostDecStmtNode, ReadStmtNode,   WriteStmtNode
//        IfStmtNode,      IfElseStmtNode,  WhileStmtNode,  RepeatStmtNode,
//        CallStmtNode
//        ReturnStmtNode,  DotAccessNode,   AssignExpNode,  CallExpNode,
//        UnaryExpNode,    BinaryExpNode,   UnaryMinusNode, NotNode,
//        PlusNode,        MinusNode,       TimesNode,      DivideNode,
//        AndNode,         OrNode,          EqualsNode,     NotEqualsNode,
//        LessNode,        GreaterNode,     LessEqNode,     GreaterEqNode
//
// **********************************************************************

// **********************************************************************
// ASTnode class (base class for all other kinds of nodes)
// **********************************************************************

abstract class ASTnode {
    // every subclass must provide an unparse operation
    abstract public void unparse(PrintWriter p, int indent);

    // this method can be used by the unparse methods to do indenting
    protected void addIndentation(PrintWriter p, int indent) {
        for (int k = 0; k < indent; k++)
            p.print(" ");
    }

    protected void safeAddDecl(SymTable table, IdNode id, TSym sym) {
        try {
            table.addDecl(id.name(), sym);
        } catch (DuplicateSymException e) {
            ErrMsg.multiplyDecl(id.getLineNum(), id.getCharNum());
        } catch (EmptySymTableException e) {
            System.out.println(e);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }
    }
}

// **********************************************************************
// ProgramNode, DeclListNode, FormalsListNode, FnBodyNode,
// StmtListNode, ExpListNode
// **********************************************************************

class ProgramNode extends ASTnode {
    public ProgramNode(DeclListNode L) {
        myDeclList = L;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
    }

    public void analyze(SymTable table) {
        myDeclList.analyze(table);
    }

    private DeclListNode myDeclList;
}

class DeclListNode extends ASTnode {
    public DeclListNode(List<DeclNode> S) {
        myDecls = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator it = myDecls.iterator();
        try {
            while (it.hasNext()) {
                ((DeclNode) it.next()).unparse(p, indent);
            }
        } catch (NoSuchElementException e) {
            System.err.println("unexpected NoSuchElementException in DeclListNode.print");
            System.exit(-1);
        }
    }

    private List<DeclNode> myDecls;

    // ---

    public SymTable analyze(SymTable table) {
        for (DeclNode decl : myDecls) {
            decl.analyze(table);
        }
        return table;
    }

    public SymTable analyze(SymTable table, SymTable outerTable) {
        for (DeclNode node : myDecls) {

            if (((VarDeclNode) node).getSize() == VarDeclNode.NOT_STRUCT) {
                ((VarDeclNode) node).analyze(outerTable);
                continue;
            }
            ((VarDeclNode) node).analyzeStruct(table, outerTable);

        }
        return table;
    }

    public SymTable analyzeFuncBody(SymTable table) {
        HashMap<String, Integer> memo = new HashMap<String, Integer>();

        for (DeclNode decl : myDecls) {
            IdNode id = decl.getId();
            String name = id.name();
            int num = memo.getOrDefault(name, 0) + 1;
            memo.put(name, num);

            if (memo.get(name) > 1) {
                ErrMsg.multiplyDecl(id.getLineNum(), id.getCharNum());
                continue;
            }

            try {
                if (table.lookupLocal(name) == null) {
                    if (memo.get(name) == 1)
                        decl.analyze(table);
                }
            } catch (EmptySymTableException e) {
                System.err.println(e);
            }

        }
        return table;
    }
}

class FormalsListNode extends ASTnode {
    public FormalsListNode(List<FormalDeclNode> S) {
        myFormals = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<FormalDeclNode> it = myFormals.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) { // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    private List<FormalDeclNode> myFormals;
    private List<DeclNode> myDecls;

    // ---
    public List<String> getTypeList() {
        List<String> params = new LinkedList<String>();

        for (FormalDeclNode formal : myFormals) {
            params.add(formal.getName());
        }
        return params;
    }

    public SymTable analyze(SymTable table) {

        for (FormalDeclNode formal : myFormals) {
            formal.analyze(table);
        }
        return table;
    }
}

class FnBodyNode extends ASTnode {
    public FnBodyNode(DeclListNode declList, StmtListNode stmtList) {
        myDeclList = declList;
        myStmtList = stmtList;
    }

    public void unparse(PrintWriter p, int indent) {
        myDeclList.unparse(p, indent);
        myStmtList.unparse(p, indent);
    }

    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    // ---
    public void analyze(SymTable table) {
        myDeclList.analyzeFuncBody(table);
        myStmtList.analyze(table);
    }
}

class StmtListNode extends ASTnode {
    public StmtListNode(List<StmtNode> S) {
        myStmts = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<StmtNode> it = myStmts.iterator();
        while (it.hasNext()) {
            it.next().unparse(p, indent);
        }
    }

    private List<StmtNode> myStmts;

    // ---
    public void analyze(SymTable table) {
        for (StmtNode stmt : myStmts) {
            stmt.analyze(table);
        }
    }
}

class ExpListNode extends ASTnode {
    public ExpListNode(List<ExpNode> S) {
        myExps = S;
    }

    public void unparse(PrintWriter p, int indent) {
        Iterator<ExpNode> it = myExps.iterator();
        if (it.hasNext()) { // if there is at least one element
            it.next().unparse(p, indent);
            while (it.hasNext()) { // print the rest of the list
                p.print(", ");
                it.next().unparse(p, indent);
            }
        }
    }

    private List<ExpNode> myExps;

    public void analyze(SymTable table) {
        for (ExpNode exp : myExps) {
            exp.analyze(table);
        }
    }
}

// **********************************************************************
// DeclNode and its subclasses
// **********************************************************************

abstract class DeclNode extends ASTnode {
    abstract public SymTable analyze(SymTable table);

    abstract public IdNode getId();
}

class VarDeclNode extends DeclNode {
    public VarDeclNode(TypeNode type, IdNode id, int size) {
        _type = type;
        _id = id;
        _size = size;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        _type.unparse(p, 0);
        p.print(" ");
        _id.unparse(p, 0);
        p.println(";");
    }

    private TypeNode _type;
    private IdNode _id;
    private int _size; // use value NOT_STRUCT if this is not a struct type

    public static int NOT_STRUCT = -1;

    // ---
    public int getSize() {
        return _size;
    }

    public IdNode getId() {
        return this._id;
    }

    public SymTable analyze(SymTable table) {

        if (_type instanceof VoidNode) {
            ErrMsg.voidDecl(this._id.getLineNum(), this._id.getCharNum());
            return table;
        }

        if (_type instanceof StructNode) {
            boolean flag = true;

            IdNode structId = ((StructNode) this._type).getId();
            TSym sym = null;

            try {
                sym = table.lookupGlobal(structId.name());
            } catch (EmptySymTableException e) {
                System.out.println(e);
            }

            if (sym == null || !sym.getType().equals("_struct_")) {
                ErrMsg.badStructType(structId.getLineNum(), structId.getCharNum());
                flag = false;
            }

            TSym structSym = null;
            TSym newSym = new TSym(this._type.name());
            TSym mySym = null;

            try {
                structSym = table.lookupGlobal(((StructNode) _type).getId().name());
            } catch (EmptySymTableException e) {
                System.out.println(e);
            }
            if (structSym == null || flag == false) {
                return table;
            }

            this.safeAddDecl(table, this._id, newSym);

            try {
                mySym = table.lookupGlobal(_id.name());
            } catch (EmptySymTableException e) {
                System.out.println(e);
            }

            _id.setStruct(structSym.getStruct(), mySym);
            return table;
        }

        TSym newSym = new TSym(this._type.name());

        this.safeAddDecl(table, this._id, newSym);

        return table;
    }

    public void analyzeStruct(SymTable table, SymTable symTableStruct) {

        TSym newSym = new TSym(this._type.name());

        this.safeAddDecl(table, this._id, newSym);

        IdNode structId = ((StructNode) this._type).getId();
        TSym sym = null;
        try {
            sym = table.lookupGlobal(structId.name());
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }

        if (sym == null || !sym.getType().equals("_struct_")) {
            ErrMsg.badStructType(structId.getLineNum(), structId.getCharNum());
        }

        if (_type instanceof StructNode) {
            TSym structSym = null;
            TSym mySym = null;

            try {
                structSym = table.lookupGlobal(((StructNode) _type).getId().name());
                mySym = symTableStruct.lookupGlobal(_id.name());
            } catch (EmptySymTableException e) {
                System.out.println(e);
            }

            if (structSym != null && mySym != null) {
                _id.setStruct(structSym.getStruct(), mySym);
            }
        }
    }
}

class FnDeclNode extends DeclNode {
    public FnDeclNode(TypeNode type,
            IdNode id,
            FormalsListNode formalList,
            FnBodyNode body) {
        _type = type;
        _id = id;
        myFormalsList = formalList;
        myBody = body;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        _type.unparse(p, 0);
        p.print(" ");
        _id.unparse(p, 0);
        p.print("(");
        myFormalsList.unparse(p, 0);
        p.println(") {");
        myBody.unparse(p, indent + 4);
        p.println("}\n");
    }

    private TypeNode _type;
    private IdNode _id;
    private FormalsListNode myFormalsList;
    private FnBodyNode myBody;

    // ---
    public SymTable analyze(SymTable table) {
        List<String> params = myFormalsList.getTypeList();

        this.safeAddDecl(table, this._id, new FuncSym(_type.name(), params));

        try {
            table.addScope();
            myFormalsList.analyze(table);
            myBody.analyze(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }
        return table;
    }

    public IdNode getId() {
        return this._id;
    }
}

class FormalDeclNode extends DeclNode {
    public FormalDeclNode(TypeNode type, IdNode id) {
        _type = type;
        _id = id;
    }

    public void unparse(PrintWriter p, int indent) {
        _type.unparse(p, 0);
        p.print(" ");
        _id.unparse(p, 0);
    }

    private TypeNode _type;
    private IdNode _id;

    // ---
    public SymTable analyze(SymTable table) {
        this.safeAddDecl(table, this._id, new TSym(this._type.name()));
        return table;
    }

    public String getName() {
        return _type.name();
    }

    public IdNode getId() {
        return this._id;
    }
}

class StructDeclNode extends DeclNode {
    public StructDeclNode(IdNode id, DeclListNode declList) {
        _id = id;
        myDeclList = declList;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("struct ");
        _id.unparse(p, 0);
        p.println("{");
        myDeclList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("};\n");

    }

    private IdNode _id;
    private DeclListNode myDeclList;

    // ---
    private SymTable mySymTable;

    public SymTable analyze(SymTable table) {
        TSym newSym = new TSym("_struct_");

        this.safeAddDecl(table, _id, newSym);

        mySymTable = new SymTable();
        _id.setStruct(this, newSym);
        myDeclList.analyze(table, mySymTable);
        return table;
    }

    public SymTable getSymTable() {
        return mySymTable;
    }

    public IdNode getId() {
        return this._id;
    }
}

// **********************************************************************
// TypeNode and its Subclasses
// **********************************************************************

abstract class TypeNode extends ASTnode {
    abstract public String name();
}

class IntNode extends TypeNode {
    public IntNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("int");
    }

    public String name() {
        return "int";
    }
}

class BoolNode extends TypeNode {
    public BoolNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("bool");
    }

    public String name() {
        return "bool";
    }
}

class VoidNode extends TypeNode {
    public VoidNode() {
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("void");
    }

    public String name() {
        return "void";
    }
}

class StructNode extends TypeNode {
    public StructNode(IdNode id) {
        _id = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("struct ");
        _id.unparse(p, 0);
    }

    private IdNode _id;

    public String name() {
        return _id.name();
    }

    public IdNode getId() {
        return _id;
    }
}

// **********************************************************************
// StmtNode and its subclasses
// **********************************************************************

abstract class StmtNode extends ASTnode {
    public abstract void analyze(SymTable table);
}

class AssignStmtNode extends StmtNode {
    public AssignStmtNode(AssignNode assign) {
        myAssign = assign;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myAssign.unparse(p, -1); // no parentheses
        p.println(";");
    }

    private AssignNode myAssign;

    public void analyze(SymTable table) {
        myAssign.analyze(table);
    }
}

class PostIncStmtNode extends StmtNode {
    public PostIncStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("++;");
    }

    private ExpNode myExp;

    public void analyze(SymTable table) {
        myExp.analyze(table);
    }
}

class PostDecStmtNode extends StmtNode {
    public PostDecStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myExp.unparse(p, 0);
        p.println("--;");
    }

    private ExpNode myExp;

    public void analyze(SymTable table) {
        myExp.analyze(table);
    }
}

class ReadStmtNode extends StmtNode {
    public ReadStmtNode(ExpNode e) {
        myExp = e;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cin >> ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    // 1 child (actually can only be an IdNode or an ArrayExpNode)
    private ExpNode myExp;

    public void analyze(SymTable table) {
        myExp.analyze(table);
    }

}

class WriteStmtNode extends StmtNode {
    public WriteStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("cout << ");
        myExp.unparse(p, 0);
        p.println(";");
    }

    private ExpNode myExp;

    public void analyze(SymTable table) {
        myExp.analyze(table);
    }
}

class IfStmtNode extends StmtNode {
    public IfStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myDeclList = dlist;
        myExp = exp;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void analyze(SymTable table) {
        myExp.analyze(table);

        try {
            table.addScope();
            myDeclList.analyze(table);
            myStmtList.analyze(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }
    }
}

class IfElseStmtNode extends StmtNode {
    public IfElseStmtNode(ExpNode exp, DeclListNode dlist1,
            StmtListNode slist1, DeclListNode dlist2,
            StmtListNode slist2) {
        myExp = exp;
        myThenDeclList = dlist1;
        myThenStmtList = slist1;
        myElseDeclList = dlist2;
        myElseStmtList = slist2;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("if (");
        myExp.unparse(p, 0);
        p.println(") {");
        myThenDeclList.unparse(p, indent + 4);
        myThenStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
        addIndentation(p, indent);
        p.println("else {");
        myElseDeclList.unparse(p, indent + 4);
        myElseStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myThenDeclList;
    private StmtListNode myThenStmtList;
    private StmtListNode myElseStmtList;
    private DeclListNode myElseDeclList;

    public void analyze(SymTable table) {
        myExp.analyze(table);

        try {
            table.addScope();
            myThenDeclList.analyze(table);
            myThenStmtList.analyze(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }

        table.addScope();
        myElseDeclList.analyze(table);
        myElseStmtList.analyze(table);

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }
    }
}

class WhileStmtNode extends StmtNode {
    public WhileStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("while (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void analyze(SymTable table) {
        myExp.analyze(table);

        try {
            table.addScope();
            myDeclList.analyze(table);
            myStmtList.analyze(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }
    }
}

class RepeatStmtNode extends StmtNode {
    public RepeatStmtNode(ExpNode exp, DeclListNode dlist, StmtListNode slist) {
        myExp = exp;
        myDeclList = dlist;
        myStmtList = slist;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("repeat (");
        myExp.unparse(p, 0);
        p.println(") {");
        myDeclList.unparse(p, indent + 4);
        myStmtList.unparse(p, indent + 4);
        addIndentation(p, indent);
        p.println("}");
    }

    private ExpNode myExp;
    private DeclListNode myDeclList;
    private StmtListNode myStmtList;

    public void analyze(SymTable table) {
        myExp.analyze(table);

        try {
            table.addScope();
            myDeclList.analyze(table);
            myStmtList.analyze(table);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            table.removeScope();
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }
    }
}

class CallStmtNode extends StmtNode {
    public CallStmtNode(CallExpNode call) {
        myCall = call;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        myCall.unparse(p, indent);
        p.println(";");
    }

    private CallExpNode myCall;

    public void analyze(SymTable table) {
        myCall.analyze(table);
    }
}

class ReturnStmtNode extends StmtNode {
    public ReturnStmtNode(ExpNode exp) {
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        addIndentation(p, indent);
        p.print("return");
        if (myExp != null) {
            p.print(" ");
            myExp.unparse(p, 0);
        }
        p.println(";");
    }

    private ExpNode myExp; // possibly null

    public void analyze(SymTable table) {
        if (myExp != null) {
            myExp.analyze(table);
        }
    }
}

// **********************************************************************
// ExpNode and its subclasses
// **********************************************************************

abstract class ExpNode extends ASTnode {
    public abstract void analyze(SymTable table);
}

class IntLitNode extends ExpNode {
    public IntLitNode(int lineNum, int charNum, int intVal) {
        linNum = lineNum;
        chaNum = charNum;
        myIntVal = intVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myIntVal);
    }

    private int linNum;
    private int chaNum;
    private int myIntVal;

    public void analyze(SymTable table) {
    }
}

class StringLitNode extends ExpNode {
    public StringLitNode(int lineNum, int charNum, String strVal) {
        linNum = lineNum;
        chaNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
    }

    private int linNum;
    private int chaNum;
    private String myStrVal;

    public void analyze(SymTable table) {
    }
}

class TrueNode extends ExpNode {
    public TrueNode(int lineNum, int charNum) {
        linNum = lineNum;
        chaNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("true");
    }

    private int linNum;
    private int chaNum;

    public void analyze(SymTable table) {
    }
}

class FalseNode extends ExpNode {
    public FalseNode(int lineNum, int charNum) {
        linNum = lineNum;
        chaNum = charNum;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("false");
    }

    private int linNum;
    private int chaNum;

    public void analyze(SymTable table) {
    }
}

class IdNode extends ExpNode {
    public IdNode(int lineNum, int charNum, String strVal) {
        linNum = lineNum;
        chaNum = charNum;
        myStrVal = strVal;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print(myStrVal);
        if (mySym != null) {
            p.print("(");
            p.print(mySym.name());
            p.print(")");
        }
    }

    private int linNum;
    private int chaNum;
    private String myStrVal;

    // ---
    private TSym mySym;
    private StructDeclNode myStruct;

    public int getLineNum() {
        return linNum;
    }

    public int getCharNum() {
        return chaNum;
    }

    public String name() {
        return myStrVal;
    }

    public void analyze(SymTable table) {
        try {
            this.mySym = table.lookupGlobal(myStrVal);
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }

        if (mySym == null) {
            ErrMsg.undeclaredId(linNum, chaNum);
        } else {
            this.myStruct = mySym.getStruct();
        }
        return;
    }

    public TSym getSym() {
        return mySym;
    }

    public void setSym(TSym mySym) {
        this.mySym = mySym;
    }

    public StructDeclNode getStruct() {
        return myStruct;
    }

    public void setStruct(StructDeclNode myStruct, TSym sym) {
        this.myStruct = myStruct;
        sym.setStruct(myStruct);
    }
}

class DotAccessExpNode extends ExpNode {
    public DotAccessExpNode(ExpNode loc, IdNode id) {
        myLoc = loc;
        _id = id;
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myLoc.unparse(p, 0);
        p.print(").");
        _id.unparse(p, 0);
    }

    private ExpNode myLoc;
    private IdNode _id;

    // ---
    public void analyze(SymTable table) {
        myLoc.analyze(table);
        StructDeclNode lhs = this.getLeftStruct(table);

        if (lhs == null)
            return;

        SymTable leftTable = lhs.getSymTable();

        TSym found = null;
        try {
            found = leftTable.lookupGlobal(_id.name());
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }

        if (found == null) {
            ErrMsg.invalidStructField(((IdNode) _id).getLineNum(), ((IdNode) _id).getCharNum());
            return;
        }

        _id.setSym(found);

    }

    private StructDeclNode getLeftStruct(SymTable table) {
        if (myLoc instanceof IdNode) {
            TSym findingSym = null;

            try {
                findingSym = table.lookupGlobal(((IdNode) myLoc).name());
            } catch (EmptySymTableException e) {
                System.out.println(e);
            }

            if (findingSym == null)
                return null;

            if (findingSym.getStruct() == null) {
                ErrMsg.nonStruct(((IdNode) myLoc).getLineNum(), ((IdNode) myLoc).getCharNum());
                return null;
            }
            return ((IdNode) myLoc).getStruct();
        }
        return getStruct(table);

    }

    private StructDeclNode getStruct(SymTable table) {
        StructDeclNode lhs = ((DotAccessExpNode) myLoc).getLeftStruct(table);
        if (lhs == null)
            return null;

        SymTable leftTable = lhs.getSymTable();

        TSym found = null;

        try {
            found = leftTable.lookupGlobal(((DotAccessExpNode) myLoc)._id.name());
        } catch (EmptySymTableException e) {
            System.out.println(e);
        }

        if (found == null)
            return null;

        return found.getStruct();
    }
}

class AssignNode extends ExpNode {
    public AssignNode(ExpNode lhs, ExpNode exp) {
        myLhs = lhs;
        myExp = exp;
    }

    public void unparse(PrintWriter p, int indent) {
        if (indent != -1)
            p.print("(");
        myLhs.unparse(p, 0);
        p.print(" = ");
        myExp.unparse(p, 0);
        if (indent != -1)
            p.print(")");
    }

    private ExpNode myLhs;
    private ExpNode myExp;

    public void analyze(SymTable table) {
        myLhs.analyze(table);
        myExp.analyze(table);
    }
}

class CallExpNode extends ExpNode {
    public CallExpNode(IdNode name, ExpListNode elist) {
        _id = name;
        myExpList = elist;
    }

    public CallExpNode(IdNode name) {
        _id = name;
        myExpList = new ExpListNode(new LinkedList<ExpNode>());
    }

    public void unparse(PrintWriter p, int indent) {
        _id.unparse(p, 0);
        p.print("(");
        if (myExpList != null) {
            myExpList.unparse(p, 0);
        }
        p.print(")");
    }

    private IdNode _id;
    private ExpListNode myExpList; // possibly null

    public void analyze(SymTable table) {
        _id.analyze(table);
        myExpList.analyze(table);
    }
}

abstract class UnaryExpNode extends ExpNode {
    public UnaryExpNode(ExpNode exp) {
        myExp = exp;
    }

    protected ExpNode myExp;

    public void analyze(SymTable table) {
        myExp.analyze(table);
    }
}

abstract class BinaryExpNode extends ExpNode {
    public BinaryExpNode(ExpNode exp1, ExpNode exp2) {
        myExp1 = exp1;
        myExp2 = exp2;
    }

    protected ExpNode myExp1;
    protected ExpNode myExp2;

    public void analyze(SymTable table) {
        myExp1.analyze(table);
        myExp2.analyze(table);
    }
}

// **********************************************************************
// Subclasses of UnaryExpNode
// **********************************************************************

class UnaryMinusNode extends UnaryExpNode {
    public UnaryMinusNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(-");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

class NotNode extends UnaryExpNode {
    public NotNode(ExpNode exp) {
        super(exp);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(!");
        myExp.unparse(p, 0);
        p.print(")");
    }
}

// **********************************************************************
// Subclasses of BinaryExpNode
// **********************************************************************

class PlusNode extends BinaryExpNode {
    public PlusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" + ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class MinusNode extends BinaryExpNode {
    public MinusNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" - ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class TimesNode extends BinaryExpNode {
    public TimesNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" * ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class DivideNode extends BinaryExpNode {
    public DivideNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" / ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class AndNode extends BinaryExpNode {
    public AndNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" && ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class OrNode extends BinaryExpNode {
    public OrNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" || ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class EqualsNode extends BinaryExpNode {
    public EqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" == ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class NotEqualsNode extends BinaryExpNode {
    public NotEqualsNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" != ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessNode extends BinaryExpNode {
    public LessNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" < ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterNode extends BinaryExpNode {
    public GreaterNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" > ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class LessEqNode extends BinaryExpNode {
    public LessEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" <= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}

class GreaterEqNode extends BinaryExpNode {
    public GreaterEqNode(ExpNode exp1, ExpNode exp2) {
        super(exp1, exp2);
    }

    public void unparse(PrintWriter p, int indent) {
        p.print("(");
        myExp1.unparse(p, 0);
        p.print(" >= ");
        myExp2.unparse(p, 0);
        p.print(")");
    }
}
