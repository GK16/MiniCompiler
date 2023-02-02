package guikai.P1;

public class P1 {
    /**
     * P1
     *
     * Author: Guikai Huang
     * Email: ghuang49@wisc.edu
     *
     * This is a class whose sole purpose is to test all the Sym and SymTable operations
     * and all situations under which exceptions are thrown.
     *
     * This code tests every operation, including both correct and
     * bad calls to the operation that can throw an exception.
     * It produces output ONLY if a test fails.
     */

    public static void main(String [] args){
        testSym();
        testSymTable();
        testDuplicateSymException();
        testEmptySymTableException();
    }


    /**
     * test the EmptySymTableException
     */
    public static void testEmptySymTableException(){
        // test EmptySymTableException at SymTable.addDecl()
        try {
            // new a SymTable, Sym
            SymTable symTable = new SymTable();
            Sym sym = new Sym("testType");
            // empty the table
            symTable.removeScope();
            // cause the EmptySymTableException at addDecl()
            symTable.addDecl("testType", sym);
            // if no EmptySymTableException is thrown, print error
            System.err.println("ERROR at SymTable.addDecl()! EmptySymTableException not thrown");
        } catch (EmptySymTableException e) {
            ;
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.addDecl()! Wrong Exception Thrown");
        }

        // test EmptySymTableException at SymTable.lookupLocal()
        try {
            // new a SymTable
            SymTable symTable = new SymTable();
            // empty the table
            symTable.removeScope();
            // cause the EmptySymTableException at lookupLocal()
            symTable.lookupLocal("testType");
            // if no EmptySymTableException is thrown, print error
            System.err.println("ERROR at SymTable.lookupLocal()! EmptySymTableException not thrown");
        } catch (EmptySymTableException e) {
            ;
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.lookupLocal()! Wrong Exception Thrown");
        }

        // test EmptySymTableException at SymTable.lookupGlobal()
        try {
            // new a SymTable
            SymTable symTable = new SymTable();
            // empty the table
            symTable.removeScope();
            // cause the EmptySymTableException at lookupGlobal()
            symTable.lookupGlobal("testType");
            // if no EmptySymTableException is thrown, print error
            System.err.println("ERROR at SymTable.lookupGlobal()! EmptySymTableException not thrown");
        } catch (EmptySymTableException e) {
            ;
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.lookupGlobal()! Wrong Exception Thrown");
        }

        // test EmptySymTableException at SymTable.removeScope()
        try {
            // new a SymTable
            SymTable symTable = new SymTable();
            // empty the table
            symTable.removeScope();
            // cause the EmptySymTableException
            symTable.removeScope();
            // if no EmptySymTableException is thrown, print error
            System.err.println("ERROR at SymTable.removeScope()! EmptySymTableException not thrown");
        } catch (EmptySymTableException e) {
            ;
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.removeScope()! Wrong Exception Thrown");
        }
    }


    /**
     * test the DuplicateSymException
     */
    public static void testDuplicateSymException(){

        // test DuplicateSymException at SymTable.addDecl()
        try {
            // new a SymTable, Sym
            SymTable symTable = new SymTable();
            Sym sym = new Sym("testType");
            // add testType
            symTable.addDecl("testType", sym);
            // cause the DuplicateSymException
            symTable.addDecl("testType", sym);
            // if no DuplicateSymException is thrown, print error
            System.err.println("ERROR at SymTable.addDecl()! DuplicateSymException not thrown");
        } catch (DuplicateSymException e) {
            ;
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.addDecl()! Wrong Exception Thrown");
        }
    }

    /**
    * test methods in Sym class
    */
    public static void testSym(){
        // test constructor of Sym
        try {
            Sym sym = new Sym("testType");
        } catch (Exception e) {
            System.err.println("ERROR at Sym constructor!");
        }

        // test Sym.getType()
        Sym sym_1 = new Sym("testType");
        if(sym_1.getType() != "testType"){
            System.err.println("ERROR at Sym.getType()!");
        }

        // test Sym.toString()
        Sym sym_2 = new Sym("testType");
        if(sym_2.toString() != "testType"){
            System.err.println("ERROR at Sym.toString()!");
        }
    }

    public static void testSymTable(){
        // test constructor of SymTable
        try {
            // new a SymTable
            SymTable symTable = new SymTable();
            if(symTable == null){
                System.err.println("ERROR at SymTable constructor!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable constructor!");
        }

        // test SymTable.addDecl()
        SymTable symTable_1 = new SymTable();
        Sym sym_1 = new Sym("testType");
        try {
            // test addDecl
            symTable_1.addDecl("testType", sym_1);
            if(symTable_1.getList().getFirst().get("testType") != sym_1){
                System.err.println("ERROR at SymTable.addDecl()!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.addDecl()!");
        }

        // test SymTable.addScope()
        SymTable symTable_2 = new SymTable();
        try {
            // test addScope
            symTable_2.addScope();
            if(symTable_2.getList().size() != 2){
                System.err.println("ERROR at SymTable.addScope()!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.addScope()!");
        }

        // test SymTable.lookupLocal()
        SymTable symTable_3 = new SymTable();
        Sym sym_3 = new Sym("testType");
        try {
            // test lookupLocal
            symTable_3.addDecl("testType", sym_3);
            if(symTable_3.lookupLocal("testType") != sym_3){
                System.err.println("ERROR at SymTable.lookupLocal()!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.lookupLocal()!");
        }

        // test SymTable.lookupGlobal()
        SymTable symTable_4 = new SymTable();
        Sym sym_4 = new Sym("testType");
        Sym sym_4_2 = new Sym("testType_2");
        try {
            // test addDecl
            symTable_4.addDecl("testType", sym_4);
            symTable_4.addScope();
            symTable_4.addDecl("testType_2", sym_4_2);
            if(symTable_4.lookupGlobal("testType_2") != sym_4_2){
                System.err.println("ERROR at SymTable.lookupGlobal()!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.lookupGlobal()!");
        }

        // test SymTable.removeScope()
        SymTable symTable_5 = new SymTable();
        try {
            // test removeScope
            symTable_5.removeScope();
            if(symTable_5.getList().size() != 0){
                System.err.println("ERROR at SymTable.removeScope()!");
            }
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.removeScope()!");
        }

        // test SymTable.print()
        SymTable symTable_6 = new SymTable();
        Sym sym_6 = new Sym("testType");
        try {
            // test removeScope
            symTable_6.addDecl("testType", sym_6);
            symTable_6.print();
        } catch (Exception e) {
            System.err.println("ERROR at SymTable.print()!");
        }
    }
}
