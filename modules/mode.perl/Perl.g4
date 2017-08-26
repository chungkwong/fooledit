grammar Perl;

KEYWORD:
	'__FILE__'|'__LINE__'|'__PACKAGE__'|'__SUB__'|'__DATA__'|'__END__'|'and'|'cmp'|'continue'|'dump'|'else'|'elsif'|'eq'|'for'|'foreach'|'ge'|'given'|'gt'|'goto'|'if'|'last'|'le'|'lt'|'ne'|'next'|'not'|'or'|'redo'|'unless'|'until'|'when'|'while'|'xor';

NAME:
	[_a-zA-Z][_a-zA-Z0-9]*;

VARIABLE:
	[@%$*](ID|'{' ID '}');

STRING:
	'\'' ((~[\'\\])|'\\' ['\\])* '\''|
	'"' ((~[\"\\])|'\\' .)* '"';

WHITESPACE:
	[ \t\r\n]+ ->skip;

COMMENT:
	'#' (~[\r\n])*-> skip;

FLOAT:
	(DEC('_'?DEC)*'.'(DEC('_'?DEC)*)?|'.'DEC('_'?DEC)*)(EXP10)?|'0'[xX](HEX('_'?HEX)*'.'?|(HEX('_'?HEX)*)?'.'(HEX('_'?HEX)*))EXP16|DEC(_?DEC)*EXP10;

INTEGER:
	[1-9]('_'?DEC)*|'0'[xX]HEX('_'?HEX)*|'0'[bB]BIN('_'?BIN)*|'0'('_'?OCT)*;

OPERATOR:
	'::'|'=>'|'->'|'::'|'..'|'.'|'-'|'+'|'*'|'/'|'%'|'\\'|'~'|'\''|'='|'?'|':'|'<'|'>'|'|'|'&'|'^'|'!';

SEPARATOR:
	'...'|','|';'|'('|')'|'['|']'|'{'|'}';

fragment ID:
	[_a-zA-Z]([_0-9a-zA-Z']|'::')* | '^' [A-Z^_?\\\\[\\]]|[0-9]+|[\p{Punct}&&[^}{]]);

fragment HEX:
	 [0-9a-fA-F];

fragment DEC:
	 [0-9];

fragment OCT:
	 [0-7];

fragment BIN:
	 [01];

fragment EXP10:
	 [eE][-+]?DEC+;

fragment EXP16:
	 [pP][-+]?DEC+;
