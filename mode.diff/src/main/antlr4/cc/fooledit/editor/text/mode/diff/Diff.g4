grammar Diff;

file
    : row*
    ;

row
    : (KEY|VALUE|PLAIN|DIRECTIVE) WHITESPACE?
    ;

KEY:[-<](~[\r\n])*;
VALUE:[+>!](~[\r\n])*;
PLAIN:' ' (~[\r\n])*;
DIRECTIVE:(~[-+<>!\r\n ])(~[\r\n])*;
WHITESPACE: [\r\n]+;
