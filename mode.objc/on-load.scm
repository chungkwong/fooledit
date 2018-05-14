(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.objc/target/mode.objc-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.objc.ObjectiveCLexer") (string->String "mode.objc/tokens.json") (string->String "text/x-objcsrc"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.objc/target/mode.objc-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.objc.ObjectiveCParser") (string->String "translationUnit") (string->String "text/x-objcsrc"))
