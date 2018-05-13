grammar Texinfo;

doc
    : (COMMENT|NAME|SEPARATOR|PLAIN)*
    ;

COMMENT
    : '@c' ([\r\n]|(~[a-zA-Z\r\n]) (~[\r\n])*)
    ;

NAME
    : '@' ([a-zA-Z]+|[-!"'*,./:=?@^`}{~\u0080-\uFFFF])
    ;

SEPARATOR
    : [}{]
    ;

PLAIN
    : (~[}{@])+
    ;
