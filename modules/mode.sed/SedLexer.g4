lexer grammar SedLexer;


FROM_LINE: Number('~'Number)?'!'? -> pushMode(comma);
FROM_REGEX: '/'(~[/\\]|'\\'.)*'/'[IM]*'!'? -> pushMode(comma);
FROM_END: '$' '!'?->pushMode(comma);
COMMENT: '#' (~[\r\n])*;
END_BLOCK: '}';
LABEL_MARK: ':'->pushMode(label);
WHITESPACE: [ \t\r\n]+;
SEMI: ';';
NO_ADDRESS:  -> pushMode(command);

mode comma;

COMMA: ','->mode(address);
EMPTY: ->mode(command);

mode address;

TO_LINE:(Number('~'Number)?|[+~]Number)'!'?->mode(rangeCommand);
TO_REGEX:'/'(~[/\\]|'\\'.)*'/'[IM]* '!'?->mode(rangeCommand);
TO_END:'$' '!'?->mode(rangeCommand);

mode command;

COMMAND: [{dDgGhHnNpPtTx]->popMode;
FILE_COMMAND:[rRwW]->mode(file);
LABEL_COMMAND:[btT]->mode(label);
TEXT_COMMAND:[aic]->mode(text);
REPLACE_COMMAND:'s'->mode(replace0);
TR_COMMAND:'y'->mode(tr0);
WIDTH_COMMAND:'l'->mode(width);
EXIT_COMMAND:[qQ]->mode(exit);

mode rangeCommand;

COMMAND_RANGE:[{dDgGhHnNpPtTx=]->popMode;
FILE_COMMAND_RANGE:[wW]->mode(file);
LABEL_COMMAND_RANGE:[btT]->mode(label);
TEXT_COMMAND_RANGE:'c'->mode(text);
REPLACE_COMMAND_RANGE:'s'->mode(replace0);
TR_COMMAND_RANGE:'y'->mode(tr0);
WIDTH_COMMAND_RANGE:'l'->mode(width);

mode label;
LABEL: (~[;\r\n])+ -> popMode;
mode text;
TEXT: (~[\r\n])+ -> popMode;
mode file;
FILE: (~[;\r\n])+ -> popMode;
mode exit;
EXIT_CODE:[0-9]* -> popMode;
mode width;
WIDTH:[0-9]* -> popMode;

mode replace0;
REPLACE_SEP1:'/'->mode(replace1);
mode replace1;
REPLACE: (~[/\\]|'\\'.)*->mode(replace2);
mode replace2;
REPLACE_SEP2:'/'->mode(replace3);
mode replace3;
REPLACEMENT:(~[/\\]|'\\'.)*->mode(replace4);
mode replace4;
REPLACE_SEP3:'/'->mode(replace5);
mode replace5;
REPLACE_FLAG:[0-9giImMpe]*('w'(~';')*)?->popMode;

mode tr0;
TR_SEP1:'/'->mode(tr1);
mode tr1;
TR_OLD:(~[/\\]|'\\'.)*->mode(tr2);
mode tr2;
TR_SEP2:'/'->mode(tr3);
mode tr3;
TR_NEW:(~[/\\]|'\\'.)*->mode(tr4);
mode tr4;
TR_SEP3:'/'->popMode;

fragment Number : [0-9]+;
