package cps450;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ParserRuleContext;

import cps450.DreamParser.Addsub_expContext;
import cps450.DreamParser.And_expContext;
import cps450.DreamParser.Assignment_stmtContext;
import cps450.DreamParser.BoolTypeContext;
import cps450.DreamParser.Call_stmtContext;
import cps450.DreamParser.Call_tailContext;
import cps450.DreamParser.Class_declContext;
import cps450.DreamParser.Concat_expContext;
import cps450.DreamParser.ExpCallContext;
import cps450.DreamParser.ExpressionContext;
import cps450.DreamParser.IdContext;
import cps450.DreamParser.IdTypeContext;
import cps450.DreamParser.If_stmtContext;
import cps450.DreamParser.IntTypeContext;
import cps450.DreamParser.Loop_stmtContext;
import cps450.DreamParser.Method_declContext;
import cps450.DreamParser.Muldiv_expContext;
import cps450.DreamParser.NewTypeContext;
import cps450.DreamParser.Or_expContext;
import cps450.DreamParser.ParCallContext;
import cps450.DreamParser.ParTypeContext;
import cps450.DreamParser.Relation_expContext;
import cps450.DreamParser.StartContext;
import cps450.DreamParser.StatementContext;
import cps450.DreamParser.Statement_listContext;
import cps450.DreamParser.TypeContext;
import cps450.DreamParser.Unary_expContext;
import cps450.DreamParser.ValueContext;
import cps450.DreamParser.Var_declContext;

public class CodeGen {
    private List<TargetInstruction> targetInstructions;
    boolean inData = false;
    boolean inText = false;
    boolean hasRet = false;
    int ifLabelCounter = 0;
    int activeIfs = 0;
    int loopLabelCounter = 0;
    int activeLoops = 0;
    String op = "";
    String retName = "";

    private static final int GLOBAL_SCOPE = 0;
    private static final int CLASS_SCOPE = 1;
    private static final int LOCAL_SCOPE = 2;

    public CodeGen() {
        targetInstructions = new ArrayList<>();
    }

    public void traverse(StartContext node) {
        List<Class_declContext> classes = node.class_decl();

        for (Class_declContext class_decl : classes) {
            List<ParseTree> childList = class_decl.children;
            for (int i = 0; i < childList.size(); ++i) {
                if (childList.get(i) instanceof Var_declContext) {
                    if (!inData)
                        emit(".data");
                        inData = true;
                        inText = false;
                    visitVar_Decl((Var_declContext)childList.get(i));
                } else if (childList.get(i) instanceof Method_declContext) {
                    if (!inText)
                        emit(".text");
                        inData = false;
                        inText = true;
                    visitMethod_Decl((Method_declContext)childList.get(i));
                }
            }
            emit(".global main\nmain:");
            emit("call", "start");
            emitComment("Line " + node.getStop().getLine() + ": Exit call");
            emit("pushl", "$0");
            emit("call", "exit");
        }
    }

    private void visitVar_Decl(Var_declContext node) {
        String varName = node.IDENTIFER().getText();
        emitComment("Line " + node.getStart().getLine() + ": " + node.IDENTIFER().getText()+node.COLON().getText()+node.type().getText());
        emit(".comm", "_" + varName, "4, 4");
	}

    private void visitMethod_Decl(Method_declContext node) {
        String varName = node.IDENTIFER(0).getText();
        int numLocs = 0;

        if (node.type() != null) {
            hasRet = true;
            retName = node.IDENTIFER(0).getText();
        }
        for (var child : node.children)
            if (child instanceof Var_declContext)
                ++numLocs;

        emit(".global " + varName);
        if (node.argument_decl_list() != null || numLocs != 0) {
            if (!varName.equals("start")) varName = "_" + varName; 
            emit(varName + ":" + "\n");
            emit("pushl", "%ebp");
            emit("movl", "%esp", "%ebp");
            if (hasRet) ++numLocs;
            for (int i = 0; i < numLocs; ++i) {
                emit("pushl", "$0");
            }
            visitStmtList(node.statement_list());
            emit("","");
            emit("movl", "-4(%ebp)", "%eax");
            emit("movl","%ebp", "%esp");
            emit("popl", "%ebp");
        } else {
            emit(varName + ":");
            visitStmtList(node.statement_list());
        }
        
        emit("ret", " ");
        hasRet = false;
        retName = "";
	}

    private void visitStmtList(Statement_listContext node) {
        for (var child : node.children) {
            if (child instanceof StatementContext){
                visitStmt((StatementContext)child);
            }
        }
    }

    private void visitStmt(StatementContext node) {
        for (var child : node.children) {
            if (child instanceof Assignment_stmtContext) {
                Assignment_stmtContext c =  (Assignment_stmtContext) child;
                emitComment(
                    "Line " 
                    + node.getStart().getLine() 
                    + ": " 
                    + c.IDENTIFER().getText()
                    + c.ASSIGNMENT().getText()
                    + c.expression(0).getText()
                );
                visitAsmtStmt((Assignment_stmtContext)child);
            } else if (child instanceof If_stmtContext) {
                If_stmtContext ifStmt = (If_stmtContext) child;
                emitComment(
                    "Line "
                    + node.getStart().getLine() 
                    + ": "
                    + ifStmt.expression().getText()
                );
                visitIfStmt((If_stmtContext)child);
            } else if (child instanceof Loop_stmtContext) {
                Loop_stmtContext loop = (Loop_stmtContext) child;
                emitComment(
                    "Line "
                    + node.getStart().getLine() 
                    + ": "
                    + loop.expression().getText()
                );
                visitLoop((Loop_stmtContext)child);
            } else if (child instanceof Call_stmtContext) {
                emitComment(
                    "Line "
                    + node.getStart().getLine() 
                    + ": "
                    + node.getText()
                );
                visitCallStmt((Call_stmtContext)child);
            }
        }
    }

    private void visitAsmtStmt(Assignment_stmtContext node) {
        int exp = (node.getChildCount() == 3) ? 0 : 1;
        visitExpr((ExpressionContext) node.expression(exp));
        
        if (hasRet) {
            if (node.sym.scope == LOCAL_SCOPE) {
                int offset = ((VarDecl)node.sym.decl).getOffset();
                emit("popl", "%eax");
                if (offset < 0) --offset; 
                emit("movl","%eax", (offset*4) + "(%ebp)");
            } else if (node.sym.name.equals(retName)) {
                emit("popl", "-4(%ebp)");
            } else {
                emit("popl", "_" + node.IDENTIFER().getText());
            }
        } else {
            if (node.sym.scope == LOCAL_SCOPE) {
                int offset = ((VarDecl)node.sym.decl).getOffset();
                emit("popl", offset*4 + "(%ebp)");
            } else {
                emit("popl", "_" + node.IDENTIFER().getText());
            }
        }
    }

    private void visitIfStmt(If_stmtContext node) {
        activeIfs++;
        int label = ifLabelCounter + activeIfs;
        String op = visitExpr(node.expression());

        emit("popl", "%eax");
        emit("cmpl", "$0", "%eax");
        emit(op, "_doif" + (label));
        emit("jmp", "_else" + (label));
        emit("_doif" + (label) + ":");
        visitStmtList(node.statement_list(0));

        if (node.statement_list(1) != null) {
            emit("jmp", "_endif" + (label));
            emit("_else" + (label) + ":");
            visitStmtList(node.statement_list(1));
            emit("_endif" + label + ":");
            activeIfs--;
        } else {
            emit("_else" + (label) + ":");
            emit("_endif" + (label) + ":");
            activeIfs--;
        }
        ifLabelCounter++;   
    }

    private void visitLoop(Loop_stmtContext node) {
        activeLoops++;
        int label = loopLabelCounter + activeLoops;

        emit("_while" + label + ":");
        String op = visitExpr(node.expression());
        emit("popl", "%eax");
        emit("cmpl", "$0", "%eax");
        emit(op, "_startwhilebody" + label);
        emit("jmp", "_endwhile" + label);
        emit("_startwhilebody" + label + ":");
        visitStmtList(node.statement_list());
        emit("jmp", "_while" + label);
        emit("_endwhile" + label + ":");
        activeLoops--;
        loopLabelCounter++;
    }

    private void visitCallStmt(Call_stmtContext node) {
        int numParam = 0;
        // int numParam = (hasRet) ? 1 : 0;
        if (node.expression_list() != null) {
            for (int i = node.expression_list().expression().size() - 1; i >= 0; --i) {
                visitExpr(node.expression_list().expression(i));
                ++numParam;
            }
        }
        String meth = node.IDENTIFER().getText();
        if (meth.equals("writeint") || meth.equals("readint")) {
            emit("call", meth);
        } else {
            emit("call", "_" + meth);
        }
        if (numParam > 0)
            emit("addl", "$" + 4 * numParam, "%esp");        
    }

    /* This function recursively evaluates expression nodes.
     *
     * Because of the way my grammar is set up, this seemed the 
     * easiest way to implement expression evaluation, but it's
     * a little confusing. Essentially, it always evaluates the 
     * first child, then it checks the remaining children by
     * evaluating and pushing the next expression, then performing
     * the next operation. 
     * 
     * Note: My grammar accounts for operator precedence. Also,
     * unary expressions are handled differently, but since they
     * are still expressions, I left them in this function.
     */ 
    private String visitExpr(ParserRuleContext node) {
        // get number of children the node has minus 1 (always evaluates first child)
        int childsLeft = node.getChildCount() - 1;

        // index variable
        int idx;

        op = "jne";

        if (node instanceof ExpressionContext) {
            op = visitExpr((Or_expContext)node.getChild(0));
        } else if (node instanceof Or_expContext) {
            // Evaluate first child
            op = visitExpr((And_expContext)node.getChild(0));

            while (childsLeft != 0) {
                idx = node.getChildCount() - childsLeft;
                op = visitExpr((And_expContext)node.getChild(idx + 1));
                emit("popl", "%ebx");
                emit("popl", "%eax");
                emit("orl ", "%ebx", "%eax");
                emit ("pushl", "%eax");
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof And_expContext) {
            // Evaluate first child
            op = visitExpr((Relation_expContext)node.getChild(0));

            while (childsLeft != 0) {
                idx = node.getChildCount() - childsLeft;
                op = visitExpr((Relation_expContext)node.getChild(idx + 1));
                emit("popl", "%ebx");
                emit("popl", "%eax");
                emit("andl", "%ebx", "%eax");
                emit ("pushl", "%eax");
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof Relation_expContext) {
            // Evaluate first child
            op = visitExpr((Concat_expContext)node.getChild(0));
            
            while (childsLeft != 0) {
                op = (((Relation_expContext)node).no_assoc_op().getText().equals("=")) ? "je" : "jne";

                idx = node.getChildCount() - childsLeft;
                op = visitExpr((Concat_expContext)node.getChild(idx + 1));
                visitRelExp((Relation_expContext)node);
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof Concat_expContext) { // Unsupported
            // Evaluate first child
            op = visitExpr((Addsub_expContext)node.getChild(0));

            while (childsLeft != 0) {
                idx = node.getChildCount() - childsLeft;
                op = visitExpr((Addsub_expContext)node.getChild(idx + 1));
                System.out.println("concat op");
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof Addsub_expContext) {
            String AddSub_op;
            
            // Evaluate first child
            op = visitExpr((Muldiv_expContext)node.getChild(0));
            
            while (childsLeft != 0) {
                idx = node.getChildCount() - childsLeft;
                op = visitExpr((Muldiv_expContext)node.getChild(idx + 1));
                AddSub_op = (node.getChild(idx).getText().equals("+")) ? "addl" : "subl";
                emit("popl", "%ebx");
                emit("popl", "%eax");
                emit(AddSub_op, "%ebx", "%eax");
                emit ("pushl", "%eax");
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof Muldiv_expContext) {
            // Evaluate first child
            op = visitExpr((Unary_expContext)node.getChild(0));

            while (childsLeft != 0) {
                idx = node.getChildCount() - childsLeft;
                op = visitExpr((Unary_expContext)node.getChild(idx + 1));
                if (node.getChild(idx).getText().equals("*")) {
                    emit("popl", "%ebx");
                    emit("popl", "%eax");
                    emit("imull", "%ebx", "%eax");
                    emit ("pushl", "%eax");
                } else {
                    emit("popl", "%ebx");
                    emit("popl", "%eax");
                    emit("movl", "$0", "%edx");
                    emit("idivl", "%ebx");
                    emit ("pushl", "%eax");
                }                
                childsLeft = childsLeft - 2;
            }
        } else if (node instanceof Unary_expContext) {
            if (node.getChildCount() == 2) {
                op = visitExpr((ValueContext)node.getChild(1));
                if (node.getChild(0).getText().equals("not")) {
                    emit("popl", "%eax");
                    emit("xorl", "$1", "%eax");
                    emit ("pushl", "%eax");
                } else if (node.getChild(0).getText().equals("-")) {
                    emit("popl", "%eax");
                    emit("negl", "%eax");
                    emit ("pushl", "%eax");
                }
            } else {
                op = visitExpr((ValueContext)node.getChild(0));
            }
        } else if (node instanceof ValueContext) {
            if (node.getChildCount() > 1) {
                visitCall((Call_tailContext)node.getChild(1));
            } else {
                visitId(((IdContext)node.getChild(0)));
            }
        }
    
        return op;
    }

    private void visitRelExp(Relation_expContext node) {
        String rel_op = "";
        switch (node.no_assoc_op().getText()) {
            case "=":
                rel_op = "sete";
                break;
            case "<":
                rel_op = "setl";
                break;
            case ">":
                rel_op = "setg";
                break;
            case "<=":
                rel_op = "setle";
                break;
            case ">=":
                rel_op = "setge";
                break;
        }
        emit("popl", "%ebx");
        emit("popl", "%eax");
        emit("cmpl", "%ebx", "%eax");
        emit(rel_op, "%al");
        emit("movzbl", "%al", "%eax");
        emit ("pushl", "%eax");
    }

    private void visitId(IdContext node) {
        if (node instanceof IdTypeContext) {
            Symbol sym = node.symbol;
            if (sym.decl instanceof MethodDecl) {
                emit("pushl", "-4(%ebp)");
            } else if (sym.getScope() == CLASS_SCOPE) {
                String name = "_" + node.getChild(0).getText();
                emit("pushl", name);
            } else if (sym.getScope() == LOCAL_SCOPE) {
                int offset = ((VarDecl)sym.decl).getOffset() * 4;
                if (hasRet && offset < 0) offset = offset - 4;
                emit("pushl", offset + "(%ebp)");
            }
        } else if (node instanceof ParTypeContext) {
            visitExpr((ExpressionContext)node.getChild(1));
        } else if (node instanceof IntTypeContext) {
            emit("pushl", "$" + node.getChild(0).getText());
        } else if (node instanceof BoolTypeContext) {
            String boolVal = (node.getChild(0).getText().equals("true")) ? "1" : "0";
            emit("pushl", "$" + boolVal);
        } else if (node instanceof NewTypeContext) {
            NewTypeContext newType = (NewTypeContext) node;
            Type type = newType.t;
            int numLocVars = type.classDecl.getLocalVars() * 4;
            emit("pushl", "$" + (8 + numLocVars));
            emit("pushl", "$1");
            emit("call", "calloc");
            emit("addl", "$8", "%esp");
            emit("pushl", "%eax");
        }
    }

    private void visitCall(Call_tailContext node) {
        if (node instanceof ParCallContext) {
            ParCallContext child = (ParCallContext) node;

            int numParam = 0;
            for (int i = child.expression_list().expression().size() - 1; i >= 0; --i) {
                visitExpr(child.expression_list().expression(i));
                ++numParam;
            }
            String methName = node.parent.getChild(0).getText();
            emit("call", "_" + methName);

            // if (hasRet) ++numParam;
            if (numParam > 0) emit("addl", "$" + numParam * 4 ,"%esp");
            emit("pushl", "%eax");
        } else {
            ExpCallContext child = (ExpCallContext) node;
            String meth = child.expr_tail().id().getText();
            if (!meth.equals("writeint") & !meth.equals("readint")) {
                meth = "_" + meth;
            }
            emit("call", meth);
            emit("pushl", "%eax");
        }
    }

    // private void visitId(IdContext node) {}
    
    // private void visitAsmtStmt(AsmtStmtContext node) {
    //     if (node.children.size() == 1) {
    //         // emit("pushl", "$" + node.getChild(0).getText());
    //     }


    //     // Assignment_stmtContext asmt = (Assignment_stmtContext) stmt;
    //     // // AsmtStmtContext asmt = (Assignment_stmtContext) stmt;
    //     // emit("pushl", "$" + asmt.expression().getText());
    // }

    

    private void emit(String label) {
        TargetInstruction targetInstruction = new TargetInstruction();
        
        // Set instruction and operand
        targetInstruction.label = label;
                
        // Add the TargetInstruction to the list of target instructions
        targetInstructions.add(targetInstruction);
    }

    private void emit(String instruction, String operand) {
        TargetInstruction targetInstruction = new TargetInstruction();
        
        // Set instruction and operand
        targetInstruction.instruction = instruction;
        targetInstruction.operand1 = operand;
                
        // Add the TargetInstruction to the list of target instructions
        targetInstructions.add(targetInstruction);
    }

    // private void emit(String instruction, String operand) {
    //     TargetInstruction targetInstruction = new TargetInstruction();
        
    //     // Set instruction and operand
    //     targetInstruction.instruction = instruction;
    //     targetInstruction.operand1 = operand;
                
    //     // Add the TargetInstruction to the list of target instructions
    //     targetInstructions.add(targetInstruction);
    // }

    private void emit(String instruction, String operand1, String operand2) {
        TargetInstruction targetInstruction = new TargetInstruction();
        
        // Set instruction and operand
        targetInstruction.instruction = instruction;
        targetInstruction.operand1 = operand1;
        targetInstruction.operand2 = operand2;
                
        // Add the TargetInstruction to the list of target instructions
        targetInstructions.add(targetInstruction);
    }

    private void emitComment(String comment) {
        TargetInstruction targetInstruction = new TargetInstruction();

        // Set comment
        targetInstruction.comment = comment;

        // Add the TargetInstruction to the list of target instructions
        targetInstructions.add(targetInstruction);
    }

    public List<TargetInstruction> getTargetInstructions() {
        return targetInstructions;
    }
}
