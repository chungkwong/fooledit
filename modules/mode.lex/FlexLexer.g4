lexer grammar FlexLexer;

COMMENT
    : Comment -> skip
    ;

DIRECTIVE
    : '%' [sx] -> pushMode(value)
    ;

KEY
    : Id -> pushMode(value)
    ;

PLAIN
    : {getCharPositionInLine() == 0}? '%' 'top'? '{' .*? [\r\n] '%}'
    | {getCharPositionInLine() == 0}? '\t' (~[\r\n])*
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

START_RULES
    : '%%' -> mode(rules);

mode rules;

START_USER
    : '%%' -> mode(user)
    ;

RULE_CODE
    : {getCharPositionInLine() == 0}? '%{' .*? [\r\n] '%}'
    | {getCharPositionInLine() == 0}? '\t' (~[\r\n])*
    ;

RULE_WHITESPACE
    : [ \t\r\n]+
    ;

REGEX
    : (~[\\ \t\r\n]|'\\'.)+ -> pushMode(action)
    ;

mode user;

USER
    : .+
    ;

mode value;

VALUE
    : (~[\r\n])* -> popMode
    ;

VALUE_WHITESPACE
    : [ \t]+ ->skip
    ;

mode action;

ACTION
    : (Comment|Char|String|~['"}{/\r\n]|'/'(~[*/]))+
    ;

BLOCK_START
    : '{' -> pushMode(block)
    ;

NEWLINE
    : [\r\n]+ -> popMode
    ;

mode block;

MORE_BLOCK
    : '{' -> pushMode(block)
    ;

BLOCK_END
    : '}' -> popMode
    ;

BLOCK
    : (Comment|Char|String|~['"}{/]|'/'(~[*/]))+
    ;


fragment Id
    : [_a-zA-Z][-_0-9a-zA-Z]*
    ;

fragment String
    : '"' (~["\\]|'\\'.)* '"'
    ;

fragment Comment
    : '//' (~[\r\n])*
    | '/*' .*? '*/'
    ;

fragment Char
    : '\'' (~[\\"\r\n]|'\\'([ntvbrfa?'"\\]|'x'[0-9a-fA-F]+|[0-7]([0-7][0-7]?)?)) '\''
    ;
