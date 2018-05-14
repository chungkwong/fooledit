(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.tcl/target/mode.tcl-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.tcl.TclLexer") (string->String "mode.tcl/tokens.json") (string->String "text/x-tcl"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.tcl/target/mode.tcl-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.tcl.TclParser") (string->String "program") (string->String "text/x-tcl"))
