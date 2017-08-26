grammar Dot;

graph : 'strict'? ('graph' | 'digraph') ID? '{' stmt_list '}';
stmt_list : ( stmt  ';'? stmt_list )? ;
stmt : node_stmt | edge_stmt | attr_stmt | ID '=' ID | subgraph;
attr_stmt : (graph | node | edge) attr_list;
attr_list : '[' a_list? ']' attr_list? ;
a_list : ID '=' ID (';' | ',')? a_list? ;
edge_stmt : (node_id | subgraph) edgeRHS attr_list? ;
edgeRHS : edgeop (node_id | subgraph) edgeRHS?
edgeop : '--'|'->';
de_stmt : node_id attr_list? ;
node_id : ID port? ;
port : ':' ID (':' compass_pt)? | ':' compass_pt ;
subgraph : ('subgraph' ID?)? '{' stmt_list '}';
compass_pt : 'ne'|'se'|'sw'|'nw'|'n'|'e'|'s'|'w'|'c'|'_';


COMMENT
    : '#' (~[\r\n])*
    | '/*' .*? '*/' -> skip
    ;

WHITESPACE: [ \t\r\n]+ -> skip;

/*TYPE: 'strict'|'digraph'|'graph'|'subgraph'|'node'|'edge';

KEY:'arrowhead'|'arrowtail'|'bgcolor'|'center'|'color'|'constraint'|'decorate'|'dir'|'distortion'|'epsilon'|'fillcolor'|'fixedsize'|'fontcolor'|'fontname'|'fontsize'|'headclip'|'headhref'|'headlabel'|'headtarget'|'headtooltip'|'headURL'|'height'|'href'|'K'|'label'|'labelangle'|'labeldistance'|'layer'|'layout'|'len'|'margin'|'maxiter'|'mindist'|'minlen'|'nodesep'|'ordering'|'orientation'|'overlap'|'peripheries'|'pin'|'rank'|'rankdir'|'ranksep'|'ratio'|'regular'|'root'|'rotate'|'samehead'|'sametail'|'shape'|'sides'|'size'|'skew'|'splines'|'start'|'style'|'stylesheet'|'tailclip'|'tailhref'|'taillabel'|'tailtarget'|'tailtooltip'|'tailURL'|'target'|'tooltip'|'URL'|'w'|'weight'|'width';

OTHER: 'ne'|'se'|'sw'|'nw'|[neswc_];*/


ID: [_a-zA-Z\u{0080}-\u{10FFFF}][_0-9a-zA-Z\u{0080}-\u{10FFFF}]*|'-'?('.'[0-9]+|[0-9]+('.'[0-9]*)?)|'"'(~[\"]|'\\'.)*'"';


SEPARATOR:'--'|'->'|[:=,;}{\[\]<>];