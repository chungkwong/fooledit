grammar Bibtex;

COMMENT:'%' (~[\r\n])*;
TYPE: '@' ('ARTICLE'|'BOOK'|'BOOKLET'|'CONFERENCE'|'INBOOK'|'INCOLLECTION'|'INPROCEEDINGS'|'MANUAL'|'MASTERSTHESIS'|'MISC'|'PHDTHESIS'|'PROCEEDINGS'|'TECHREPORT'|'UNPUBLISHED');
KEY:'address'|'annote'|'author'|'booktitle'|'chapter'|'crossref'|'edition'|'editor'|'howpublished'|'institution'|'journal'|'key'|'month'|'note'|'number'|'organization'|'pages'|'publisher'|'school'|'series'|'title'|'type'|'volume'|'year';
SEPARATOR:[=}{#"];
COMMAND: '\\' ((~[a-zA-Z])|[a-zA-Z]+);
PLAIN: (~[%=}{#"\\])+;