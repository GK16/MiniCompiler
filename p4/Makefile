###
# This Makefile can be used to make a parser for the Gibberish language
# (parser.class) and to make a program (P4.class) that tests the parser, unparse and
# name analysis methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
FLAGS = -g
CP = ./deps:.

P4.class: P4.java parser.class Yylex.class ASTnode.class
	$(JC) $(FLAGS) -cp $(CP) P4.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) parser.java

parser.java: gibberish.cup
	java -cp $(CP) java_cup.Main < gibberish.cup

Yylex.class: gibberish.jlex.java sym.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) gibberish.jlex.java

ASTnode.class: ast.java SymTable.class
	$(JC) $(FLAGS) -cp $(CP) ast.java

gibberish.jlex.java: gibberish.jlex sym.class
	java -cp $(CP) JLex.Main gibberish.jlex

sym.class: sym.java
	$(JC) $(FLAGS) -cp $(CP) sym.java

sym.java: gibberish.cup
	java -cp $(CP) java_cup.Main < gibberish.cup

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) -cp $(CP) ErrMsg.java

TSym.class: TSym.java
	$(JC) $(FLAGS) -cp $(CP) TSym.java

SymTable.class: SymTable.java TSym.class DuplicateSymException.class EmptySymTableException.class
	$(JC) $(FLAGS) -cp $(CP) SymTable.java

DuplicateSymException.class: DuplicateSymException.java
	$(JC) $(FLAGS) -cp $(CP) DuplicateSymException.java

EmptySymTableException.class: EmptySymTableException.java
	$(JC) $(FLAGS) -cp $(CP) EmptySymTableException.java

##test
test:
	java -cp $(CP) P4 test.gibberish test.out
	java -cp $(CP) P4 nameErrors.gibberish nameErrors.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java gibberish.jlex.java sym.java

## cleantest (delete test artifacts)
cleantest:
	rm -f *.out
