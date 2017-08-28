grammar R7RS;

COMMENT_START
    : '#|' -> pushMode(comment),skip
    ;

LINE_COMMENT
    : ';(~[\r\n])*'|'#;'

WHITESPACE
    : [ \t\r\n]+
    ;

BOOLEAN
    : '#t'|'#f'|'#true'|'#false'
    ;
    
CHARACTER
    : '#\\' (x[0-9a-fA-F]+|'alarm'|'backspace'|'delete'|'escape'|'newline'|'null'|'return'|'space'|'tab'|.)
    ;
    
STRING
    : '"' (StringChar)* '"'
    ;

FLOAT
    : Num2|Num8|Num16|Num10
    ;


SEPARATOR
    : [)('`,.]|'#('|'#u8('|',@'
    ;

IDENTIFIER
    : Initial Subsequent*
    | Peculiar
    | '|' SymbolChar* '|'
    ;

mode comment;

MORE_COMMENT
    : '#'+ '|' -> pushMode(comment),skip
    ;

COMMENT_END
    : '|'+ '#' -> popMode,skip
    ;

COMMENT
    : ((~[|#])|'#'+(~[#|])|'|'+(~[|#]))+
    ;

fragment Mnemonic
    :'\\'[abtrn]
    ;
fragment SymbolChar
    :(~[|\\])|\\\\([abtrn\"\\\\]|x[0-9a-fA-F]+)
    ;
fragment Initial
    :([a-zA-Z!$%*/:<=>?^_~&\u200C\u200D]|Lu|Ll|Lt|Lm|Lo|Mn|Nl|No|Pd|Pc|Po|Sc|Sm|Sk|So|Co)
    ;
fragment Subsequent
    :(Initial|[0-9@.]|Nd|Mc|Me
    ;
fragment Peculiar
    :[-+](([-+@]|Initial)Subsequent*)?|[-+]?'.'([-+@.]|Initial)Subsequent*
    ;
fragment StringChar
    :(~["\\])|'\\'([abtrn"\\]|'x'[0-9a-fA-F]+|[ \t]*('\r'|'\n'|'\r\n')[ \\t]*)
    ;
fragment Infnan
    :[-+]('inf'|'nan')'.0'
    ;
fragment Decimal
    :([0-9]+(\\.[0-9]*)|\\.[0-9]+)([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
    ;
fragment Digit2
    :[01]
    ;
fragment Digit8
    :[0-7]
    ;
fragment Digit10
    :[0-9]
    ;
fragment Digit16
    :[0-9a-fA-F]
    ;
fragment Ureal2
    :(Digit2+('/' Digit2+)?|Decimal)
    ;
fragment Real2
    :([-+]?Ureal2|Infnan)
    ;
fragment Num2
    :('#'[bB]('#'[ieIE])?|'#'[ieIE]'#'[bB])(Real2([-+]Ureal2?'i'|Infnan 'i'|'@' Real2|'i')?|[-+]'i')
    ;
fragment Ureal8
    :(Digit8+(/Digit8+)?|Decimal)
    ;
fragment Real8
    :([-+]?Ureal8|Infnan)
    ;
fragment Num8
    :(#[oO](#[ieIE])?|#[ieIE]#[oO])(Real8([-+]Ureal8?'i'|Infnan 'i'|'@'Real8|'i')?|[-+]'i')
    ;
fragment Ureal10
    :(Decimal|Digit10+(/Digit10+)?)
    ;
fragment Real10
    :([-+]?Ureal10|Infnan)
    ;
fragment Num10
    :(#[dD](#[ieIE])?|#[ieIE](#[dD])?)?(Real10([-+]Ureal10?'i'|Infnan 'i'|'@'Real10|'i')?|[-+]'i')
    ;
fragment Ureal16
    :(Digit16+(/Digit16+)?|Decimal)
    ;
fragment Real16
    :([-+]?Ureal16|Infnan)
    ;
fragment Num16
    :(#[xX](#[ieIE])?|#[ieIE]#[xX])(Real16([-+]Ureal16?'i'|Infnan 'i'|'@' Real16|'i')?|[-+]'i')
    ;

