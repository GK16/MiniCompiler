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

    public static void testSym(){
        try {
        } catch (Exception ex) {
            System.out.println("Exception thrown on attempt to remove "
                    + "first item");
        }
    }

    public static void testSymTable(){}

    public static void testDuplicateSymException(){}

    public static void testEmptySymTableException(){}
}
