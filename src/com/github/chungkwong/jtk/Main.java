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
package com.github.chungkwong.jtk;

import com.github.chungkwong.jschememin.type.*;
import com.github.chungkwong.json.*;
import static com.github.chungkwong.jtk.api.KeymapRegistry.encode;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.command.*;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.example.text.*;
import com.github.chungkwong.jtk.example.tool.*;
import com.github.chungkwong.jtk.model.*;
import com.github.chungkwong.jtk.setting.*;
import com.github.chungkwong.jtk.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends Application{
	public static Main INSTANCE;
	private static final File PATH=new File(System.getProperty("user.home"),".jtk");
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private MenuRegistry menuRegistry;
	private final KeymapRegistry keymapRegistry;
	private final FileCommands fileCommands;
	private final Notifier notifier;
	private final BorderPane root;
	private MiniBuffer input;
	private HBox commander;
	private final ScriptAPI script;
	private final Scene scene;
	private Stage stage;
	private Node currentNode;
	public Main(){
		INSTANCE=this;
		notifier=new Notifier(this);
		Logger.getGlobal().setLevel(Level.INFO);
		try{
			Logger.getGlobal().addHandler(new StreamHandler(new FileOutputStream(new File(PATH,"LOG")),new Notifier.SystemLogFormatter()));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		Logger.getGlobal().addHandler(notifier);
		script=new ScriptAPI(this);
		runScript();
		root=new BorderPane(getDefaultWorkSheet());
		initMenuBar();
		root.setBottom(notifier.getStatusBar());
		scene=new Scene(root);
		//scene.setUserAgentStylesheet("com/github/chungkwong/jtk/dark.css");
		this.fileCommands=new FileCommands(this);
		registerStandardCommand();
		keymapRegistry=new KeymapRegistry();
		keymapRegistry.registerKeys((Map<String,String>)(Object)loadJSON("keymap.json"));
		new KeymapSupport();
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		//notifier.addItem(Notifier.createTimeField(DateFormat.getDateTimeInstance()));
	}
	private void initMenuBar(){
		menuRegistry=new MenuRegistry();
		menuRegistry.setMenus(loadJSON("menu.json"));
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		input=new MiniBuffer(this);
		MenuBar bar=menuRegistry.getMenuBar();
		commander=new HBox(bar,new Label(),input);
		HBox.setHgrow(input,Priority.ALWAYS);
		root.setTop(commander);
	}
	private void registerStandardCommand(){
		commandRegistry.put("new",()->fileCommands.create());
		commandRegistry.put("open_file",()->fileCommands.open());
		commandRegistry.put("save",()->fileCommands.save());
		commandRegistry.put("full_screen",()->stage.setFullScreen(true));
		commandRegistry.put("maximize_frame",()->stage.setMaximized(true));
		commandRegistry.put("iconify_frame",()->stage.setIconified(true));
		commandRegistry.put("always_on_top_frame",()->stage.setAlwaysOnTop(true));
		commandRegistry.put("split_vertically",()->getCurrentWorkSheet().splitVertically(getCurrentDataObject(),getCurrentWorkSheet().getDataEditor()));
		commandRegistry.put("split_horizontally",()->getCurrentWorkSheet().splitHorizontally(getCurrentDataObject(),getCurrentWorkSheet().getDataEditor()));
		commandRegistry.put("keep_only",()->((WorkSheet)root.getCenter()).keepOnly(getCurrentDataObject(),getCurrentWorkSheet().getDataEditor()));
		commandRegistry.put("browser",()->addAndShow(new BrowserData(),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Browser")));
		commandRegistry.put("command",()->input.requestFocus());
		commandRegistry.put("next_buffer",()->showDefault(DataObjectRegistry.getNextDataObject(getCurrentDataObject())));
		commandRegistry.put("previous_buffer",()->showDefault(DataObjectRegistry.getPreviousDataObject(getCurrentDataObject())));
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
				item.setOnAction((e)->getCurrentWorkSheet().keepOnly(curr,editor));
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
						File file=new File(new URI(prop.get(DataObjectRegistry.URI)));
						if(prop.containsKey(DataObjectRegistry.MIME)){
							fileCommands.open(file,MimeType.fromString(prop.get(DataObjectRegistry.MIME)));
						}else{
							fileCommands.open(file);
						}
					}catch(URISyntaxException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
					}
				});
				l.add(item);
			}
		};
	}
	private MenuItem createCommandMenuItem(String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name.toUpperCase()));
		item.setOnAction((e)->commandRegistry.get(name).accept(ScmNil.NIL));
		return item;
	}
	private void updateCurrentNode(Node node){
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null){
			currentNode=((WorkSheet)node).getCenter();
			commander.getChildren().set(1,((WorkSheet)node).getDataEditor().getMenuRegistry().getMenuBar());
		}
	}
	public void addAndShow(DataObject data,Map<String,String> prop){
		DataObjectRegistry.addDataObject(data,prop);
		showDefault(data);
	}
	private void showDefault(DataObject data){
		getCurrentWorkSheet().keepOnly(data,getDefaultEditor(data));
	}
	public DataEditor getDefaultEditor(DataObject data){
		return DataObjectTypeRegistry.getDataEditors(data.getClass()).get(0);
	}
	public DataObject getCurrentDataObject(){
		return getCurrentWorkSheet().getDataObject();
	}
	public Node getCurrentNode(){
		return currentNode;
	}
	public WorkSheet getCurrentWorkSheet(){
		return (WorkSheet)currentNode.getParent();
	}
	private WorkSheet getDefaultWorkSheet(){
		PersistenceStatusManager.registerConvertor("layout.json",WorkSheet.CONVERTOR);
		return (WorkSheet)PersistenceStatusManager.getOrDefault("layout.json",()->{
			String msg=MessageRegistry.getString("WELCOME");
			TextObject welcome=new TextObject(msg);
			DataObjectRegistry.addDataObject(welcome,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,msg,DataObjectRegistry.TYPE,TextObjectType.class.getName()));
			return new WorkSheet(welcome,getDefaultEditor(welcome));
		});
	}
	public Command getCommand(String key){
		return getLocalCommandRegistry().getOrDefault(key,getGlobalCommandRegistry().get(key));
	}
	public Stream<String> getCommandKeys(){
		return Arrays.asList(getLocalCommandRegistry(),getGlobalCommandRegistry()).stream().flatMap((m)->m.keySet().stream());
	}
	private CommandRegistry getGlobalCommandRegistry(){
		return commandRegistry;
	}
	private CommandRegistry getLocalCommandRegistry(){
		return getCurrentWorkSheet().getDataEditor().getCommandRegistry();
	}
	private KeymapRegistry getGlobalKeymapRegistry(){
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
	public Stage getStage(){
		return stage;
	}
	public Notifier getNotifier(){
		return notifier;
	}
	@Override
	public void start(Stage primaryStage){
		stage=primaryStage;
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/com/github/chungkwong/jtk/cross.png")));
		primaryStage.setTitle("IDEM");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
	public static File getPath(){
		return PATH;
	}
	public static Map<Object,Object> loadJSON(String name){
		Map<Object,Object> obj;
		try{
			obj=(Map<Object,Object>)JSONDecoder.decode(new InputStreamReader(new FileInputStream(new File(getPath(),name)),StandardCharsets.UTF_8));
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
		checkInstall();
		launch(args);
	}
	private static void checkInstall(){
		//if(!PATH.exists()){
			restoreToDefault();
		//}//FIXME remove command
	}
	private static void restoreToDefault(){
		PATH.mkdir();
		installFile("init.scm");
		installFile("keymap.json");
		installFile("menu.json");
		installFile("module.json");
		installFile("suffix.json");
		installFile("highlight.json");
		installFile("locale/base.properties");
		installFile("locale/base_zh_CN.properties");
	}
	private static void installFile(String filename){
		try{
			String from="/com/github/chungkwong/jtk/default/"+filename;
			File to=new File(PATH,filename);
			to.getParentFile().mkdirs();
			Files.copy(Main.class.getResourceAsStream(from),to.toPath(),StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private void runScript(){
		try{
			script.eval(new InputStreamReader(new FileInputStream(new File(getPath(),"init.scm")),StandardCharsets.UTF_8));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,"Error in init script",ex);
		}
	}
	public ScriptAPI getScriptAPI(){
		return script;
	}
	public CommandRegistry getCommandRegistry(){
		return commandRegistry;
	}
	class KeymapSupport{
		private String curr=null;
		boolean ignore=false;
		public KeymapSupport(){
			root.addEventFilter(KeyEvent.ANY,(KeyEvent e)->{
				if(e.getEventType().equals(KeyEvent.KEY_TYPED)){
					if(ignore){
						ignore=false;
						e.consume();
					}
				}else if(e.getEventType().equals(KeyEvent.KEY_PRESSED)){
					if(e.getCode().isModifierKey()){
						e.consume();
						return;
					}
					String code=curr==null?encode(e):curr+' '+encode(e);
					Map.Entry<String,String> localEntry=getLocalKeymapRegistry().ceilingEntry(code);
					Map.Entry<String,String> globalEntry=getGlobalKeymapRegistry().ceilingEntry(code);
					String next;
					String commandName;
					if(localEntry!=null){
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
						Command command=getCommand(commandName);
						getNotifier().notifyStarted(command.getDisplayName());
						command.accept(ScmNil.NIL);
						getNotifier().notifyFinished(command.getDisplayName());
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
}