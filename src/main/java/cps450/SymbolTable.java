package cps450;
import java.util.*;

public class SymbolTable {
    private static SymbolTable instance = new SymbolTable();
    private List<Symbol> table;
    private int currentScope;

    ArrayList<Symbol> inList, outList;
    ArrayList<Type> readintList, writeintList;

    private SymbolTable() {
        table = new ArrayList<Symbol>();
        currentScope = 0;
        initializeClasses();
    }

    public static synchronized SymbolTable getInstance() {
        return instance;
    }

    public Symbol push(String name, Declaration decl) {
        Symbol symbol = new Symbol(currentScope, name, decl);
        table.add(symbol);
        return symbol;
    }

    public Symbol lookup(String name) {
        for (int i = table.size() - 1; i >= 0; i--) {
            Symbol symbol = table.get(i);
            if (symbol.name.equals(name)) {
                return symbol;
            }
        }
        return null;
    }

    public void beginScope() {
        currentScope++;
    }

    public void endScope() {
        if (currentScope <= 0) {
            throw new IllegalStateException("Scope level would drop below 0");
        }
        Iterator<Symbol> iterator = table.iterator();
        while (iterator.hasNext()) {
            Symbol s = iterator.next();
            if (s.getScope() == currentScope) {
                // System.out.println("removing " + s.name + "...");
                iterator.remove(); // Use iterator to remove the current element
            }
        }
        currentScope--;
    }

    public int getScope() {
        return currentScope;
    }

    private void initializeClasses() {
        inList = new ArrayList<>();
        outList = new ArrayList<>();
        readintList = new ArrayList<>();
        writeintList = new ArrayList<>();

        writeintList.add(Type.INT);

        inList.add(new Symbol(currentScope, "readint", new MethodDecl(Type.INT, readintList)));
        outList.add(new Symbol(currentScope, "writeint", new MethodDecl(Type.NULL, writeintList)));
        push("in", new ClassDecl("in", Type.IDENTIFIER, inList, 0));
        push("out", new ClassDecl("out", Type.IDENTIFIER, outList, 0));
    }

    public void prnt() {
        for (var t : table) {
            t.printall();
        }
    }

    public boolean insNumArgsTo(String meth, int numArgs) {
        if (lookup(meth) == null) {
            return false;
        } else {
            MethodDecl writeint = (MethodDecl) lookup(meth).getDecl();
            writeint.setNumArgs(numArgs); 
            return true;
        }
    }
}

class Symbol {
    int scope;
    String name;
    Declaration decl;

    public Symbol(int scope, String name, Declaration decl) {
        this.scope = scope;
        this.name = name;
        this.decl = decl;
    }

    public int getScope() {
        return this.scope;
    }

    public String getName() {
        return this.name;
    }

    public Declaration getDecl() {
        return this.decl;
    }

    public void printall() {
        System.out.println("name: " + name + " | scope: " + scope + " | decl: " + decl);
    }
}

class Declaration {
    Type type;

    public Declaration(Type type) {
        this.type = type;
    }
}

class VarDecl extends Declaration {

    int offset;

    public VarDecl(Type type, int offset) {
        super(type);
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}

class MethodDecl extends Declaration {
    int numArgs;
    ArrayList<Type> argList = new ArrayList<>();

    public MethodDecl(Type type, ArrayList<Type> argList) {
        super(type);
        this.numArgs = argList.size();
        this.argList = argList;
    }

    public int getNumAgrs() {
        return numArgs;
    }

    public void setNumArgs(int numArgs) {
        this.numArgs = numArgs;
    }

    public void addArg(Type s) {
        argList.add(s);
    }

    public Type getArgType(int i) {
        return argList.get(i);
    }

}

class ClassDecl extends Declaration {

    String name;
    ClassDecl inheritsFrom;
    ArrayList<Symbol> methList = new ArrayList<Symbol>();
    int localVars;

    public ClassDecl(String name, Type type, ArrayList<Symbol> methList, int localVars) {
        super(type);
        this.methList = methList;
        this.name = name;
        this.localVars = localVars;
    }

    // Remember to use endscope() to remove class methods every time this is called!
    public void insertClassMethods() {
        SymbolTable table = SymbolTable.getInstance();
        table.beginScope();

        for (Symbol method : methList) {
            table.push(method.name, method.decl);
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<Symbol> getMethList() {
        return methList;
    }

    public void addMeth(Symbol s) {
        methList.add(s);
    }

    public void setInheritsFrom(ClassDecl inheritsFrom) {
        this.inheritsFrom = inheritsFrom;
    }

    public int getLocalVars() {
        return localVars;
    }

    public void incLocalVars() {
        ++this.localVars;
    }

    // public void extractClassMethods() {
    //     SymbolTable table = SymbolTable.getInstance();
    //     table.endScope();
    // }
}

