lexer grammar HaskellLexer;

KEYWORD
    : Keyword
    ;

FLOAT
    : [0-9]+'.'[0-9]+([eE][-+]?[0-9]+)?
    | [0-9]+[eE][-+]?[0-9]+
    ;

INTEGER
    : '0'[oO][0-7]+|'0'[xX][0-9a-fA-F]+|[0-9]+
    ;

CHARACTER
    : '\'' (~['\\]|Escape) '\''
    ;

STRING
    : '"' ((~["\\\t\r\n])|' '|Escape|'\\&'|'\\'[\p{Z}]+'\\')* '"'
    ;

NAME
    : [\p{Lu}] [0-9'\p{Ll}\p{Lu}]*|':'(Symbol|':')* Op?
    ;

VARIABLE
    : [\p{Ll}] [0-9'\p{Ll}\p{Lu}]* Keyword? | (Symbol[\p{Sc}\p{Sm}\p{Sk}\p{So}])(Op|('-' '-'+))?
    ;

WHITESPACE
    : [\p{Z}]+
    ;

SEPARATOR
    : '..'|'::'|'<-'|'->'|'=>'|[)(,;[\]`{}:=\\@~|]
    ;

LINE_COMMENT
    : '--' (~[\r\n]) -> channel(HIDDEN)
    ;

COMMENT_START
    : '{-' -> pushMode(comment),channel(HIDDEN)
    ;

mode comment;

MORE_COMMENT
    : '{'+ '-' -> pushMode(comment),channel(HIDDEN)
    ;

COMMENT_END
    : '-'+ '}' -> popMode,channel(HIDDEN)
    ;

COMMENT
    : ((~[-{])|'-'+(~'}'|'{'+(~'-')))+
    ;


fragment Escape: '\\' ([abfnrtv\\"']|'^'[A-Z@[\]\\_^]|'NUL'|'SOH'|'STX'|'ETX'|'EOT'|'ENQ'|'ACK'|'BEL'|'BS'|'HT'|'LF'|'VT'|'FF'|'CR'|'SO'|'SI'|'DLE'|'DC1'|'DC2'|'DC3'|'DC4'|'NAK'|'SYN'|'ETB'|'CAN'|'EM'|'SUB'|'ESC'|'FS'|'GS'|'RS'|'US'|'SP'|'DEL'|'o'[0-7]+|'x'[0-9a-fA-F]+|[0-9]+);
fragment Keyword:'case'|'class'|'data'|'default'|'deriving'|'do'|'else'|'foreign'|'if'|'import'|'in'('fix'[lr]?|'stance')?|'let'|'module'|'newtype'|'of'|'then'|'type'|'where'|'_';
fragment Op:'..'|'::'|'<-'|'->'|'=>'|[:=\\@~|];
fragment Symbol:[!#$%&⋆+./<=>?@\\^|-~\p{Sc}\p{Sm}\p{Sk}\p{So}];
