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
package cc.fooledit.editor.zip;
import cc.fooledit.core.*;
import java.util.*;
import java.util.function.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.apache.commons.compress.archivers.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveViewer extends BorderPane{
	private final TableView<ArchiveEntry> tree=new TableView<>();
	private Consumer<Collection<ArchiveEntry>> action;
	private Collection<ArchiveEntry> marked=Collections.emptySet();
	public ArchiveViewer(List<ArchiveEntry> entries){
		tree.getItems().setAll(entries);
		tree.setTableMenuButtonVisible(true);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.<String>createColumnChooser(MessageRegistry.getString("NAME",Activator.class),(param)
				->new ReadOnlyStringWrapper(getFileName(param.getValue())),true);
		this.<Number>createColumnChooser(MessageRegistry.getString("SIZE",Activator.class),(param)
				->new ReadOnlyLongWrapper(getSize(param.getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("LAST_MODIFIED",Activator.class),(param)
				->new ReadOnlyStringWrapper(getLastModified(param.getValue())),true);
		((TableColumn<ArchiveEntry,String>)tree.getColumns().get(0)).setCellFactory((p)->new ZipCell());
		((TableColumn<ArchiveEntry,String>)tree.getColumns().get(0)).prefWidthProperty().bind(tree.widthProperty().multiply(0.4));
		setCenter(tree);
		tree.getFocusModel().focusedIndexProperty().addListener(((e,o,n)->tree.scrollTo(n.intValue())));
	}
	private static String getFileName(ArchiveEntry entry){
		return entry.getName();
	}
	private static String getLastModified(ArchiveEntry entry){
		return Objects.toString(entry.getLastModifiedDate(),"");
	}
	private static long getSize(ArchiveEntry entry){
		return entry.getSize();
	}
	private static boolean isDirectory(ArchiveEntry entry){
		return entry.isDirectory();
	}
	private <T> void createColumnChooser(String name,Callback<TableColumn.CellDataFeatures<ArchiveEntry,T>,ObservableValue<T>> callback,boolean visible){
		TableColumn<ArchiveEntry,T> column=new TableColumn<>(name);
		column.setCellValueFactory(callback);
		column.setEditable(true);
		tree.getColumns().add(column);
		column.setVisible(visible);
	}
	public void setAction(Consumer<Collection<ArchiveEntry>> action){
		this.action=action;
	}
	public Consumer<Collection<ArchiveEntry>> getAction(){
		return action;
	}
	public void fireAction(){
		if(action!=null){
			action.accept(getSelectedPaths());
		}
	}
	public void markPaths(){
		marked=getSelectedPaths();
	}
	public Collection<ArchiveEntry> getMarkedPaths(){
		return marked;
	}
	@Override
	public void requestFocus(){
		tree.requestFocus();
	}
	public final Collection<ArchiveEntry> getSelectedPaths(){
		return tree.getSelectionModel().getSelectedItems();
	}
	TableView<ArchiveEntry> getTree(){
		return tree;
	}
	class ZipCell extends TableCell<ArchiveEntry,String>{
		public ZipCell(){
		}
		@Override
		protected void updateItem(String item,boolean empty){
			super.updateItem(item,empty);
			ArchiveEntry entry=(ArchiveEntry)getTableRow().getItem();
			if(empty||entry==null||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item);
				if(entry.isDirectory()){
					setGraphic(new ImageView(FOLDER));
				}else{
					setGraphic(new ImageView(REGULAR));
				}
			}
		}
	}
	private static Image FOLDER, REGULAR;
	static{
		FOLDER=new Image(ArchiveViewer.class.getResourceAsStream("/folder.png"));
		REGULAR=new Image(ArchiveViewer.class.getResourceAsStream("/regular.png"));
	}
}
