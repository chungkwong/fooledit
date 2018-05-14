(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.troff/target/mode.troff-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.troff.TroffLexer") (string->String "mode.troff/tokens.json") (string->String "text/troff"))
