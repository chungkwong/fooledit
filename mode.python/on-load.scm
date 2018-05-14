(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.python/target/mode.python-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.python.Python3Lexer") (string->String "mode.python/tokens.json") (string->String "text/x-python"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.python/target/mode.python-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.python.Python3Parser") (string->String "file_input") (string->String "text/x-python"))
