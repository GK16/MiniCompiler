# P2

Author: Guikai Huang (ghuang49@wisc.edu)

For this assignment, I have implemented a lexical scanner for the Gibberish language using `Jlex`. The scanning rules and corresponding actions are defined in the `gibberish.jlex` file, and the test files are located in the `testFile` folder. In the `P2.java` file, I have conducted thorough testing on both valid and invalid tokens.

## Build Instructions

To build the lexical scanner project, run:
`$ make`

To run the tests on valid tokens, use:
`$ make test`

To run the tests on invalid tokens, use:
`$ make testInvalidTokens`

To remove the compiled files created by the program, run:
`$ make clean`

To remove the output files created by the tests, run:
`$ make cleantest`
