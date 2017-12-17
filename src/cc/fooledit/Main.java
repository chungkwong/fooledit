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
package cc.fooledit;

import cc.fooledit.api.*;
import cc.fooledit.command.*;
import cc.fooledit.control.*;
import cc.fooledit.example.filesystem.*;
import cc.fooledit.example.text.*;
import cc.fooledit.model.*;
import cc.fooledit.setting.*;
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
	private final File SYSTEM_PATH=computePath();
	private final File MODULE_PATH=new File(SYSTEM_PATH,"modules");
	private final File USER_PATH=new File(System.getProperty("user.home"),".fooledit");
	private final CommandRegistry globalCommandRegistry=new CommandRegistry();
	private final BiMap<String,Command> commandRegistry=new BiMap<>(globalCommandRegistry,new HashMap<>());
	private MenuRegistry menuRegistry;
	private final NavigableRegistryNode<String,String,String> keymapRegistry;
	private final Notifier notifier;
	private final BorderPane root=new BorderPane();
	private MiniBuffer input;
	private HBox commander;
	private final ScriptAPI script;
	private final Scene scene=new Scene(root);
	private Stage stage;
	private Node currentNode;
	private List<KeyEvent> macro=new ArrayList<>();
	private boolean recording=false;
	private HistoryRing<Map<Object,Object>> worksheets=new HistoryRing<>();
	public Main(){
		INSTANCE=this;
		System.setProperty("user.dir",SYSTEM_PATH.toString());
		URL.setURLStreamHandlerFactory(FoolURLStreamHandler.INSTNACE);
		CoreModule.PROTOCOL_REGISTRY.addChild("application",new ApplicationRegistry());
		notifier=new Notifier(this);
		Logger.getGlobal().setLevel(Level.INFO);
		try{
			Logger.getGlobal().addHandler(new StreamHandler(new FileOutputStream(new File(USER_PATH,"LOG")),new Notifier.SystemLogFormatter()));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		Logger.getGlobal().addHandler(notifier);
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		registerStandardCommand();
		keymapRegistry=Registry.ROOT.registerKeymap(CoreModule.NAME);
		new KeymapSupport();
		initMenuBar();
		script=new ScriptAPI();
		ModuleRegistry.loadDefault();
		loadDefaultWorkSheet();
		root.setBottom(notifier.getStatusBar());
		runScript();
		//notifier.addItem(Notifier.createTimeField(DateFormat.getDateTimeInstance()));
	}
	private void initMenuBar(){
		menuRegistry=new MenuRegistry(CoreModule.NAME);
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		input=new MiniBuffer(this);
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
		addCommand("split-vertically",()->getCurrentWorkSheet().splitVertically(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		addCommand("split-horizontally",()->getCurrentWorkSheet().splitHorizontally(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		addCommand("keep-only",()->((WorkSheet)root.getCenter()).keepOnly(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		addCommand("file-system",()->addAndShow(DataObjectRegistry.create(FileSystemDataType.INSTANCE)));
		addCommand("command",()->input.requestFocus());
		addCommand("cancel",()->getCurrentNode().requestFocus());
		addCommand("next-buffer",()->showDefault(DataObjectRegistry.getNextDataObject(getCurrentDataObject())));
		addCommand("previous-buffer",()->showDefault(DataObjectRegistry.getPreviousDataObject(getCurrentDataObject())));
		addCommand("start-record",()->{macro.clear();recording=true;});
		addCommand("stop-record",()->{recording=false;macro.remove(0);macro.remove(macro.size()-1);});
		addCommand("replay",()->{macro.forEach((e)->((Node)e.getTarget()).fireEvent(e));});
		addCommand("restore",()->getMiniBuffer().restore());
		addCommand("repeat",(o)->Command.repeat(o instanceof ScmNil?1:SchemeConverter.toInteger(ScmList.first(o))));
		addCommand("map-mime-to-type",(o)->{
			DataObjectTypeRegistry.registerMime(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommand("map-suffix-to-mime",(o)->{
			ContentTypeDetectorRegistry.getSUFFIX_GUESSER().registerSuffix(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommand("map-glob-to-mime",(o)->{
			ContentTypeDetectorRegistry.getURL_GUESSER().registerPathPattern(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommand("mime-alias",(o)->{
			CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.addChild(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		addCommand("ensure-loaded",(o)->{
			ModuleRegistry.ensureLoaded(SchemeConverter.toString(ScmList.first(o)));
			return null;
		});
		addCommand("get-registry",(o)->{
			return SchemeConverter.toScheme(Registry.ROOT.resolve(SchemeConverter.toString(ScmList.first(o))));
		});
		addCommand("get-entry",(o)->{
			return SchemeConverter.toScheme(((RegistryNode)SchemeConverter.toJava(ScmList.first(o))).getChild(SchemeConverter.toString(ScmList.second(o))));
		});
		addCommand("set-entry!",(o)->{
			return SchemeConverter.toScheme(((RegistryNode)SchemeConverter.toJava(ScmList.first(o))).addChild(SchemeConverter.toString(ScmList.second(o)),SchemeConverter.toJava(ScmList.third(o))));
		});
	}
	private void addCommand(String name,Runnable action){
		globalCommandRegistry.put(name,action,CoreModule.NAME);
	}
	private void addCommand(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		globalCommandRegistry.put(name,action,CoreModule.NAME);
	}
	private Consumer<ObservableList<MenuItem>> getBufferMenu(){
		return (l)->{
			for(String name:DataObjectRegistry.getDataObjectNames()){
				MenuItem item=new MenuItem(name);
				item.setOnAction((e)->showDefault(DataObjectRegistry.getDataObject(name)));
				l.add(item);
			}
			l.add(new SeparatorMenuItem());
			DataObject curr=getCurrentDataObject();
			DataObjectTypeRegistry.getDataEditors(curr.getClass()).forEach((editor)->{
				MenuItem item=new MenuItem(editor.getName());
				item.setOnAction((e)->getCurrentWorkSheet().keepOnly(curr,editor,null));
				l.add(item);
			});
			l.add(new SeparatorMenuItem());
			l.add(createCommandMenuItem("next_buffer"));
			l.add(createCommandMenuItem("previous_buffer"));
		};
	}
	private Consumer<ObservableList<MenuItem>> getHistoryMenu(){
		return (l)->{
			for(Map<String,String> prop:DataObjectRegistry.getHistoryList()){
				MenuItem item=new MenuItem(prop.get(DataObject.BUFFER_NAME));
				item.setOnAction((e)->{
					try{
						URL file=new URI(prop.get(DataObject.URI)).toURL();
						if(prop.containsKey(DataObject.MIME)){
							show(DataObjectRegistry.readFrom(file,new MimeType(prop.get(DataObject.MIME))));
						}else{
							show(DataObjectRegistry.readFrom(file));
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
		item.setOnAction((e)->globalCommandRegistry.get(name).accept(ScmNil.NIL));
		return item;
	}
	private void updateCurrentNode(Node node){
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null){
			currentNode=((WorkSheet)node).getCenter();
			commander.getChildren().set(1,((WorkSheet)node).getDataEditor().getMenuRegistry().getMenuBar());
			commandRegistry.setLocal(getLocalCommandRegistry());
		}
	}
	public void addAndShow(DataObject data){
		DataObjectRegistry.addDataObject(data);
		showDefault(data);
	}
	public void show(DataObject data){
		showDefault(data);
	}
	private void showDefault(DataObject data){
		getCurrentWorkSheet().keepOnly(data,getDefaultEditor(data),null);
	}
	public DataEditor getDefaultEditor(DataObject data){
		return DataObjectTypeRegistry.getDataEditors(data.getClass()).get(0);
	}
	public DataObject getCurrentDataObject(){
		return getCurrentWorkSheet().getDataObject();
	}
	public DataEditor getCurrentDataEditor(){
		return getCurrentWorkSheet().getDataEditor();
	}
	public Object getCurrentRemark(){
		return getCurrentDataEditor().getRemark(currentNode);
	}
	public Node getCurrentNode(){
		return currentNode;
	}
	public WorkSheet getCurrentWorkSheet(){
		return (WorkSheet)currentNode.getParent();
	}
	public void setCurrentWorkSheet(WorkSheet workSheet){
		updateCurrentNode(workSheet.getCenter());
	}
	private void loadDefaultWorkSheet(){
		worksheets.registerComamnds("worksheet",()->((WorkSheet)root.getCenter()).toJSON(),(json)->{
			root.setCenter(WorkSheet.fromJSON(json));
		},globalCommandRegistry);
		PersistenceStatusManager.registerConvertor("layout.json",WorkSheet.CONVERTOR);
		root.setCenter((WorkSheet)PersistenceStatusManager.USER.getOrDefault("layout.json",()->{
			String msg=MessageRegistry.getString("WELCOME",CoreModule.NAME);
			TextObject welcome=DataObjectRegistry.create(TextObjectType.INSTANCE);
			welcome.getText().set(msg);
			DataObjectRegistry.addDataObject(welcome);
			WorkSheet workSheet=new WorkSheet(welcome,getDefaultEditor(welcome),null);
			setCurrentWorkSheet(workSheet);
			return workSheet;
		}));
	}
	public CommandRegistry getGlobalCommandRegistry(){
		return globalCommandRegistry;
	}
	private CommandRegistry getLocalCommandRegistry(){
		return getCurrentWorkSheet().getDataEditor().getCommandRegistry();
	}
	public NavigableRegistryNode<String,String,String> getGlobalKeymapRegistry(){
		return keymapRegistry;
	}
	private NavigableRegistryNode<String,String,String> getLocalKeymapRegistry(){
		return getCurrentWorkSheet().getDataEditor().getKeymapRegistry();
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
	}
	@Override
	public void stop() throws Exception{
		super.stop();
		System.exit(0);
	}
	public File getSystemPath(){
		return SYSTEM_PATH;
	}
	public File getDataPath(){
		return MODULE_PATH;
	}
	public File getUserPath(){
		return USER_PATH;
	}
	public File getFile(String path,String module){
		return new File(getModulePath(module),path);
	}
	public File getModulePath(String module){
		return new File(MODULE_PATH,module);
	}
	private File computePath(){
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
	public Map<Object,Object> loadJSON(String name){
		return loadJSON(new File(getDataPath(),name));
	}
	public Map<Object,Object> loadJSON(File file){
		Map<Object,Object> obj;
		try{
			obj=(Map<Object,Object>)JSONDecoder.decode(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
		}catch(IOException|SyntaxException ex){
			obj=Collections.emptyMap();
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return obj;
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
	public BiMap<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	class KeymapSupport{
		private String curr=null;
		boolean ignore=false;
		public KeymapSupport(){
			root.addEventFilter(KeyEvent.ANY,(KeyEvent e)->{
				if(recording)
					macro.add(e.copyFor(e.getSource(),e.getTarget()));
				if(e.getEventType().equals(KeyEvent.KEY_TYPED)){
					if(ignore){
						e.consume();
					}
				}else if(e.getEventType().equals(KeyEvent.KEY_RELEASED)){
					if(ignore){
						e.consume();
					}
				}else if(e.getEventType().equals(KeyEvent.KEY_PRESSED)){
					if(e.getCode().isModifierKey()){
						e.consume();
						return;
					}
					String code=curr==null?encode(e):curr+'+'+encode(e);
					Map.Entry<String,String> localEntry=getLocalKeymapRegistry().ceilingEntry(code);
					Map.Entry<String,String> globalEntry=getGlobalKeymapRegistry().ceilingEntry(code);
					String next;
					String commandName;
					if(localEntry!=null&&isCurrentNodeFocused()){
						if(globalEntry!=null&&globalEntry.getKey().compareTo(localEntry.getKey())<0){
							next=globalEntry.getKey();commandName=globalEntry.getValue();
						}else{
							next=localEntry.getKey();commandName=localEntry.getValue();
						}
					}else if(globalEntry!=null){
						next=globalEntry.getKey();commandName=globalEntry.getValue();
					}else{
						next=null;commandName=null;
					}
					if(code.equals(next)){
						e.consume();
						curr=null;
						input.executeCommand(getCommandRegistry().get(commandName));
						ignore=true;
					}else if(next!=null&&next.startsWith(code+' ')){
						e.consume();
						curr=code;
						getNotifier().notify(MessageRegistry.getString("ENTERED",CoreModule.NAME)+code);
						ignore=true;
					}else{
						curr=null;
						getNotifier().notify("");
						ignore=false;
					}
				}
			});
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
	}
	private boolean isCurrentNodeFocused(){
		Node owner=scene.getFocusOwner();
		while(owner!=null&&owner!=currentNode)
			owner=owner.getParent();
		return owner!=null;
	}
}