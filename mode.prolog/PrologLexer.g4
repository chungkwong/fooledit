/*
BSD License

Copyright (c) 2013, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Tom Everett nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

lexer grammar PrologLexer;

/*
program 
    : clauselist query?
    ;

clauselist 
    :  clause*
    ;

clause 
    : term '.'
    ;

termlist 
    : term | termlist ',' term
    ;

term 
    : number | atom | VARIABLE | compound | list | expression
    ;

list
    : '[' termlist ('|' term)? ']'
    ;

expression
    : '(' term ')'
    | term (NAME|',') term
    ;

number
    : '-'? (INTEGER | FLOAT)
    ;

compound 
    : atom '(' termlist ')'
    | '{' term '}'
    ;

query 
    : '?-' termlist '.'
    ;

atom 
    : NAME | emptylist | emptybracket
    ;

emptylist
    : '[' ']'
    ;

emptybracket
    : '{' '}'
    ;
*/

NAME: [a-z][_A-Za-z0-9]*|[-#$&*+.\\:<=>?@^~][-#$&*+./\\:<=>?@^~]?|'/' [-#$&+./\\:<=>?@^~]?|'\'' (NONQUOTE|'"'|'`'|'\'\''|'\n'|'\r')+ '\''|'!'|';'
    ;

VARIABLE
    : [_A-Z][_a-zA-Z0-9]*
    ;

FLOAT
    : [0-9]+'.'[0-9]+([eE][-+]?[0-9]+)?
    ;

INTEGER
    : '0b'[01]+|'0o'[0-7]+|'0x'[a-zA-Z]+|'0\''(NONQUOTE|['"`])|[0-9]+
    ;

STRING
    : '"' (NONQUOTE|'\''|'`'|'""'|'\\n'|'\\r')* '"'
    | '`' (NONQUOTE|'"'|'\''|'``'|'\\n'|'\\r')* '`'
    ;

WS
    : [ \t\r\n] -> skip
    ;

COMMENT
    : ('%' (~[\r\n])*
    |'/*' .*? '*/') -> skip
    ;

SEPARATOR
    : [)(\][}{|,.]
    ;

fragment NONQUOTE
    : '\\' [abfnrtv\\`'"]|'\\'[0-7]+'\\'|'\\x'[0-9a-fA-F]+'\\'| ~[`'"\\]
    ;
