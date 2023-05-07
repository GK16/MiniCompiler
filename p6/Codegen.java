import java.io.*;
import java.util.HashMap;

// **********************************************************************
// The Codegen class provides constants and operations useful for code
// generation.
//
// The constants are:
//     Registers: FP, SP, RA, V0, V1, A0, T0, T1
//     Values: TRUE, FALSE
//
// The operations include various "generate" methods to print nicely
// formatted assembly code:
//     generateWithComment
//     generate
//     generateIndexed
//     generateLabeled
//     genPush
//     genPop
//     genLabel
// and a method nextLabel to create and return a new label.
//
// **********************************************************************

public class Codegen {
    // file into which generated code is written
    public static PrintWriter p = null;

    // values of true and false
    public static final String TRUE = "1";
    public static final String FALSE = "0";

    // registers
    public static final String FP = "$fp";
    public static final String SP = "$sp";
    public static final String RA = "$ra";
    public static final String V0 = "$v0";
    public static final String V1 = "$v1";
    public static final String A0 = "$a0";
    public static final String T0 = "$t0";
    public static final String T1 = "$t1";

    // for pretty printing generated code
    private static final int MAXLEN = 4;

    // for generating labels
    private static int currLabel = 0;

    // **********************************************************************
    // **********************************************************************
    // GENERATE OPERATIONS
    // **********************************************************************
    // **********************************************************************

    // **********************************************************************
    // generateWithComment
    // given: op code, comment, and 0 to 3 string args
    // do: write nicely formatted code (ending with new line)
    // **********************************************************************
    public static void generateWithComment(String opcode, String comment,
            String arg1, String arg2, String arg3) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        if (arg1 != "") {
            for (int k = 1; k <= space; k++)
                p.print(" ");
            p.print(arg1);
            if (arg2 != "") {
                p.print(", " + arg2);
                if (arg3 != "")
                    p.print(", " + arg3);
            }
        }
        if (comment != "")
            p.print("\t\t#" + comment);
        p.println();
    }

    public static void generateWithComment(String opcode, String comment,
            String arg1, String arg2) {
        generateWithComment(opcode, comment, arg1, arg2, "");
    }

    public static void generateWithComment(String opcode, String comment,
            String arg1) {
        generateWithComment(opcode, comment, arg1, "", "");
    }

    public static void generateWithComment(String opcode, String comment) {
        generateWithComment(opcode, comment, "", "", "");
    }

    // **********************************************************************
    // generate
    // given: op code, and 0 to 3 string args
    // do: write nicely formatted code (ending with new line)
    // **********************************************************************
    public static void generate(String opcode, String arg1, String arg2,
            String arg3) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        if (arg1 != "") {
            for (int k = 1; k <= space; k++)
                p.print(" ");
            p.print(arg1);
            if (arg2 != "") {
                p.print(", " + arg2);
                if (arg3 != "")
                    p.print(", " + arg3);
            }
        }
        p.println();
    }

    public static void generate(String opcode, String arg1, String arg2) {
        generate(opcode, arg1, arg2, "");
    }

    public static void generate(String opcode, String arg1) {
        generate(opcode, arg1, "", "");
    }

    public static void generate(String opcode) {
        generate(opcode, "", "", "");
    }

    // **********************************************************************
    // generate (two string args, one int)
    // given: op code and args
    // do: write nicely formatted code (ending with new line)
    // **********************************************************************
    public static void generate(String opcode, String arg1, String arg2,
            int arg3) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        for (int k = 1; k <= space; k++)
            p.print(" ");
        p.println(arg1 + ", " + arg2 + ", " + arg3);
    }

    // **********************************************************************
    // generate (one string arg, one int)
    // given: op code and args
    // do: write nicely formatted code (ending with new line)
    // **********************************************************************
    public static void generate(String opcode, String arg1, int arg2) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        for (int k = 1; k <= space; k++)
            p.print(" ");
        p.println(arg1 + ", " + arg2);
    }

    // **********************************************************************
    // generateIndexed
    // given: op code, target register T1 (as string), indexed register T2
    // (as string), - offset xx (int), and optional comment
    // do: write nicely formatted code (ending with new line):
    // op T1, xx(T2) #comment
    // **********************************************************************
    public static void generateIndexed(String opcode, String arg1, String arg2,
            int arg3, String comment) {
        int space = MAXLEN - opcode.length() + 2;

        p.print("\t" + opcode);
        for (int k = 1; k <= space; k++)
            p.print(" ");
        p.print(arg1 + ", " + arg3 + "(" + arg2 + ")");
        if (comment != "")
            p.print("\t#" + comment);
        p.println();
    }

    public static void generateIndexed(String opcode, String arg1, String arg2,
            int arg3) {
        generateIndexed(opcode, arg1, arg2, arg3, "");
    }

    // **********************************************************************
    // generateLabeled (string args -- perhaps empty)
    // given: label, op code, comment, and arg
    // do: write nicely formatted code (ending with new line)
    // **********************************************************************
    public static void generateLabeled(String label, String opcode,
            String comment, String arg1) {
        int space = MAXLEN - opcode.length() + 2;

        p.print(label + ":");
        p.print("\t" + opcode);
        if (arg1 != "") {
            for (int k = 1; k <= space; k++)
                p.print(" ");
            p.print(arg1);
        }
        if (comment != "")
            p.print("\t# " + comment);
        p.println();
    }

    public static void generateLabeled(String label, String opcode,
            String comment) {
        generateLabeled(label, opcode, comment, "");
    }

    // **********************************************************************
    // genPush
    // generate code to push the given value onto the stack
    // **********************************************************************
    public static void genPush(String s) {
        generateIndexed("sw", s, SP, 0, "PUSH");
        generate("subu", SP, SP, 4);
    }

    // **********************************************************************
    // genPop
    // generate code to pop into the given register
    // **********************************************************************
    public static void genPop(String s) {
        generateIndexed("lw", s, SP, 4, "POP");
        generate("addu", SP, SP, 4);
    }

    // **********************************************************************
    // genLabel
    // given: label L and comment (comment may be empty)
    // generate: L: # comment
    // **********************************************************************
    public static void genLabel(String label, String comment) {
        p.print(label + ":");
        if (comment != "")
            p.print("\t\t" + "# " + comment);
        p.println();
    }

    public static void genLabel(String label) {
        genLabel(label, "");
    }

    // **********************************************************************
    // Return a different label each time:
    // L0 L1 L2, etc.
    // **********************************************************************
    public static String nextLabel() {
        Integer k = new Integer(currLabel++);
        String tmp = ".L" + k;
        return (tmp);
    }

    // store str literals
    private static HashMap<String, String> stringCache;

    public static void init(PrintWriter printWriter) {
        p = printWriter;
        stringCache = new HashMap<>();
    }

    public static void cleanup() {
        p.close();
    }

    public static void genVar(String varName) {
        p.println("\t\t.data");
        p.println("\t\t.align 2");
        String blank = varName.length() < 2 ? "\t" : "";
        p.println(String.format("_%s:%s\t.space 4", varName, blank));
    }

    public static void genFuncPreamble(String funcName) {
        if (funcName.equals("main")) {
            genMainPreamble();
            return;
        }

        // for all other functions
        p.println("\t\t.text");
        p.println(String.format("_%s:", funcName));
    }

    public static void prepareFunc(FnSym funcSym) {
        // 1. push return address
        genPush(RA);

        // 2. push control link (saved FP)
        genPush(FP);

        // 3. set $fp
        // set the FP point to the bottom of the new AR
        // +8: 4 bytes each for the control link and return address
        // offset: size of params in bytes
        String offsetParam = String.valueOf(funcSym.getParamSize() + 8);
        // addu: Binary Addition Algorithm
        generateWithComment("addu", "", FP, SP, offsetParam);

        // 4. push space for local variablea
        String offsetLocal = String.valueOf(funcSym.getLocalSize());
        generateWithComment("subu", "", SP, SP, offsetLocal);
    }

    private static void genMainPreamble() {
        p.println("\t\t.text");
        p.println("\t\t.globl main");
        p.println("main:\t\t# METHOD ENTRY");
        p.println("__start:\t# add __start label for main only");
    }

    public static void comment(String comment) {
        p.println("\t\t\t# " + comment);
    }

    public static void genFuncExit(String funcName, String label, FnSym sym) {

        // 0. exit lable
        comment("FUNCTION EXIT");
        generateLabeled(label, "", "");

        // 1. load return address
        // lw $ra, -<param size>($fp)
        generateIndexed("lw", RA, FP, -sym.getParamSize());

        // 2. save control link
        // use a temporary register (t0) to save the address that is initially in the FP
        // move $t0, $fp
        generateWithComment("move", " save control link", T0, FP);

        // 3. restore FP
        // lw $fp, -<paramsize+4>($fp)
        generateIndexed("lw", FP, FP, -(sym.getParamSize() + 4), " restore FP");

        // 4. restore SP
        // move $sp, $t0
        generateWithComment("move", " restore SP", SP, T0);

        // 5. return
        // jr $ra
        if (funcName.equals("main")) {
            generateWithComment("li", " load exit code for syscall", Codegen.V0, "10");
            generateWithComment("syscall", " only do this for main");
            return;
        }
        generate("jr", RA);
    }

    public static void genPushStrLit(String s) {
        // Get the label to the string literal
        String label;
        if (stringCache.containsKey(s)) {
            label = stringCache.get(s);
        } else {
            label = nextLabel();
            p.println("\t\t.data");
            p.println(label + ":\t.asciiz " + s);
            stringCache.put(s, label);
            p.println("\t\t.text");
        }

        // Push the address of the literal to the stack
        generate("la", T0, label);
        genPush(T0);
    }

    public static void genAssignSmt() {
        // store value into the address
        // put address into $t0
        genPop(T0);
        // put value into $t1
        genPop(T1);
        generateIndexed("sw", T1, T0, 0);
        // have a copied value on stack
        genPush(T1);
    }
}
