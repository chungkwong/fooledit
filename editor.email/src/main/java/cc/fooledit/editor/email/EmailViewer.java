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
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EmailViewer extends BorderPane{
	private final TreeTableView<Object> folders;
	public EmailViewer(Session session) throws NoSuchProviderException,MessagingException{
		folders=new TreeTableWrapper<>();
		folders.getColumns().add(getSubjectColumn());
		folders.getColumns().add(getFromColumn());
		folders.getColumns().add(getDateColumn());
		folders.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			setCenter(n!=null&&n.getValue() instanceof Message?new MessageViewer((Message)n.getValue()):null);
		});
		Store store=session.getStore();
		store.connect();
		folders.setShowRoot(true);
		folders.setRoot(getNode(store.getDefaultFolder()));
		setLeft(folders);
	}
	private TreeItem<Object> getNode(Folder folder){
		return new LazyTreeItem<>(folder,()->{
			List<TreeItem<Object>> children=new ArrayList<>();
			Arrays.stream(folder.list()).forEach((f)->children.add(getNode(f)));
			if((folder.getType()&Folder.HOLDS_MESSAGES)!=0){
				if(!folder.isOpen()){
					folder.open(Folder.READ_ONLY);
				}
				Arrays.stream(folder.getMessages()).forEach((m)->children.add(new TreeItem<>(m)));
			}
			return children;
		});
	}
	private TreeTableColumn<Object,String> getSubjectColumn(){
		TreeTableColumn<Object,String> column=new TreeTableColumn<>("Subject");
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object,String> param)->{
			try{
				Object value=param.getValue().getValue();
				if(value instanceof Message){
					return new ReadOnlyObjectWrapper<>(((Message)value).getSubject());
				}else{
					return new ReadOnlyObjectWrapper<>(((Folder)value).getName());
				}
			}catch(MessagingException ex){
				Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				return new ReadOnlyObjectWrapper<>("UNKNOWN");
			}
		});
		return column;
	}
	private TreeTableColumn<Object,String> getFromColumn(){
		TreeTableColumn<Object,String> column=new TreeTableColumn<>("From");
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object,String> param)->{
			try{
				Object value=param.getValue().getValue();
				if(value instanceof Message){
					return new ReadOnlyObjectWrapper<>(Arrays.toString(((Message)value).getFrom()));
				}else{
					return new ReadOnlyObjectWrapper<>("");
				}
			}catch(MessagingException ex){
				Logger.getLogger(EmailViewer.class.getName()).log(Level.SEVERE,null,ex);
				return new ReadOnlyObjectWrapper<>("UNKNOWN");
			}
		});
		return column;
	}
	private TreeTableColumn<Object,Date> getDateColumn(){
		TreeTableColumn<Object,Date> column=new TreeTableColumn<>("Date");
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Object,Date> param)->{
			try{
				Object value=param.getValue().getValue();
				if(value instanceof Message){
					return new ReadOnlyObjectWrapper<>(((Message)value).getSentDate());
				}else{
					return new ReadOnlyObjectWrapper<>(null);
				}
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
			StringBuilder buf=new StringBuilder();
			Object content=message.getContent();
			if(content instanceof Multipart){
				Multipart multipart=(Multipart)content;
				for(int i=0;i<multipart.getCount();i++){
					BodyPart bodyPart=multipart.getBodyPart(i);
					Enumeration<Header> en=bodyPart.getAllHeaders();
					while(en.hasMoreElements()){
						Header nextElement=en.nextElement();
						buf.append(nextElement.getName()).append('=').append(nextElement.getValue()).append('\n');
					}
					buf.append(Objects.toString(bodyPart.getContent()));
				}
			}else{
				buf.append(Objects.toString(content));
			}
			setCenter(new TextArea(buf.toString()));
		}catch(IOException|MessagingException ex){
			Logger.getLogger(MessageViewer.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
}
