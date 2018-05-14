(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.fortran/target/mode.fortran-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.fortran.fortran77Lexer") (string->String "mode.fortran/tokens.json") (string->String "text/x-fortran"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.fortran/target/mode.fortran-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.fortran.fortran77Parser") (string->String "program") (string->String "text/x-fortran"))
