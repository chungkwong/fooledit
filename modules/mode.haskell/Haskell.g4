grammar Haskell;



mode comment;

    "shorthands":["comment0","\\{-([^-]|-+(?!\\}))*-\\}",
		  "comment1","\\{-([^-\\{\\}]|-+(?!\\})|\\{+(?!-)|{comment0})*-\\}",
		  "comment2","\\{-([^-\\{\\}]|-+(?!\\})|\\{+(?!-)|{comment1})*-\\}",
		  "comment3","\\{-([^-\\{\\}]|-+(?!\\})|\\{+(?!-)|{comment2})*-\\}",
		  "comment4","\\{-([^-\\{\\}]|-+(?!\\})|\\{+(?!-)|{comment3})*-\\}"],
    	"rules":[
		{"old state":"init","new state":"init","type":"keyword","regex":"{keyword}"},
	    {"old state":"init","new state":"init","type":"float","regex":"\\p{javaDigit}+\\.\\p{javaDigit}+([eE][-+]?\\p{javaDigit}+)?"},
	    {"old state":"init","new state":"init","type":"float","regex":"\\p{javaDigit}+[eE][-+]?\\p{javaDigit}+"},
		{"old state":"init","new state":"init","type":"integer","regex":"0[oO][0-7]+|0[xX][0-9a-fA-F]+|\\p{javaDigit}+"},
		{"old state":"init","new state":"init","type":"char","regex":"'([^'\\\\]|{escape})'"},
		{"old state":"init","new state":"init","type":"string","regex":"\"([^\"\\\\&&[^\\p{javaWhitespace}]]| |{escape}|\\\\&|\\\\\\p{javaWhitespace}+\\\\)*\""},
		{"old state":"init","new state":"init","type":"name","regex":"\\p{javaUpperCase}[\\p{javaLowerCase}\\p{javaUpperCase}\\p{javaDigit}']*|:[{symbol}]*({op})?"},
		{"old state":"init","new state":"init","type":"variable","regex":"\\p{javaLowerCase}[\\p{javaLowerCase}\\p{javaUpperCase}\\p{javaDigit}']*({keyword})?|([{symbol}&&[^:]][\\p{Sc}\\p{Sm}\\p{Sk}\\p{So}])({op}|-{2,})?"},
		{"old state":"init","new state":"init","type":"whitespace","regex":"\\p{javaWhitespace}+"},
	    {"old state":"init","new state":"init","type":"comment","regex":"{comment4}"},
		{"old state":"init","new state":"init","type":"other","regex":"\\.\\.|::|<-|->|=>|[\\(\\),;\\[\\]`\\{\\}:=\\\\@~\\|]"},
COMMENT: '--' (~[\r\n]) -> skip;

fragment Escape: ","\\\\([abfnrtv\\\\\"']|\\^[A-Z@\\[\\]\\\\_\\^]|NUL'|'SOH'|'STX'|'ETX'|'EOT'|'ENQ'|'ACK'|'BEL'|'BS'|'HT'|'LF'|'VT'|'FF'|'CR'|'SO'|'SI'|'DLE'|'DC1'|'DC2'|'DC3'|'DC4'|'NAK'|'SYN'|'ETB'|'CAN'|'EM'|'SUB'|'ESC'|'FS'|'GS'|'RS'|'US'|'SP'|'DEL|o[0-7]+|x[0-9a-fA-F]+|\\p{javaDigit}+);
fragment Keyword:'case'|'class'|'data'|'default'|'deriving'|'do'|'else'|'foreign'|'if'|'import'|'in('fix'[lr]?'|'stance')?'|'let'|'module'|'newtype'|'of'|'then'|'type'|'where'|'_;
fragment Op:'..'|'::'|'<-'|'->'|'=>'|[:=\\@~|]",
fragment Symbol:!#\\$%\\&⋆+\\./<=>?@\\\\^\\|\\-~:\\p{Sc}\\p{Sm}\\p{Sk}\\p{So} ;
		  