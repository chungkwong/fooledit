(import (java))
(invoke-static 'cc.fooledit.editor.text.TextEditorModule 'onInstall)
(map-mime-to-type "text/x-manifest" "cc.fooledit.editor.text.TextObjectType")
(map-suffix-to-mime "mf" "text/x-manifest")
(mime-alias "text/x-yaml" "application/x-yaml")

