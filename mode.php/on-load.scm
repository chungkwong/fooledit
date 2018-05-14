(import (java))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerHighlighter (string->String "mode.php/target/mode.php-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.php.PHPLexer") (string->String "mode.php/tokens.json") (string->String "application/x-php"))
(invoke (get-static 'cc.fooledit.editor.text.StructuredTextEditor 'INSTANCE) 'registerParser (string->String "mode.php/target/mode.php-1.0-SNAPSHOT.jar!cc.fooledit.editor.text.mode.php.PHPParser") (string->String "htmlDocument") (string->String "application/x-php"))
