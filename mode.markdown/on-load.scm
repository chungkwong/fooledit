(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.markdown/target/mode.markdown-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.markdown.MarkdownLexer") (string->String "mode.markdown/tokens.json") (string->String "text/markdown"))
