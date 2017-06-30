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
package com.github.chungkwong.fooledit.example.text;
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.model.Template;
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
	public TextTemplate(String name,String description,String file,String mime){
		this.name=name;
		this.description=description;
		this.file=file;
		this.mime=mime;
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
	public Collection<String> getParameters(){
		return Collections.emptySet();
	}
	@Override
	public TextObject apply(Properties properties){
		try{
			StringWriter out=new StringWriter();
			freemarker.template.Template template=ENGINE.getTemplate(file);
			Map<String,Object> props=new HashMap<>();
			template.process(props,out);
			return new TextObject(out.toString());
		}catch(IOException|TemplateException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return null;
		}
	}
	static{
		ENGINE=new Configuration(new Version(2,3,26));
		ENGINE.setDefaultEncoding("UTF-8");
		try{
			ENGINE.setDirectoryForTemplateLoading(new File(Main.getModulePath("code-editor"),"modes"));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public static void main(String[] args) throws IOException, TemplateException{
		Configuration configuration=new Configuration(new Version(2,3,26));
		configuration.setDefaultEncoding("UTF-8");
		configuration.setDirectoryForTemplateLoading(new File(Main.getModulePath("code-editor"),"modes"));
		freemarker.template.Template template=configuration.getTemplate("java/Main.java");
		Map<String,Object> props=new HashMap<>();
		props.put("name","Name");
		props.put("project",Helper.hashMap("licensePath","../headers/GPL-3"));
		props.put("date","2017-6-29");
		props.put("user","kwong");
		template.process(props,new OutputStreamWriter(System.out));
	}
}
