(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.c/target/mode.c-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.c.CLexer") (string->String "mode.c/tokens.json") (string->String "text/x-csrc"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.c/target/mode.c-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.c.CParser") (string->String "compilationUnit") (string->String "text/x-csrc"))
