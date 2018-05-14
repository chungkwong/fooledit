(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.java/target/mode.java-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.java.Java9Lexer") (string->String "mode.java/tokens.json") (string->String "text/x-java"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.java/target/mode.java-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.java.Java9Parser") (string->String "compilationUnit") (string->String "text/x-java"))
