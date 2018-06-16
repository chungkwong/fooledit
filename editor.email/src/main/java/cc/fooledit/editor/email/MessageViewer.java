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
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MessageViewer extends BorderPane{
	private final Message message;
	public MessageViewer(Message message){
		this.message=message;
		try{
			TableView<Header> head=new TableView<>();
			for(Enumeration<Header> headers=message.getAllHeaders();headers.hasMoreElements();){
				Header header=headers.nextElement();
				head.getItems().add(header);
				setTop(head);
				setCenter(new MultipartViewer((Multipart)message.getContent()));
			}
		}catch(IOException|MessagingException ex){
			Logger.getLogger(MessageViewer.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
}
