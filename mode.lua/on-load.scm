(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.lua/target/mode.lua-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.lua.LuaLexer") (string->String "mode.lua/tokens.json") (string->String "text/x-lua"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.lua/target/mode.lua-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.lua.LuaParser") (string->String "chunk") (string->String "text/x-lua"))
