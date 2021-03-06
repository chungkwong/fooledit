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
import cc.fooledit.editor.text.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.INSTALLED_MODULE_REGISTRY.put(NAME,this.getClass());
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(StructuredTextEditor.INSTANCE,TextObject.class);
		DataObjectTypeRegistry.addToolBox(FindToolBox.INSTANCE,StructuredTextEditor.class);
		CoreModule.TEMPLATE_TYPE_REGISTRY.put(TextTemplate.class.getName(),(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime"),(String)obj.get("module")));
		TextObject logObject=new TextObject("");
		logObject.getText().bind(Notifier.MESSAGES);
		RegistryNode<String,Object> log=new SimpleRegistryNode<>();
		log.put(DataObject.DEFAULT_NAME,MessageRegistry.getString("LOG",getClass()));
		log.put(DataObject.TYPE,TextObjectType.class.getName());
		log.put(DataObject.MIME,"text/plain");
		log.put(DataObject.DATA,logObject);
		DataObjectRegistry.addDataObject(log);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
