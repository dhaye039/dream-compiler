package cps450;

import java.util.HashMap;

public class Type {
    private static HashMap<String, Type> types = new HashMap<>();
    ClassDecl classDecl;

    public static final Type 
        ERROR = new Type("<error>"),
        INT = new Type("int"),
        BOOLEAN = new Type("boolean"),
        STRING = new Type("string"),
        IDENTIFIER = new Type("id"),
        NULL = new Type("null");

    protected String name;

    protected Type(String name) {
        this.name = name;
    }

    // Method to create a new Type instance and store it in the types map
    public static Type createType(ClassDecl decl) {
        Type newType = new Type(decl.getName()); // Create a new Type instance with the class name
        newType.classDecl = decl; // Store a reference to the class declaration
        types.put(decl.getName(), newType); // Store the Type instance in the types map using the class name as the key
        return newType;
    }

    // Method to get the Type instance from the types map
    public static Type getTypeForName(String className) {
        return types.get(className); // Return the Type instance from the types map, or null if not found
    }

    public ClassDecl getClassDecl() {
        return classDecl;
    }
}
