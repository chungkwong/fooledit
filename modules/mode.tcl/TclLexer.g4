lexer grammar TclLexer;

START
    : -> pushMode(expression)
    ;

mode expression;

COMMENT
    : '#' (~[\r\n\\]|'\\'(~[\r\n])|'\\'('\r''\n'?|'\n'))* ->skip
    ;

PLAIN
    : (~[}{[\];"\r\n\t $\\#]|'\\'.)+
    ;

WHITESPACE
    : [ \t] -> skip
    ;

VARIABLE
    : Variable
    ;

VARIABLE_START
    : IndexedVariable -> pushMode(index)
    ;

START_QUOTE
    : '{' -> pushMode(quoted)
    ;

START_STRING
    : '"' -> pushMode(string)
    ;

START_EXPRESSION
    : '[' -> pushMode(expression)
    ;

END_EXPRESSION
    : ']' -> popMode
    ;

SEPARATOR
    : [;\r\n]+
    ;

mode quoted;

MORE_QUOTE
    : '{' -> pushMode(quoted)
    ;

LESS_QUOTE
    : '}' ->popMode
    ;

QUOTED
    : ((~[\\}{])|'\\'.)+
    ;

mode string;

END_STRING
    : '"' -> popMode
    ;

STRING
    : ((~["\\[])|'\\'.)+
    ;

START_EXPRESSION_IN_STRING
    : '[' -> pushMode(expression)
    ;

VARIABLE_IN_STRING
    : Variable
    ;

VARIABLE_START_IN_STRING
    : IndexedVariable -> pushMode(index)
    ;

mode index;

END_INDEX
    : ')' -> popMode
    ;

INDEX
    : ((~[)\\[])|'\\'.)+
    ;

START_EXPRESSION_IN_INDEX
    : '[' -> pushMode(expression)
    ;

VARIABLE_IN_INDEX
    : Variable
    ;

VARIABLE_START_IN_INDEX
    : IndexedVariable -> pushMode(index)
    ;

fragment Variable
    : '$' ([_a-zA-Z0-9]*|'{'(~'}')*'}')
    ;

fragment IndexedVariable
    : '$' [_a-zA-Z0-9]* '('
    ;
