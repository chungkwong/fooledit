lexer grammar OcamlLexer;

KEYWORD
    :'and'|'as'('sert'|'r')?|'begin'|'class'|'constraint'|'do'('ne'|'wnto')?|'else'|'end'|'exception'|'external'|'for'|'fun'('ction'|'ctor')?|'if'|'in'('clude'|'herit'|'itializer')?|'land'|'lazy'|'let'|'lor'|'lsl'|'lsr'|'lxor'|'match'|'method'|'mod'('ule')?|'mutable'|'new'|'nonrec'|'object'|'of'|'open'|'or'|'private'|'rec'|'sig'|'struct'|'then'|'to'|'try'|'type'|'val'|'virtual'|'when'|'while'|'with'
    ;

BOOLEAN
    :'true'|'false'
    ;

CHARACTER
    : '\'' (~['\\]|Escape) '\''
    ;

STRING
    : '"' (~["\\]|Escape|'\\'('\r' '\n'?|'\n'))* '"'
    ;

IDENTIFIER
    : [A-Z][_0-9a-zA-Z']*
    | [?~]?[_a-z][_0-9a-zA-Z']*
    ;

FLOAT
    : '0'[xX][0-9a-fA-F][_0-9a-fA-F]*'.'[_0-9a-fA-F]*([pP][-+]?[0-9][_0-9]*)?|[0-9][_0-9]*'.'[_0-9]*([eE][-+]?[0-9][_0-9]*)?
    | '0'[xX][0-9a-fA-F][_0-9a-fA-F]*[pP][-+]?[0-9][_0-9]*|[0-9][_0-9]*[eE][-+]?[0-9][_0-9]*
    ;

INTEGER
    : '0'[xX][0-9a-fA-F][_0-9a-fA-F]*|'0'[oO][0-7][_0-7]*|'0'[bB][01][_01]*|[0-9][_0-9]*
    ;

COMMENT_START
    : '(*'->pushMode(comment)
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

OPERATOR
    : [-+*/$%=<>@^|&!]Operator*
    | [#?~]Operator+
    | '&&'|'||'|'::'|';;'|'..'|'!='|':='|':>'|'-.'|'->'|'<-'|'>]'|'>}'|'[<'|'[>'|'[|'|'{<'|'|]'
    | [-+*/_~`,':#=?&()[\]<>{}]
    ;

mode comment;

MORE_COMMENT
    : '('+ '*'->pushMode(comment)
    ;

COMMENT_END
    : '*'+ ')'->popMode
    ;

COMMENT
    : ((~[*(])|'*'+(~')')|'('+(~'*'))+
    ;

fragment Escape
    : '\\'([nrtb'" \\]|'x'[0-9a-fA-F][0-9a-fA-F]|'o'[0-3][0-7][0-7]|[0-9][0-9][0-9])
    ;

fragment Operator
    : [-+*/!$%&.:<=>?@^|~]
    ;
