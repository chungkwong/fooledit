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

lexer grammar TclLexer;

START
    : -> pushMode(expression),skip
    ;

mode expression;

COMMENT
    : '#' (~[\r\n\\]|'\\'(~[\r\n])|'\\'('\r''\n'?|'\n'))* ->channel(HIDDEN)
    ;

PLAIN
    : (~[}{[\];"\r\n\t $\\#]|'\\'.)+
    ;

WHITESPACE
    : [ \t] -> skip
    ;

VARIABLE
    : Variable
    ;

VARIABLE_START
    : IndexedVariable -> pushMode(index)
    ;

START_QUOTE
    : '{' -> pushMode(quoted)
    ;

START_STRING
    : '"' -> pushMode(string)
    ;

START_EXPRESSION
    : '[' -> pushMode(expression)
    ;

END_EXPRESSION
    : ']' -> popMode
    ;

SEPARATOR
    : [;\r\n]+
    ;

mode quoted;

MORE_QUOTE
    : '{' -> pushMode(quoted)
    ;

LESS_QUOTE
    : '}' ->popMode
    ;

QUOTED
    : ((~[\\}{])|'\\'.)+
    ;

mode string;

END_STRING
    : '"' -> popMode
    ;

STRING
    : ((~["\\[])|'\\'.)+
    ;

START_EXPRESSION_IN_STRING
    : '[' -> pushMode(expression)
    ;

VARIABLE_IN_STRING
    : Variable
    ;

VARIABLE_START_IN_STRING
    : IndexedVariable -> pushMode(index)
    ;

mode index;

END_INDEX
    : ')' -> popMode
    ;

INDEX
    : ((~[)\\[])|'\\'.)+
    ;

START_EXPRESSION_IN_INDEX
    : '[' -> pushMode(expression)
    ;

VARIABLE_IN_INDEX
    : Variable
    ;

VARIABLE_START_IN_INDEX
    : IndexedVariable -> pushMode(index)
    ;

fragment Variable
    : '$' ([_a-zA-Z0-9]*|'{'(~'}')*'}')
    ;

fragment IndexedVariable
    : '$' [_a-zA-Z0-9]* '('
    ;
