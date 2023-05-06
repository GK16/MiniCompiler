###
# This Makefile can be used to make a parser for the C-- language
# (parser.class) and to make a program (P5.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac

CP = ./deps:.

P5.class: P5.java parser.class Yylex.class ASTnode.class
	$(JC) -g -cp $(CP) P5.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) -g -cp $(CP) parser.java

parser.java: gibberish.cup
	java -cp $(CP) java_cup.Main < gibberish.cup

Yylex.class: gibberish.jlex.java sym.class ErrMsg.class
	$(JC) -g -cp $(CP) gibberish.jlex.java

ASTnode.class: ast.java Type.java TSym.class
	$(JC) -g -cp $(CP) ast.java Type.java

gibberish.jlex.java: gibberish.jlex sym.class
	java -cp $(CP) JLex.Main gibberish.jlex

sym.class: sym.java
	$(JC) -g -cp $(CP) sym.java

sym.java: gibberish.cup
	java java_cup.Main < gibberish.cup

ErrMsg.class: ErrMsg.java
	$(JC) -g -cp $(CP) ErrMsg.java

TSym.class: TSym.java Type.class ast.java
	$(JC) -g -cp $(CP) TSym.java ast.java

SymTable.class: SymTable.java TSym.class DuplicateSymException.class EmptySymTableException.class
	$(JC) -g -cp $(CP) SymTable.java

Type.class: Type.java
	$(JC) -g -cp $(CP) Type.java ast.java

DuplicateSymException.class: DuplicateSymException.java
	$(JC) -g -cp $(CP) DuplicateSymException.java

EmptySymTableException.class: EmptySymTableException.java
	$(JC) -g -cp $(CP) EmptySymTableException.java

###
# test
#
test:
	java -cp $(CP) P5 test.gibberish test.out
	java -cp $(CP) P5 typeErrors.gibberish testErr.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java gibberish.jlex.java sym.java

cleantest:
	rm -f test.out
