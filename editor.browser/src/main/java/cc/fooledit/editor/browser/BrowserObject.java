/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.editor.browser;
import cc.fooledit.core.DataObject;
import cc.fooledit.core.DataObjectType;
import javafx.scene.*;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserObject implements DataObject<BrowserObject>{
	private final WebView webView;
	private final Node editor;
	public BrowserObject(){
		this.webView=new WebView();
		this.editor=new BrowserViewer(webView);
	}
	public BrowserObject(String url){
		this.webView=new WebView();
		webView.getEngine().load(url);
		this.editor=new BrowserViewer(webView);
	}
	public WebView getWebView(){
		return webView;
	}
	public Node getEditor(){
		return editor;
	}
	@Override
	public DataObjectType<BrowserObject> getDataObjectType(){
		return BrowserObjectType.INSTANCE;
	}
}