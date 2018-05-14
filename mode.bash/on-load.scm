(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.bash/target/mode.bash-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.bash.BashLexer") (string->String "mode.bash/tokens.json") (string->String "text/x-shellscript"))
