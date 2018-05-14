(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.csharp/target/mode.csharp-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.csharp.CSharpLexer") (string->String "mode.csharp/tokens.json") (string->String "text/x-csharp"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.csharp/target/mode.csharp-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.csharp.CSharpParser") (string->String "compilation_unit") (string->String "text/x-csharp"))
