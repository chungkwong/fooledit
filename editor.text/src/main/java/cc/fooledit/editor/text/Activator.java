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
package cc.fooledit.editor.text;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.nio.charset.*;
import java.util.logging.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	@Override
	public void start(BundleContext bc) throws Exception{
		Registry.providesDataObjectType(TextObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(StructuredTextEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(TextObject.class.getName(),NAME);
		Registry.providesTemplateType(TextTemplate.class.getName(),NAME);
		Registry.providesToolBox(FindToolBox.class.getName(),NAME);
		try{
			((ListRegistryNode)CoreModule.TEMPLATE_REGISTRY.getOrCreateChild("children")).put(
					StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(new InputStreamReader(
							Activator.class.getResourceAsStream("templates/templates.json"),StandardCharsets.UTF_8))));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(StructuredTextEditor.INSTANCE,TextObject.class);
		DataObjectTypeRegistry.addToolBox(FindToolBox.INSTANCE,StructuredTextEditor.class);
		CoreModule.TEMPLATE_TYPE_REGISTRY.put(TextTemplate.class.getName(),(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime"),(String)obj.get("module")));
		TextObject logObject=new TextObject("");
		logObject.getText().bind(Notifier.MESSAGES);
		RegistryNode<String,Object> log=new SimpleRegistryNode<>();
		log.put(DataObject.DEFAULT_NAME,MessageRegistry.getString("LOG",NAME));
		log.put(DataObject.TYPE,TextObjectType.class.getName());
		log.put(DataObject.MIME,"text/plain");
		log.put(DataObject.DATA,logObject);
		DataObjectRegistry.addDataObject(log);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
	public static void main(String[] args){
		for(File module:new File("/home/kwong/NetBeansProjects/fooledit").listFiles()){
			File descriptor=new File(module,"description.json");
			if(module.isDirectory()&&descriptor.exists()){
				File resources=new File(module,"src/main/resources");
				File meta=new File(resources,"META-INF");
				meta.mkdirs();
				descriptor.renameTo(new File(meta,"description.json"));
				File menu=new File(module,"menus/default.json");
				if(menu.exists()){
					menu.renameTo(new File(resources,"menu.json"));
					menu.getParentFile().delete();
				}
				File kepmap=new File(module,"keymaps/default.json");
				if(kepmap.exists()){
					kepmap.renameTo(new File(resources,"keymap.json"));
					kepmap.getParentFile().delete();
				}
				File msg=new File(module,"locales/messages_zh_CN.properties");
				if(msg.exists()){
					msg.renameTo(new File(resources,"messages_zh_CN.properties"));
					new File(module,"locales/messages.properties").renameTo(new File(resources,"messages.properties"));
					msg.getParentFile().delete();
				}
				new File(module,"on-unload.scm").delete();
			}
		}
	}
}
