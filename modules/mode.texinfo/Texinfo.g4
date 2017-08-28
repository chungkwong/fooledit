grammar Texinfo;

COMMENT
    : '@c' ([\r\n]|(~[a-ZA-Z\r\n]) (~[\r\n])*)
    ;

NAME
    : '@' ([a-ZA-Z]+|[-!"'*,./:=?@^`}{~\u0080-\uFFFF])
    ;

SEPARATOR
    : [}{]
    ;

PLAIN
    : (~[}{@])+
    ;