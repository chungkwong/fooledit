grammar Troff;

doc
   : (COMMAND|COMMENT|WHITESPACE|PLAIN)*
   ;

COMMAND
    : {getCharPositionInLine() == 0}? [\\.'](~[\r\n])*
    | '\\' (~[([]|'[' (~']')* ']'|'('. .) ([0-9a-zA-Z]|'['(~']')*']'|'\''(~'\'')*'\''|'('. .)?
    ;

COMMENT
    : '\\' [#"] (~[\r\n])*
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

PLAIN
    : (~[\\\r\n])+
    ;
