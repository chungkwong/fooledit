grammar Gettext;

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

COMMENT
    : Comment -> skip
    ;

WHITESPACE
    : Whitespace -> skip
    ;

KEY
    : String
    ;

MSGCTEXT
    : 'msgctxt' -> mode(context)
    ;

MSGID
    : Msgid -> mode(key)
    ;

MSGSTR
    : Msgstr -> popMode
    ;

mode context;

COMMENT
    : Comment -> skip
    ;

WHITESPACE
    : Whitespace -> skip
    ;

DIRECTIVE
    : String
    ;

MSGCTEXT
    : 'msgctxt' -> mode(context)
    ;

MSGID
    : Msgid -> mode(key)
    ;

MSGSTR
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