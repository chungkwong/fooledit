grammar Octave;

program
    :
    ;

KEYWORD
    :'__FILE__'|'__LINE__'|'break'|'case'|'catch'|'classdef'|'continue'|'do'|'else'('if')?|'end'('_try_catch'|'_unwind_protect'|'classdef'|'enumeration'|'events'|'for'|'function'|'if'|'methods'|'parfor'|'properties'|'switch'|'while')?|'enumeration'|'events'|'for'|'function'|'global'|'if'|'methods'|'otherwise'|'parfor'|'persistent'|'properties'|'return'|'switch'|'try'|'until'|'unwind_protect'('_cleanup')?|'while'
    ;
    
COMMENT
    : ([#%] (~[\r\n])* | '#{' .*? '#}' | '%{' .*? '%}') -> skip
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

BOOLEAN
    : 'true'|'false'
    ;

NULL
    : 'NA'
    ;

STRING
    : '\'' ((~'\'')|'\'\'')* '\''
    | '"' (~["\\]|'\\'(["'\\abfnrtv]|'x'[0-9a-fA-F]+|[0-7]([0-7][0-7]?)?))* '"'
    ;

FLOAT
    : 'NaN'|'Inf'|([0-9]+('.'[0-9]*)?|'.'[0-9]+)([eE][-+]?[0-9]+)?[ijIJ]?
    | [0-9]+[eE][-+]?[0-9]+[ijIJ]?
    ;

INTEGER
    : ('0'[xX][0-9a-fA-F]('_'?[0-9a-fA-F])*|'0'[bB][01]('_'?[01])*|[0-9]('_'?[0-9])*)[ijIJ]?
    ;

IDENTIFIER
    : [_a-zA-Z][_0-9a-zA-Z]*
    ;

OPERATOR
    : '.**'|'--'|'++'|'||'|'&&'|'.\''|('.'[*/\\^]|[-+*/!><=~\\^|&])'='?|'\''
    ;

SEPARATOR
    : [,;:)([\]}{.]
    ;
