(inform-jar "editor.media/target/editor.media-1.0-SNAPSHOT.jar" "cc.fooledit.editor.media.MediaEditorModule" "onInstall")
(map-mime-to-type "audio/midi" "cc.fooledit.editor.media.MidiObjectType")
(map-suffix-to-mime "midi" "audio/midi")
(map-suffix-to-mime "mid" "audio/midi")
(map-suffix-to-mime "kar" "audio/midi")
(mime-alias "audio/x-midi" "audio/midi")

