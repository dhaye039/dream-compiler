grammar Dream;

start: (NEWLINE* class_decl)+ NEWLINE*;

class_decl: CLASS IDENTIFER (INHERITS FROM IDENTIFER)? IS NEWLINE+
      (var_decl | method_decl | NEWLINE)*
      END IDENTIFER;

var_decl: IDENTIFER (COLON type)? (ASSIGNMENT expression)? NEWLINE;

method_decl: IDENTIFER LPAREN argument_decl_list? RPAREN (COLON type)? IS NEWLINE+
      (var_decl NEWLINE*)*
      BEGIN NEWLINE+
      statement_list
      END IDENTIFER NEWLINE;

argument_decl_list: (argument_decl SEMICOLON)* argument_decl;

argument_decl: IDENTIFER COLON type;

type returns [Type t]: INT #TypeInt
   | STRING          #TypeStr
   | BOOLEAN         #TypeBool
   | IDENTIFER       #TypeId
   | type LBRACKET (expression)? RBRACKET #TypeArr;

statement_list: (statement NEWLINE+)*;

statement: assignment_stmt #AsmtStmt
   | if_stmt         #IfStmt
   | loop_stmt       #LoopStmt
   | call_stmt       #CallStmt;

assignment_stmt returns [Symbol sym]: IDENTIFER (LBRACKET expression RBRACKET)* ASSIGNMENT expression;

if_stmt: IF expression THEN NEWLINE+
      statement_list
      (ELSE NEWLINE+ statement_list)?
      END IF;

loop_stmt: LOOP WHILE expression NEWLINE+
      statement_list
      END LOOP;

call_stmt: (expression PERIOD)? IDENTIFER LPAREN expression_list? RPAREN;

expression_list returns [Type t]: (expression COMMA)* expression;

expression returns [Type t]: or_exp;

or_exp returns [Type t]: and_exp (OR and_exp)*;

and_exp returns [Type t]: relation_exp (AND relation_exp)*;

relation_exp returns [Type t]: concat_exp (no_assoc_op concat_exp)?;

concat_exp returns [Type t]: addsub_exp (CONCAT addsub_exp)*;

addsub_exp returns [Type t]: muldiv_exp ((PLUS | MINUS) muldiv_exp)*;

muldiv_exp returns [Type t]: unary_exp ((TIMES | DIV) unary_exp)*;

unary_exp returns [Type t]: unary_op? value;

value returns [Type t]: id call_tail?;

id returns [Type t, Symbol symbol]: IDENTIFER (LBRACKET expression RBRACKET (LBRACKET expression RBRACKET)*)? #IdType
   | LPAREN expression RPAREN   #ParType
   | INTLIT          #IntType
   | TRUE            #BoolType
   | FALSE           #BoolType
   | NULL            #NullType
   | ME              #MeType
   | NEW type        #NewType
   | STRING_LITERAL  #StrType;     

expr_tail returns [Type t]: PERIOD? id call_tail;

call_tail returns [Type t]: LPAREN expression_list? RPAREN expr_tail?  #ParCall
   | expr_tail #ExpCall;

no_assoc_op: EQ | GT | GE | LT | LE;

unary_op: MINUS  #MinusOp
   | PLUS   #PlusOp
   | NOT    #NotOp;

NEWLINE: '\r'? '\n';

COMMENT: '~'+ ~[\r\n] * -> skip;

WS: [ \t]+ -> skip;

AND: 'and';

BOOLEAN: 'boolean';

BEGIN: 'begin';

CLASS: 'class';

ELSE: 'else';

END: 'end';

FALSE: 'false';

FROM: 'from';

IF: 'if';

INHERITS: 'inherits';

INT: 'int';

IS: 'is';

LOOP: 'loop';

ME: 'me';

NEW: 'new';

NOT: 'not';

NULL: 'null';

OR: 'or';

STRING: 'string';

THEN: 'then';

TRUE: 'true';

WHILE: 'while';

INTLIT: ('-')? ('0' .. '9')+;

CONCAT: '&';

PLUS: '+';

MINUS: '-';

TIMES: '*';

DIV: '/';

GT: '>';

GE: '>=';

LT: '<';

LE: '<=';

EQ: '=';

ASSIGNMENT: ':=';

LPAREN: '(';

RPAREN: ')';

LBRACKET: '[';

RBRACKET: ']';

COMMA: ',';

SEMICOLON: ';';

COLON: ':';

PERIOD: '.';

IDENTIFER: VALID_ID_START VALID_ID_CHAR*;

fragment VALID_ID_START: ('a' .. 'z') | ('A' .. 'Z') | '_';

fragment VALID_ID_CHAR: VALID_ID_START | ('0' .. '9');

UNTERMINATED_STRING: '"' ~["\n]*;

ILLEGAL_STRING: '"' ~["\n]* ILLEGAL_ESCAPE_SEQ  '"';

fragment ILLEGAL_ESCAPE_SEQ: '\\' ~[tnfr"\\];

STRING_LITERAL: '"' ( ~["\n] | ESCAPE_SEQ )* '"';

fragment ESCAPE_SEQ: '\\' ( [tnfr"\\] | OCTAL_SEQ );

fragment OCTAL_SEQ: ('0' .. '7' ) ('0' .. '7' ) ('0' .. '7' );

LINE_CONT: '_'[\r\n] -> skip;

ENDOFFILE: EOF -> skip ;

ERR: .;
