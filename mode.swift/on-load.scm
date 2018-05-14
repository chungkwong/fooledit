(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.swift/target/mode.swift-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.swift.Swift4Lexer") (string->String "mode.swift/tokens.json") (string->String "text/x-swift"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.swift/target/mode.swift-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.swift.Swift4Parser") (string->String "top_level") (string->String "text/x-swift"))
