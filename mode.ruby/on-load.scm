(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.ruby/target/mode.ruby-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.ruby.CorundumLexer") (string->String "mode.ruby/tokens.json") (string->String "application/x-ruby"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.ruby/target/mode.ruby-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.ruby.CorundumParser") (string->String "prog") (string->String "application/x-ruby"))
