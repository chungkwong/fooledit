grammar Makefile;

COMMENT
    : '#' (~[\r\n])*
    ;

KEYWORD
    : 'define'|'else'|'endef'|'endif'|'ifeq'|'ifneq'|'ifdef'|'ifndef'
    ;

OPERATOR
    : Assign
    ;

SEPARATOR
    : [(),]
    ;

STRING
    : '\'' ((~['\\])|'\\'.)* '\''
    | '"' ((~['\\])|'\\'.)* '"'
    ;

VARIABLE
    : '$(' (~')')* ')'
    | '${' (~'}')* '}'
    ;

COMMAND
    : ((~[+?='" \t\r\n$:\\(),])|'\\'.)+
    ;

DEPEND
    : ':'|'::' -> pushMode(prerequisites)
    ;

WHITESPACE
    : [ \t\r\n]
    ;

mode prerequisites;

LOCAL_OPERATOR
    : Assign
    ;
FILE
    : ([^\\r\\n\\\\;\\$]|\\\\.)+
    ;

LOCAL_VARIABLE
    : '$'[@%<?^+*$]
    | '$(' (~')')* ')'
    | '${' (~'}')* '}'
    ;

START_RECIPE
    : ';'|('\r'('\n')?|'\n')'\t' ->mode(recipe)

EXIT_PREREQUISITES
    : [\r\n] ->popMode

mode recipe;

PLAIN
    : ([^\\r\\n\\\\]|\\\\.)+|$(\\r(\\n)?|\\n)\\t
    ;

NEXT_RECIPE
    : ('\r'('\n')?|'\n')'\t' ->mode(recipe)

EXIT_RECIPE
    : [\r\n] -> popMode,skip
    ;

fragment Assign
    : '+='|'?='|'='
    ;