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
package cc.fooledit.editor.email;
import cc.fooledit.*;
import cc.fooledit.core.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EmailModule{
	public static final String NAME="editor.email";
	public static final String APPLICATION_NAME="email";
	public static final String CONTENT_TYPE_NAME="fooledit/email";
	public static void onLoad(){
		Registry.registerApplication(APPLICATION_NAME,CONTENT_TYPE_NAME,EmailObjectType.INSTANCE,EmailObject.class,EmailEditor.INSTANCE);
		Main.INSTANCE.getGlobalCommandRegistry().put("email",new Command("email",()->Main.INSTANCE.addAndShow(DataObjectRegistry.create(EmailObjectType.INSTANCE)),NAME));
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		Registry.providesDataObjectType(EmailObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(EmailEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(EmailObject.class.getName(),NAME);
		Registry.providesApplication(APPLICATION_NAME,NAME);
		Registry.providesContentTypeLoader(CONTENT_TYPE_NAME,NAME);
		Registry.providesCommand(APPLICATION_NAME,NAME);
	}
}
