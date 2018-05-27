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
package cc.fooledit.editor.odf.writer;
import cc.fooledit.editor.odf.*;
import java.util.logging.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import org.odftoolkit.simple.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdtViewer extends BorderPane{
	private final TextDocument document;
	public OdtViewer(TextDocument document){
		this.document=document;
		WebView view=new WebView();
		try{
			view.getEngine().load(OdfXslt.transform(document).toString());
		}catch(Exception ex){
			java.util.logging.Logger.getLogger(OdtViewer.class.getName()).log(Level.SEVERE,null,ex);
		}
		setCenter(view);
		/*try{
			setText(EditableTextExtractor.newOdfEditableTextExtractor(document).getText());
		}catch(Exception ex){
			java.util.logging.Logger.getLogger(OdtViewer.class.getName()).log(Level.SEVERE,null,ex);
		}*/
	}
}
