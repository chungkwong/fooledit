(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.go/target/mode.go-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.go.GolangLexer") (string->String "mode.go/tokens.json") (string->String "text/x-go"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.go/target/mode.go-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.go.GolangParser") (string->String "sourceFile") (string->String "text/x-go"))
