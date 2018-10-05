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
package cc.fooledit.core;
import cc.fooledit.control.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import static javafx.application.Application.launch;
import javafx.application.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.activation.*;
import org.osgi.framework.*;
import org.osgi.framework.launch.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Main extends Application{
	public static Main INSTANCE;
	private static final File USER_PATH=new File(System.getProperty("user.home"),".fooledit");
	private MenuRegistry menuRegistry;
	private final Notifier notifier=new Notifier();
	private final BorderPane root=new BorderPane();
	private MiniBuffer input;
	private HBox commander;
	private final ScriptAPI script;
	private final Scene scene=new Scene(root);
	private Stage stage;
	private Node mainFocusOwner;
	private WorkSheet currentWorksheet;
	private KeymapManager keymapManager=new KeymapManager(scene);
	private HistoryRing<Map<Object,Object>> worksheets=new HistoryRing<>();
	public Main(){
		INSTANCE=this;
		Logger.getGlobal().setLevel(Level.INFO);
		USER_PATH.mkdirs();
		try{
			Logger.getGlobal().addHandler(new StreamHandler(new FileOutputStream(new File(USER_PATH,"LOG")),new Notifier.SystemLogFormatter()));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		Logger.getGlobal().addHandler(notifier);
		scene.getStylesheets().add(getClass().getResource("/base.css").toString());
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		//scene.focusOwnerProperty().addListener((e,o,n)->System.out.println(n));
		script=new ScriptAPI();
		registerStandardCommand();
		keymapManager.adopt(root,Registry.ROOT.registerKeymap(Activator.class),CoreModule.COMMAND_REGISTRY);
		initMenuBar();
		root.setBottom(notifier.getStatusBar());
		loadDefaultWorkSheet();
		runScript();
		//notifier.addItem(Notifier.createTimeField(DateFormat.getDateTimeInstance()));
	}
	private void initMenuBar(){
		menuRegistry=Registry.ROOT.registerMenu(Activator.class);
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		input=new MiniBuffer();
		MenuBar bar=menuRegistry.getMenuBar();
		commander=new HBox(bar,new Label(),input);
		HBox.setHgrow(input,Priority.ALWAYS);
		root.setTop(commander);
	}
	private void registerStandardCommand(){
		addCommand("full-screen",()->stage.setFullScreen(true));
		addCommand("toggle-full-screen",()->stage.setFullScreen(!stage.isFullScreen()));
		addCommand("exit-full-screen",()->stage.setFullScreen(false));
		addCommand("maximize-frame",()->stage.setMaximized(true));
		addCommand("iconify-frame",()->stage.setIconified(true));
		addCommand("always-on-top-frame",()->stage.setAlwaysOnTop(true));
		addCommand("split-vertically",()->{
			WorkSheet workSheet=getCurrentWorkSheet();
			if(workSheet.isCompound()){
				workSheet.split(new WorkSheet(),Orientation.VERTICAL);
			}else{
				workSheet.split(new WorkSheet(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()),Orientation.VERTICAL);
			}
		});
		addCommand("split-horizontally",()->{
			WorkSheet workSheet=getCurrentWorkSheet();
			if(workSheet.isCompound()){
				workSheet.split(new WorkSheet(),Orientation.HORIZONTAL);
			}else{
				workSheet.split(new WorkSheet(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()),Orientation.HORIZONTAL);
			}
		});
		addCommand("focus-left",()->focusLeft());
		addCommand("focus-right",()->focusRight());
		addCommand("focus-outer",()->focusOuter());
		addCommand("focus-inner",()->focusInner());
		addCommand("close-current-worksheet",()->closeCurrentWorkSheet());
		addCommand("registry",()->addAndShow(DataObjectRegistry.create(RegistryEditor.INSTANCE)));
		addCommand("command",()->input.requestFocus());
		addCommand("cancel",()->getCurrentNode().requestFocus());
		addCommand("next-buffer",()->showOnCurrentTab(DataObjectRegistry.getNextDataObject(getCurrentDataObject())));
		addCommand("previous-buffer",()->showOnCurrentTab(DataObjectRegistry.getPreviousDataObject(getCurrentDataObject())));
		addCommand("start-record",()->keymapManager.startRecording());
		addCommand("stop-record",()->keymapManager.stopRecording());
		addCommand("replay",()->keymapManager.getMacro().forEach((e)->((Node)e.getTarget()).fireEvent(e)));
		addCommand("restore",()->getMiniBuffer().restore());
		addCommand("repeat",(o)->Command.repeat(o.length==0?1:((Number)o[0]).intValue()));
		addCommandBatch("map-mime-to-type",(o)->{
			DataObjectTypeRegistry.registerMime((String)o[0],(String)o[1]);
			return null;
		});
		addCommandBatch("map-suffix-to-mime",(o)->{
			ContentTypeHelper.getSUFFIX_GUESSER().registerSuffix((String)o[0],(String)o[1]);
			return null;
		});
		addCommandBatch("map-glob-to-mime",(o)->{
			ContentTypeHelper.getURL_GUESSER().registerPathPattern((String)o[0],(String)o[1]);
			return null;
		});
		addCommandBatch("mime-alias",(o)->{
			CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put((String)o[0],(String)o[1]);
			return null;
		});
		addCommandBatch("mime-parent",(o)->{
			CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.put((String)o[0],(String)o[1]);
			return null;
		});
		addCommandBatch("ensure-installed",(o)->{
			ModuleRegistry.ensureInstalled((String)o[0]);
			return null;
		});
		addCommandBatch("get-registry",(o)->{
			return Registry.ROOT.resolve((String)o[0]);
		});
		addCommandBatch("get-entry",(o)->{
			return ((RegistryNode)o[0]).get((String)o[1]);
		});
		addCommandBatch("set-entry!",(o)->{
			return ((RegistryNode)o[0]).put((String)o[1],o[2]);
		});
		addCommandBatch("get-or-create-registry",(o)->{
			return ((RegistryNode)o[0]).getOrCreateChild((String)o[1]);
		});
		addCommand("reload",(o)->{
			showOnCurrentTab(getCurrentDataObject());
			return null;
		});
	}
	private void addCommand(String name,Runnable action){
		CoreModule.COMMAND_REGISTRY.put(name,new Command(name,action,Activator.class));
	}
	private void addCommand(String name,ThrowableVarargsFunction<Object,Object> action){
		CoreModule.COMMAND_REGISTRY.put(name,new Command(name,action,Activator.class));
	}
	private void addCommandBatch(String name,Runnable action){
		CoreModule.COMMAND_REGISTRY.put(name,new Command(name,action,Activator.class,false));
	}
	private void addCommandBatch(String name,ThrowableVarargsFunction<Object,Object> action){
		CoreModule.COMMAND_REGISTRY.put(name,new Command(name,action,Activator.class,false));
	}
	private Consumer<ObservableList<MenuItem>> getBufferMenu(){
		return (l)->{
			for(String name:DataObjectRegistry.getDataObjectNames()){
				MenuItem item=new MenuItem(name);
				item.setOnAction((e)->showOnNewTab(DataObjectRegistry.getDataObject(name)));
				l.add(item);
			}
			l.add(new SeparatorMenuItem());
			if(getCurrentWorkSheet().isCompound()){
				return;
			}
			DataObject curr=getCurrentData();
			DataObjectTypeRegistry.getDataEditors(curr.getClass()).forEach((editor)->{
				MenuItem item=new MenuItem(editor.getName());
				item.setOnAction((e)->getCurrentWorkSheet().keepOnly(new WorkSheet(getCurrentDataObject(),editor,null)));
				l.add(item);
			});
			l.add(new SeparatorMenuItem());
			l.add(createCommandMenuItem("next_buffer"));
			l.add(createCommandMenuItem("previous_buffer"));
			if(!currentWorksheet.isCompound()){
				l.add(new SeparatorMenuItem());
				DataObjectTypeRegistry.getDataToolBoxs(getCurrentDataEditor().getClass()).forEach((tool)->{
					MenuItem item=new MenuItem(((ToolBox)tool).getDisplayName());
					item.setOnAction((e)->getCurrentWorkSheet().showToolBox((ToolBox)tool,null));
					l.add(item);
				});
			}
		};
	}
	private Consumer<ObservableList<MenuItem>> getHistoryMenu(){
		return (l)->{
			ListRegistryNode<RegistryNode<String,Object>> history=CoreModule.HISTORY_REGISTRY;
			for(int i=0;i<history.size();i++){
				RegistryNode<String,Object> prop=history.get(i);
				MenuItem item=new MenuItem((String)prop.get(DataObject.BUFFER_NAME));
				item.setOnAction((e)->{
					try{
						URL file=new URI((String)prop.get(DataObject.URI)).toURL();
						if(prop.containsKey(DataObject.MIME)){
							showOnNewTab(DataObjectRegistry.readFrom(file,new MimeType((String)prop.get(DataObject.MIME))));
						}else{
							showOnNewTab(DataObjectRegistry.readFrom(file));
						}
					}catch(Exception ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
					}
				});
				l.add(item);
			}
		};
	}
	private MenuItem createCommandMenuItem(String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name.toUpperCase(),Activator.class));
		item.setOnAction((e)->TaskManager.executeCommand(CoreModule.COMMAND_REGISTRY.get(name)));
		return item;
	}
	private void updateCurrentNode(Node node){
		Node focusOwner=node;
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null){
			mainFocusOwner=focusOwner;
			if(currentWorksheet!=null){
				currentWorksheet.getStyleClass().remove("current");
				currentWorksheet.applyCss();
			}
			node.getStyleClass().add("current");
			currentWorksheet=(WorkSheet)node;
			if(!((WorkSheet)node).isCompound()){
				commander.getChildren().set(1,((WorkSheet)node).getDataEditor().getMenuRegistry().getMenuBar());
			}
		}
	}
	public void addAndShow(RegistryNode<String,Object> data){
		DataObjectRegistry.addDataObject(data);
		showOnNewTab(data);
	}
	public void showOnCurrentTab(RegistryNode<String,Object> data){
		getCurrentWorkSheet().keepOnly(new WorkSheet(data,getDefaultEditor(data),null));
	}
	public void showOnNewTab(RegistryNode<String,Object> data){
		addNewTab(new WorkSheet(data,getDefaultEditor(data),null));
	}
	public void showOnNewTabGroup(RegistryNode<String,Object> data){
		WorkSheet currentWorkSheet=getCurrentWorkSheet();
		if(currentWorkSheet.getWidth()<currentWorkSheet.getHeight()){
			currentWorkSheet.split(new WorkSheet(data,getDefaultEditor(data),null),Orientation.VERTICAL);
		}else{
			currentWorkSheet.split(new WorkSheet(data,getDefaultEditor(data),null),Orientation.HORIZONTAL);
		}
	}
	public DataEditor getDefaultEditor(RegistryNode<String,Object> data){
		return DataObjectTypeRegistry.getDataEditors((Class<? extends DataObject>)data.get(DataObject.DATA).getClass()).get(0);
	}
	public DataObject getCurrentData(){
		return (DataObject)getCurrentDataObject().get(DataObject.DATA);
	}
	public RegistryNode<String,Object> getCurrentDataObject(){
		return getCurrentWorkSheet().getDataObject();
	}
	public DataEditor getCurrentDataEditor(){
		return getCurrentWorkSheet().getDataEditor();
	}
	public Object getCurrentRemark(){
		return getCurrentDataEditor().getRemark(getCurrentNode());
	}
	public Node getCurrentNode(){
		return currentWorksheet!=null?currentWorksheet.getNode():null;
	}
	public WorkSheet getCurrentWorkSheet(){
		return currentWorksheet;
	}
	public void setCurrentWorkSheet(WorkSheet workSheet){
		updateCurrentNode(workSheet.getCenter());
	}
	public KeymapManager getKeymapManager(){
		return keymapManager;
	}
	private void loadDefaultWorkSheet(){
		//Activator.bundleContext.getBundle(currKey);
		SimpleRegistryNode<String,Object> last;
		try{
			last=(SimpleRegistryNode<String,Object>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(new File(Main.INSTANCE.getUserPath(),"layout.json")));
			resetRootWorkSheet(WorkSheet.fromJSON(last));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,"Failed to restore workspace",ex);
			resetRootWorkSheet(new WorkSheet());
		}
		EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->{
			try{
				Helper.writeText(StandardSerializiers.JSON_SERIALIZIER.encode(CoreModule.REGISTRY.get(CoreModule.WINDOW_REGISTRY_NAME)),new File(Main.INSTANCE.getUserPath(),"layout.json"));
			}catch(Exception ex){
				Logger.getLogger(CoreModule.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	public RegistryNode<String,Command> getGlobalCommandRegistry(){
		return CoreModule.COMMAND_REGISTRY;
	}
	private RegistryNode<String,Command> getLocalCommandRegistry(){
		WorkSheet workSheet=getCurrentWorkSheet();
		if(workSheet.isCompound()){
			return (RegistryNode<String,Command>)CoreModule.REGISTRY.getOrCreateChild("EMPTY");
		}else{
			return workSheet.getDataEditor().getCommandRegistry();
		}
	}
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public MiniBuffer getMiniBuffer(){
		return input;
	}
	public Scene getScene(){
		return scene;
	}
	public Stage getStage(){
		return stage;
	}
	public Notifier getNotifier(){
		return notifier;
	}
	@Override
	public void start(Stage primaryStage){
		stage=primaryStage;
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
		primaryStage.setTitle("fooledit");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.show();
		Platform.runLater(()->EventManager.fire(EventManager.SHOWN,primaryStage));
	}
	@Override
	public void stop() throws Exception{
		super.stop();
		EventManager.fire(EventManager.SHUTDOWN,null);
		for(Bundle bundle:Activator.bundleContext.getBundles()){
			if(bundle instanceof Framework){
				bundle.stop();
			}
		}
		System.exit(0);
	}
	public static File getUserPath(){
		return USER_PATH;
	}
	public static Map<Object,Object> loadJSON(File file){
		Map<Object,Object> obj;
		try{
			obj=(Map<Object,Object>)JSONDecoder.decode(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
		}catch(IOException|SyntaxException ex){
			obj=Collections.emptyMap();
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return obj;
	}
	public Command getCommand(String name){
		Node node=mainFocusOwner;
		while(node!=null){
			Object local=node.getProperties().get("commands");
			if(local instanceof RegistryNode){
				Command command=((RegistryNode<String,Command>)local).get(name);
				if(command!=null){
					return command;
				}
			}
			node=node.getParent();
		}
		return CoreModule.COMMAND_REGISTRY.get(name);
	}
	public Set<String> getCommandNames(){
		Node node=mainFocusOwner;
		Set<String> set=new HashSet<>(CoreModule.COMMAND_REGISTRY.keySet());
		while(node!=null){
			Object local=node.getProperties().get("commands");
			if(local instanceof RegistryNode){
				set.addAll(((RegistryNode<String,Command>)local).keySet());
			}
			node=node.getParent();
		}
		return set;
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		launch(args);
	}
	private void runScript(){
		try{
			//script.eval(new InputStreamReader(new FileInputStream(new File(new File(MODULE_PATH.getParent(),"etc"),"init.scm")),StandardCharsets.UTF_8));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,"Error in init script",ex);
		}
	}
	public ScriptAPI getScriptAPI(){
		return script;
	}
	private final StringBuilder buf=new StringBuilder();
	private String encode(KeyEvent evt){
		buf.setLength(0);
		if(evt.isControlDown()||evt.isShortcutDown()){
			buf.append("C-");
		}
		if(evt.isAltDown()){
			buf.append("M-");
		}
		if(evt.isShiftDown()){
			buf.append("S-");
		}
		buf.append(evt.getCode().getName());
		return buf.toString();
	}
	private void focusOuter(){
		WorkSheet workSheet=getCurrentWorkSheet().getParentWorkSheet();
		if(workSheet!=null){
			workSheet.requestFocus();
		}
	}
	private void focusInner(){
		WorkSheet workSheet=getCurrentWorkSheet();
		if(workSheet!=null){
			if(workSheet.isSplit()){
				workSheet.getFirst().requestFocus();
			}else if(workSheet.isTabed()){
				workSheet.getTabs().findFirst().ifPresent((c)->c.requestFocus());
			}
		}
	}
	private void focusRight(){
		WorkSheet workSheet=getCurrentWorkSheet().getParentWorkSheet();
		if(workSheet!=null){
			if(workSheet.isSplit()){
				if(workSheet.getFirst()==currentWorksheet){
					workSheet.getLast().requestFocus();
				}else{
					workSheet.getFirst().requestFocus();
				}
			}else if(workSheet.isTabed()){
				((TabPane)workSheet.getCenter()).getSelectionModel().selectNext();
			}
		}
	}
	private void focusLeft(){
		WorkSheet workSheet=getCurrentWorkSheet().getParentWorkSheet();
		if(workSheet!=null){
			if(workSheet.isSplit()){
				if(workSheet.getFirst()==currentWorksheet){
					workSheet.getLast().requestFocus();
				}else{
					workSheet.getFirst().requestFocus();
				}
			}else if(workSheet.isTabed()){
				((TabPane)workSheet.getCenter()).getSelectionModel().selectPrevious();
			}
		}
	}
	private void addNewTab(WorkSheet sheet){
		WorkSheet workSheet=getCurrentWorkSheet().getParentWorkSheet();
		if(workSheet==null){
			workSheet=new WorkSheet();
			workSheet.addTab((WorkSheet)root.getCenter());
			workSheet.addTab(sheet);
			resetRootWorkSheet(workSheet);
		}else{
			workSheet.addTab(sheet);
		}
	}
	private void closeCurrentWorkSheet(){
		WorkSheet workSheet=getCurrentWorkSheet();
		WorkSheet parentWorkSheet=workSheet.getParentWorkSheet();
		if(parentWorkSheet==null){
			resetRootWorkSheet(new WorkSheet());
		}else if(parentWorkSheet.isTabed()){
			parentWorkSheet.removeTab(workSheet);
		}else if(parentWorkSheet.isSplit()){
			parentWorkSheet.keepOnly(parentWorkSheet.getAnother(workSheet));
		}
	}
	private void resetRootWorkSheet(WorkSheet workSheet){
		if(root.getCenter() instanceof WorkSheet){
			((WorkSheet)root.getCenter()).dispose(workSheet);
		}
		currentWorksheet=workSheet;
		root.setCenter(workSheet);
		CoreModule.REGISTRY.put(CoreModule.WINDOW_REGISTRY_NAME,workSheet.getRegistry());
	}
}
