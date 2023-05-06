# Programming Assignment 3: Parser

Guikai Huang ( ghuang49@wisc.edu )

In this assignment, the parser-generator Java Cup was employed in the creation of a parser for the Gibberish language. The parser is designed to detect syntax errors and generate an abstract-syntax tree (AST) representation of valid programs.

Subsequently, methods were developed for the purpose of unparsing the AST produced by the parser, and an input file was created to enable the parser's evaluation.

In addition, a set of comprehensive test cases was composed in the file test.gibberish to guarantee the parser's precision and efficiency.

## Build and Test

- Build the project: `$ make`
- Run the test in _test.gibberish_ : `$ make test`
- Clean all generated files: `$ make clean`
