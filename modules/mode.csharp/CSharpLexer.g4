// Eclipse Public License - v 1.0, http://www.eclipse.org/legal/epl-v10.html
// Copyright (c) 2013, Christian Wulf (chwchw@gmx.de)
// Copyright (c) 2016-2017, Ivan Kochurkin (kvanttt@gmail.com), Positive Technologies.

lexer grammar CSharpLexer;

@lexer::header
{import java.util.Stack;}

channels { COMMENTS_CHANNEL, DIRECTIVE }

@lexer::members
{private int interpolatedStringLevel;
private Stack<Boolean> interpolatedVerbatiums = new Stack<Boolean>();
private Stack<Integer> curlyLevels = new Stack<Integer>();
private boolean verbatium;
}

BYTE_ORDER_MARK: '\u00EF\u00BB\u00BF';

SINGLE_LINE_DOC_COMMENT: '///' InputCharacter*    -> channel(COMMENTS_CHANNEL);
DELIMITED_DOC_COMMENT:   '/**' .*? '*/'           -> channel(COMMENTS_CHANNEL);
SINGLE_LINE_COMMENT:     '//'  InputCharacter*    -> channel(COMMENTS_CHANNEL);
DELIMITED_COMMENT:       '/*'  .*? '*/'           -> channel(COMMENTS_CHANNEL);

WHITESPACES:   (Whitespace | NewLine)+            -> channel(HIDDEN);
SHARP:         '#'                                -> mode(DIRECTIVE_MODE);

ABSTRACT:      'abstract';
ADD:           'add';
ALIAS:         'alias';
ARGLIST:       '__arglist';
AS:            'as';
ASCENDING:     'ascending';
ASYNC:         'async';
AWAIT:         'await';
BASE:          'base';
BOOL:          'bool';
BREAK:         'break';
BY:            'by';
BYTE:          'byte';
CASE:          'case';
CATCH:         'catch';
CHAR:          'char';
CHECKED:       'checked';
CLASS:         'class';
CONST:         'const';
CONTINUE:      'continue';
DECIMAL:       'decimal';
DEFAULT:       'default';
DELEGATE:      'delegate';
DESCENDING:    'descending';
DO:            'do';
DOUBLE:        'double';
DYNAMIC:       'dynamic';
ELSE:          'else';
ENUM:          'enum';
EQUALS:        'equals';
EVENT:         'event';
EXPLICIT:      'explicit';
EXTERN:        'extern';
FALSE:         'false';
FINALLY:       'finally';
FIXED:         'fixed';
FLOAT:         'float';
FOR:           'for';
FOREACH:       'foreach';
FROM:          'from';
GET:           'get';
GOTO:          'goto';
GROUP:         'group';
IF:            'if';
IMPLICIT:      'implicit';
IN:            'in';
INT:           'int';
INTERFACE:     'interface';
INTERNAL:      'internal';
INTO:          'into';
IS:            'is';
JOIN:          'join';
LET:           'let';
LOCK:          'lock';
LONG:          'long';
NAMEOF:        'nameof';
NAMESPACE:     'namespace';
NEW:           'new';
NULL:          'null';
OBJECT:        'object';
ON:            'on';
OPERATOR:      'operator';
ORDERBY:       'orderby';
OUT:           'out';
OVERRIDE:      'override';
PARAMS:        'params';
PARTIAL:       'partial';
PRIVATE:       'private';
PROTECTED:     'protected';
PUBLIC:        'public';
READONLY:      'readonly';
REF:           'ref';
REMOVE:        'remove';
RETURN:        'return';
SBYTE:         'sbyte';
SEALED:        'sealed';
SELECT:        'select';
SET:           'set';
SHORT:         'short';
SIZEOF:        'sizeof';
STACKALLOC:    'stackalloc';
STATIC:        'static';
STRING:        'string';
STRUCT:        'struct';
SWITCH:        'switch';
THIS:          'this';
THROW:         'throw';
TRUE:          'true';
TRY:           'try';
TYPEOF:        'typeof';
UINT:          'uint';
ULONG:         'ulong';
UNCHECKED:     'unchecked';
UNSAFE:        'unsafe';
USHORT:        'ushort';
USING:         'using';
VAR:		   'var';
VIRTUAL:       'virtual';
VOID:          'void';
VOLATILE:      'volatile';
WHEN:          'when';
WHERE:         'where';
WHILE:         'while';
YIELD:         'yield';

//B.1.6 Identifiers
// must be defined after all keywords so the first branch (Available_identifier) does not match keywords
// https://msdn.microsoft.com/en-us/library/aa664670(v=vs.71).aspx
IDENTIFIER:          '@'? IdentifierOrKeyword;

//B.1.8 Literals
// 0.Equals() would be parsed as an invalid real (1. branch) causing a lexer error
LITERAL_ACCESS:      [0-9]+ IntegerTypeSuffix? '.' '@'? IdentifierOrKeyword;
INTEGER_LITERAL:     [0-9]+ IntegerTypeSuffix?;
HEX_INTEGER_LITERAL: '0' [xX] HexDigit+ IntegerTypeSuffix?;
REAL_LITERAL:        [0-9]* '.' [0-9]+ ExponentPart? [FfDdMm]? | [0-9]+ ([FfDdMm] | ExponentPart [FfDdMm]?);

CHARACTER_LITERAL:                   '\'' (~['\\\r\n\u0085\u2028\u2029] | CommonCharacter) '\'';
REGULAR_STRING:                      '"'  (~["\\\r\n\u0085\u2028\u2029] | CommonCharacter)* '"';
VERBATIUM_STRING:                    '@"' (~'"' | '""')* '"';
INTERPOLATED_REGULAR_STRING_START:   '$"'
    { interpolatedStringLevel++; interpolatedVerbatiums.push(false); verbatium = false; } -> pushMode(INTERPOLATION_STRING);
INTERPOLATED_VERBATIUM_STRING_START: '$@"'
    { interpolatedStringLevel++; interpolatedVerbatiums.push(true); verbatium = true; }  -> pushMode(INTERPOLATION_STRING);

//B.1.9 Operators And Punctuators
OPEN_BRACE:               '{'
{
if (interpolatedStringLevel > 0)
{
    curlyLevels.push(curlyLevels.pop() + 1);
}};
CLOSE_BRACE:              '}'
{
if (interpolatedStringLevel > 0)
{
    curlyLevels.push(curlyLevels.pop() - 1);
    if (curlyLevels.peek() == 0)
    {
        curlyLevels.pop();
        skip();
        popMode();
    }
}
};
OPEN_BRACKET:             '[';
CLOSE_BRACKET:            ']';
OPEN_PARENS:              '(';
CLOSE_PARENS:             ')';
DOT:                      '.';
COMMA:                    ',';
COLON:                    ':'
{
if (interpolatedStringLevel > 0)
{
    int ind = 1;
    boolean switchToFormatString = true;
    while ((char)_input.LA(ind) != '}')
    {
        if (_input.LA(ind) == ':' || _input.LA(ind) == ')')
        {
            switchToFormatString = false;
            break;
        }
        ind++;
    }
    if (switchToFormatString)
    {
        mode(INTERPOLATION_FORMAT);
    }
}
};
SEMICOLON:                ';';
PLUS:                     '+';
MINUS:                    '-';
STAR:                     '*';
DIV:                      '/';
PERCENT:                  '%';
AMP:                      '&';
BITWISE_OR:               '|';
CARET:                    '^';
BANG:                     '!';
TILDE:                    '~';
ASSIGNMENT:               '=';
LT:                       '<';
GT:                       '>';
INTERR:                   '?';
DOUBLE_COLON:             '::';
OP_COALESCING:            '??';
OP_INC:                   '++';
OP_DEC:                   '--';
OP_AND:                   '&&';
OP_OR:                    '||';
OP_PTR:                   '->';
OP_EQ:                    '==';
OP_NE:                    '!=';
OP_LE:                    '<=';
OP_GE:                    '>=';
OP_ADD_ASSIGNMENT:        '+=';
OP_SUB_ASSIGNMENT:        '-=';
OP_MULT_ASSIGNMENT:       '*=';
OP_DIV_ASSIGNMENT:        '/=';
OP_MOD_ASSIGNMENT:        '%=';
OP_AND_ASSIGNMENT:        '&=';
OP_OR_ASSIGNMENT:         '|=';
OP_XOR_ASSIGNMENT:        '^=';
OP_LEFT_SHIFT:            '<<';
OP_LEFT_SHIFT_ASSIGNMENT: '<<=';

// https://msdn.microsoft.com/en-us/library/dn961160.aspx
mode INTERPOLATION_STRING;

DOUBLE_CURLY_INSIDE:           '{{';
OPEN_BRACE_INSIDE:             '{' { curlyLevels.push(1); } -> skip, pushMode(DEFAULT_MODE);
REGULAR_CHAR_INSIDE:           { !verbatium }? SimpleEscapeSequence;
VERBATIUM_DOUBLE_QUOTE_INSIDE: {  verbatium }? '""';
DOUBLE_QUOTE_INSIDE:           '"' { interpolatedStringLevel--; interpolatedVerbatiums.pop();
    verbatium = (interpolatedVerbatiums.size() > 0 ? interpolatedVerbatiums.peek() : false); } -> popMode;
REGULAR_STRING_INSIDE:         { !verbatium }? ~('{' | '\\' | '"')+;
VERBATIUM_INSIDE_STRING:       {  verbatium }? ~('{' | '"')+;

mode INTERPOLATION_FORMAT;

DOUBLE_CURLY_CLOSE_INSIDE:      '}}' -> type(FORMAT_STRING);
CLOSE_BRACE_INSIDE:             '}' { curlyLevels.pop(); }   -> skip, popMode;
FORMAT_STRING:                  ~'}'+;

mode DIRECTIVE_MODE;

DIRECTIVE_WHITESPACES:         Whitespace+                      -> channel(HIDDEN);
DIGITS:                        [0-9]+                           -> channel(DIRECTIVE);
DIRECTIVE_TRUE:                'true'                           -> channel(DIRECTIVE), type(TRUE);
DIRECTIVE_FALSE:               'false'                          -> channel(DIRECTIVE), type(FALSE);
DEFINE:                        'define'                         -> channel(DIRECTIVE);
UNDEF:                         'undef'                          -> channel(DIRECTIVE);
DIRECTIVE_IF:                  'if'                             -> channel(DIRECTIVE), type(IF);
ELIF:                          'elif'                           -> channel(DIRECTIVE);
DIRECTIVE_ELSE:                'else'                           -> channel(DIRECTIVE), type(ELSE);
ENDIF:                         'endif'                          -> channel(DIRECTIVE);
LINE:                          'line'                           -> channel(DIRECTIVE);
ERROR:                         'error' Whitespace+              -> channel(DIRECTIVE), mode(DIRECTIVE_TEXT);
WARNING:                       'warning' Whitespace+            -> channel(DIRECTIVE), mode(DIRECTIVE_TEXT);
REGION:                        'region' Whitespace*             -> channel(DIRECTIVE), mode(DIRECTIVE_TEXT);
ENDREGION:                     'endregion' Whitespace*          -> channel(DIRECTIVE), mode(DIRECTIVE_TEXT);
PRAGMA:                        'pragma' Whitespace+             -> channel(DIRECTIVE), mode(DIRECTIVE_TEXT);
DIRECTIVE_DEFAULT:             'default'                        -> channel(DIRECTIVE), type(DEFAULT);
DIRECTIVE_HIDDEN:              'hidden'                         -> channel(DIRECTIVE);
DIRECTIVE_OPEN_PARENS:         '('                              -> channel(DIRECTIVE), type(OPEN_PARENS);
DIRECTIVE_CLOSE_PARENS:        ')'                              -> channel(DIRECTIVE), type(CLOSE_PARENS);
DIRECTIVE_BANG:                '!'                              -> channel(DIRECTIVE), type(BANG);
DIRECTIVE_OP_EQ:               '=='                             -> channel(DIRECTIVE), type(OP_EQ);
DIRECTIVE_OP_NE:               '!='                             -> channel(DIRECTIVE), type(OP_NE);
DIRECTIVE_OP_AND:              '&&'                             -> channel(DIRECTIVE), type(OP_AND);
DIRECTIVE_OP_OR:               '||'                             -> channel(DIRECTIVE), type(OP_OR);
DIRECTIVE_STRING:              '"' ~('"' | [\r\n\u0085\u2028\u2029])* '"' -> channel(DIRECTIVE), type(STRING);
CONDITIONAL_SYMBOL:            IdentifierOrKeyword              -> channel(DIRECTIVE);
DIRECTIVE_SINGLE_LINE_COMMENT: '//' ~[\r\n\u0085\u2028\u2029]*  -> channel(COMMENTS_CHANNEL), type(SINGLE_LINE_COMMENT);
DIRECTIVE_NEW_LINE:            NewLine                          -> channel(DIRECTIVE), mode(DEFAULT_MODE);

mode DIRECTIVE_TEXT;

TEXT:                          ~[\r\n\u0085\u2028\u2029]+       -> channel(DIRECTIVE);
TEXT_NEW_LINE:                 NewLine    -> channel(DIRECTIVE), type(DIRECTIVE_NEW_LINE), mode(DEFAULT_MODE);

// Fragments

fragment InputCharacter:       ~[\r\n\u0085\u2028\u2029];

fragment NewLineCharacter
	: '\u000D' //'<Carriage Return CHARACTER (U+000D)>'
	| '\u000A' //'<Line Feed CHARACTER (U+000A)>'
	| '\u0085' //'<Next Line CHARACTER (U+0085)>'
	| '\u2028' //'<Line Separator CHARACTER (U+2028)>'
	| '\u2029' //'<Paragraph Separator CHARACTER (U+2029)>'
	;

fragment IntegerTypeSuffix:         [lL]? [uU] | [uU]? [lL];
fragment ExponentPart:              [eE] ('+' | '-')? [0-9]+;

fragment CommonCharacter
	: SimpleEscapeSequence
	| HexEscapeSequence
	| UnicodeEscapeSequence
	;

fragment SimpleEscapeSequence
	: '\\\''
	| '\\"'
	| '\\\\'
	| '\\0'
	| '\\a'
	| '\\b'
	| '\\f'
	| '\\n'
	| '\\r'
	| '\\t'
	| '\\v'
	;

fragment HexEscapeSequence
	: '\\x' HexDigit
	| '\\x' HexDigit HexDigit
	| '\\x' HexDigit HexDigit HexDigit
	| '\\x' HexDigit HexDigit HexDigit HexDigit
	;

fragment NewLine
	: '\r\n' | '\r' | '\n'
	| '\u0085' // <Next Line CHARACTER (U+0085)>'
	| '\u2028' //'<Line Separator CHARACTER (U+2028)>'
	| '\u2029' //'<Paragraph Separator CHARACTER (U+2029)>'
	;

fragment Whitespace
	: [\p{Zs}] //'<Any Character With Unicode Class Zs>'
	| '\u0009' //'<Horizontal Tab Character (U+0009)>'
	| '\u000B' //'<Vertical Tab Character (U+000B)>'
	| '\u000C' //'<Form Feed Character (U+000C)>'
	;
fragment IdentifierOrKeyword
	: IdentifierStartCharacter IdentifierPartCharacter*
	;

fragment IdentifierStartCharacter
	: LetterCharacter
	| '_'
	;

fragment IdentifierPartCharacter
	: LetterCharacter
	| DecimalDigitCharacter
	| ConnectingCharacter
	| CombiningCharacter
	| FormattingCharacter
	;

//'<A Unicode Character Of Classes Lu, Ll, Lt, Lm, Lo, Or Nl>'
// WARNING: ignores UnicodeEscapeSequence
fragment LetterCharacter
	: [\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}]
	| UnicodeEscapeSequence
	;

//'<A Unicode Character Of The Class Nd>'
// WARNING: ignores UnicodeEscapeSequence
fragment DecimalDigitCharacter
	: [\p{Nd}]
	| UnicodeEscapeSequence
	;

//'<A Unicode Character Of The Class Pc>'
// WARNING: ignores UnicodeEscapeSequence
fragment ConnectingCharacter
	: [\p{Pc}]
	| UnicodeEscapeSequence
	;

//'<A Unicode Character Of Classes Mn Or Mc>'
// WARNING: ignores UnicodeEscapeSequence
fragment CombiningCharacter
	: [\p{Mn}\p{Mc}]
	| UnicodeEscapeSequence
	;

//'<A Unicode Character Of The Class Cf>'
// WARNING: ignores UnicodeEscapeSequence
fragment FormattingCharacter
	: [\p{Cf}]
	| UnicodeEscapeSequence
	;

//B.1.5 Unicode Character Escape Sequences
fragment UnicodeEscapeSequence
	: '\\u' HexDigit HexDigit HexDigit HexDigit
	| '\\U' HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit HexDigit
	;

fragment HexDigit : [0-9] | [A-F] | [a-f];

