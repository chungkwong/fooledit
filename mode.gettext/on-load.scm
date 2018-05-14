(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.gettext/target/mode.gettext-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.gettext.GettextLexer") (string->String "mode.gettext/tokens.json") (string->String "text/x-gettext-translation"))
