grammar Ada;

program
    :
    ;

KEYWORD: 'abort'|'abs'|'abstract'|'accept'|'access'|'aliased'|'all'|'and'|'array'|'at'|'begin'|'body'|'case'|'constant'|'declare'|'delay'|'delta'|'digits'|'do'|'else'|'elsif'|'end'|'entry'|'exception'|'exit'|'for'|'function'|'generic'|'goto'|'if'|'in'|'interface'|'is'|'limited'|'loop'|'mod'|'new'|'not'|'null'|'of'|'or'|'others'|'out'|'overriding'|'package'|'pragma'|'private'|'procedure'|'protected'|'raise'|'range'|'record'|'rem'|'renames'|'requeue'|'return'|'reverse'|'select'|'separate'|'some'|'subtype'|'synchronized'|'tagged'|'task'|'terminate'|'then'|'type'|'until'|'use'|'when'|'while'|'with'|'xor';

STRING: '"' ((~'"')|'""')* '"';

CHARACTER: '\'' (~'\'') '\'';

FLOAT: Numeral10 '#' Numeral ('.' Numeral)?([eE][-+]?Numeral10)? |
       Numeral10 ('.' Numeral10)?([eE][-+]?Numeral10)?;

IDENTIFIER: [\p{Lu}\p{Ll}\p{Lt}\p{Lo}\p{Lm}\p{Nl}][\p{Lu}\p{Ll}\p{Lt}\p{Lo}\p{Lm}\p{Nl}\p{Mn}\p{Mc}\p{Nd}\p{Pc}]* ;

COMMENT: '--' (~[\r\n])*;

WHITESPACE: [ \t\r\n]+;

OPERATOR: '=>'|'..'|'**'|':='|'/='|'>='|'<='|'<<'|'>>'|'<>'|[-+*/\\&'.<=>|];

SEPARATOR: [,;)(:];

fragment Numeral10: [0-9]('_'?[0-9])* ;
fragment Numeral: [0-9a-fA-F]('_'?[0-9a-fA-F])* ;
