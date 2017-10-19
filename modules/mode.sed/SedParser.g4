parser grammar SedParser;

options { tokenVocab=SedLexer; }

script
   : command* EOF
   ;

command
   : firstAddress oneArgCommand
   | firstAddress secondAddress twoArgCommand
   | LABEL_MARK LABEL
   | END_BLOCK
   ;

firstAddress
   : FROM_LINE|FROM_REGEX|FROM_END
   ;
   
secondAddress
   : TO_LINE|TO_REGEX|TO_END
   ;

oneArgCommand
   : COMMAND
   | FILE_COMMAND FILE
   | LABEL_COMMAND LABEL
   | TEXT_COMMAND TEXT
   | REPLACE_COMMAND REPLACE REPLACEMENT REPLACE_FLAG
   | TR_COMMAND TR_OLD TR_NEW
   | WIDTH_COMMAND WIDTH
   | EXIT_COMMAND EXIT_CODE
   ;

twoArgCommand
   : COMMAND_RANGE
   | FILE_COMMAND_RANGE FILE
   | LABEL_COMMAND_RANGE LABEL
   | TEXT_COMMAND_RANGE TEXT
   | REPLACE_COMMAND_RANGE REPLACE REPLACEMENT REPLACE_FLAG
   | TR_COMMAND_RANGE TR_OLD TR_NEW
   | WIDTH_COMMAND_RANGE WIDTH
   ;
