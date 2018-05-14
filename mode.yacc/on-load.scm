(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.yacc/target/mode.yacc-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.yacc.BisonLexer") (string->String "mode.yacc/tokens.json") (string->String "text/x-yacc"))
