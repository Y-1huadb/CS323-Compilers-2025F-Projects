lexer grammar Splc;

// IDEA Plugin Settings
// - Output Directory: src/main/java/
// - package name: generated.Splc

// =========================
// Lexer Rules
// =========================

// ---------- Keywords ----------
INT     : 'int';
CHAR    : 'char';
STRUCT  : 'struct';
RETURN  : 'return';
IF      : 'if';
ELSE    : 'else';
WHILE   : 'while';
// ---------- Operators ----------
LE      : '<=';
GE      : '>=';
EQ      : '==';
NEQ     : '!=';
AND     : '&&';
OR      : '||';
INC     : '++';
DEC     : '--';
ARROW   : '->';

ASSIGN  : '=';
PLUS    : '+';
MINUS   : '-';
STAR    : '*';
DIV     : '/';
MOD     : '%';
LT      : '<';
GT      : '>';
NOT     : '!';
DOT     : '.';
AMP     : '&';
// ---------- Separators ----------

SEMI    : ';';
COMMA   : ',';
LPAREN  : '(';
RPAREN  : ')';
LBRACE  : '{';
RBRACE  : '}';
LBRACK  : '[';
RBRACK  : ']';
// ---------- Identifiers & Literals ----------

Identifier      : [a-zA-Z_][a-zA-Z_0-9]*;
Number          : '0' | [1-9][0-9]*;
Char            : '\'' ( ESC | ~[\\'\n\r] ) '\'';
fragment ESC    : '\\' [nt0'\\];

// ---------- Whitespace & Comments ----------

WS              : [ \t\r\n]+ -> skip;
LINE_COMMENT    : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT   : '/*' .*? '*/' -> skip;