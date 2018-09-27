grammar Text;

text
 : token * EOF
 ;

token
 : WORD
 | INTEGER
 | WHITESPACE
 | OTHER
 ;

INTEGER
 : [\p{Nd}\p{No}\p{Nl}]+
 ;

WORD
 : [\p{Ll}\p{Lm}\p{Lo}\p{Lu}\p{Lt}]+
 ;
WHITESPACE
 : [\p{Mc}\p{Zl}\p{Zp}\p{Zs}]+
 ;
OTHER
 : [\p{Me}\p{Mn}\p{Pc}\p{Pd}\p{Pe}\p{Pf}\p{Pi}\p{Po}\p{Ps}\p{Sc}\p{Sk}\p{Sm}\p{So}]
 ;
