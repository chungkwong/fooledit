grammar EmacsLisp;

program
    :
    ;

COMMENT:';'(~[\r\n])* -> skip;
FLOAT: [-+]?([0-9]+'.'[0-9]*|'.'[0-9]+)([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+;
INTEGER:[-+]?[0-9]+;
CHARACTER:'?'('\\'[CMSHsA]'-'|'^')*(~'\\'|'\\'(~[uUx0-7]|'u'[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]|'U'[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]|'x'[0-9a-fA-F]+|[0-7](([0-7][0-7]?)?))) ;
STRING:'"' ((~["\\])|'\\'.)* '"';
SEPARATOR:'#' [[(] | [)([\]] ;
WHITESPACE: [ \t\r\n]+;
IDENTIFIER:((~[^\\ \t\r\n()[\]])|'\\'.)+;
