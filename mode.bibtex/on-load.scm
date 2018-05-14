(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.bibtex/target/mode.bibtex-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.bibtex.BibtexLexer") (string->String "mode.bibtex/tokens.json") (string->String "text/x-bibtex"))
