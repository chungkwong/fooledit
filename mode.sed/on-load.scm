(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.sed/target/mode.sed-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.sed.SedLexer") (string->String "mode.sed/tokens.json") (string->String "text/x-sed"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.sed/target/mode.sed-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.sed.SedParser") (string->String "script") (string->String "text/x-sed"))
