package guikai.P1;

import java.util.*;

public class SymTable {

    private  LinkedList<HashMap<String,Sym>> list;

    // constructor
    // initialize the SymTable's List field to contain a single, empty HashMap.
    SymTable(){
        list = new LinkedList<>();
        HashMap<String,Sym> map = new HashMap<>();
        list.addFirst(map);
    }

    // add the given name and sym to the first HashMap in the list.
    void addDecl(String name, Sym sym) throws DuplicateSymException, EmptySymTableException {
        // If this SymTable's list is empty, throw an EmptySymTableException.
        if (list == null || list.isEmpty()){
            EmptySymTableException e = new EmptySymTableException();
            throw e;
        }

        // If either name or sym (or both) is null, throw a IllegalArgumentException.
        if (name == null || sym == null){
            IllegalArgumentException e = new IllegalArgumentException();
            throw e;
        }

        // If the first HashMap in the list already contains the given name as a key, throw a DuplicateSymException.
        if (list.getFirst().containsKey(name)){
            DuplicateSymException e = new DuplicateSymException();
            throw e;
        }

        // add name and sym
        list.getFirst().put(name, sym);
    }

    // Add a new, empty HashMap to the front of the list.
    void addScope(){
        HashMap<String, Sym> newMap = new HashMap<>();
        list.addFirst(newMap);
    }

    // lookup sym with its name in the first Hashmap of the linked-list
    Sym lookupLocal(String name) throws EmptySymTableException{
        // If this SymTable's list is empty, throw an EmptySymTableException.
        if (list == null || list.isEmpty()){
            EmptySymTableException e = new EmptySymTableException();
            throw e;
        }

        // If the first HashMap in the list contains name as a key, return the associated Sym;
        if(list.getFirst().containsKey(name)){
            return list.getFirst().get(name);
        }

        // Otherwise, return null.
        return null;
    }

    // lookup sym with its name in all Hashmaps of the linked-list
    Sym lookupGlobal(String name) throws EmptySymTableException{
        // If this SymTable's list is empty, throw an EmptySymTableException.
        if (list == null || list.isEmpty()){
            EmptySymTableException e = new EmptySymTableException();
            throw e;
        }

        // If any HashMap in the list contains name as a key, return the first associated Sym
        // (i.e., the one from the HashMap that is closest to the front of the list);
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Sym> currMap = list.get(i);
            if(currMap.containsKey(name)){
                return currMap.get(name);
            }
        }

        // otherwise, return null.
        return null;
    }

    void removeScope() throws EmptySymTableException{
        // If this SymTable's list is empty, throw an EmptySymTableException;
        if (list == null || list.isEmpty()){
            EmptySymTableException e = new EmptySymTableException();
            throw e;
        }

        // otherwise, remove the HashMap from the front of the list.
        // To clarify, throw an exception only if before attempting to remove, the list is empty
        // (i.e. there are no HashMaps to remove).
        list.removeFirst();
    }

    // Print all hashmaps in the list for debugging
    // All output go to System.out.
    void print(){
        // First, print “\nSym Table\n”.
        System.out.print("\nSym Table\n");

        // Then, for each HashMap M in the list, print M.toString() followed by a newline.
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Sym> currMap = list.get(i);
            System.out.println(currMap.toString());
        }
        // Finally, print one more newline.
        System.out.print("\n");
    }

    // return list for debugging
    public LinkedList<HashMap<String,Sym>> getList(){
        return list;
    }
}
