
lexer grammar PropertiesLexer;

WHITESPACE0
    :
    [ \t\u000C]+ -> skip;

COMMENT
    : ('#'|'!') (~ [\r\n])*
    ;

EOL 
    : 
    '\r'? '\n' ;

KEY  
    : 
    ((~[:= \r\n\t\u000C\\]) | '\\' ('\r\n'|.))+ ->pushMode(MIDDLE);

mode MIDDLE;

WHITESPACE1
    :
    [ \t\u000C]+ -> skip;

SEPARATOR
    :
    ('='|':')? ->mode(REMAINING);

mode REMAINING;

WHITESPACE2
    :
    [ \t\u000C]+ -> skip;

VALUE  
    : 
    ((~[\r\n\\]) | '\\' ('\r\n'|.))* -> popMode;
