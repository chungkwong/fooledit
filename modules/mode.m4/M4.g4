grammar M4;

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
   : [ \t\r\n\f\v)(,$]
   ;


PLAIN
    : (~[#_a-zA-Z`' \t\r\n\f\v)(,$])+
    ;

mode quoted;

QUOTE_START
    : '`' ->pushMode(quoted)
    ;

QUOTE_END
    : '\'' ->popMode
    ;

QUOTED
    : (~[`'])+
    ;
