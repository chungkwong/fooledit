
/*
  Converted to ANTLR 4 by James Ladd (Redline Smalltalk Project http://redline.st).
  Adapted from the Amber Smalltalk grammar parser.pegjs
  
  2015/01/18 James Ladd (object@redline.st)
*/
grammar Smalltalk;

script
   : element+ EOF
   ;

element
   : class_def|global|pool|initializer
   ;

class_def
   : class_name superclass_name? instance_state? class_instance_variables_names? class_variable_names? imported_pool_names? instance_methods? class_methods? class_initializer?
   ;
class_name: IDENTIFIER;
superclass_name: IDENTIFIER;
instance_state: NUMBER|IDENTIFIER instance_variable_names;
instance_variable_names: IDENTIFIER*;
class_instance_variables_names: IDENTIFIER*;
class_variable_names:IDENTIFIER*;
imported_pool_names:IDENTIFIER*;
instance_methods: method*;
class_methods:method*;
class_initializer:initializer;

global
   : designator? IDENTIFIER initializer?
   ;
   
pool
   : IDENTIFIER pool_variable*
   ;
pool_variable
   : designator IDENTIFIER initializer?
   ;
designator
   : KEYWORD
   ;

method
   : pattern temporaries? statements
   ;
pattern
   : IDENTIFIER|BINARY_SELECTOR IDENTIFIER|(KEYWORD IDENTIFIER)+
   ;
temporaries
   : '|' IDENTIFIER* '|'
   ;

initializer
   : temporaries statements?|statements
   ;

block_constructor
   : '[' (block_argument* '|')? temporaries? statements? ']'
   ;
block_argument
   : ':' IDENTIFIER
   ;

statements
   : return_statement '.'?
   | expression ('.' statements)
   ;
return_statement
   : RETURN_OPERATOR expression
   ;

expression
   : assignment
   | basic_expression
   ;
assignment
   : IDENTIFIER ASSIGNMENT_OPERATOR expression
   ;
basic_expression
   : primary (messages cascaded_messages)?
   ;
primary
   : IDENTIFIER
   | literal
   | block_constructor
   | '(' expression ')'
   ;
   

messages
   : unary_message+ binary_message* keyword_message?
   | binary_message+ keyword_message?
   | keyword_message
   ;
unary_message:IDENTIFIER;
binary_message:BINARY_SELECTOR primary unary_message*;
keyword_message:(KEYWORD primary unary_message* binary_message*)+;
cascaded_messages: (';' messages)*;

literal
   : SELECTOR | '-'? NUMBER | CHARACTER | STRING | SYMBOL | array
   ;

array
   : '#(' (literal|IDENTIFIER|RESERVED_WORD)* ')'
   ;

NUMBER
   : (([2-9]|[12]?[0-9]|'3'[0-6]) 'r')? [0-9A-Z]+ 
   | [0-9]+ ('.' [0-9]+)? ([edq] '-'? [0-9]+|'s' [0-9]*)?
   ;

STRING
   : '\'' (~'\''|'\'\'')* '\''
   ;

SYMBOL
   : '#' STRING  
   ;

CHARACTER
   : '$' .
   ;

BINARY_SELECTOR
   : [-&!%*+,/<=>?\\~|]+
   ;

SELECTOR
   : '#' (IDENTIFIER|BINARY_SELECTOR|KEYWORD+)
   ;

RESERVED_WORD
   : 'nil' | 'true' | 'false' | 'self' | 'super'
   ;


IDENTIFIER
   : [a-zA-Z_] [a-zA-Z0-9_]*
   ;


RETURN_OPERATOR
   : '^'
   ;

ASSIGNMENT_OPERATOR
   : ':='
   ;

KEYWORD
   : IDENTIFIER ':'
   ;

SEPARATOR
   : (WHITESPACE|COMMENT)+ ->channel(HIDDEN)
   ;

fragment WHITESPACE
   : [ \t\r\n]
   ;
fragment COMMENT
   : '"' .*? '"'
   ;
