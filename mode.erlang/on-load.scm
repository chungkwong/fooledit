(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.erlang/target/mode.erlang-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.erlang.ErlangLexer") (string->String "mode.erlang/tokens.json") (string->String "text/x-erlang"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.erlang/target/mode.erlang-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.erlang.ErlangParser") (string->String "forms") (string->String "text/x-erlang"))
