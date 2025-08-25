package cps450;
import java.util.*;
import org.antlr.v4.runtime.ParserRuleContext;
import cps450.DreamParser.Expression_listContext;
import cps450.DreamParser.Var_declContext;

public class SemanticChecker extends DreamBaseListener {
    private SymbolTable table = SymbolTable.getInstance();
    private String filename;
    private int sem_errno = 0;
    private boolean insMeths = false;
    private String CurrIdName = null;
    private String CurrClassName = null;
    private int idHit = 0;
    private boolean isFirst = true;
    private boolean inCall = false;

    public SemanticChecker(String filename) {
        this.filename = filename;
    }

    private void reportError(ParserRuleContext node, String msg) {
        int line = node.getStart().getLine();
        int col = node.getStart().getCharPositionInLine();
        System.out.println(filename + ":" + line + "," + col + ":" + msg);
        ++sem_errno;
    }

    public int getNumberOfSemanticErrors() {
        return sem_errno;
    }

    @Override
    public void enterClass_decl(DreamParser.Class_declContext node) {
        String className = node.IDENTIFER(0).getText();
        CurrClassName = className;
        // String inheritsFrom = (node.IDENTIFER().size() == 3) ? node.IDENTIFER(1).getText() : null;
        ArrayList<Symbol> methList = new ArrayList<>();

        ClassDecl classDecl = new ClassDecl(className, new Type("id"), methList, 0);
        Type classType = Type.createType(classDecl);

        table.push(className, classDecl);
        
        table.beginScope();
    }

    @Override
    public void exitClass_decl(DreamParser.Class_declContext node) {
        table.endScope();
        CurrClassName = null;
    }

    @Override
    public void exitVar_decl(DreamParser.Var_declContext node) {
        // Perform semantic checks for variable declaration
        String var = node.IDENTIFER().getText();
        Type type;

        if (node.type() == null && node.ASSIGNMENT() == null) {
            reportError(node, "Undefined Type.");
            type = new Type("<error>");
        } else if (node.type() == null && node.ASSIGNMENT() != null) {
            reportError(node, "Feature unsupported: Variable Initializer Expression");
            type = new Type("<error>");
        } else if (node.type() != null && node.ASSIGNMENT() == null) {
            type = new Type(node.type().t.name);
            // System.out.println(type.name);

            if (table.lookup(var) != null && table.getScope() == table.lookup(var).getScope()) {
                reportError(node, "Variable '" + var + "' is already declared in the current scope.");
            } else {
                var parent = node.getParent();
                int numChild = parent.getChildCount();
                String name = node.getText();
                int index = 0;
                int j = 1;

                for (int i = 0; i < numChild; ++i) {
                    if (parent.getChild(i) instanceof Var_declContext) {
                        if (name.equals(node.getParent().getChild(i).getText()))
                            index = j * -1;
                        ++j;
                    }
                }
                table.push(var, new VarDecl(type, index));
                Type.getTypeForName(CurrClassName).classDecl.incLocalVars();
            }
        } else {
            reportError(node, "Feature unsupported: Variable Initializer Expression");
            type = new Type("<error>");
        }
    }

    @Override
    public void enterMethod_decl(DreamParser.Method_declContext node) {
        String methName = node.IDENTIFER().get(0).getText();
        ArrayList<Type> argList = new ArrayList<>();
        
        if (node.type() == null) 
            table.push(methName, new MethodDecl(Type.NULL, argList));
        else {
            String type = node.type().children.get(0).getText();
            table.push(methName, new MethodDecl(new Type(type), argList));
        }
        table.beginScope();
    }

    @Override
    public void exitMethod_decl(DreamParser.Method_declContext node) {
        String className = node.getParent().getChild(1).getText();
        String methName = node.IDENTIFER().get(0).getText();
        Symbol s = table.lookup(methName);
        Type.getTypeForName(className).getClassDecl().addMeth(s);
        table.endScope();
    }

    @Override
    public void exitArgument_decl_list(DreamParser.Argument_decl_listContext node) {
        int numArgs = 0;
        String methName = node.parent.getChild(0).getText();
        numArgs = node.getChildCount();            
        for (int i = 0; i < numArgs; i++)
            if (node.children.get(i).getText().equals(";"))
                --numArgs;
        table.insNumArgsTo(methName, numArgs);
        for (int i = 0; i < numArgs; i++) {
            MethodDecl method = (MethodDecl) table.lookup(methName).getDecl();
            method.addArg(node.argument_decl(i).type().t);
        }
    }

    @Override
    public void exitArgument_decl(DreamParser.Argument_declContext node) {
        String var = node.IDENTIFER().getText();
        Type type = new Type(node.type().t.name);
        
        if (table.lookup(var) != null && table.getScope() == table.lookup(var).getScope()) {
            reportError(node, "Variable '" + var + "' is already declared in the current scope.");
        } else {
            int childIdx = 0;
            int offset = 0;

            for (int i = 0; i < node.parent.getChildCount(); ++i) { // idk T.T
                String nodeID = node.getText();
                String childID = node.parent.getChild(i).getText();
                if (nodeID.equals(childID)) {
                    if (i == 0) {
                        offset = 1;
                    } else {
                        childIdx = i;
                    }
                }
            }
            if (childIdx != 0 && childIdx != 1) {
                offset = (childIdx / 2) + 1; // get rid of the ';' accounted for in getChildCount()
            }
            table.push(var, new VarDecl(type, offset + 1));
        }
    }

    // type
    @Override
    public void exitTypeInt(DreamParser.TypeIntContext node) {
        node.t = new Type(node.INT().getText());
    }

    @Override
    public void exitTypeStr(DreamParser.TypeStrContext node) {
        node.t = new Type(node.STRING().getText());
        // node.t = new Type("<error>");
        reportError(node, "Feature unsupported: Strings");
    }

    @Override
    public void exitTypeBool(DreamParser.TypeBoolContext node) {
        node.t = new Type(node.BOOLEAN().getText());
    }

    @Override
    public void exitTypeId(DreamParser.TypeIdContext node) {
        Type type = Type.getTypeForName(node.getText());

        if (type != null) {
            node.t = type;
        } else {
            node.t = new Type("<error>");
            reportError(node, "Use of undeclared class '" + node.getText() + "'.");
        }
    }

    @Override
    public void exitTypeArr(DreamParser.TypeArrContext node) {
        node.t = new Type("<error>");
        reportError(node, "Feature unsupported: Array Types");
    }

    // statements
    @Override
    public void exitAssignment_stmt(DreamParser.Assignment_stmtContext node) {
        // Perform semantic checks for assignment statement
        String var = node.IDENTIFER().getText();
        // int i = (node.children.size() == 3) ? 0 : 1;

        if (table.lookup(var) == null) {
            reportError(node, "Use of undeclared variable '" + var + "'.");
        } else {
            Symbol s = table.lookup(var);

            if (node.expression().size() == 1) {
                String firstType = s.getDecl().type.name;
                String exprType = node.expression(0).t.name;

                if (!exprType.equals(firstType) && !exprType.equals("<error>")) {
                    reportError(node, "Type mismatch in assignment for variable '" + var + "'. Cannot assign " + exprType + " to " + firstType + "." );
                }
            } else { // array indexing?
                // reportError(node, "Feature unsupported: Array Indexing");
            }
            node.sym = s;
        }

        if (insMeths) {
            table.endScope();
            insMeths = false;
        }
        CurrIdName = null;
    }

    @Override
    public void exitIf_stmt(DreamParser.If_stmtContext node) {
        if (!node.expression().t.name.equals("boolean")) {
            reportError(node, "if statement requires a boolean but type of '" + node.expression().getText() + "' is a(n) '" + node.expression().t.name + "'");
        }
    }

    @Override
    public void exitLoop_stmt(DreamParser.Loop_stmtContext node) {
        if (!node.expression().t.name.equals("boolean")) {
            reportError(node, "while loop requires a boolean but type of '" + node.expression().getText() + "' is a(n) '" + node.expression().t.name + "'");
        }
    }

    @Override
    public void enterCall_stmt(DreamParser.Call_stmtContext node) {
        inCall = true;
    }

    @Override
    public void exitCall_stmt(DreamParser.Call_stmtContext node) {
        String methodName = node.IDENTIFER().getText();

        if (node.expression() != null) { // if the call stmt is a "expr.meth()"
            if (node.expression().t.equals(Type.IDENTIFIER)) {
                String objectName = node.expression().children.get(0).getText();
                Symbol object = table.lookup(objectName);
                ((ClassDecl) object.getDecl()).insertClassMethods();
                insMeths = true;
            } else {
                String objectName = CurrIdName;
                Symbol object = table.lookup(objectName);
                String objTypeStr = object.decl.type.name;
                Type objType = Type.getTypeForName(objTypeStr);
                if (objType != null) {
                    ClassDecl classDecl = objType.getClassDecl();
                    classDecl.insertClassMethods();
                    insMeths = true;
                } else {
                    reportError(node, objectName + " is not a valid type");
                }
            }
        }

        if (table.lookup(methodName) == null) {
            reportError(node, "Use of undeclared method '" + methodName + "'");
        } else {
            Expression_listContext exp_list = node.expression_list();

            if (exp_list != null) {
                MethodDecl meth = (MethodDecl) table.lookup(methodName).getDecl();
                int expectedNumArgs = meth.getNumAgrs();
                int numArgs = exp_list.children.size();
        
                for (int i = 0; i < numArgs; ++i) {
                    if (exp_list.children.get(i).getText().equals(","))
                        --numArgs;
                }
    
                if (expectedNumArgs != numArgs)
                    reportError(node, "Incorrect number of parameters in " + methodName + ", expecting " + expectedNumArgs + " got " + numArgs);
                else {
                    for (int i = 0; i < numArgs; i++) {
                        Type t = meth.getArgType(i);
                        if (!t.name.equals(exp_list.expression(i).t.name)) {
                            reportError(node, "Incorrect parameter type for '" + exp_list.expression(i).getText() + "' in " + methodName + ", expecting " + t.name + " got " + exp_list.expression(i).t.name);
                        }
                    }
                }
            }
        }

        if (insMeths) {
            table.endScope();
            insMeths = false;
        }
        CurrIdName = null;
        inCall = false;
        isFirst = true;
    }

    // expressions
    @Override
    public void exitExpression(DreamParser.ExpressionContext node) {
        node.t = node.or_exp().t;
    }

    @Override
    public void exitOr_exp(DreamParser.Or_expContext node) {
        Type firstType = node.and_exp(0).t;

        // Check the type of all subsequent and_exp and ensure they match the firstType
        for (int i = 1; i < node.and_exp().size(); i++) {
            if (!node.and_exp(i).t.name.equals(firstType.name) && !node.and_exp(i).t.name.equals("<error>")) {
                reportError(node, "Incorrect type for 'or': requires booleans, given " + node.and_exp(i - 1).t.name + " and " + node.and_exp(i).t.name + ".");
                node.t = new Type("<error>");
            }
        }
        // Set the resulting type of the or_exp
        node.t = firstType;
    }

    @Override
    public void exitAnd_exp(DreamParser.And_expContext node) {
        Type firstType = node.relation_exp(0).t;

        // Check the type of all subsequent relation_exp and ensure they match the firstType
        for (int i = 1; i < node.relation_exp().size(); i++) {
            if (!node.relation_exp(i).t.name.equals(firstType.name) && !node.relation_exp(i).t.name.equals("<error>")) {
                reportError(node, "Incorrect type for 'and': requires booleans, given " + node.relation_exp(i - 1).t.name + " and " + node.relation_exp(i).t.name + ".");
                node.t = new Type("<error>");
            }
        }
        // Set the resulting type of the and_exp
        node.t = firstType;
    }

    @Override
    public void exitRelation_exp(DreamParser.Relation_expContext node) {
        Type firstType = node.concat_exp(0).t;
        String op = "";
        if (node.no_assoc_op() != null) {
            op = node.no_assoc_op().getText();
            node.t = new Type("boolean");
        } else {
            // Set the resulting type of the relation_exp
            node.t = firstType;
        }

        // Check the type of all subsequent concat_exp and ensure they match the firstType
        for (int i = 1; i < node.concat_exp().size(); i++) {
      
            if (op.equals(">=") || op.equals("<=")) {
                if (node.concat_exp(i).t.name.equals("boolean")) {
                    reportError(node, "Incorrect type for '" + op + "': requires strings or ints, given boolean.");
                }
            } else if (!node.concat_exp(i).t.name.equals(firstType.name) && !node.concat_exp(i).t.name.equals("<error>")) {
                if (op.equals("=")) {
                    reportError(node, "Incorrect type for '=': requires strings, booleans, or ints, given " + node.concat_exp(i - 1).t.name + " and " + node.concat_exp(i).t.name + ".");
                } else {
                    reportError(node, "Incorrect type for '" + op + "': requires strings, or ints, given " + node.concat_exp(i - 1).t.name + " and " + node.concat_exp(i).t.name + ".");
                }
                node.t = new Type("<error>");
            }
        }
    }

    @Override
    public void exitConcat_exp(DreamParser.Concat_expContext node) {
        Type firstType = node.addsub_exp(0).t;

        // Check the type of all subsequent addsub_exp and ensure they match the firstType
        for (int i = 1; i < node.addsub_exp().size(); i++) {
            if (!node.addsub_exp(i).t.name.equals(firstType.name) && node.addsub_exp(i).t.name.equals("<error>")) {
                reportError(node, "Incorrect type for '&': requires strings, given " + node.addsub_exp(i - 1).t.name + " and " + node.addsub_exp(i).t.name + ".");
                node.t = new Type("<error>");
            } else if (!firstType.name.equals("string")) {
                reportError(node, "Incorrect type for '&': requires strings, given " + node.addsub_exp(i - 1).t.name + " and " + node.addsub_exp(i).t.name + ".");
                node.t = new Type("<error>");
            }
        }
        // Set the resulting type of the concat_exp
        node.t = firstType;
    }

    @Override
    public void exitAddsub_exp(DreamParser.Addsub_expContext node) {
        Type firstType = node.muldiv_exp(0).t;

        // Check the type of all subsequent muldiv_exp and ensure they match the firstType
        for (int i = 1; i < node.muldiv_exp().size(); i++) {
            if (!node.muldiv_exp(i).t.name.equals(firstType.name) && !node.muldiv_exp(i).t.name.equals("<error>")) {
                reportError(node, "Incorrect type for '" + node.getChild(i+1).getText() + "' (binary op): requires ints, given " + node.muldiv_exp(i - 1).t.name + " and " + node.muldiv_exp(i).t.name + ".");
                node.t = new Type("<error>");
            } else if (!firstType.name.equals("int")) {
                reportError(node, "Incorrect type for '" + node.getChild(i+1).getText() + "' (binary op): requires ints, given " + node.muldiv_exp(i - 1).t.name + " and " + node.muldiv_exp(i).t.name + ".");
                node.t = new Type("<error>");
            }
        }
        // Set the resulting type of the addsub_exp
        node.t = firstType;
    }

    @Override
    public void exitMuldiv_exp(DreamParser.Muldiv_expContext node) {
        Type firstType = node.unary_exp(0).t;


        // Check the type of all subsequent unary_exp and ensure they match the firstType
        for (int i = 1; i < node.unary_exp().size(); i++) {
            if (!node.unary_exp(i).t.name.equals(firstType.name) && !node.unary_exp(i).t.name.equals("<error>")) {
                reportError(node, "Incorrect type for '" + node.getChild(1).getText() + "': requires ints, given " + node.unary_exp(i - 1).t.name + " and " + node.unary_exp(i).t.name + ".");
                node.t = new Type("<error>");
            }
        }
        // Set the resulting type of the muldiv_exp
        node.t = firstType;
    }

    @Override
    public void exitUnary_exp(DreamParser.Unary_expContext node) {
        if (node.unary_op() != null) {
            if (node.unary_op().getText().equals("not")) {
                if (!node.value().t.name.equals("boolean")) {
                    reportError(node, "Incorrect type for '" + node.unary_op().getText() + "' (unary op): requires a boolean, given " + node.value().t.name + ".");
                    node.t = new Type("<error>");
                } else {
                    node.t = node.value().t;
                }
            } else {
                if (!node.value().t.name.equals("int")) {
                    reportError(node, "Incorrect type for '" + node.unary_op().getText() + "' (unary op): requires ints, given " + node.value().t.name + ".");
                    node.t = new Type("<error>");
                } else {
                    node.t = node.value().t;
                }
            }
        } else {
            node.t = node.value().t;
        }
    }

    @Override
    public void enterValue(DreamParser.ValueContext node) {
        if (idHit == 0 && isFirst) {
            if (inCall) {
                isFirst = false;
            }
            if (node.id().children.size() == 3) {
                CurrIdName = node.id().children.get(1).getText();
            } else {
                CurrIdName = node.id().children.get(0).getText();
            }
        }
        ++idHit;
    }

    @Override
    public void exitValue(DreamParser.ValueContext node) {
        String idName;
        boolean error = false;

        if (node.id().children.size() == 3) {
            idName = node.id().children.get(1).getText();
        } else {
            idName = node.id().children.get(0).getText();
        }

        if (idName.equals("new")) {
            Type type = Type.getTypeForName(node.id().children.get(1).getText());
            if (type == null) {
                node.t = new Type("<error>");
                error = true;
            } 
        }

        Symbol symbol = table.lookup(idName); // Look up the symbol in the symbol table
        int children = node.children.size();
        if (children == 2 && !node.call_tail().getChild(0).getText().equals("(")) {
            String methName = node.call_tail().getChild(0).getChild(1).getText();
            if (table.lookup(methName) == null) {
                node.t = new Type("<error>");
                error = true;
            } else {
                symbol = table.lookup(methName);
            }
        }
        if (!error) {
            if (symbol != null) {
                node.t = symbol.getDecl().type; // Set the type of the value node to the type of the symbol
            } else {
                // Handle the case where the symbol is not found in the symbol table
                if (node.id().t != null) {
                    if (children == 2 && !node.call_tail().getChild(0).getText().equals("(")) {
                        node.t = node.call_tail().t;
                    } else {
                        node.t = node.id().t;
                    }
                } else {
                    reportError(node, "Use of undeclared variable '" + node.getText() + "'.");
                    node.t = new Type("<error>");
                }
            }
        }
        --idHit;
    }

    @Override
    public void exitExpr_tail(DreamParser.Expr_tailContext node) {
        node.call_tail();
        String id;

        if (node.id().children.size() == 3) {
            id = node.id().children.get(1).getText();
        } else {
            id = node.id().children.get(0).getText();
        }

        if (table.lookup(id) == null) {
            reportError(node, "Use of undeclared method '" + id + "'.");
        }
    }

    @Override
    public void exitParCall(DreamParser.ParCallContext node) {
        String methName;
        if (node.parent.getChild(0).getText().equals(".")) {
            methName = node.parent.getChild(1).getText();
            String objectName = CurrIdName;

            if (objectName.startsWith("new")) {
                String className = objectName.substring(3);
                Type objType = Type.getTypeForName(className);
                if (objType != null) {
                    ClassDecl classDecl = objType.getClassDecl();
                    classDecl.insertClassMethods();
                    insMeths = true;
                } else {
                    if (table.lookup(objectName) == null)
                        reportError(node, objectName + " is not a valid type");
                }
            } else {
                Symbol object = table.lookup(objectName);
                if (object.getDecl() instanceof VarDecl) {
                    String objTypeStr = object.decl.type.name;
                    Type objType = Type.getTypeForName(objTypeStr);
                    if (objType != null) {
                        ClassDecl classDecl = objType.getClassDecl();
                        classDecl.insertClassMethods();
                        insMeths = true;
                    } else {
                        if (table.lookup(objectName) == null)
                            reportError(node, objectName + " is not a valid type");
                    }
                } else {
                    ((ClassDecl) object.getDecl()).insertClassMethods();
                }
            }
            insMeths = true;
        } else {
            methName = node.parent.getChild(0).getText();
        }
        if (table.lookup(methName) != null) {
            Expression_listContext exp_list = node.expression_list();

            if (exp_list != null) {
                MethodDecl meth = (MethodDecl) table.lookup(methName).getDecl();
                int expectedNumArgs = meth.getNumAgrs();
                int numArgs = exp_list.children.size();
        
                for (int i = 0; i < numArgs; ++i)
                    if (exp_list.children.get(i).getText().equals(","))
                        --numArgs;
        
                if (expectedNumArgs != numArgs)
                    reportError(node, "Incorrect number of parameters in " + methName + ", expecting " + expectedNumArgs + " got " + numArgs);
                else {
                    for (int i = 0; i < numArgs; ++i) {
                        Type t = meth.getArgType(i);
                        if (!t.name.equals(exp_list.expression(i).t.name)) {
                            reportError(node, "Incorrect parameter type for " + exp_list.expression(i).getText() + ", expecting " + t.name + " got " + exp_list.expression(i).t.name);
                        }
                    }
                }
            }
        }
    }

    // id
    @Override
    public void exitIdType(DreamParser.IdTypeContext node) {
        String id = node.IDENTIFER().getText();

        if (!node.parent.getChild(0).getText().equals(".")) {
            if (table.lookup(id) == null) {
                reportError(node, "Use of undeclared method '" + node.getText() + "'.");
                node.t = new Type("<error>");
            } else {
                node.t = new Type("id");
                node.symbol = table.lookup(id);
            }
        }
    }

    @Override
    public void exitParType(DreamParser.ParTypeContext node) {
        node.t = node.expression().t;
    }

    @Override
    public void exitStrType(DreamParser.StrTypeContext node) {
        // node.t = new Type("string");
        node.t = new Type("<error>");
        reportError(node, "Feature unsupported: String Type");
    }

    @Override
    public void exitIntType(DreamParser.IntTypeContext node) {
        node.t = new Type("int");
    }

    @Override
    public void exitBoolType(DreamParser.BoolTypeContext node) {
        node.t = new Type("boolean");
    }

    @Override
    public void exitNullType(DreamParser.NullTypeContext node) {
        // node.t = new Type("null");
        node.t = new Type("<error>");
        reportError(node, "Feature unsupported: Null Type");
    }

    @Override
    public void exitMeType(DreamParser.MeTypeContext node) {
        // node.t = Type.getTypeForName(CurrClassName);
        node.t = new Type("<error>");
        reportError(node, "Feature unsupported: Me Type");
    }

    @Override
    public void exitNewType(DreamParser.NewTypeContext node) {
        // node.t = node.type().t;
        node.t = new Type("<error>");
        reportError(node, "Feature unsupported: New Type");

    }
}



