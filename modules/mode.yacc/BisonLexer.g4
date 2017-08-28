lexer grammar BisonLexer;

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

START_RULES
    : '%%' -> mode(rules);

mode rules;

RULES_COMMENT
    : Comment -> skip
    ;

START_USER
    : '%%' -> mode(user)
    ;

KEY
   : (Id | Char | String) ->pushMode(clauses)
   ;

RULES_WHITESPACE
    : [ \t\r\n]+
    ;

mode user;

USER
    : .+
    ;

mode value;

VALUE_COMMENT
    : Comment -> skip
    ;

VALUE_NAME
    : Id
    ;

VALUE_CHARACTER
    : Char
    ;

VALUE_SEPARATOR
    : [<>]
    ;

VALUE_STRING
    : String
    ;

VALUE_BLOCK_START
    : '{' -> pushMode(block)
    ;

VALUE_WHITESPACE
    : [ \t]+
    ;

VALUE_NEWLINE
    : [\r\n]+ ->popMode
    ;


mode clauses;

CLAUSE_COMMENT
    : Comment -> skip
    ;

CLAUSE_END
    : ';' -> popMode;

CLAUSE_NAME
    : Id
    ;

CLAUSE_CHARACTER
    : Char
    ;

CLAUSE_STRING
    : String
    ;

CLAUSE_BLOCK_START
    : '{' -> pushMode(block)
    ;

CLAUSE_WHITESPACE
    : [ \t\r\n]+
    ;

CLAUSE_SEPARATOR
    : [:|]
    ;


mode block;

BLOCK_START
    : '{' -> pushMode(block)
    ;

BLOCK_END
    : '}' -> popMode
    ;

BLOCK
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
    : '\'' (~[\\"\r\n]|'\\'([ntvbrfa?'"\\]|'x'[0-9a-fA-F]+|[0-7]([0-7][0-7]?)?)) '\''
    ;
