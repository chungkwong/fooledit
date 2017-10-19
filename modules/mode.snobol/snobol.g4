/*
[The "BSD licence"]
Copyright (c) 2012 Tom Everett
All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

grammar snobol;

prog
   : line*
   ;

line
   : statement (EOL|';')
   | control EOL
   | COMMENT EOL
   ;

statement
   : assign
   | match
   | repl
   | degen
   | end
   ;
   
control
   : '-' (('LIST' ('LEFT'|'RIGHT'))|'UNLIST'|'EJECT')
   ;
   
assign
   : label? subject '=' object? next?
   ;
match
   : label? subject '=' pattern next?
   ;
repl
   : label? subject pattern '=' object? next?
   ;
degen
   : label? subject?  next?
   ;
end
   : 'END' (label|'END')?
   ;

next
   : ':' (location|'S' location ('F' location)?|'F' location ('S' location)?)
   ;
location
   : '(' expression ')'
   | '<' expression '>'
   ;
function
   : IDENTIFIER '(' arglist ')'
   ;
arglist
   : expression (',' expression)*
   ;
expression
   : (element|operation)?
   ;
operation
   : element binary (element|expression)
   ;
element
   : unary* (IDENTIFIER|literal|function|reference|'(' expression ')')
   ;
unary
   : OPERATOR
   ;
binary
   : OPERATOR | '**'
   ;
literal
   : SLITERAL|DLITERAL|INTEGER|REAL
   ;
reference
   : IDENTIFIER '<' arglist '>'
   ;
subject
   : element
   ;
pattern
   : expression
   ;
object
   : expression
   ;

label
   : LABEL|IDENTIFIER
   ;

COMMENT
   : '*' ~ [\r\n]*
   ;
EOL
   : [\r\n] +
   ;

WS
   : (' ' | '\t'| EOL [+.]) + -> skip
   ;
IDENTIFIER
   : [a-zA-Z] [a-zA-Z0-9._]*
   ;
LABEL
   : [0-9](~[ \t;])*
   ; 
SLITERAL
   : '\'' (~'\'')* '\''
   ;
DLITERAL
   : '"' (~'"')* '"'
   ;
INTEGER
   : [0-9] +
   ;
REAL
   : INTEGER '.' INTEGER
   ;
OPERATOR
   : [-~|@?$.!%*/#+&]
   ;
