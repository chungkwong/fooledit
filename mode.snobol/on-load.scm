(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.snobol/target/mode.snobol-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.snobol.snobolLexer") (string->String "mode.snobol/tokens.json") (string->String "text/x-snobol"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.snobol/target/mode.snobol-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.snobol.snobolParser") (string->String "prog") (string->String "text/x-snobol"))
