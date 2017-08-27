grammar Markdown;


COMMENT
    : '<!--' ('-'? (~'-'))* '-->'
    ;

TAG
    : '<' '/'? Name '>'
    ;

START_TAG
    : '<' Name [ \\t\\r\\n] ->pushMode(tag)
    ;

KEYWORD
    : {col==0}? [ \t\r\n]* '#'+ (~[\r\n])*
    | {col==0}?(~[\r\n])*('\r''\n'?|\\n)('='+|'-'+)$
    ;

INTEGER
    : {col==0}? [ \t\r\n]*([0-9]+'.'|[-+*])
    ;

SEPARATOR
    : {col==0}? [ \t]* [-_*] [ \t]* [-_*] [ \t]* [-_*] [-_* \t]*[\r\n]
    ;

STRING
    : {col==0}?[ \t\r\n]*>(~[\r\n])*
    | (`+).*?\\1
    |([_*]{1,2})[^ \\t]([ \\t]*[^ \\t])*?\\1
    ;

URL
    : '!'?'['(~[\]\\]|'\\'.)*']('(~[)\\]|'\\'.)* ')'
    |'(' ([^\\)\\\\]|\\\\.)* ')'
    | '<(~'>')*>
    |{col==0}?[ \t\r\n]*\\[([^\\]\\\\]|\\\\.)*\\]:.*?$(|\\1[ \\t].*?$)
    ;

PLAIN
    : (~[-*!<_[`\r\n])+
    ;

WHITESPACE
    : [\r\n]+
    ;


mode tag;

TAG_END
    : '>' -> popMode
    ;

KEY
    : Name
    ;

SEPARATOR
    : '='
    ;

VALUE
    : '"' (~'"')* '"'
    | '\'' (~'\'')* '\''
    ;

fragment Whitespace
    : [ \t\r\n]
    ;

fragment Name
    : [a-zA-Z][0-9a-zA-Z]*
    ;