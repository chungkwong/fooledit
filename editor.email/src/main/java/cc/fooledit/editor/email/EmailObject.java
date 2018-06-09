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
import cc.fooledit.core.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EmailObject implements DataObject<EmailObject>{
	private final Session session;
	private final Message message;
	public EmailObject(Properties props){
		this.session=Session.getInstance(props);
		this.message=new MimeMessage(session);
		
	}
	public Message getMessage(){
		return message;
	}
	public Session getSession(){
		return session;
	}
	@Override
	public DataObjectType<EmailObject> getDataObjectType(){
		return EmailObjectType.INSTANCE;
	}
}