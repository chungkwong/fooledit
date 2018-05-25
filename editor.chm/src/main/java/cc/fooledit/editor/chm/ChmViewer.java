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
package cc.fooledit.editor.chm;
import javafx.beans.property.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import org.jchmlib.*;
import org.w3c.dom.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ChmViewer extends BorderPane{
	private final ChmFile document;
	private final WebView content=new WebView();
	private final Label title=new Label();
	private ImageView currentPage;
	private String path;
	public ChmViewer(ChmFile document){
		this.document=document;
		setTop(title);
		setCenter(content);
		content.getEngine().getLoadWorker().stateProperty().addListener((e,o,n)->{
			if(n==Worker.State.SUCCEEDED){
				Document doc=content.getEngine().getDocument();
				NodeList lista=doc.getElementsByTagName("a");
				for(int i=0;i<lista.getLength();i++){
					Node src=lista.item(i).getAttributes().getNamedItem("href");
					if(src!=null){
						String link=src.getNodeValue();
						if(!link.contains(":"))
							src.setNodeValue(document.getUrl()+"!"+normailizePath(link));
					}
				}
				NodeList listimg=doc.getElementsByTagName("img");
				for(int i=0;i<listimg.getLength();i++){
					Node src=listimg.item(i).getAttributes().getNamedItem("src");
					if(src!=null){
						String link=src.getNodeValue();
						if(!link.contains(":"))
							src.setNodeValue(document.getUrl()+"!"+normailizePath(link));
					}
				}
			}
		});
		setPath(document.getHomeFile());
	}
	private String normailizePath(String link){
		if(link.startsWith("/")){
			return link;
		}else{
			int i=path.lastIndexOf('/');
			if(i!=-1)
				return path.substring(0,i+1)+link;
			else
				return "/"+link;
		}
	}
	public void setPath(String path){
		title.setText(document.getTitleOfObject(path)+" : "+document.getTitle());
		//content.getEngine().load("jar:file:///home/kwong/.ivy2/cache/org.apache.commons/commons-compress/javadocs/commons-compress-1.15-javadoc.jar!/index.html");
		content.getEngine().load(document.getUrl()+"!"+path);
		this.path=path;
	}
	public ChmFile getDocument(){
		return document;
	}
	public void setScale(double scale){
		content.setZoom(scale);
	}
	public double getScale(){
		return content.getZoom();
	}
	public DoubleProperty scaleProperty(){
		return content.zoomProperty();
	}
	@Override
	public void requestFocus(){
		content.requestFocus();
	}
}
