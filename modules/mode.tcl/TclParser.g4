parser grammar TclParser;

options { tokenVocab=TclLexer; }

program
    :
    | SEPARATOR (command SEPARATOR)* command?
    | command (SEPARATOR command)* SEPARATOR?
    ;

command
    : word+
    ;

word
    : PLAIN
    | variable
    | quoted
    | string
    | expression
    ;

quoted
    : '{' (QUOTED|quoted)* '}'
    ;

string
    : '"' (STRING|variable|expression)* '"'
    ;

variable
    : VARIABLE
    | (VARIABLE_START|VARIABLE_START_IN_INDEX|VARIABLE_START_IN_STRING) (INDEX|variable|expression)* ')'
    ;

expression
    : '[' program ']'
    ;
