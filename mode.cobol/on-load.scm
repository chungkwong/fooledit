(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.cobol/target/mode.cobol-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.cobol.Cobol85Lexer") (string->String "mode.cobol/tokens.json") (string->String "text/x-cobol"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.cobol/target/mode.cobol-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.cobol.Cobol85Parser") (string->String "startRule") (string->String "text/x-cobol"))
