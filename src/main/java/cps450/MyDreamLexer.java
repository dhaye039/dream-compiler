package cps450;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;

public class MyDreamLexer extends DreamLexer {
	
	boolean dumpTokens;
    String filename;

	public MyDreamLexer(CharStream input, boolean dumpTokens, String filename) {
		super(input);
        this.dumpTokens = dumpTokens;
		this.filename = filename;
	}

@Override
	public Token nextToken() {
		Token tok = super.nextToken();

        // used for signifying if the token encountered an error
        boolean isError = false; 

        // the token type (used when printing)
        String type;

        // switches cases based on the token type 
        switch (tok.getType()) {
            case DreamLexer.NEWLINE:
            case DreamLexer.COMMENT:
                type = "cr";
                break; 

            // cases keywords
            case DreamLexer.AND:
            case DreamLexer.BOOLEAN:
            case DreamLexer.BEGIN:
            case DreamLexer.CLASS:
            case DreamLexer.ELSE:
            case DreamLexer.END:
            case DreamLexer.FALSE:
            case DreamLexer.FROM:
            case DreamLexer.IF:
            case DreamLexer.INHERITS:
            case DreamLexer.INT:
            case DreamLexer.IS:
            case DreamLexer.LOOP:
            case DreamLexer.ME:
            case DreamLexer.NEW:
            case DreamLexer.NOT:
            case DreamLexer.NULL:
            case DreamLexer.OR:
            case DreamLexer.STRING:
            case DreamLexer.THEN:
            case DreamLexer.TRUE:
            case DreamLexer.WHILE:
                type = "keyword" + ":" + tok.getText();
                break;
        
            case DreamLexer.INTLIT:
                type = "number" + ":" + tok.getText();
                break;

            // operators
            case DreamLexer.CONCAT:
            case DreamLexer.PLUS:
            case DreamLexer.MINUS:
            case DreamLexer.TIMES:
            case DreamLexer.DIV:
            case DreamLexer.GT:
            case DreamLexer.GE:
            case DreamLexer.LT:
            case DreamLexer.LE:
            case DreamLexer.EQ:
                type = "operator" + ":" + '\''+ tok.getText() + '\'';
                break;

            // chars/other operators
            case DreamLexer.ASSIGNMENT:
            case DreamLexer.LPAREN:
            case DreamLexer.RPAREN:
            case DreamLexer.LBRACKET:
            case DreamLexer.RBRACKET:
            case DreamLexer.COMMA:
            case DreamLexer.SEMICOLON:
            case DreamLexer.COLON:
            case DreamLexer.PERIOD:
                type = '\'' + tok.getText() + '\'';
                break;

            case DreamLexer.IDENTIFER:
                type = "identifier" + ":" + tok.getText();
                break;

            case DreamLexer.UNTERMINATED_STRING:
                type = "Unterminated string" + ':' + tok.getText();
                isError = true;
                break;

            case DreamLexer.ILLEGAL_STRING:
                type = "Illegal string" + ':' + tok.getText();
                isError = true;
                break;

            case DreamLexer.STRING_LITERAL:
                type = "string lit" + ':' + tok.getText();
                break;
            
            default:
                type = "Unrecognized char" + ": " + tok.getText();
                isError = true;

                if (tok.getText().equals("<EOF>")) {
                    return tok;
                }
        }

        // prints if -ds option specified or if the token encountered an error
        if (dumpTokens || isError == true) {
            System.out.println(filename + ":" + tok.getLine() + "," + (tok.getCharPositionInLine() + 1) + ":" + type);
        }
		return tok;
	}


}


	// @Override
	// public Token nextToken() {
	// 	Token tok = super.nextToken();
    //     System.out.println("hello");


    //     // used for signifying if the token encountered an error
    //     boolean isError = false; 

    //     // the token type (used when printing)
    //     String type;

    //     // switches cases based on the token type 
    //     switch (tok.getType()) {
    //         case DreamLexer.NEWLINE:
    //         case DreamLexer.COMMENT:
    //             type = "cr";
    //             break; 

    //         // cases keywords
    //         case DreamLexer.AND:
    //         case DreamLexer.BOOLEAN:
    //         case DreamLexer.BEGIN:
    //         case DreamLexer.CLASS:
    //         case DreamLexer.ELSE:
    //         case DreamLexer.END:
    //         case DreamLexer.FALSE:
    //         case DreamLexer.FROM:
    //         case DreamLexer.IF:
    //         case DreamLexer.INHERITS:
    //         case DreamLexer.INT:
    //         case DreamLexer.IS:
    //         case DreamLexer.LOOP:
    //         case DreamLexer.ME:
    //         case DreamLexer.NEW:
    //         case DreamLexer.NOT:
    //         case DreamLexer.NULL:
    //         case DreamLexer.OR:
    //         case DreamLexer.STRING:
    //         case DreamLexer.THEN:
    //         case DreamLexer.TRUE:
    //         case DreamLexer.WHILE:
    //             type = "keyword" + ":" + tok.getText();
    //             break;
        
    //         case DreamLexer.INTLIT:
    //             type = "number" + ":" + tok.getText();
    //             break;

    //         // operators
    //         case DreamLexer.CONCAT:
    //         case DreamLexer.PLUS:
    //         case DreamLexer.MINUS:
    //         case DreamLexer.TIMES:
    //         case DreamLexer.DIV:
    //         case DreamLexer.GT:
    //         case DreamLexer.GE:
    //         case DreamLexer.LT:
    //         case DreamLexer.LE:
    //         case DreamLexer.EQ:
    //             type = "operator" + ":" + '\''+ tok.getText() + '\'';
    //             break;

    //         // chars/other operators
    //         case DreamLexer.ASSIGNMENT:
    //         case DreamLexer.LPAREN:
    //         case DreamLexer.RPAREN:
    //         case DreamLexer.LBRACKET:
    //         case DreamLexer.RBRACKET:
    //         case DreamLexer.COMMA:
    //         case DreamLexer.SEMICOLON:
    //         case DreamLexer.COLON:
    //         case DreamLexer.PERIOD:
    //             type = '\'' + tok.getText() + '\'';
    //             break;

    //         case DreamLexer.IDENTIFER:
    //             type = "identifier" + ":" + tok.getText();
    //             break;

    //         case DreamLexer.UNTERMINATED_STRING:
    //             type = "Unterminated string" + ':' + tok.getText();
    //             isError = true;
    //             break;

    //         case DreamLexer.ILLEGAL_STRING:
    //             type = "Illegal string" + ':' + tok.getText();
    //             isError = true;
    //             break;

    //         case DreamLexer.STRING_LITERAL:
    //             type = "string lit" + ':' + tok.getText();
    //             break;
            
    //         default:
    //             type = "Unrecognized char" + ": " + tok.getText();
    //             isError = true;
    //     }

    //     // prints if -ds option specified or if the token encountered an error
    //     if (dumpTokens || isError == true) {
    //         System.out.println(filename + ":" + tok.getLine() + "," + (tok.getCharPositionInLine() + 1) + ":" + type);
    //     }
	// 	return tok;
	// }

