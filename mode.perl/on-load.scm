(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.perl/target/mode.perl-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.perl.PerlLexer") (string->String "mode.perl/tokens.json") (string->String "application/x-perl"))
