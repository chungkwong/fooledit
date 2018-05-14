(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.ocaml/target/mode.ocaml-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.ocaml.OcamlLexer") (string->String "mode.ocaml/tokens.json") (string->String "text/x-ocaml"))
