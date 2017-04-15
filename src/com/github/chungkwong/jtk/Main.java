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

import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.command.*;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.example.text.*;
import com.github.chungkwong.jtk.example.tool.*;
import com.github.chungkwong.jtk.model.*;
import com.github.chungkwong.jtk.setting.*;
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
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends Application{
	private static final File PATH=new File(System.getProperty("user.home"),".jtk");
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final MenuRegistry menuRegistry;
	private final KeymapRegistry keymapRegistry;
	private final FileCommands fileCommands;
	private final Notifier notifier;
	private final BorderPane root;
	private final MiniBuffer input;
	private final ScriptAPI script;
	private final Scene scene;
	private Stage stage;
	private Node currentNode;
	public Main(){
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
		input=new MiniBuffer(this);
		menuRegistry=new MenuRegistry(loadJSON("menu.json"),this);
		menuRegistry.registerDynamicMenu("buffer",getBufferMenu());
		menuRegistry.registerDynamicMenu("file_history",getHistoryMenu());
		MenuBar bar=menuRegistry.getMenuBar();
		HBox commander=new HBox(bar,input);
		HBox.setHgrow(bar,Priority.NEVER);
		HBox.setHgrow(input,Priority.ALWAYS);
		PersistenceStatusManager.registerConvertor("layout.json",WorkSheet.CONVERTOR);
		root=new BorderPane(getDefaultWorkSheet());
		root.setTop(commander);
		root.setBottom(notifier.getStatusBar());
		scene=new Scene(root);
		//scene.setUserAgentStylesheet("com/github/chungkwong/jtk/dark.css");
		this.fileCommands=new FileCommands(this);
		registerStandardCommand();
		keymapRegistry=new KeymapRegistry(loadJSON("keymap.json"),root,this);
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		//notifier.addItem(Notifier.createTimeField(DateFormat.getDateTimeInstance()));
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
		commandRegistry.put("command",()->{input.requestFocus();System.out.println("hello");});
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
							fileCommands.open(file,prop.get(DataObjectRegistry.MIME));
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
		item.setOnAction((e)->commandRegistry.get(name).accept(this));
		return item;
	}
	private void updateCurrentNode(Node node){
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null)
			currentNode=((WorkSheet)node).getCenter();
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
	public static JSONObject loadJSON(String name){
		JSONObject obj;
		try{
			obj=(JSONObject)JSONParser.parse(new InputStreamReader(new FileInputStream(new File(getPath(),name)),StandardCharsets.UTF_8));
		}catch(IOException|SyntaxException ex){
			obj=new JSONObject(Collections.emptyMap());
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
}
