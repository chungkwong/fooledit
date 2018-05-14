(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.dot/target/mode.dot-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.dot.DOTLexer") (string->String "mode.dot/tokens.json") (string->String "text/vnd.graphviz"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.dot/target/mode.dot-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.dot.DOTParser") (string->String "graph") (string->String "text/vnd.graphviz"))
