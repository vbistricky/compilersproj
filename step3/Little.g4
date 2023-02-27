grammar Little;

program: 'PROGRAM' id 'BEGIN' pgm_body 'END' ;
id: IDENTIFIER ;
pgm_body: decl func_declarations ;
decl: string_decl decl | var_decl decl | ;

string_decl: 'STRING' id ':=' str ';' ;
str: STRINGLITERAL ;

var_decl: var_type id_list ';' ;
var_type: 'FLOAT' | 'INT' ;
any_type: var_type | 'VOID' ;
id_list: id id_tail ;
id_tail: ',' id id_tail | ;

param_decl_list: param_decl param_decl_tail | ;
param_decl: var_type id ;
param_decl_tail: ',' param_decl param_decl_tail | ;

func_declarations: func_decl func_declarations | ;
func_decl: 'FUNCTION' any_type id '(' param_decl_list ')' 'BEGIN' func_body 'END' ;
func_body: decl stmt_list ;

stmt_list: stmt stmt_list | ;
stmt: base_stmt | if_stmt | while_stmt ;
base_stmt: assign_stmt | read_stmt | write_stmt | return_stmt ;

assign_stmt: assign_expr ';' ;
assign_expr: id ':=' expr ;
read_stmt: 'READ' '(' id_list ')' ';' ;
write_stmt: 'WRITE' '(' id_list ')' ';' ;
return_stmt: 'RETURN' expr ';' ;

expr: expr_prefix factor ;
expr_prefix: expr_prefix factor addop | ;
factor: factor_prefix postfix_expr ;
factor_prefix: factor_prefix postfix_expr mulop | ;
postfix_expr: primary | call_expr ;
call_expr: id '(' expr_list ')' ;
expr_list: expr expr_list_tail | ;
expr_list_tail: ',' expr expr_list_tail | ;
primary: '(' expr ')' | id | INTLITERAL | FLOATLITERAL ;
addop: '+' | '-' ;
mulop: '*' | '/' ;

if_stmt: 'IF' '(' cond ')' decl stmt_list else_part 'ENDIF' ;
else_part: 'ELSE' decl stmt_list | ;
cond: expr compop expr ;
compop: '<' | '>' | '=' | '!=' | '<=' | '>=' ;

while_stmt: 'WHILE' '(' cond ')' decl stmt_list 'ENDWHILE' ;


KEYWORD : PROGRAM|BEGIN|END|FUNCTION|READ|WRITE|
IF|ELSE|ENDIF|WHILE|ENDWHILE|CONTINUE|BREAK|
RETURN|INT|VOID|STRING|FLOAT ;

OPERATOR : ADD|SUBTRACT|ASSIGN|MULTIPLY|DIVIDE|NOTEQUALS|EQUALS|
LESSTHAN|GREATTHAN|OPPAREN|CLOSPAREN|ENDLINE|COMMA|LESSTHANEQ|GREATTHANEQ ; 

WS : [ \t\r\n]+ -> skip ;
INTLITERAL : [0-9]+ ;
IDENTIFIER : [a-zA-Z] [a-zA-Z0-9]* ;
FLOATLITERAL : [0-9]*'.'[0-9]+ ;
STRINGLITERAL : '"'~[\n\r"]*'"' ;
COMMENT : '--' ~[\r\n]* -> skip;

fragment PROGRAM : 'PROGRAM' ;
fragment BEGIN : 'BEGIN' ;
fragment END : 'END' ;
fragment FUNCTION : 'FUNCTION' ;
fragment READ : 'READ' ;
fragment WRITE : 'WRITE' ;
fragment IF : 'IF' ;
fragment ELSE : 'ELSE' ;
fragment ENDIF : 'ENDIF' ;
fragment WHILE : 'WHILE' ;
fragment ENDWHILE : 'ENDWHILE' ;
fragment CONTINUE : 'CONTINUE' ;
fragment BREAK : 'BREAK' ;
fragment RETURN : 'RETURN' ;
fragment INT : 'INT' ;
fragment VOID : 'VOID' ;
fragment STRING : 'STRING' ;
fragment FLOAT : 'FLOAT' ;

fragment ADD : '+' ;
fragment SUBTRACT : '-' ;
fragment ASSIGN : ':=' ;
fragment MULTIPLY : '*' ;
fragment DIVIDE : '/' ;
fragment NOTEQUALS : '!=' ;
fragment EQUALS : '=' ;
fragment LESSTHAN : '<' ;
fragment GREATTHAN : '>' ;
fragment OPPAREN : '(' ;
fragment CLOSPAREN : ')' ;
fragment ENDLINE  : ';' ;
fragment COMMA : ',' ;
fragment LESSTHANEQ : '<=' ;
fragment GREATTHANEQ : '>=' ;
