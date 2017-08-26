grammar Bibtex;

file
    :
    ;

COMMENT:'%' (~[\r\n])* -> skip;
TYPE: '@' ( A R T I C L E | B O O K | B O O K L E T | C O N F E R E N C E | I N B O O K | I N C O L L E C T I O N | I N P R O C E E D I N G S | M A N U A L | M A S T E R S T H E S I S | M I S C | P H D T H E S I S | P R O C E E D I N G S | T E C H R E P O R T | U N P U B L I S H E D);
KEY:'address'|'annote'|'author'|'booktitle'|'chapter'|'crossref'|'edition'|'editor'|'howpublished'|'institution'|'journal'|'key'|'month'|'note'|'number'|'organization'|'pages'|'publisher'|'school'|'series'|'title'|'type'|'volume'|'year';
SEPARATOR:[=}{#"];
COMMAND: '\\' ((~[a-zA-Z])|[a-zA-Z]+);
PLAIN: (~[@%=}{#"\\])+;

fragment A:[aA];
fragment B:[bB];
fragment C:[cC];
fragment D:[dD];
fragment E:[eE];
fragment F:[fF];
fragment G:[gG];
fragment H:[hH];
fragment I:[iI];
fragment J:[jJ];
fragment K:[kK];
fragment L:[lL];
fragment M:[mM];
fragment N:[nN];
fragment O:[oO];
fragment P:[pP];
fragment Q:[qQ];
fragment R:[rR];
fragment S:[sS];
fragment T:[tT];
fragment U:[uU];
fragment V:[vV];
fragment W:[wW];
fragment X:[xX];
fragment Y:[yY];
fragment Z:[zZ];

