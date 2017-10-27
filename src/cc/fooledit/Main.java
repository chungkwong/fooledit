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

import static cc.fooledit.api.KeymapRegistry.encode;
import cc.fooledit.api.*;
import cc.fooledit.command.*;
import cc.fooledit.control.*;
import cc.fooledit.example.browser.*;
import cc.fooledit.example.filesystem.*;
import cc.fooledit.example.terminal.*;
import cc.fooledit.example.text.*;
import cc.fooledit.model.*;
import cc.fooledit.setting.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
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
	private static final File SYSTEM_PATH=computePath();
	private static final File MODULE_PATH=new File(SYSTEM_PATH,"modules");
	private static final File USER_PATH=new File(System.getProperty("user.home"),".fooledit");
	private final CommandRegistry globalCommandRegistry=new CommandRegistry();
	private final BiMap<String,Command> commandRegistry=new BiMap<>(globalCommandRegistry,new HashMap<>());
	private MenuRegistry menuRegistry;
	private final KeymapRegistry keymapRegistry;
	private final Notifier notifier;
	private static final BorderPane root=new BorderPane();
	private MiniBuffer input;
	private HBox commander;
	private final ScriptAPI script;
	private static final Scene scene=new Scene(root);
	private Stage stage;
	private static Node currentNode;
	private List<KeyEvent> macro=new ArrayList<>();
	private boolean recording=false;
	private HistoryRing<Map<Object,Object>> worksheets=new HistoryRing<>();
	public Main(){
		INSTANCE=this;
		System.setProperty("user.dir",SYSTEM_PATH.toString());
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
		keymapRegistry=new KeymapRegistry();
		keymapRegistry.registerKeys((Map<String,String>)(Object)loadJSON((File)SettingManager.getOrCreate("core").get("keymap-file",null)));
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
		menuRegistry=new MenuRegistry();
		menuRegistry.setMenus(loadJSON((File)SettingManager.getOrCreate("core").get("menubar-file",null)));
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		input=new MiniBuffer(this);
		MenuBar bar=menuRegistry.getMenuBar();
		commander=new HBox(bar,new Label(),input);
		HBox.setHgrow(input,Priority.ALWAYS);
		root.setTop(commander);
	}
	private void registerStandardCommand(){
		globalCommandRegistry.put("new",()->FileCommands.create());
		globalCommandRegistry.put("open-file",()->FileCommands.open());
		globalCommandRegistry.put("open-url",()->FileCommands.openUrl());
		globalCommandRegistry.put("save",()->FileCommands.save());
		globalCommandRegistry.put("save-as",()->FileCommands.saveAs());
		globalCommandRegistry.put("full-screen",()->stage.setFullScreen(true));
		globalCommandRegistry.put("toggle-full-screen",()->stage.setFullScreen(!stage.isFullScreen()));
		globalCommandRegistry.put("exit-full-screen",()->stage.setFullScreen(false));
		globalCommandRegistry.put("maximize-frame",()->stage.setMaximized(true));
		globalCommandRegistry.put("iconify-frame",()->stage.setIconified(true));
		globalCommandRegistry.put("always-on-top-frame",()->stage.setAlwaysOnTop(true));
		globalCommandRegistry.put("split-vertically",()->getCurrentWorkSheet().splitVertically(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		globalCommandRegistry.put("split-horizontally",()->getCurrentWorkSheet().splitHorizontally(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		globalCommandRegistry.put("keep-only",()->((WorkSheet)root.getCenter()).keepOnly(getCurrentDataObject(),getCurrentDataEditor(),getCurrentRemark()));
		globalCommandRegistry.put("browser",()->addAndShow(new BrowserData(),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Browser")));
		globalCommandRegistry.put("terminal",()->addAndShow(new TerminalData(),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Terminal")));
		globalCommandRegistry.put("file-system",()->addAndShow(new FileSystemData(null),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"File System")));
		globalCommandRegistry.put("command",()->input.requestFocus());
		globalCommandRegistry.put("cancel",()->getCurrentNode().requestFocus());
		globalCommandRegistry.put("next-buffer",()->showDefault(DataObjectRegistry.getNextDataObject(getCurrentDataObject())));
		globalCommandRegistry.put("previous-buffer",()->showDefault(DataObjectRegistry.getPreviousDataObject(getCurrentDataObject())));
		globalCommandRegistry.put("start-record",()->{macro.clear();recording=true;});
		globalCommandRegistry.put("stop-record",()->{recording=false;macro.remove(0);macro.remove(macro.size()-1);});
		globalCommandRegistry.put("replay",()->{macro.forEach((e)->((Node)e.getTarget()).fireEvent(e));});
		globalCommandRegistry.put("restore",()->getMiniBuffer().restore());
		globalCommandRegistry.put("repeat",(o)->Command.repeat(o instanceof ScmNil?1:SchemeConverter.toInteger(ScmList.first(o))));
		globalCommandRegistry.put("map-mime-to-type",(o)->{
			DataObjectTypeRegistry.registerMime(SchemeConverter.toString(ScmList.first(o)),SchemeConverter.toString(ScmList.second(o)));
			return null;
		});
		globalCommandRegistry.put("ensure-loaded",(o)->{
			ModuleRegistry.ensureLoaded(SchemeConverter.toString(ScmList.first(o)));
			return null;
		});
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
				MenuItem item=new MenuItem(prop.get(DataObjectRegistry.BUFFER_NAME));
				item.setOnAction((e)->{
					try{
						Path file=new File(new URI(prop.get(DataObjectRegistry.URI))).toPath();
						if(prop.containsKey(DataObjectRegistry.MIME)){
							FileCommands.open(file,new MimeType(prop.get(DataObjectRegistry.MIME)));
						}else{
							FileCommands.open(file);
						}
					}catch(URISyntaxException|MimeTypeParseException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
					}
				});
				l.add(item);
			}
		};
	}
	private MenuItem createCommandMenuItem(String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name.toUpperCase()));
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
	public static void addAndShow(DataObject data,Map<String,String> prop){
		DataObjectRegistry.addDataObject(data,prop);
		showDefault(data);
	}
	private static void showDefault(DataObject data){
		getCurrentWorkSheet().keepOnly(data,getDefaultEditor(data),null);
	}
	public static DataEditor getDefaultEditor(DataObject data){
		return DataObjectTypeRegistry.getDataEditors(data.getClass()).get(0);
	}
	public static DataObject getCurrentDataObject(){
		return getCurrentWorkSheet().getDataObject();
	}
	public static DataEditor getCurrentDataEditor(){
		return getCurrentWorkSheet().getDataEditor();
	}
	public static Object getCurrentRemark(){
		return getCurrentDataEditor().getRemark(currentNode);
	}
	public Node getCurrentNode(){
		return currentNode;
	}
	public static WorkSheet getCurrentWorkSheet(){
		return (WorkSheet)currentNode.getParent();
	}
	public void setCurrentWorkSheet(WorkSheet workSheet){
		updateCurrentNode(workSheet.getCenter());
	}
	private void loadDefaultWorkSheet(){
		worksheets.registryComamnds("worksheet",()->((WorkSheet)root.getCenter()).toJSON(),(json)->{
			root.setCenter(WorkSheet.fromJSON(json));
		},globalCommandRegistry);
		PersistenceStatusManager.registerConvertor("layout.json",WorkSheet.CONVERTOR);
		root.setCenter((WorkSheet)PersistenceStatusManager.USER.getOrDefault("layout.json",()->{
			String msg=MessageRegistry.getString("WELCOME");
			TextObject welcome=new TextObject(msg);
			DataObjectRegistry.addDataObject(welcome,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,msg,DataObjectRegistry.TYPE,TextObjectType.class.getName()));
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
	public KeymapRegistry getGlobalKeymapRegistry(){
		return keymapRegistry;
	}
	private KeymapRegistry getLocalKeymapRegistry(){
		return getCurrentWorkSheet().getDataEditor().getKeymapRegistry();
	}
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public MiniBuffer getMiniBuffer(){
		return input;
	}
	public static Scene getScene(){
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
						getNotifier().notify(MessageRegistry.getString("ENTERED")+code);
						ignore=true;
					}else{
						curr=null;
						getNotifier().notify("");
						ignore=false;
					}
				}
			});
		}
	}
	private boolean isCurrentNodeFocused(){
		Node owner=scene.getFocusOwner();
		while(owner!=null&&owner!=currentNode)
			owner=owner.getParent();
		return owner!=null;
	}
}