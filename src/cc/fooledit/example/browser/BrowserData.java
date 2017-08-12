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
package cc.fooledit.example.browser;
import cc.fooledit.model.*;
import javafx.scene.*;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserData implements DataObject<BrowserData>{
	private final WebView webView;
	private final Node editor;
	public BrowserData(){
		this.webView=new WebView();
		this.editor=new BrowserViewer(webView);
	}
	public BrowserData(String url){
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
	public DataObjectType<BrowserData> getDataObjectType(){
		return BrowserDataType.INSTANCE;
	}
}