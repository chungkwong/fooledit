(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.diff/target/mode.diff-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.diff.DiffLexer") (string->String "mode.diff/tokens.json") (string->String "text/x-patch"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.diff/target/mode.diff-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.diff.DiffParser") (string->String "file") (string->String "text/x-patch"))
