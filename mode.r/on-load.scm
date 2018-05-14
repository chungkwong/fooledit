(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.r/target/mode.r-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.r.RLexer") (string->String "mode.r/tokens.json") (string->String "text/x-r"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.r/target/mode.r-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.r.RParser") (string->String "prog") (string->String "text/x-r"))
