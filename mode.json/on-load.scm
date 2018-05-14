(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.json/target/mode.json-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.json.JSONLexer") (string->String "mode.json/tokens.json") (string->String "application/json"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.json/target/mode.json-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.json.JSONParser") (string->String "json") (string->String "application/json"))
