(inform-jar "editor.epub/target/editor.epub-1.0-SNAPSHOT.jar" "cc.fooledit.editor.epub.EpubModule" "onInstall")
(map-mime-to-type "application/epub+zip" "cc.fooledit.editor.epub.EpubObjectType")
(map-suffix-to-mime "epub" "application/epub+zip")
(mime-parent "application/epub+zip" "application/zip")

