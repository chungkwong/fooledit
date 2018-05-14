(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.cpp/target/mode.cpp-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.cpp.CPP14Lexer") (string->String "mode.cpp/tokens.json") (string->String "text/x-c++src"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.cpp/target/mode.cpp-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.cpp.CPP14Parser") (string->String "translationunit") (string->String "text/x-c++src"))
