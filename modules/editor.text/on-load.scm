(import (java))
(invoke-static 'cc.fooledit.editor.text.TextEditorModule 'onLoad)
(map-glob-to-mime "(.*[/\\\\])?(GNUmakefile|makefile|Makefile)" "text/x-makefile")

