grammar Bison;

COMMENT
    : Comment -> skip
    ;

KEYWORD
    : '%' ('define'|'union'|'require'|'token'|'left'|'right'|'nonassoc'|'precedence'|'type'|'initial-action'|'destructor'|'printer'|'expect' '-rr'?|'code'|'start'|'file-prefix'|'output'|'language'|'name-prefix'|'skeleton'|'verbose') -> pushMode(value)
    ;

PLAIN
    : '%{' .*? [\r\n] '%}'
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

SEPARATOR
    : '%%' -> mode(rules);

mode rules;

COMMENT
    : Comment -> skip
    ;

SEPARATOR
    : '%%' -> mode(user)
    ;

KEY
   : Id|Char|String ->pushMode(clause)
   ;

WHITESPACE
    : [ \t\r\n]+
    ;

mode user;

PLAIN
    : .+
    ;

mode value;

COMMENT
    : Comment -> skip
    ;

NAME
    : Id
    ;

CHARACTER
    : Char
    ;

SEPARATOR
    : [<>]
    ;

STRING
    : String
    ;

BLOCK_START
    : '{' -> pushMode(block)
    ;

WHITESPACE
    : [ \t]+
    ;

NEWLINE
    : [\r\n]+ ->popMode
    ;


mode clauses;

COMMENT
    : Comment -> skip
    ;

SEPARATOR
    : ';' -> popMode;

NAME
    : Id
    ;

CHARACTER
    : Char
    ;

STRING
    : String
    ;

BLOCK_START
    : '{' -> pushMode(block)
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

SEPARATOR
    : [:|]
    ;


mode block;

BLOCK_START
    : '{' -> pushMode(block)
    ;

BLOCK_END
    : '}' -> popMode
    ;

PLAIN
    : (Comment|Char|String|~['"}{/]|'/'(~[*/]))+
    ;

fragment Id
    : [_a-zA-Z.][-_0-9a-zA-Z.]*
    ;

fragment String
    : '"' (~'"'|'\\'.)* '"'
    ;

fragment Comment
    : '//' (~[\r\n])*
    | '/*' .*? '*/'
    ;

fragment Char
    : '\'' (~[\\"\r\n]|'\\'([ntvbrfa?'"\\]|x[0-9a-fA-F]+|[0-7]([0-7][0-7]?)?)) '\''
    ;
