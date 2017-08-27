grammar Haskell;

KEYWORD
    : Keyword
    ;

FLOAT
    : [0-9]+\\.[0-9]+([eE][-+]?[0-9]+)?
    | [0-9]+[eE][-+]?[0-9]+
    ;

INTEGER
    : '0'[oO][0-7]+|'0'[xX][0-9a-fA-F]+|[0-9]+
    ;

CHARACTER
    : '\'' ([^'\\]|Escape) '\''
    ;

STRING
    : \"([^\"\\\\&&[^\\p{javaWhitespace}]]| |{escape}|\\\\&|\\\\\\p{javaWhitespace}+\\\\)*\"
    ;

NAME
    : {"old state":"init","new state":"init","type":"name","regex":"\\p{javaUpperCase}[\\p{javaLowerCase}\\p{javaUpperCase}[0-9]']*|:[{symbol}]*({op})?
    ;

VARIABLE
    : \\p{javaLowerCase}[\\p{javaLowerCase}\\p{javaUpperCase}[0-9]']*({keyword})?|([{symbol}&&[^:]][\\p{Sc}\\p{Sm}\\p{Sk}\\p{So}])({op}|-{2,})?
    ;

WHITESPACE
    : \\p{javaWhitespace}+
    ;

SEPARATOR
    : '..'|'::'|'<-'|'->'|'=>'|[)(,;[\]`{}:=\\@~|]
    ;

LINE_COMMENT
    : '--' (~[\r\n]) -> skip;
    ;

COMMENT_START
    : '{-' -> pushMode(comment),skip;
    ;

mode comment;

COMMENT_START
    : '{'+ '-' -> pushMode(comment),skip;
    ;

COMMENT_END
    : '-'+ '}' -> popMode,skip;
    ;

COMMENT
    : ((~[-{])|'-'+(~'}'|'{'+(~'-')))+
    ;


fragment Escape: ","\\\\([abfnrtv\\\\\"']|\\^[A-Z@\\[\\]\\\\_\\^]|NUL'|'SOH'|'STX'|'ETX'|'EOT'|'ENQ'|'ACK'|'BEL'|'BS'|'HT'|'LF'|'VT'|'FF'|'CR'|'SO'|'SI'|'DLE'|'DC1'|'DC2'|'DC3'|'DC4'|'NAK'|'SYN'|'ETB'|'CAN'|'EM'|'SUB'|'ESC'|'FS'|'GS'|'RS'|'US'|'SP'|'DEL|o[0-7]+|x[0-9a-fA-F]+|[0-9]+);
fragment Keyword:'case'|'class'|'data'|'default'|'deriving'|'do'|'else'|'foreign'|'if'|'import'|'in('fix'[lr]?'|'stance')?'|'let'|'module'|'newtype'|'of'|'then'|'type'|'where'|'_;
fragment Op:'..'|'::'|'<-'|'->'|'=>'|[:=\\@~|]",
fragment Symbol:!#\\$%\\&⋆+\\./<=>?@\\\\^\\|\\-~:\\p{Sc}\\p{Sm}\\p{Sk}\\p{So} ;
		  