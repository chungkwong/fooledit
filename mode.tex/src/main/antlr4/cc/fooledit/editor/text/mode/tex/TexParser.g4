parser grammar TexParser;

options { tokenVocab=TexLexer; }

document: (COMMAND|PLAIN|OTHER|math)*; 

math: START_INLINE inline END_INLINE|START_DISPLAY display END_DISPLAY;

inline: (INLINE_COMMAND|INLINE_PLAIN|INLINE_OTHER)*;

display: (DISPLAY_COMMAND|DISPLAY_PLAIN|DISPLAY_OTHER);

