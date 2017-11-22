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
package cc.fooledit.example.text;
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.control.*;
import cc.fooledit.example.text.CharsetDetector;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.control.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextEditorModule{
	public static final String NAME="editor.code";
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->new StructuredTextEditor(),TextObject.class);
		TemplateEditor.registerTemplateType("text",(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
		Main.INSTANCE.getMenuRegistry().registerDynamicMenu("reload",(items)->{
			DataObject curr=Main.getCurrentDataObject();
			String url=(String)curr.getProperties().get(DataObject.URI);
			String currCharset=(String)curr.getProperties().getOrDefault("CHARSET","UTF-8");
			if(url!=null){
				ToggleGroup group=new ToggleGroup();
				Consumer<Charset> reload=(set)->{
					try{
						MimeType mime=new MimeType((String)curr.getProperties().get(DataObject.MIME));
						mime.setParameter("charset",set.name());
						Main.show(DataObjectRegistry.readFrom(new URL(url),TextObjectType.INSTANCE,mime));
					}catch(Exception ex){
						Logger.getLogger(TextEditorModule.class.getName()).log(Level.SEVERE,null,ex);
					}
				};
				try(InputStream in=FoolURLConnection.open(new URL(url)).getInputStream()){
					items.setAll(CharsetDetector.probeCharsets(in).stream()
							.map((set)->createCharsetItem(set,reload,group,currCharset)).collect(Collectors.toList()));
				}catch(IOException ex){
					Logger.getGlobal().log(Level.INFO,null,ex);
					items.setAll(Charset.availableCharsets().values().stream()
							.map((set)->createCharsetItem(set,reload,group,currCharset)).collect(Collectors.toList()));
				}
			}
		});
		Main.INSTANCE.getMenuRegistry().registerDynamicMenu("charset",(items)->{
			TextObject curr=(TextObject)Main.getCurrentDataObject();
			String currCharset=(String)curr.getProperties().getOrDefault("CHARSET","UTF-8");
			ToggleGroup group=new ToggleGroup();
			items.setAll(Charset.availableCharsets().values().stream()
					.map((set)->createCharsetItem(set,(s)->{
						curr.getProperties().put("CHARSET",s.name());
					},group,currCharset)).collect(Collectors.toList()));
		});

	}
	private static MenuItem createCharsetItem(Charset charset,Consumer<Charset> action,ToggleGroup group,String def){
		RadioMenuItem radioMenuItem=new RadioMenuItem(charset.displayName());
		radioMenuItem.setToggleGroup(group);
		if(charset.name().equalsIgnoreCase(def))
			group.selectToggle(radioMenuItem);
		radioMenuItem.setOnAction((e)->action.accept(charset));
		return radioMenuItem;
	}
	public static void onUnLoad(){

	}
}
