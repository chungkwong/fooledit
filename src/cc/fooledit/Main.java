/*
 * Copyright (C) 2017,2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit;

import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends Application{
	public static Main INSTANCE;
	private static final File USER_PATH=new File(System.getProperty("user.home"),".fooledit");
	private static final File SYSTEM_PATH=computePath();
	private static final File MODULE_PATH=new File(SYSTEM_PATH,"modules");
	private final RegistryNode<String,Command> globalCommandRegistry=new SimpleRegistryNode<>();
	private MenuRegistry menuRegistry;
	private final NavigableRegistryNode<String,String> keymapRegistry;
	private final Notifier notifier=new Notifier();
	private final BorderPane root=new BorderPane();
	private MiniBuffer input;
	private HBox commander;
	private final ScriptAPI script;
	private final Scene scene=new Scene(root);
	private Stage stage;
	private WorkSheet currentWorksheet;
	private List<KeyEvent> macro=new ArrayList<>();
	private boolean recording=false;
	private HistoryRing<Map<Object,Object>> worksheets=new HistoryRing<>();
	public Main(){
		INSTANCE=this;
		System.setProperty("user.dir",SYSTEM_PATH.toString());
		Logger.getGlobal().setLevel(Level.INFO);
		USER_PATH.mkdirs();
		try{
			Logger.getGlobal().addHandler(new StreamHandler(new FileOutputStream(new File(USER_PATH,"LOG")),new Notifier.SystemLogFormatter()));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		Logger.getGlobal().addHandler(notifier);
		scene.getStylesheets().add(getFile("stylesheets/base.css",CoreModule.NAME).toURI().toString());
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		scene.focusOwnerProperty().addListener((e,o,n)->System.out.println(n));
		URL.setURLStreamHandlerFactory(FoolURLStreamHandler.INSTNACE);
		script=new ScriptAPI();
		registerStandardCommand();
		Registry.ROOT.loadPreference();
		ModuleRegistry.loadDefault();
		CoreModule.PROTOCOL_REGISTRY.put("application",new ApplicationRegistry());
		CoreModule.PROTOCOL_REGISTRY.put("data",new DataStreamHandler());
		keymapRegistry=Registry.ROOT.registerKeymap(CoreModule.NAME);
		root.addEventFilter(KeyEvent.ANY,getKeyFilter());
		initMenuBar();
		root.setBottom(notifier.getStatusBar());
		loadDefaultWorkSheet();
		runScript();
		//notifier.addItem(Notifier.createTimeField(DateFormat.getDateTimeInstance()));
	}
	private void initMenuBar(){
		menuRegistry=Registry.ROOT.registerMenu(CoreModule.NAME);
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		input=new MiniBuffer();
		MenuBar bar=menuRegistry.getMenuBar();
		commander=new HBox(bar,new Label(),input);
		HBox.setHgrow(input,Priority.ALWAYS);
		root.setTop(commander);
	}
	private void registerStandardCommand(){
		addCommand("new",()->FileCommands.create());
		addCommand("open-file",()->FileCommands.open());
		addCommand("open-url",()->FileCommands.openUrl());
		addCommand("save",()->FileCommands.save());
		addCommand("save-as",()->FileCommands.saveAs());
		addCommand("full-screen",()->stage.setFullScreen(true));
		addCommand("toggle-full-screen",()->stage.setFullScreen(!stage.isFullScreen()));
		addCommand("exit-full-screen",()->stage.setFullScreen(false));
		addCommand("maximize-frame",()->stage.setMaximized(true));
		addCommand("iconify-frame",()->stage.setIconified(true));
		addCommand("always-on-top-frame",()->stage.setAlwaysOnTop(true));
		addCommand("split-vertically",()->{
			WorkSheet workSheet=getCurrentWorkSheet();
			if(workSheet.isCompound())
				workSheet.split(new WorkSheet(),Orientation.VERTICAL);
			else
				workSheet.split(new WorkSheet(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()),Orientation.VERTICAL);
		});
		addCommand("split-horizontally",()->{
			WorkSheet workSheet=getCurrentWorkSheet();
			if(workSheet.isCompound())
				workSheet.split(new WorkSheet(),Orientation.HORIZONTAL);
			else
				workSheet.split(new WorkSheet(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()),Orientation.HORIZONTAL);
		});
		addCommand("focus-left",()->focusLeft());
		addCommand("focus-right",()->focusRight());
		addCommand("focus-outer",()->focusOuter());
		addCommand("focus-inner",()->focusInner());
		addCommand("close-current-worksheet",()->closeCurrentWorkSheet());
		addCommand("file-system",()->addAndShow(DataObjectRegistry.create(FileSystemObjectType.INSTANCE)));
		addCommand("registry",()->addAndShow(DataObjectRegistry.create(RegistryEditor.INSTANCE)));
		addCommand("command",()->input.requestFocus());
		addCommand("cancel",()->getCurrentNode().requestFocus());
		addCommand("next-buffer",()->showOnCurrentTab(DataObjectRegistry.getNextDataObject(getCurrentDataObject())));
		addCommand("previous-buffer",()->showOnCurrentTab(DataObjectRegistry.getPreviousDataObject(getCurrentDataObject())));
		addCommand("start-record",()->{macro.clear();recording=true;});
		addCommand("stop-record",()->{recording=false;macro.remove(0);macro.remove(macro.size()-1);});
		addCommand("replay",()->{macro.forEach((e)->((Node)e.getTarget()).fireEvent(e));});
		addCommand("restore",()->getMiniBuffer().restore());
		addCommand("repeat",(o)->Command.repeat(o instanceof ScmNil?1:SchemeConverter.toInteger(ScmList.first(o))));
		addCommandBatch("map-mime-to-type",(o)->{
			DataObjectTypeRegistry.registerMime(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommandBatch("map-suffix-to-mime",(o)->{
			ContentTypeHelper.getSUFFIX_GUESSER().registerSuffix(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommandBatch("map-glob-to-mime",(o)->{
			ContentTypeHelper.getURL_GUESSER().registerPathPattern(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommandBatch("mime-alias",(o)->{
			CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommandBatch("mime-parent",(o)->{
			CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.put(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommandBatch("ensure-loaded",(o)->{
			ModuleRegistry.ensureLoaded(SchemeConverter.toString(ScmList.first(o)));
			return ScmNil.NIL;
		});
		addCommandBatch("get-registry",(o)->{
			return SchemeConverter.toScheme(Registry.ROOT.resolve(SchemeConverter.toString(ScmList.first(o))));
		});
		addCommandBatch("get-entry",(o)->{
			return SchemeConverter.toScheme(((RegistryNode)SchemeConverter.toJava(ScmList.first(o))).get(SchemeConverter.toString(ScmList.second(o))));
		});
		addCommandBatch("set-entry!",(o)->{
			return SchemeConverter.toScheme(((RegistryNode)SchemeConverter.toJava(ScmList.first(o))).put(SchemeConverter.toString(ScmList.second(o)),SchemeConverter.toJava(ScmList.third(o))));
		});
		addCommandBatch("get-or-create-registry",(o)->{
			return SchemeConverter.toScheme(((RegistryNode)SchemeConverter.toJava(ScmList.first(o))).getOrCreateChild(SchemeConverter.toString(ScmList.second(o))));
		});
		addCommandBatch("inform-jar",(o)->{
			String jar=SchemeConverter.toString(ScmList.first(o));
			String cls=SchemeConverter.toString(ScmList.second(o));
			String method=SchemeConverter.toString(ScmList.third(o));
			URLClassLoader loader=new URLClassLoader(new URL[]{new File(SYSTEM_PATH,jar).toURI().toURL()});
			loader.loadClass(cls).getMethod(method).invoke(null);
			return null;
		});
	}
	private void addCommand(String name,Runnable action){
		globalCommandRegistry.put(name,new Command(name,action,CoreModule.NAME));
	}
	private void addCommand(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		globalCommandRegistry.put(name,new Command(name,action,CoreModule.NAME));
	}
	private void addCommandBatch(String name,Runnable action){
		globalCommandRegistry.put(name,new Command(name,action,CoreModule.NAME,false));
	}
	private void addCommandBatch(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		globalCommandRegistry.put(name,new Command(name,action,CoreModule.NAME,false));
	}
	private Consumer<ObservableList<MenuItem>> getBufferMenu(){
		return (l)->{
			for(String name:DataObjectRegistry.getDataObjectNames()){
				MenuItem item=new MenuItem(name);
				item.setOnAction((e)->showOnNewTab(DataObjectRegistry.getDataObject(name)));
				l.add(item);
			}
			l.add(new SeparatorMenuItem());
			if(getCurrentWorkSheet().isCompound())
				return;
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
		MenuItem item=new MenuItem(MessageRegistry.getString(name.toUpperCase(),CoreModule.NAME));
		item.setOnAction((e)->TaskManager.executeCommand(globalCommandRegistry.get(name)));
		return item;
	}
	private void updateCurrentNode(Node node){
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null){
			if(currentWorksheet!=null)
				currentWorksheet.getStyleClass().remove("current");
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
	private void loadDefaultWorkSheet(){
		SimpleRegistryNode<String,Object> last;
		try{
			last=(SimpleRegistryNode<String,Object>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(new File(Main.INSTANCE.getUserPath(),"layout.json")));
			resetRootWorkSheet(WorkSheet.fromJSON(last));
		}catch(Exception ex){
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
		return globalCommandRegistry;
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
		try{
			primaryStage.getIcons().add(new Image(new File(getDataPath(),"core/icons/logo.png").toURI().toURL().toString()));
		}catch(MalformedURLException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
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
		System.exit(0);
	}
	public static File getSystemPath(){
		return SYSTEM_PATH;
	}
	public static File getDataPath(){
		return MODULE_PATH;
	}
	public static File getUserPath(){
		return USER_PATH;
	}
	public static File getFile(String path,String module){
		return new File(getModulePath(module),path);
	}
	public static File getModulePath(String module){
		return new File(MODULE_PATH,module);
	}
	private static File computePath(){
		URL url=Main.class.getResource("");
		if(url.getProtocol().equals("file")){
			File file=new File(url.getFile());
			while(!new File(file,"lib").exists()){
				file=file.getParentFile();
				if(file==null)
					return USER_PATH;
			}
			return file;
		}else{
			try{
				return new File(URLDecoder.decode(url.toString().substring(9,url.toString().indexOf('!')),"UTF-8")).getParentFile();
			}catch(UnsupportedEncodingException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return USER_PATH;
			}
		}
	}
	public static Map<Object,Object> loadJSON(String name){
		return loadJSON(new File(getDataPath(),name));
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
	private String currKey=null;
	private boolean ignoreKey=false;
	private EventHandler<KeyEvent> getKeyFilter(){
		return (KeyEvent e)->{
			if(recording)
				macro.add(e.copyFor(e.getSource(),e.getTarget()));
			if(e.getEventType().equals(KeyEvent.KEY_TYPED)){
				if(ignoreKey){
					e.consume();
				}
			}else if(e.getEventType().equals(KeyEvent.KEY_RELEASED)){
				if(ignoreKey){
					e.consume();
				}
			}else if(e.getEventType().equals(KeyEvent.KEY_PRESSED)){
				if(e.getCode().isModifierKey()){
					e.consume();
					return;
				}
				String code=currKey==null?encode(e):currKey+'+'+encode(e);
				Node node=scene.getFocusOwner();
				while(node!=null){
					Object local=node.getProperties().get("keymap");
					if(local instanceof NavigableRegistryNode&&checkForKey(code,(NavigableRegistryNode<String,String>)local)){
						e.consume();
						return;
					}
					node=node.getParent();
				}
				if(checkForKey(code,keymapRegistry)){
					e.consume();
				}else{
					currKey=null;
					getNotifier().notify("");
					ignoreKey=false;
				}
			}
		};
	}
	private boolean checkForKey(String code,NavigableRegistryNode<String,String> keymapRegistryNode){
		Map.Entry<String,String> entry=keymapRegistryNode.ceilingEntry(code);
		if(entry!=null){
			if(code.equals(entry.getKey())){
				currKey=null;
				TaskManager.executeCommand(getCommand(entry.getValue()));
				ignoreKey=true;
				return true;
			}else if(entry.getKey().startsWith(code+' ')){
				currKey=code;
				getNotifier().notify(MessageRegistry.getString("ENTERED",CoreModule.NAME)+code);
				ignoreKey=true;
				return true;
			}
		}
		return false;
	}
	public Command getCommand(String name){
		Node node=scene.getFocusOwner();
		while(node!=null){
			Object local=node.getProperties().get("commands");
			if(local instanceof RegistryNode){
				Command command=((RegistryNode<String,Command>)local).get(name);
				if(command!=null)
					return command;
			}
			node=node.getParent();
		}
		return globalCommandRegistry.get(name);
	}
	public Set<String> getCommandNames(){
		Node node=scene.getFocusOwner();
		Set<String> set=new HashSet<>(globalCommandRegistry.keySet());
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
			script.eval(new InputStreamReader(new FileInputStream(new File(new File(getSystemPath(),"etc"),"init.scm")),StandardCharsets.UTF_8));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,"Error in init script",ex);
		}
	}
	public ScriptAPI getScriptAPI(){
		return script;
	}
	class ApplicationRegistry extends URLStreamHandler{
		@Override
		protected URLConnection openConnection(URL u) throws IOException{
			return new ApplicationURLConnection(u);
		}
		private class ApplicationURLConnection extends URLConnection{
			public ApplicationURLConnection(URL url){
				super(url);
			}
			@Override
			public void connect() throws IOException{

			}
			@Override
			public String getContentType(){
				return CoreModule.APPLICATION_REGISTRY.get(getURL().getPath());
			}
		}
	}
	private final StringBuilder buf=new StringBuilder();
	private String encode(KeyEvent evt){
		buf.setLength(0);
		if(evt.isControlDown()||evt.isShortcutDown())
			buf.append("C-");
		if(evt.isAltDown())
			buf.append("M-");
		if(evt.isShiftDown())
			buf.append("S-");
		buf.append(evt.getCode().getName());
		return buf.toString();
	}
	private boolean isCurrentNodeFocused(){
		Node owner=scene.getFocusOwner();
		while(owner!=null&&owner!=currentWorksheet)
			owner=owner.getParent();
		return owner!=null;
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
				if(workSheet.getFirst()==currentWorksheet)
					workSheet.getLast().requestFocus();
				else
					workSheet.getFirst().requestFocus();
			}else if(workSheet.isTabed()){
				((TabPane)workSheet.getCenter()).getSelectionModel().selectNext();
			}
		}
	}
	private void focusLeft(){
		WorkSheet workSheet=getCurrentWorkSheet().getParentWorkSheet();
		if(workSheet!=null){
			if(workSheet.isSplit()){
				if(workSheet.getFirst()==currentWorksheet)
					workSheet.getLast().requestFocus();
				else
					workSheet.getFirst().requestFocus();
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
		currentWorksheet=workSheet;
		root.setCenter(workSheet);
		CoreModule.REGISTRY.put(CoreModule.WINDOW_REGISTRY_NAME,workSheet.getRegistry());
	}
}