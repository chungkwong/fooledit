(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.ada/target/mode.ada-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.ada.AdaLexer") (string->String "mode.ada/tokens.json") (string->String "text/x-adasrc"))
