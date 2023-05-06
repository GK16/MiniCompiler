# P4: Name Analyzer

Guikai Huang (ghuang49@wisc.edu)

For this assignment I wrote a name analyzer for Gibberish programs represented as abstract-syntax trees.

The name analyzer will perform the following tasks:

1. **Build symbol tables.** I used the "list of hashtables" approach .
2. **Find multiply declared names, uses of undeclared names, bad struct accesses, and bad declarations.**
3. **Add IdNode links** : For each IdNode in the abstract-syntax tree that represents a _use_ of a name add a "link" to the corresponding symbol-table entry.

## Build and Test

```
# build
make
# test
make test
```
