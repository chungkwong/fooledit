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
package cc.fooledit.control;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.Node;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RestfulRegistry extends Prompt{
	private final String URI;
	private final String parameter;
	private final Consumer<String> callback;
	public RestfulRegistry(String URI,String parameter,Consumer<String> callback){
		this.URI=URI;
		this.parameter=parameter;
		this.callback=callback;
	}
	@Override
	public String getDisplayName(){
		return "OAUTH";
	}
	@Override
	public Node edit(Prompt data,Object remark,RegistryNode<String,Object,String> meta){
		WebView webView=new WebView();
		webView.getEngine().load(URI);
		webView.getEngine().locationProperty().addListener((e,o,n)->{
			try{
				Map<String,String> values=parseQuery(new URL(n).getQuery());
				if(values.containsKey(parameter)){
					callback.accept(values.get(parameter));
				}
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINE,null,ex);
			}
		});
		return webView;
	}
	@Override
	public String getName(){
		return "OAUTH";
	}
	private static Map<String,String> parseQuery(String query){
		if(query==null)
			return Collections.emptyMap();
		return Arrays.stream(query.split("&")).map((s)->parseParameter(s)).collect(Collectors.toMap(Pair::getKey,Pair::getValue));
	}
	private static Pair<String,String> parseParameter(String p) {
		int i=p.indexOf('=');
		String key=i!=-1?p.substring(0,i):p;
		String value=i!=-1?p.substring(i+1):"";
		try{
			return new Pair<>(URLDecoder.decode(key,"UTF-8"),URLDecoder.decode(value,"UTF-8"));
		}catch(UnsupportedEncodingException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return new Pair<>(key,value);
		}
	}
}
