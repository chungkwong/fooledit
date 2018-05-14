(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.sql/target/mode.sql-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.sql.SQLLexer") (string->String "mode.sql/tokens.json") (string->String "application/sql"))
