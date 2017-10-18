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

/** XML parser derived from ANTLR v4 ref guide book example */
parser grammar XMLParser;

options { tokenVocab=XMLLexer; }

document    :   prolog? element misc*;

prolog      :   xmlDecl? misc* (doctypeDecl misc*)? ;
xmlDecl     :   '<?xml' versionInfo encodingDecl? sdDecl? s? '?>' ;
versionInfo :   s name {$name.text.equals("version")}? EQ (startApos versionNum endApos|startQuot versionNum endQuot) ;
versionNum  :   PlainTextInApos {XMLSupport.isVersionNumber($PlainTextInApos.text)}?| PlainTextInQuot {XMLSupport.isVersionNumber($PlainTextInQuot.text)}?;
sdDecl      :   s name {$name.text.equals("standalone")}? EQ (startApos PlainTextInApos {XMLSupport.isYesNo($name.text)}? endApos|startQuot PlainTextInQuot {XMLSupport.isYesNo($name.text)}? endQuot) ;
misc        :   COMMENT | PI | s ;

doctypeDecl :   '<!' 'DOCTYPE' s name (s externalID)? s? ('[' intSubset ']' s?)? DTD_CLOSE;
declSep     :   PERef | s;
intSubset   :   (markupDecl | declSep)* ;
markupDecl  :   elementDecl | attlistDecl | entityDecl | notationDecl | PI | COMMENT;

extSubset   :   textDecl? extSubsetDecl;
extSubsetDecl
            :   (markupDecl|conditionalSect|declSep)* ;

element     :   sTag content eTag
            |   emptyElemTag
            ;
emptyElemTag:   '<' name (s attribute)* s? SLASH_CLOSE;
sTag        :   '<' name (s attribute)* s? CLOSE;
eTag        :   '<' '/' name s? CLOSE;
attribute   :   name EQ attValue;
content     :   charData?
                ((element | reference | CHARDATA | PI | COMMENT) charData?)* ;

elementDecl :   '<!' 'ELEMENT' s name s contentSpec s? DTD_CLOSE;
contentSpec :   'EMPTY' | 'ANY' | mixed | children;
children    :   (choice|seq) ('?'|'*'|'+')?;
cp          :   (name|choice|seq) ('?'|'*'|'+')?;
choice      :   '(' s? cp (s? '|' s? cp)+ s? ')';
seq         :   '(' s? cp (s? ',' s? cp)* s? ')';
mixed       :   '(' s? '#' 'PCDATA' (s? '|' s? name)* s? ')' '*'
            |   '(' s? '#' 'PCDATA' s? ')'
            ;

attlistDecl :   '<!' 'ATTLIST' s name attDef* s? DTD_CLOSE;
attDef      :   s name s attType s defaultDecl;
attType     :   stringType|tokenizedType|enumeratedType;
stringType  :   'CDATA';
tokenizedType
            :   'ID'|'IDREF'|'IDREFS'|'ENTITY'|'ENTITIES'|'NMTOKEN'|'NMTOKENS';
enumeratedType
            :   notationType|enumeration;
notationType:   'NOTATION' s '(' s? name (s? '|' s? name)* s? ')';
enumeration :   '(' s? (Nmtoken|NameInDTD) (s? '|' s? (Nmtoken|NameInDTD))* s? ')';
defaultDecl :   '#' 'REQUIRED'|'#' 'IMPLIED'|('#' 'FIXED' s)?attValue;

conditionalSect
            :   includeSect|ignoreSect;
includeSect :   CondDeclOpen s? 'INCLUDE' s? '[' extSubsetDecl ']]>';
ignoreSect  :   CondDeclOpen s? 'IGNORE' s? '[' ignoreSectContents ']]>';
ignoreSectContents
            :   ignore (MORE_SECT ignoreSectContents ']]>' ignore)*
            ;
ignore      :   (START_QUOT_IN_DTD|START_APOS_IN_DTD|name|DS|DTD_CLOSE|PlainTextInAposDTD|ReferenceInAposDTD|EndAposInDTD|PlainTextInQuotDTD|ReferenceInQuotDTD|EndQuotInDTD)*;

entityDecl  :   geDecl|peDecl;
geDecl      :   '<!' 'ENTITY' s name s entityDef s? DTD_CLOSE;
peDecl      :   '<!' 'ENTITY' s '%' s name s peDef s? DTD_CLOSE;
entityDef   :   entityValue|externalID nDataDecl?;
peDef       :   entityValue|externalID;
externalID  :   'SYSTEM' s systemLiteral
            |   'PUBLIC' s pubidLiteral systemLiteral;
nDataDecl   :   s 'NDATA' s name;

textDecl    :   '<?xml' versionInfo? encodingDecl s? '?>';
extParsedEnt:   textDecl? content;
encodingDecl:   s name {$name.text.equals("encoding")}? EQ (startQuot encName endQuot | startApos encName endApos ) ;
encName     :   PlainTextInApos {XMLSupport.isEncName($PlainTextInApos.text)}?|PlainTextInQuot{XMLSupport.isEncName($PlainTextInQuot.text)}?;

notationDecl:   '<!' 'NOTATION' s name s (externalID | publicID) s? DTD_CLOSE;
publicID    :   'PUBLIC' s pubidLiteral;

reference   :   EntityRef | CharRef ;

/** ``All text that is not markup constitutes the character data of
 *  the document.''
 */
charData    :   TEXT;
s           :   TS|DS;
name        :   NameInTag|NameInDTD|'DOCTYPE'|'ELEMENT'|'EMPTY'|'ANY'|'PCDATA'|'ATTLIST'|'CDATA'|'ID'|'IDREF'|'IDREFS'|'ENTITY'|'ENTITIES'|'NMTOKEN'|'NMTOKENS'|'NOTATION'|'REQUIRED'|'IMPLIED'|'FIXED'|'INCLUDE'|'IGNORE'|'SYSTEM'|'PUBLIC'|'NDATA';
entityValue :   startQuot (ReferenceInQuotDTD|PlainTextInQuotDTD)* endQuot
            |   startApos (ReferenceInAposDTD|PlainTextInAposDTD)* endApos
            ;
attValue    :   startQuot (ReferenceInQuot|PlainTextInQuot)* endQuot
            |   startApos (ReferenceInApos|PlainTextInApos)* endApos
            ;
systemLiteral
            :   startQuot (ReferenceInAposDTD|PlainTextInAposDTD)* endQuot
            |   startApos (ReferenceInAposDTD|PlainTextInAposDTD)* endApos
            ;
pubidLiteral
            :   startQuot pubid endQuot
            |   startApos pubid endApos
            ;
startQuot   : START_QUOT_IN_DTD|START_QUOT_IN_TAG;
endQuot     : EndQuotInDTD|EndQuotInTag;
startApos   : START_APOS_IN_DTD|START_APOS_IN_TAG;
endApos     : EndAposInDTD|EndAposInTag;
pubid       : PlainTextInAposDTD {XMLSupport.isPublicId($PlainTextInAposDTD.text)}?|PlainTextInQuotDTD{XMLSupport.isPublicId($PlainTextInQuotDTD.text)}?;
