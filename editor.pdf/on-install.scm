(inform-jar "editor.pdf/target/editor.pdf-1.0-SNAPSHOT.jar" "cc.fooledit.editor.pdf.PdfModule" "onInstall")
(map-mime-to-type "application/pdf" "cc.fooledit.editor.pdf.PdfObjectType")
(map-suffix-to-mime "pdf" "application/pdf")
(mime-alias "image/pdf" "application/pdf")
(mime-alias "application/x-pdf" "application/pdf")
(mime-alias "application/acrobat" "application/pdf")

