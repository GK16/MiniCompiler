/**
 * ErrMsg
 *
 * This class is used to generate warning and fatal error messages.
 */
class ErrMsg {
    /**
     * Generates a fatal error message.
     * 
     * @param lineNum line number for error location
     * @param charNum character number (i.e., column) for error location
     * @param msg     associated message for error
     */
    static void fatal(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " ***ERROR*** " + msg);
    }

    /**
     * Generates a warning message.
     * 
     * @param lineNum line number for warning location
     * @param charNum character number (i.e., column) for warning location
     * @param msg     associated message for warning
     */
    static void warn(int lineNum, int charNum, String msg) {
        System.err.println(lineNum + ":" + charNum + " ***WARNING*** " + msg);
    }

    private static boolean err = false;

    public static boolean hasErr() {
        return err;
    }

    public static void multiplyDecl(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Multiply declared identifier");
    }

    public static void undeclaredId(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Undeclared identifier");
    }

    public static void nonStruct(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Dot-access of non-struct type");
    }

    public static void invalidStructField(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Invalid struct field name");
    }

    public static void voidDecl(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Non-function declared void");
    }

    public static void badStructType(int lineNum, int charNum) {
        fatal(lineNum, charNum, "Invalid name of struct type");
    }
}
