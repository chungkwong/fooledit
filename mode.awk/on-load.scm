(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.awk/target/mode.awk-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.awk.AwkLexer") (string->String "mode.awk/tokens.json") (string->String "text/x-awk"))
