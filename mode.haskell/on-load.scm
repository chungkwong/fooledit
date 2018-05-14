(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.haskell/target/mode.haskell-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.haskell.HaskellLexer") (string->String "mode.haskell/tokens.json") (string->String "text/x-haskell"))
