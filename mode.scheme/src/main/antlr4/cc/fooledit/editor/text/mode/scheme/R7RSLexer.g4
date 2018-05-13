/*
 [The "BSD licence"]
 Copyright (c) 2017 Chan Chung Kwong
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

lexer grammar R7RSLexer;

COMMENT_START
    : '#|' -> pushMode(comment),skip
    ;

LINE_COMMENT
    : ';' (~[\r\n])* -> skip
    ;

DATUM_COMMENT
    : '#;'
    ;

WHITESPACE
    : [ \t\r\n]+
    ;

BOOLEAN
    : '#t'|'#f'|'#true'|'#false'
    ;
    
CHARACTER
    : '#\\' ('x'[0-9a-fA-F]+|'alarm'|'backspace'|'delete'|'escape'|'newline'|'null'|'return'|'space'|'tab'|.)
    ;
    
STRING
    : '"' (StringChar)* '"'
    ;

NUMBER
    : Num2|Num8|Num16|Num10
    ;


BEGIN
    : '('
    ;

BEGIN_VECTOR
    : '#('
    ;

BEGIN_BYTEVECTOR
    : '#u8('
    ;

END
    : ')'
    ;

DOT
    : '.'
    ;

GET_LABEL
   : '#' [0-9]+ '#'
   ;

SET_LABEL
   : '#' [0-9]+ '='
   ;

ABBREV
    : '\''|'`'|','|',@'
    ;

IDENTIFIER
    : Initial Subsequent*
    | Peculiar
    | '|' SymbolChar* '|'
    ;

mode comment;

MORE_COMMENT
    : '#'+ '|' -> pushMode(comment),skip
    ;

COMMENT_END
    : '|'+ '#' -> popMode,skip
    ;

COMMENT
    : ((~[|#])|'#'+(~[#|])|'|'+(~[|#]))+ ->skip
    ;

fragment Mnemonic
    :'\\'[abtrn]
    ;
fragment SymbolChar
    :((~[|\\])|'\\'([abtrn"\\]|'x'[0-9a-fA-F]+))
    ;
fragment Initial
    :[a-zA-Z!$%*/:<=>?^_~&\u200C\u200D\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Mn}\p{Nl}\p{No}\p{Pd}\p{Pc}\p{Po}\p{Sc}\p{Sm}\p{Sk}\p{So}\p{Co}]
    ;
fragment Subsequent
    :(Initial|[0-9@.\p{Nd}\p{Mc}\p{Me}])
    ;
fragment Peculiar
    :[-+](([-+@]|Initial)Subsequent*)?|[-+]?'.'([-+@.]|Initial)Subsequent*
    ;
fragment StringChar
    :(~["\\])|'\\'([abtrn"\\]|'x'[0-9a-fA-F]+|[ \t]*('\r'|'\n'|'\r\n')[ \\t]*)
    ;
fragment Infnan
    :[-+]('inf'|'nan')'.0'
    ;
fragment Decimal
    :([0-9]+('.'[0-9]*)|'.'[0-9]+)([eE][-+]?[0-9]+)?|[0-9]+[eE][-+]?[0-9]+
    ;
fragment Digit2
    :[01]
    ;
fragment Digit8
    :[0-7]
    ;
fragment Digit10
    :[0-9]
    ;
fragment Digit16
    :[0-9a-fA-F]
    ;
fragment Ureal2
    :(Digit2+('/' Digit2+)?|Decimal)
    ;
fragment Real2
    :([-+]?Ureal2|Infnan)
    ;
fragment Num2
    :('#'[bB]('#'[ieIE])?|'#'[ieIE]'#'[bB])(Real2([-+]Ureal2?'i'|Infnan 'i'|'@' Real2|'i')?|[-+]'i')
    ;
fragment Ureal8
    :(Digit8+(/Digit8+)?|Decimal)
    ;
fragment Real8
    :([-+]?Ureal8|Infnan)
    ;
fragment Num8
    :('#'[oO]('#'[ieIE])?|'#'[ieIE]'#'[oO])(Real8([-+]Ureal8?'i'|Infnan 'i'|'@'Real8|'i')?|[-+]'i')
    ;
fragment Ureal10
    :(Decimal|Digit10+(/Digit10+)?)
    ;
fragment Real10
    :([-+]?Ureal10|Infnan)
    ;
fragment Num10
    :('#'[dD]('#'[ieIE])?|'#'[ieIE]('#'[dD])?)?(Real10([-+]Ureal10?'i'|Infnan 'i'|'@'Real10|'i')?|[-+]'i')
    ;
fragment Ureal16
    :(Digit16+(/Digit16+)?|Decimal)
    ;
fragment Real16
    :([-+]?Ureal16|Infnan)
    ;
fragment Num16
    :('#'[xX]('#'[ieIE])?|'#'[ieIE]'#'[xX])(Real16([-+]Ureal16?'i'|Infnan 'i'|'@' Real16|'i')?|[-+]'i')
    ;

