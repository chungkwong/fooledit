lexer grammar MarkdownLexer;


STRING
    : {getCharPositionInLine() == 0}?([ \t\r\n]*'>'|'    '|'\t')(~[\r\n])*
    | '```'('`'?'`'?(~'`'))*'```'
    | '``'('`'?(~'`'))*'``'
    | '`'.*?'`'
    | '_'(~[ \t])([ \t]*(~[ \t]))*?'_'
    | '__'(~[ \t])([ \t]*(~[ \t]))*?'__'
    | '*'(~[ \t])([ \t]*(~[ \t]))*?'*'
    | '**'(~[ \t])([ \t]*(~[ \t]))*?'**'
    ;

COMMENT
    : '<!--' ('-'? (~'-'))* '-->'
    ;

TAG
    : '<' '/'? Name '>'
    ;

START_TAG
    : '<' Name Whitespace ->pushMode(tag)
    ;

KEYWORD
    : {getCharPositionInLine() == 0}? [ \t\r\n]* '#'+ (~[\r\n])*
    | {getCharPositionInLine() == 0}?(~[\r\n])*('\r''\n'?|'\n')('='+|'-'+)('\r''\n'?|'\n') 
    ;

INTEGER
    : {getCharPositionInLine() == 0}? [ \t\r\n]*([0-9]+'.'|[-+*])
    ;

LINE
    : {getCharPositionInLine() == 0}? [ \t]* [-_*] [ \t]* [-_*] [ \t]* [-_*] [-_* \t]*[\r\n]
    ;

URL
    : '!'?'['(~[\\\]]|'\\'.)*']('(~[)\\]|'\\'.)* ')'
    | '<'(~'>')*'>'
    |{getCharPositionInLine() == 0}?[ \t\r\n]* '[' (~[\]\\]|'\\'.)* ']:' (~[\r\n])*
    ;

PLAIN
    : (~[-*!<_[`\r\n])+|[-*!<_[`\r\n]|'\\'[\\`*_{}[\]()#+-.!]
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

TAG_WHITESPACE
    : Whitespace
    ;

fragment Whitespace
    : [ \t\r\n]+
    ;

fragment Name
    : [a-zA-Z][0-9a-zA-Z]*
    ;
