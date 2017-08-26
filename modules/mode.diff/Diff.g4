Grammar Diff;

KEY:[-<][ \t](~[\r\n])*;
VALUE:[+>!][ \t](~[\r\n])*;
PLAIN:' ' (~[\r\n])*;
DIRECTIVE:([^-+<>!\r\n ]|'--')(~[\r\n])*
WHITESPACE: [\r\n]+;