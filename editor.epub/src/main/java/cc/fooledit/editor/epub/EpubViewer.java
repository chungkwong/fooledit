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
package cc.fooledit.editor.epub;
import java.io.*;
import java.nio.charset.*;
import java.util.logging.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import nl.siegmann.epublib.domain.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EpubViewer extends BorderPane{
	private final Book document;
	private final Label label=new Label();
	public EpubViewer(Book document){
		this.document=document;
		setTop(label);
		show(document.getCoverPage());
	}
	public void show(Resource resource){
		label.setText(resource.getTitle()+"|"+document.getTitle());
		WebView webView=new WebView();
		try{
			webView.getEngine().loadContent(new String(resource.getData(),Charset.forName(resource.getInputEncoding())));
		}catch(IOException ex){
			try{
				webView.getEngine().loadContent(new String(resource.getData(),StandardCharsets.UTF_8));
			}catch(IOException ex1){
				Logger.getLogger(EpubViewer.class.getName()).log(Level.SEVERE,null,ex1);
			}
		}
		setCenter(webView);
	}
	public Book getDocument(){
		return document;
	}
}
