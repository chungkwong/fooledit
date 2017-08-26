lexer grammar Tex;

COMMAND: Command;
COMMENT: Comment;
SEPARATOR: '$' ->pushMode(inline);
SEPARATOR: '$$' ->pushMode(display);
PLAIN: Plain;
OTHER: Other;

mode inline;

COMMAND: Command;
COMMENT: Comment;
SEPARATOR: '$' ->popMode;
PLAIN: Plain;
OTHER: Other;

mode display;

COMMAND: Command;
COMMENT: Comment;
SEPARATOR: '$$' ->popMode;
PLAIN: Plain;
OTHER: Other;

fragment Command: '\\' ((~[a-zA-Z])|[a-zA-Z]+);
fragment Comment: '%' (~[\r\n])*;
fragment Plain: (~[^%\\}{&^_~#$])+;
fragment Other: [}{&^_~#];

