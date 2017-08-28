lexer grammar GettextLexer;

COMMENT
    : Comment -> skip
    ;

WHITESPACE
    : Whitespace -> skip
    ;

VALUE
    : String
    ;

MSGCTEXT
    : 'msgctxt' -> pushMode(context)
    ;

MSGID
    : Msgid -> pushMode(key)
    ;

MSGSTR
    : Msgstr
    ;

mode key;

COMMENT2
    : Comment -> skip
    ;

WHITESPACE2
    : Whitespace -> skip
    ;

KEY
    : String
    ;

MSGCTEXT2
    : 'msgctxt' -> mode(context)
    ;

MSGID2
    : Msgid -> mode(key)
    ;

MSGSTR2
    : Msgstr -> popMode
    ;

mode context;

COMMENT3
    : Comment -> skip
    ;

WHITESPACE3
    : Whitespace -> skip
    ;

DIRECTIVE
    : String
    ;

MSGCTEXT3
    : 'msgctxt' -> mode(context)
    ;

MSGID3
    : Msgid -> mode(key)
    ;

MSGSTR3
    : Msgstr -> popMode
    ;

fragment Comment
    : '#' (~[\r\n])* 
    ;

fragment Whitespace
    :  [ \t\r\n]+
    ;

fragment String
    : '"' ((~[\\"])|'\\'.)* '"'
    ;

fragment Msgid
    : 'msgid'|'msgid_plural'
    ;

fragment Msgstr
    : 'msgstr' ('[' [0-9]+ ('..' ([0-9]+)?)? ']')?
    ;
