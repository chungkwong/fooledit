(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.csv/target/mode.csv-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.csv.CSVLexer") (string->String "mode.csv/tokens.json") (string->String "text/csv"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.csv/target/mode.csv-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.csv.CSVParser") (string->String "csvFile") (string->String "text/csv"))
