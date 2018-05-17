/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.fooledit.editor.msoffice.word;
import java.io.*;
import java.nio.file.*;
import java.util.logging.*;
import javafx.scene.web.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.apache.poi.hwpf.*;
import org.apache.poi.hwpf.converter.*;
import org.apache.poi.hwpf.usermodel.*;
import org.w3c.dom.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DocViewer extends HTMLEditor{
	private final HWPFDocumentCore document;
	public DocViewer(HWPFDocumentCore document){
		this.document=document;
		try{
			Document xmlDocument=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			WordToHtmlConverter converter=new WordToHtmlConverter(xmlDocument);
			converter.setPicturesManager(new TmpPicturesManager());
			converter.processDocument(document);
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			StringWriter out=new StringWriter();
			transformer.transform(new DOMSource(xmlDocument),new StreamResult(out));
			setHtmlText(out.toString());
		}catch(ParserConfigurationException|TransformerException ex){
			Logger.getLogger(DocViewer.class.getName()).log(Level.SEVERE,null,ex);
			setHtmlText(document.getDocumentText().replace("<","&lt;").replace("&","&amp;"));
		}
	}
	private static class TmpPicturesManager implements PicturesManager{
		@Override
		public String savePicture(byte[] bytes,PictureType pt,String string,float f,float f1){
			try{
				Path tmp=Files.createTempFile(string!=null&&string.length()>=3?string:"pic",'.'+pt.getExtension());
				tmp.toFile().deleteOnExit();
				Files.write(tmp,bytes);
				return tmp.toUri().toString();
			}catch(IOException ex){
				Logger.getLogger(DocViewer.class.getName()).log(Level.SEVERE,null,ex);
				return "";
			}
		}
	}
}
