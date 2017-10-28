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
package cc.fooledit.example.zip;
import cc.fooledit.*;
import cc.fooledit.api.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.zip.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipViewer extends BorderPane{
	private final TableView<ZipEntry> tree=new TableView<>();
	private final ZipFile file;
	private Consumer<Collection<ZipEntry>> action;
	private Collection<ZipEntry> marked=Collections.emptySet();
	public ZipViewer(ZipFile file){
		this.file=file;
		Enumeration<? extends ZipEntry> entries=file.entries();
		while(entries.hasMoreElements()){
			ZipEntry entry=entries.nextElement();
			tree.getItems().add(entry);
		}
		tree.setTableMenuButtonVisible(true);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.<String>createColumnChooser(MessageRegistry.getString("NAME"),(param)->
				new ReadOnlyStringWrapper(getFileName(param.getValue())),true);
		this.<Number>createColumnChooser(MessageRegistry.getString("SIZE"),(param)->
				new ReadOnlyLongWrapper(getSize(param.getValue())),true);
		this.<Number>createColumnChooser(MessageRegistry.getString("COMPRESSED_SIZE"),(param)->
				new ReadOnlyLongWrapper(getCompressedSize(param.getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("CREATION"),(param)->
				new ReadOnlyStringWrapper(getCreation(param.getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("LAST_MODIFIED"),(param)->
				new ReadOnlyStringWrapper(getLastModified(param.getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("LAST_ACCESSED"),(param)->
				new ReadOnlyStringWrapper(getLastAccessed(param.getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("COMMENT"),(param)->
				new ReadOnlyStringWrapper(getComment(param.getValue())),true);
		((TableColumn<ZipEntry,String>)tree.getColumns().get(0)).setCellFactory((p)->new ZipCell());
		((TableColumn<ZipEntry,String>)tree.getColumns().get(0)).prefWidthProperty().bind(tree.widthProperty().multiply(0.4));
		setCenter(tree);
		tree.getFocusModel().focusedIndexProperty().addListener(((e,o,n)->tree.scrollTo(n.intValue())));
		setBottom(new Label(file.getComment()));
	}
	private static String getFileName(ZipEntry entry){
		return entry.getName();
	}
	private static String getLastModified(ZipEntry entry){
		return entry.getLastModifiedTime().toString();
	}
	private static String getLastAccessed(ZipEntry entry){
		return entry.getLastAccessTime().toString();
	}
	private static String getCreation(ZipEntry entry){
		return entry.getCreationTime().toString();
	}
	private static long getSize(ZipEntry entry){
		return entry.getSize();
	}
	private static long getCompressedSize(ZipEntry entry){
		return entry.getCompressedSize();
	}
	private static String getComment(ZipEntry entry){
		return entry.getComment();
	}
	private static boolean isDirectory(ZipEntry entry){
		return entry.isDirectory();
	}
	private <T> void createColumnChooser(String name,Callback<TableColumn.CellDataFeatures<ZipEntry,T>,ObservableValue<T>> callback,boolean visible){
		TableColumn<ZipEntry,T> column=new TableColumn<>(name);
		column.setCellValueFactory(callback);
		column.setEditable(true);
		tree.getColumns().add(column);
		column.setVisible(visible);
	}
	public void setAction(Consumer<Collection<ZipEntry>> action){
		this.action=action;
	}
	public Consumer<Collection<ZipEntry>> getAction(){
		return action;
	}
	public void fireAction(){
		if(action!=null)
			action.accept(getSelectedPaths());
	}
	public void markPaths(){
		marked=getSelectedPaths();
	}
	public Collection<ZipEntry> getMarkedPaths(){
		return marked;
	}
	@Override
	public void requestFocus(){
		tree.requestFocus();
	}
	public final Collection<ZipEntry> getSelectedPaths(){
		return tree.getSelectionModel().getSelectedItems();
	}
	TableView<ZipEntry> getTree(){
		return tree;
	}
	class ZipCell extends TableCell<ZipEntry,String>{
		public ZipCell(){
		}
		@Override
		protected void updateItem(String item,boolean empty){
			super.updateItem(item,empty);
			ZipEntry entry=(ZipEntry)getTableRow().getItem();
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item);
				if(entry.isDirectory())
					setGraphic(new ImageView(FOLDER));
				else
					setGraphic(new ImageView(REGULAR));
			}
		}
	}
	private static Image FOLDER,REGULAR;
	static{
		try{
			FOLDER=new Image(new FileInputStream(Main.getFile("icons/folder.png","core")));
			REGULAR=new Image(new FileInputStream(Main.getFile("icons/regular.png","core")));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
}
