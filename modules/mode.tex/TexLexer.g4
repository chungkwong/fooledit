lexer grammar TexLexer;

COMMAND: Command;
COMMENT: Comment;
START_INLINE: '$' ->pushMode(inline);
START_DISPLAY: '$$' ->pushMode(display);
PLAIN: Plain;
OTHER: Other;

mode inline;

INLINE_COMMAND: Command;
INLINE_COMMENT: Comment;
END_INLINE: '$' ->popMode;
INLINE_PLAIN: Plain;
INLINE_OTHER: Other;

mode display;

DISPLAY_COMMAND: Command;
DISPLAY_COMMENT: Comment;
END_DISPLAY: '$$' ->popMode;
DISPLAY_PLAIN: Plain;
DISPLAY_OTHER: Other;

fragment Command: '\\' ((~[a-zA-Z])|[a-zA-Z]+);
fragment Comment: '%' (~[\r\n])*;
fragment Plain: (~[%\\}{&^_~#$])+;
fragment Other: [}{&^_~#];

