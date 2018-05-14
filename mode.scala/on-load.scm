(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.scala/target/mode.scala-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.scala.ScalaLexer") (string->String "mode.scala/tokens.json") (string->String "text/x-scala"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.scala/target/mode.scala-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.scala.ScalaParser") (string->String "compilationUnit") (string->String "text/x-scala"))
