grammar Awk;

program
    :
    ;

KEYWORD: 'function'|('BEGIN'|'END')'FILE'?|'if'|'while'|'for'|'break'|'continue'|'delete'|'exit'|'switch'|'case'|'default'|'in'|'return';
VARIABLE: 'ARGC'|'ARGIND'|'ARGV'|'BINMODE'|'CONVFMT'|'ENVIRON'|'ERRNO'|'FIELDWIDTHS'|'FILENAME'|'FNR'|'FPAT'|'FS'|'FUNCTAB'|'IGNORECASE'|'LINT'|'NF'|'NR'|'OFMT'|'OFS'|'ORS'|'PREC'|'PROCINFO'|'ROUNDMODE'|'RS'|'RT'|'RSTAT'|'RLENGTH'|'SUBSEP'|'SYMTAB'|'TEXTDOMAIN';
FUNCTION: 'close'|'getline'|'next'|'nextfile'|'print'|'printf'|'system'|'fflush'|'atan2'|'cos'|'exp'|'int'|'log'|'rand'|'sin'|'sqrt'|'srand'|'asort'|'asorti'|'gensub'|'gsub'|'index'|'length'|'match'|'patsplit'|'split'|'sprintf'|'strtonum'|'sub'|'substr'|'tolower'|'toupper'|'mktime'|'strftime'|'systime'|'and'|'compl'|'lshift'|'or'|'rshift'|'xor'|'isarray'|'bindtextdomain'|'dcngettext'|'dcgettext';
DIRECTIVE: '@include'|'@load';
COMMENT: '#' (~[\r\n])* -> skip;
WHITESPACE: [ \t\r\n]+ -> skip;
STRING:'"' ((~["\\])|'\\'.)* '"';
IDENTIFIER: [_a-zA-Z][_0-9a-zA-Z]*;
REGEX: '/' ((~[/\\])|'\\'.)* '/';
FLOAT: '0'[xX][0-9a-fA-F]+|([0-9]+('.'[0-9]*)?|'.'[0-9]+)([eE][-+]?[0-9]+)? ;
OPERATOR: [-+*/%^<>!?:|&=~$];
SEPARATOR: [;,}{[\])(];
