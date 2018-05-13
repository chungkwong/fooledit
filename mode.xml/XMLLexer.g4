/*
 [The "BSD licence"]
 Copyright (c) 2013 Terence Parr
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

/** XML lexer derived from ANTLR v4 ref guide book example */
lexer grammar XMLLexer;

// Default "mode": Everything OUTSIDE of a tag
COMMENT     :   '<!--' ('-'? ~'-')* '-->' ;
CHARDATA       :   '<![CDATA[' .*? ']]>' ;
/** Scarf all DTD stuff, Entity Declarations like <!ENTITY ...>,
 *  and Notation Declarations <!NOTATION ...>
 */
EntityRef   :   ENTITY_REF;
CharRef     :   CHAR_REF;
PERef       :   PE_REF;

XMLDeclOpen :   '<?xml'                 -> pushMode(TAG) ;
DTDDeclOpen :   '<!'                    -> pushMode(DTD) ;
CondDeclOpen:   '<!['                   -> pushMode(DTD) ;
OPEN        :   '<'                     -> pushMode(TAG) ;
PI          :   '<?' NAME (S .*?)? '?>';

TEXT        :   ~[<&]+ ;        // match any 16 bit char other than < and &

// ----------------- Everything TAG of a tag ---------------------
mode TAG;

CLOSE       :   '>'                     -> popMode ;
SPECIAL_CLOSE:  '?>'                    -> popMode ; // close <?xml...?>
SLASH_CLOSE :   '/>'                    -> popMode ;
SLASH       :   '/' ;
EQ          :   '=' ;
START_QUOT_IN_TAG
            :   '"'                     -> pushMode(QUOT_IN_TAG)
            ;
START_APOS_IN_TAG
            :   '\''                    -> pushMode(APOS_IN_TAG)
            ;
NameInTag   :   NAME ;
TS          :   S;

mode DTD;

START_GROUP : '(';
END_GROUP   : ')';
START_BLOCK : '['                       -> pushMode(DTD);
END_BLOCK   : ']'                       -> popMode;
OPTIONAL    : '?';
ZERO_PLUS   : '*';
ONE_PLUS    : '+';
PARAMETER   : '%';
OR          : '|';
CONCAT      : ',';
KEY         : '#';
DOCTYPE     : 'DOCTYPE';
ELEMENT     : 'ELEMENT';
EMPTY       : 'EMPTY';
ANY         : 'ANY';
PCDATA      : 'PCDATA';
ATTLIST     : 'ATTLIST';
CDATA       : 'CDATA';
ID          : 'ID';
IDREF       : 'IDREF';
IDREFS      : 'IDREFS';
ENTITY      : 'ENTITY';
ENTITIES    : 'ENTITIES';
NMTOKEN     : 'NMTOKEN';
NMTOKENS    : 'NMTOKENS';
NOTATION    : 'NOTATION';
REQUIRED    : 'REQUIRED';
IMPLIED     : 'IMPLIED';
FIXED       : 'FIXED';
INCLUDE     : 'INCLUDE';
IGNORE      : 'IGNORE';
SYSTEM      : 'SYSTEM';
PUBLIC      : 'PUBLIC';
NDATA       : 'NDATA';

MORE_SECT   :   '<!['                   -> pushMode(DTD);
END_SECT    :   ']]>'                   -> popMode;
START_QUOT_IN_DTD
            :   '"'                     -> pushMode(QUOT_IN_DTD)
            ;
START_APOS_IN_DTD
            :   '\''                    -> pushMode(APOS_IN_DTD)
            ;
NameInDTD   :   NAME ;
Nmtoken     :   NameChar+;
DS          :   S;
DTD_CLOSE   :   '>'                     -> popMode ;

mode APOS_IN_TAG;

PlainTextInApos
            : ~[<'&]+
            ;
ReferenceInApos
            : ENTITY_REF|CHAR_REF
            ;
EndAposInTag: '\''                      -> popMode;

mode QUOT_IN_TAG;

PlainTextInQuot
            : ~[<"&]+
            ;
ReferenceInQuot
            : ENTITY_REF|CHAR_REF
            ;
EndQuotInTag: '"'                      -> popMode;

mode APOS_IN_DTD;

PlainTextInAposDTD
            : ~[<'&%]+
            ;
ReferenceInAposDTD
            : ENTITY_REF|CHAR_REF|PE_REF
            ;
EndAposInDTD: '\''                      -> popMode;

mode QUOT_IN_DTD;

PlainTextInQuotDTD
            : ~[<"&%]+
            ;
ReferenceInQuotDTD
            : ENTITY_REF|CHAR_REF|PE_REF
            ;
EndQuotInDTD: '"'                      -> popMode;


fragment
ENTITY_REF  : '&' NAME ';' ;

fragment
PE_REF      : '%' NAME ';' ;

fragment
CHAR_REF     :   '&#' DIGIT+ ';'
            |   '&#x' HEXDIGIT+ ';'
            ;

fragment
HEXDIGIT    :   [a-fA-F0-9] ;

fragment
DIGIT       :   [0-9] ;

fragment
NAME        :   NameStartChar NameChar* ;
fragment
S           :   [ \t\r\n]+;


fragment
NameChar    :   NameStartChar
            |   [-.0-9\u00B7\u0300-\u036F\u203F-\u2040]
            ;

fragment
NameStartChar
            :   [:a-zA-Z_\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\u{10000}-\u{EFFFF}]
            ;
            
fragment
Char        : [\u0009\u000A\u000D\u0020-\uD7FF\uE000-\uFFFD\u{10000}-\u{10FFFF}];

