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
package cc.fooledit.editor.text;
import cc.fooledit.core.Helper;
import cc.fooledit.*;
import cc.fooledit.core.Template;
import freemarker.template.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextTemplate implements Template<TextObject>{
	private static final Configuration ENGINE;
	private final String name;
	private final String description;
	private final String file;
	private final String mime;
	private final String module;
	public TextTemplate(String name,String description,String file,String mime,String module){
		this.name=name;
		this.description=description;
		this.file=file;
		this.mime=mime;
		this.module=module;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public String getDescription(){
		return description;
	}
	public String getMimeType(){
		return mime;
	}
	@Override
	public String getModule(){
		return module;
	}
	@Override
	public Collection<String> getParameters(){
		/*List<String> parameters=new ArrayList<>();
		Properties properties=new Properties();
		while(true){
			try{
				tryToApply(properties);
			}catch(InvalidReferenceException ex){
				String name=ex.getBlamedExpressionString();
				getBranch(name,properties).put(getLeaf(name),"");
				parameters.add(name);
				continue;
			}catch(NonHashException ex){
				String name=ex.getBlamedExpressionString();
				getBranch(name,properties).put(getLeaf(name),new HashMap<Object,Object>());
				continue;
			}catch(IOException|TemplateException ex){

			}
			break;
		}
		return parameters;*/
		return Collections.emptySet();
	}
	private static Map getBranch(String name,Map<Object,Object> props){
		int i=0;
		int j=name.indexOf('.');
		while(j!=-1){
			props=(Map<Object,Object>)props.get(name.substring(i,j));
			i=j+1;
			j=name.indexOf('.',i);
		}
		return props;
	}
	private static String getLeaf(String name){
		int i=name.lastIndexOf('.');
		return i==-1?name:name.substring(i+1);
	}
	@Override
	public TextObject apply(Properties properties){
		try{
			return new TextObject(tryToApply(properties));
		}catch(IOException|TemplateException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return new TextObject("");
		}
	}
	private String tryToApply(Properties properties) throws TemplateException,IOException{
		StringWriter out=new StringWriter();
		freemarker.template.Template template=ENGINE.getTemplate(file);
		template.process(properties,out);
		return out.toString();
	}
	static{
		ENGINE=new Configuration(new Version(2,3,23));
		ENGINE.setDefaultEncoding("UTF-8");
		try{
			ENGINE.setDirectoryForTemplateLoading(new File(Main.INSTANCE.getModulePath(TextEditorModule.NAME),"modes"));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public static void main(String[] args) throws IOException, TemplateException{
		Configuration configuration=new Configuration(new Version(2,3,26));
		configuration.setDefaultEncoding("UTF-8");
		configuration.setDirectoryForTemplateLoading(new File(Main.INSTANCE.getModulePath(TextEditorModule.NAME),"modes"));
		freemarker.template.Template template=configuration.getTemplate("java/Main.java");
		Map<String,Object> props=new HashMap<>();
		props.put("name","Name");
		props.put("project",Helper.hashMap("licensePath","../headers/GPL-3"));
		props.put("date","2017-6-29");
		props.put("user","kwong");
	}
}
