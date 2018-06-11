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
import cc.fooledit.control.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EmailViewer extends BorderPane{
	private final TreeView<Folder> folders;
	private final TableView<Message> messages=new TableView<>();
	public EmailViewer(Session session) throws NoSuchProviderException,MessagingException{
		messages.getColumns().add(getSubjectColumn());
		messages.getColumns().add(getFromColumn());
		messages.getColumns().add(getDateColumn());
		messages.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			setBottom(n!=null?new MessageViewer(n):null);
		});
		setCenter(messages);
		Store store=session.getStore();
		store.connect();
		folders=new TreeView<>();
		folders.setShowRoot(true);
		//folders.setCellFactory((view)->new FolderCell());
		folders.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			if(n!=null){
				try{
					Folder f=n.getValue();
					if(!f.isOpen()){
						f.open(Folder.READ_ONLY);
					}
					messages.getItems().setAll(f.getMessages());
				}catch(MessagingException ex){
					Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				}
			}
		});
		folders.setRoot(getFolderNode(store.getDefaultFolder()));
		setLeft(folders);
	}
	private TreeItem<Folder> getFolderNode(Folder folder){
		try{
			Arrays.stream(folder.list()).forEach((f)->System.err.println(f.getFullName()));
		}catch(MessagingException ex){
			Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
		}
		return new LazyTreeItem<>(folder,()->Arrays.stream(folder.list()).map((f)->getFolderNode(f)).collect(Collectors.toList()));
	}
	private TableColumn<Message,String> getSubjectColumn(){
		TableColumn<Message,String> column=new TableColumn<>("Subject");
		column.setCellValueFactory((TableColumn.CellDataFeatures<Message,String> param)->{
			try{
				return new ReadOnlyObjectWrapper<>(param.getValue().getSubject());
			}catch(MessagingException ex){
				Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				return new ReadOnlyObjectWrapper<>("UNKNOWN");
			}
		});
		return column;
	}
	private TableColumn<Message,String> getFromColumn(){
		TableColumn<Message,String> column=new TableColumn<>("From");
		column.setCellValueFactory((TableColumn.CellDataFeatures<Message,String> param)->{
			try{
				return new ReadOnlyObjectWrapper<>(Arrays.toString(param.getValue().getFrom()));
			}catch(MessagingException ex){
				Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				return new ReadOnlyObjectWrapper<>("UNKNOWN");
			}
		});
		return column;
	}
	private TableColumn<Message,Date> getDateColumn(){
		TableColumn<Message,Date> column=new TableColumn<>("Date");
		column.setCellValueFactory((TableColumn.CellDataFeatures<Message,Date> param)->{
			try{
				return new ReadOnlyObjectWrapper<>(param.getValue().getSentDate());
			}catch(MessagingException ex){
				Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				return new ReadOnlyObjectWrapper<>(null);
			}
		});
		return column;
	}
	private static class FolderCell extends TreeCell<Folder>{
		@Override
		protected void updateItem(Folder item,boolean empty){
			if(!empty&&item!=null){
				setText(item.getName());
			}
		}
	}
}
class MessageViewer extends BorderPane{
	private final Message message;
	public MessageViewer(Message message){
		this.message=message;
		try{
			setCenter(new TextArea(Objects.toString(message.getContent())));
		}catch(IOException|MessagingException ex){
			Logger.getLogger(MessageViewer.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
}
