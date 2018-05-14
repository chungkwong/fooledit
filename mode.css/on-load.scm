(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.css/target/mode.css-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.css.css3Lexer") (string->String "mode.css/tokens.json") (string->String "text/css"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.css/target/mode.css-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.css.css3Parser") (string->String "stylesheet") (string->String "text/css"))
