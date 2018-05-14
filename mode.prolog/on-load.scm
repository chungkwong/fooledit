(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.prolog/target/mode.prolog-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.prolog.PrologLexer") (string->String "mode.prolog/tokens.json") (string->String "text/x-prolog"))
