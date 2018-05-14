(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.texinfo/target/mode.texinfo-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.texinfo.TexinfoLexer") (string->String "mode.texinfo/tokens.json") (string->String "text/x-texinfo"))
