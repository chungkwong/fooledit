(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.javascript/target/mode.javascript-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.javascript.ECMAScriptLexer") (string->String "mode.javascript/tokens.json") (string->String "application/javascript"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.javascript/target/mode.javascript-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.javascript.ECMAScriptParser") (string->String "program") (string->String "application/javascript"))