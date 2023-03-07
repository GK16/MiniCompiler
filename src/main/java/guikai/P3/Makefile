###
# This Makefile can be used to make a parser for the C-- language
# (parser.class) and to make a program (P3.class) that tests the parser and
# the unparse methods in ast.java.
#
# make clean removes all generated files.
#
###

JC = javac
FLAGS = -g
CP = ./deps:.

P3.class: P3.java parser.class Yylex.class ASTnode.class
	$(JC) $(FLAGS) -cp $(CP) P3.java

parser.class: parser.java ASTnode.class Yylex.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) parser.java

parser.java: gibberish.cup
	java -cp $(CP) java_cup.Main < gibberish.cup

Yylex.class: gibberish.jlex.java sym.class ErrMsg.class
	$(JC) $(FLAGS) -cp $(CP) gibberish.jlex.java

ASTnode.class: ast.java
	$(JC) $(FLAGS) -cp $(CP) ast.java

gibberish.jlex.java: gibberish.jlex sym.class
	java -cp $(CP) JLex.Main gibberish.jlex

sym.class: sym.java
	$(JC) $(FLAGS) -cp $(CP) sym.java

sym.java: gibberish.cup
	java -cp $(CP) java_cup.Main < gibberish.cup

ErrMsg.class: ErrMsg.java
	$(JC) $(FLAGS) -cp $(CP) ErrMsg.java

##test
test:
	java -cp $(CP) P3 test.gibberish test.out

###
# clean
###
clean:
	rm -f *~ *.class parser.java gibberish.jlex.java sym.java

## cleantest (delete test artifacts)
cleantest:
	rm -f *.out
