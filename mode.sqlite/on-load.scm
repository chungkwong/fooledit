(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.sqlite/target/mode.sqlite-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.sqlite.SQLiteLexer") (string->String "mode.sqlite/tokens.json") (string->String "application/sql"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.sqlite/target/mode.sqlite-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.sqlite.SQLiteParser") (string->String "sql_stmt_list") (string->String "application/sql"))
