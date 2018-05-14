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
package cc.fooledit.editor.msoffice;
import javafx.scene.web.*;
import org.apache.poi.hwpf.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DocumentViewer extends HTMLEditor{
	private final HWPFDocumentCore document;
	public DocumentViewer(HWPFDocumentCore document){
		this.document=document;
		setHtmlText(document.getDocumentText().replace("<","&lt;").replace("&","&amp;"));
	}
}
