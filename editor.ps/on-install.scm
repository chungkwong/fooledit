(inform-jar "editor.ps/target/editor.ps-1.0-SNAPSHOT.jar" "cc.fooledit.editor.ps.PsModule" "onInstall")
(map-mime-to-type "application/postscript" "cc.fooledit.editor.ps.PsObjectType")
(map-suffix-to-mime "ps" "application/postscript")
(mime-parent "application/postscript" "text/plain")

