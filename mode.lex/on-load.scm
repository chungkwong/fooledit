(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.lex/target/mode.lex-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.lex.FlexLexer") (string->String "mode.lex/tokens.json") (string->String "text/x-lex"))
