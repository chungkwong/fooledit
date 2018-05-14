(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.pascal/target/mode.pascal-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.pascal.pascalLexer") (string->String "mode.pascal/tokens.json") (string->String "text/x-pascal"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.pascal/target/mode.pascal-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.pascal.pascalParser") (string->String "program") (string->String "text/x-pascal"))
