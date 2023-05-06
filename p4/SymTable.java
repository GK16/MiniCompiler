import java.util.*;

public class SymTable {

  private List<HashMap<String, TSym>> list;

  public SymTable() {
    list = new LinkedList<HashMap<String, TSym>>();
    list.add(new HashMap<String, TSym>());
  }

  public void addDecl(String name, TSym sym) throws DuplicateSymException,
  EmptySymTableException, IllegalArgumentException {

    if (name == null && sym == null) {
      throw new IllegalArgumentException();
    } else if (name == null) {
      throw new IllegalArgumentException();
    } else if (sym == null) {
      throw new IllegalArgumentException();
    }

    if (list.isEmpty()) {
      throw new EmptySymTableException();
    }

    HashMap<String, TSym> symTab = list.get(0);
    if (symTab.containsKey(name)) {
      throw new DuplicateSymException();
    } else {
      symTab.put(name, sym);
    }
  }

  public void addScope() {
    list.add(0, new HashMap<String, TSym>());
  }

  public TSym lookupLocal(String name) throws EmptySymTableException {
    if (list.isEmpty()) {
      throw new EmptySymTableException();
    }

    if (list.get(0).containsKey(name)){
      return list.get(0).get(name);
    } else {
      return null;
    }
  }

  public TSym lookupGlobal(String name) throws EmptySymTableException {
    if (list.isEmpty()) {
      throw new EmptySymTableException();
    }

    for (HashMap<String, TSym> symTab : list) {
      if (symTab.containsKey(name)){
        return symTab.get(name);
      }
    }
    return null;
  }

  public void removeScope() throws EmptySymTableException {
    if (list.isEmpty()) {
      throw new EmptySymTableException();
    }
    list.remove(0);
  }

  public void print() {
    System.out.print("\nSym Table\n");
    for (HashMap<String, TSym> symTab : list) {
      System.out.println(symTab.toString());
    }
    System.out.println();
  }
}
