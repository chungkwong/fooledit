grammar Sed;


FROM_LINE
    : Number('~'Number)? -> pushMode(comma)
    ;

FROM_REGEX
    : (\\\\(.).*?\\2|/([^/\\\\]|\\\\.)*/)[IM]* -> pushMode(comma)
    ;

FROM_END
    : '$' ->pushMode(comma)
    ;

COMMENT
    : '#' (~[\r\n])*
    ;

END_BLOCK
    : '}'
    ;

LABEL
    : ':'
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

SEMI
    : ';'
    ;

NO_ADDRESS
    : '' -> pushMode(command)

mode comma;

COMMA
    : ','->mode(address)
    ;

EMPTY
    : ''->mode(command)
    ;

mode address;
	    {"old state":"address2","new state":"rangeCommand","type":"integer","regex":"Number(~Number)?|[+~]Number"},
	    {"old state":"address2","new state":"rangeCommand","type":"regex","regex":"(\\\\(.).*?\\2|/([^/\\\\]|\\\\.)*/)[IM]*"},
	    {"old state":"address2","new state":"rangeCommand","type":"keyword","regex":"\\$"},
	    {"old state":"command","new state":"address1","type":"command","regex":"[\\{dDhHnNpPtTx]"},
	    {"old state":"command","new state":"filename","type":"command","regex":"[rRwW]"},
	    {"old state":"command","new state":"label","type":"command","regex":"[btT]"},
	    {"old state":"command","new state":"text","type":"command","regex":"[aic]"},
	    {"old state":"command","new state":"ss0","type":"command","regex":"s"},
	    {"old state":"command","new state":"ys0","type":"command","regex":"y"},
	    {"old state":"command","new state":"width","type":"command","regex":"l"},
	    {"old state":"command","new state":"exit","type":"command","regex":"[qQ]"},
	    {"old state":"rangeCommand","new state":"address1","type":"command","regex":"[\\{dDhHnNpPtTx=]"},
	    {"old state":"rangeCommand","new state":"filename","type":"command","regex":"[wW]"},
	    {"old state":"rangeCommand","new state":"label","type":"command","regex":"[btT]"},
	    {"old state":"rangeCommand","new state":"text","type":"command","regex":"[c]"},
	    {"old state":"rangeCommand","new state":"ss0","type":"command","regex":"s"},
	    {"old state":"rangeCommand","new state":"ys0","type":"command","regex":"y"},
	    {"old state":"rangeCommand","new state":"width","type":"command","regex":"l"},
	    {"old state":"label","new state":"address1","type":"name","regex":"[^;\\r\\n]+"},
	    {"old state":"text","new state":"address1","type":"string","regex":"[^;\\r\\n]+"},
	    {"old state":"filename","new state":"address1","type":"url","regex":"[^;\\r\\n]+"},
	    {"old state":"exit","new state":"address1","type":"integer","regex":"[0-9]*"},
	    {"old state":"width","new state":"address1","type":"integer","regex":"[0-9]*"},
	    {"old state":"ss0","new state":"replace","type":"other","regex":"/"},
	    {"old state":"replace","new state":"ss1","type":"regex","regex":"([^/\\\\]|\\\\.)*"},
	    {"old state":"ss1","new state":"replacement","type":"other","regex":"/"},
	    {"old state":"replacement","new state":"ss2","type":"string","regex":"([^/\\\\]|\\\\.)*"},
	    {"old state":"ss2","new state":"flag","type":"other","regex":"/"},
	    {"old state":"flag","new state":"address1","type":"other","regex":"[0-9giImMpe]*(w[^;]*)?"},
	    {"old state":"ys0","new state":"src","type":"other","regex":"/"},
	    {"old state":"src","new state":"ys1","type":"char","regex":"([^/\\\\]|\\\\.)*"},
	    {"old state":"ys1","new state":"dest","type":"other","regex":"/"},
	    {"old state":"dest","new state":"ys2","type":"char","regex":"([^/\\\\]|\\\\.)*"},
	    {"old state":"ys2","new state":"address1","type":"other","regex":"/"}
	]
}

fragment Number
    : [0-9]+
    ;