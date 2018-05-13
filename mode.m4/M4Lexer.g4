lexer grammar M4Lexer;

COMMENT
    : '#' (~[\r\n])*
    ;

IDENTIFIER
    : [_a-zA-Z][_0-9a-zA-Z]*
    ;

QUOTE_START
    : '`' ->pushMode(quoted)
    ;

SEPARATOR
   : [ \t-\r)(,$]
   ;

PLAIN
    : (~[#_a-zA-Z`' \t-\r)(,$])+
    ;

mode quoted;

MORE_QUOTE
    : '`' ->pushMode(quoted)
    ;

QUOTE_END
    : '\'' ->popMode
    ;

QUOTED
    : (~[`'])+
    ;
