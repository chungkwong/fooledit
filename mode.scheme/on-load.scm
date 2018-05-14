(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.scheme/target/mode.scheme-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.scheme.R7RSLexer") (string->String "mode.scheme/tokens.json") (string->String "text/x-scheme"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.scheme/target/mode.scheme-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.scheme.R7RSParser") (string->String "repl") (string->String "text/x-scheme"))
