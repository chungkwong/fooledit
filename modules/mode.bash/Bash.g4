grammar Bash;

program
    :
    ;

KEYWORD:'!'|'case'|'do'|'done'|'elif'|'else'|'esac'|'fi'|'for'|'function'|'if'|'in'|'select'|'then'|'until'|'while'|'{'|'}'|'time'|'[['|']]';
COMMENT:'#'(~[\r\n])*;
REGEX:[ \t\r\n]+;
STRING
    :'$'? '\'' (~'\'')* '\''
    |'$'? '"' ((~["\\])|'\\'.)* '"'
    ;
VARIABLE
    :'$'[_a-zA-Z][_0-9a-zA-Z]*
    |'$'[-0-9@*#?$!]
    |'$((' (~')')* '))'
    |'$(' (~')')* ')'
    |'${' (~'}')* '}'
    |'`' ((~[`\\])|'\\'.)* '`'
    ;
SEPARATOR: [<>;|&)([\]];
PLAIN: (~[#`'"$<>;|&)([\] \t\r\n])(~[`'"$<>;|&)( \t\r\n])*;
