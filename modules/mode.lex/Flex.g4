grammar Flex;

COMMENT
    : Comment -> skip
    ;

KEY
    : '%' [sx]
    | Id -> pushMode(value)
    ;

PLAIN
    : '%' 'top'? '{' .*? [\r\n] '%}'
    | '\t' (~[\r\n])*
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

PLAIN
    : '%{' .*? [\r\n] '%}'
    | '\t' (~[\r\n])*
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

REGEX
    : (~[\\ \t\r\n]|'\\'.)+ -> pushMode(action)
    ;

mode user;

PLAIN
    : .+
    ;

mode value;

COMMENT
    : Comment -> skip
    ;

REGEX
    : (~[\r\n])* -> popMode
    ;

WHITESPACE
    : [ \t]+
    ;

mode action;

PLAIN
    : (Comment|Char|String|~['"}{/\r\n]|'/'(~[*/]))+
    ;

BLOCK_START
    : '{' -> pushMode(block)
    ;

NEWLINE
    : [\r\n]+ -> popMode
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
    : [_a-zA-Z][-_0-9a-zA-Z]*
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
