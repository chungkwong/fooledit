parser grammar TclParser;

options { tokenVocab=TclLexer; }

program
    : command (SEPARATOR+ command)* SEPARATOR*
    | SEPARATOR+ (command SEPARATOR+)* command? 
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
    : (START_QUOTE|MORE_QUOTE) (QUOTED|quoted)* LESS_QUOTE
    ;

string
    : START_STRING (STRING|variable|expression)* END_STRING
    ;

variable
    : VARIABLE
    | (VARIABLE_START|VARIABLE_START_IN_INDEX|VARIABLE_START_IN_STRING) (INDEX|variable|expression)* ')'
    ;

expression
    : (START_EXPRESSION|START_EXPRESSION_IN_INDEX|START_EXPRESSION_IN_STRING) program END_EXPRESSION
    ;
