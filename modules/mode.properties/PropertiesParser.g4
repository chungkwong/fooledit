
parser grammar PropertiesParser;

options { tokenVocab=PropertiesLexer; }

propertiesFile
    : 
    row* ;

row 
    : 
    (comment | decl)? EOL;

decl
    : KEY SEPARATOR? VALUE
    ;

comment
    :
    COMMENT;

