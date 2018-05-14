(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.octave/target/mode.octave-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.octave.OctaveLexer") (string->String "mode.octave/tokens.json") (string->String "text/x-matlab"))
