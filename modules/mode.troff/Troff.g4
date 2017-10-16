grammar Troff;

doc
   : (COMMAND|WHITESPACE|PLAIN)*
   ;

COMMAND
    : {getCharPositionInLine() == 0}? [\\.'](~[\r\n])*
    | '\\' (~[([]|'[' (~']')* ']'|'('. .) ([0-9a-zA-Z]|'['(~']')*']'|'\''(~'\'')*'\''|'('. .)?
    ;

COMMENT
    : '\\' [#"] (~[\r\n])*->channel(HIDDEN)
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

PLAIN
    : (~[\\\r\n])+
    ;
