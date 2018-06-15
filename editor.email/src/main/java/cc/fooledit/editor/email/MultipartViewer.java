/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.email;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.activation.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MultipartViewer extends BorderPane{
	private final Multipart object;
	public MultipartViewer(Multipart object){
		this.object=object;
		try{
			switch(object.getContentType()){
				case "multipart/digest":
				case "multipart/mixed":
					VBox mixed=new VBox();
					for(int i=0;i<object.getCount();i++){
						mixed.getChildren().add(getViewer(object.getBodyPart(i)));
					}
					setCenter(new ScrollPane(mixed));
					break;
				case "multipart/alternative":
					for(int i=0;i<object.getCount();i++){
						setCenter(getViewer(object.getBodyPart(i)));
					}
					break;
				case "multipart/parallel":
					SplitPane parallel=new SplitPane();
					for(int i=0;i<object.getCount();i++){
						parallel.getItems().add(getViewer(object.getBodyPart(i)));
					}
					setCenter(parallel);
					break;
				case "multipart/encrypted":
					break;
				case "multipart/signed":
					break;
				case "multipart/report":
					break;
			}
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,"",ex);
		}
	}
	private static Node getViewer(BodyPart part) throws Exception{
		List<DataObjectType> types=DataObjectTypeRegistry.getPreferedDataObjectType(new MimeType(part.getContentType()));
		if(!types.isEmpty()){
			RegistryNode<String,Object> meta=new SimpleRegistryNode<>();
			DataObject obj=types.get(0).readFrom(new InputStreamConnection(part.getInputStream(),part.getContentType()),new MimeType(part.getContentType()),meta);
			List<DataEditor> editors=DataObjectTypeRegistry.getDataEditors(obj.getClass());
			if(editors!=null){
				return editors.get(0).edit(obj,null,meta);
			}
		}
		return null;
	}
}
class InputStreamConnection extends URLConnection{
	private final InputStream in;
	private final String mime;
	public InputStreamConnection(InputStream in,String mime) throws MalformedURLException{
		super(new URL("inputstream://localhost/"+System.identityHashCode(in)));
		this.in=in;
		this.mime=mime;
	}
	@Override
	public void connect() throws IOException{
	}
	@Override
	public InputStream getInputStream() throws IOException{
		return in;
	}
	@Override
	public String getContentType(){
		return mime;
	}
}
