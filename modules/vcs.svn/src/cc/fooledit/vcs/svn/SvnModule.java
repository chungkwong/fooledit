/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.vcs.svn;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.net.*;
import java.util.*;
import javafx.scene.control.*;
import org.tmatesoft.svn.core.wc.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SvnModule{
	public static final String NAME="vcs.svn";
	public static final String APPLICATION_NAME="svn";
	public static final String SETTINGS_REGISTRY_NAME="settings";
	public static final RegistryNode<String,Object> SETTINGS_REGISTRY=
			(RegistryNode<String,Object>)Registry.ROOT.getOrCreateChild(SvnModule.NAME).getOrCreateChild(SETTINGS_REGISTRY_NAME);
	public static void onLoad() throws ClassNotFoundException, MalformedURLException{

		Argument allowMixedRevisions=createArgument("ALLOW_MIXED_REVISIONS");
		Argument allowUnversionedObstructions=createArgument("ALLOW_UNVERSIONED_OBSTRUCTIONS");
		Argument applyAutoProperties=createArgument("APPLY_AUTO_PROPERTIES");
		Argument breakLock=createArgument("BREAK_LOCK");
		Argument changeListFilter=createArgument("CHANGE_LIST_FILTER");
		Argument collectParentExternal=createArgument("COLLECT_PARENT_EXTERNAL");
		Argument conflictChoice=createArgument("CONFLICT_CHOICE");
		Argument deleteFile=createArgument("DELETE_FILE");
		Argument deleteWCProperties=createArgument("DELETE_WC_PROPERTIES");
		Argument depth=createArgument("DEPTH");
		Argument depthIsSticky=createArgument("DEPTH_IS_STICKY");
		Argument discoverChangedPaths=createArgument("DISCOVER_CHANGED_PATHS");
		Argument dryrun=createArgument("DRY_RUN");
		Argument encoding=createArgument("ENCODING");
		Argument endRevision=createArgument("END_REVISION");
		Argument eol=createArgument("EOL");
		Argument failWhenDstExists=createArgument("FAIL_WHEN_DST_EXISTS");
		Argument fetchLock=createArgument("FETCH_LOCK");
		Argument force=createArgument("FORCE");
		Argument ignoreAncestry=createArgument("IGNORE_ANCESTRY");
		Argument ignoreMimeType=createArgument("IGNORE_MIME_TYPE");
		Argument ignoreUnknownNodeTypes=createArgument("IGNORE_UNKNOWN_NODE_TYPES");
		Argument ignoreWhitespace=createArgument("IGNORE_WHITESACES");
		Argument includeExternals=createArgument("INCLUDE_EXTERNALS");
		Argument includeIgnored=createArgument("INCLUDE_IGNORED");
		Argument includeMergedRevisions=createArgument("INCLUDE_MERGED_REVISIONS");
		Argument isMove=createArgument("IS_MOVE");
		Argument keepChangeLists=createArgument("KEEP_CHANGE_LISTS");
		Argument keepLock=createArgument("KEEP_LOCK");
		Argument limit=createArgument("LIMIT");
		Argument makeParent=createArgument("MAKE_PARENT");
		Argument metadataOnly=createArgument("METADATA_ONLY");
		Argument mkdir=createArgument("MKDIR");
		Argument overwrite=createArgument("OVERWRITE");
		Argument pegRevision=createArgument("PEG_REVISION");
		Argument properties=createArgument("PROPERTIES");
		Argument recordOnly=createArgument("RECORD_ONLY");
		Argument recursive=createArgument("RECURSIVE");
		Argument remote=createArgument("REMOTE");
		Argument removeIgnoredItems=createArgument("REMOVE_IGNORED_ITEMS");
		Argument removeTempFiles=createArgument("REMOVE_TEMP_FILE");
		Argument removeUnversionedItems=createArgument("REMOVE_UNVERSIONED_ITEMS");
		Argument reportAll=createArgument("REPORT_ALL");
		Argument resolveContent=createArgument("RESOLVE_CONTENT");
		Argument resolveProp=createArgument("RESOLVE_PROPERTIES");
		Argument resolveTree=createArgument("RESOLVE_TREE");
		Argument reverse=createArgument("REVERSE");
		Argument revision=createArgument("REVISION");
		Argument revProp=createArgument("REVISION_PROPERTIES");
		Argument skipChecks=createArgument("SKIP_CHECKS");
		Argument startRevision=createArgument("START_REVISION");
		Argument stealLock=createArgument("STEAL_LOCK");
		Argument stopOnCopy=createArgument("STOP_ON_COPY");
		Argument stripCount=createArgument("STRIP_COUNT");
		Argument useAncestry=createArgument("USE_ANCESTRY");
		Argument useGlobalIgnores=createArgument("USE_GLOBAL_IGNORE");
		Argument vacuumPristines=createArgument("VACUUM_PRISTINES");

		Argument files=new Argument("FILES",()->{
			return ((FileSystemViewer)Main.INSTANCE.getCurrentNode()).getSelectedPaths();
		});
		Argument file=new Argument("FILE",()->{
			return ((FileSystemViewer)Main.INSTANCE.getCurrentNode()).getSelectedPaths().iterator().next();
		});
		Argument urls=new Argument("URLS",()->{
			return ((FileSystemViewer)Main.INSTANCE.getCurrentNode()).getSelectedPaths();
		});
		Argument url=new Argument("URL",()->{
			return ((FileSystemViewer)Main.INSTANCE.getCurrentNode()).getSelectedPaths().iterator().next();
		});
		Argument name=new Argument("NAME");
		Argument msg=new Argument("MESSAGE");
		Argument value=new Argument("VALUE");

		addCommand("svn-add",Arrays.asList(files,force,mkdir,depth,depthIsSticky,includeIgnored,makeParent),(args)->{
			SvnCommands.add(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-blame",Arrays.asList(url,pegRevision,startRevision,endRevision,ignoreMimeType,includeMergedRevisions,encoding),(args)->{
			SvnCommands.blame(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-cat",Arrays.asList(file,revision),(args)->{
			SvnCommands.cat(args[0],args[1]);
			return null;
		});
		addCommand("svn-changelist-add",Arrays.asList(files,name,depth,changeListFilter),(args)->{
			SvnCommands.changelistAdd(args[0],args[1],args[2],args[3]);
			return null;
		});
		addCommand("svn-changelist-remove",Arrays.asList(files,changeListFilter,depth),(args)->{
			SvnCommands.changelistRemove(args[0],args[1],args[2]);
			return null;
		});
		addCommand("svn-changelist-get",Arrays.asList(file,changeListFilter,depth),(args)->{
			SvnCommands.changelistGet(args[0],args[1],args[2]);
			return null;
		});
		addCommand("svn-checkout",Arrays.asList(url,file,pegRevision,revision,depth,recursive,force),(args)->{
			SvnCommands.checkout(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-cleanup",Arrays.asList(file,deleteWCProperties,breakLock,vacuumPristines,removeUnversionedItems,removeIgnoredItems,includeExternals),(args)->{
			SvnCommands.cleanup(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-commit",Arrays.asList(files,keepLock,msg,revProp,changeListFilter,keepChangeLists,force,depth),(args)->{
			return SvnCommands.commit(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
		});
		addCommand("svn-copy",Arrays.asList(urls,pegRevision,revision,file,isMove,makeParent,failWhenDstExists,allowMixedRevisions,metadataOnly),(args)->{
			SvnCommands.copy(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
			return null;
		});
		addCommand("svn-delete",Arrays.asList(file,force,deleteFile,dryrun),(args)->{
			SvnCommands.delete(args[0],args[1],args[2],args[3]);
			return null;
		});
		addCommand("svn-diff",Arrays.asList(file,pegRevision,revision,revision,depth,useAncestry,changeListFilter),(args)->{
			SvnCommands.diff(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-export",Arrays.asList(url,file,pegRevision,revision,eol,overwrite,depth),(args)->{
			SvnCommands.export(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-import",Arrays.asList(file,url,msg,revProp,useGlobalIgnores,ignoreUnknownNodeTypes,depth,applyAutoProperties),(args)->{
			SvnCommands.im(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
			return null;
		});
		addCommand("svn-info",Arrays.asList(url),(args)->{
			return ScmInteger.valueOf(SvnCommands.info(args[0]));
		});
		addCommand("svn-list",Arrays.asList(url,pegRevision,revision,fetchLock,depth),(args)->{
			SvnCommands.list(args[0],args[1],args[2],args[3],args[4]);
			return null;
		});
		addCommand("svn-lock",Arrays.asList(url,stealLock,msg),(args)->{
			SvnCommands.lock(args[0],args[1],args[2]);
			return null;
		});
		addCommand("svn-log",Arrays.asList(url,file,pegRevision,startRevision,endRevision,stopOnCopy,discoverChangedPaths,includeMergedRevisions,limit,revProp),(args)->{
			SvnCommands.log(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]);
			return null;
		});
		addCommand("svn-merge",Arrays.asList(url,revision,url,revision,file,depth,useAncestry,force,dryrun,recordOnly),(args)->{
			SvnCommands.merge(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7],args[8],args[9]);
			return null;
		});
		addCommand("svn-mergeinfo",Arrays.asList(url,pegRevision),(args)->{
			return SvnCommands.mergeinfo(args[0],args[1]);
		});
		addCommand("svn-mkdir",Arrays.asList(url,msg,properties,makeParent),(args)->{
			SvnCommands.mkdir(args[0],args[1],args[2],args[3]);
			return null;
		});
		addCommand("svn-move",Arrays.asList(file,file),(args)->{
			SvnCommands.move(args[0],args[1]);
			return null;
		});
		addCommand("svn-patch",Arrays.asList(file,file,dryrun,stripCount,ignoreWhitespace,removeTempFiles,reverse),(args)->{
			SvnCommands.patch(args[0],args[1],args[2],args[3],args[4],args[5],args[6]);
			return null;
		});
		addCommand("svn-propdel",Arrays.asList(file,name,skipChecks,depth,changeListFilter),(args)->{
			SvnCommands.propdel(args[0],args[1],args[2],args[3],args[4]);
			return null;
		});
		addCommand("svn-propget",Arrays.asList(file,name,pegRevision,revision,depth,changeListFilter),(args)->{
			SvnCommands.propget(args[0],args[1],args[2],args[3],args[4],args[5]);
			return null;
		});
		addCommand("svn-proplist",Arrays.asList(file,pegRevision,revision,depth,changeListFilter),(args)->{
			SvnCommands.proplist(args[0],args[1],args[2],args[3],args[4]);
			return null;
		});
		addCommand("svn-propset",Arrays.asList(file,name,value,skipChecks,depth,changeListFilter),(args)->{
			SvnCommands.propset(args[0],args[1],args[2],args[3],args[4],args[5]);
			return null;
		});
		addCommand("svn-relocate",Arrays.asList(file,url,url,recursive),(args)->{
			SvnCommands.relocate(args[0],args[1],args[2],args[3]);
			return null;
		});
		addCommand("svn-resolve",Arrays.asList(file,depth,resolveContent,resolveProp,resolveTree,conflictChoice),(args)->{
			SvnCommands.resolve(args[0],args[1],args[2],args[3],args[4],args[5]);
			return null;
		});
		addCommand("svn-revert",Arrays.asList(files,depth,changeListFilter),(args)->{
			SvnCommands.revert(args[0],args[1],args[2]);
			return null;
		});
		addCommand("svn-status",Arrays.asList(file,revision,depth,remote,reportAll,includeIgnored,collectParentExternal,changeListFilter),(args)->{
			SvnCommands.status(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
			return null;
		});
		addCommand("svn-switch",Arrays.asList(file,url,pegRevision,revision,depth,allowUnversionedObstructions,depthIsSticky,ignoreAncestry),(args)->{
			SvnCommands.sw(args[0],args[1],args[2],args[3],args[4],args[5],args[6],args[7]);
			return null;
		});
		addCommand("svn-unlock",Arrays.asList(url,breakLock),(args)->{
			SvnCommands.unlock(args[0],args[1]);
			return null;
		});
		addCommand("svn-update",Arrays.asList(file,revision,depth,allowUnversionedObstructions,depthIsSticky,makeParent),(args)->{
			SvnCommands.update(args[0],args[1],args[2],args[3],args[4],args[5]);
			return null;
		});
		addCommand("svn-upgrade",Arrays.asList(file),(args)->{
			SvnCommands.upgrade(args[0]);
			return null;
		});

		CoreModule.PROTOCOL_REGISTRY.put("svn",new SvnStreamHandler());
		ContentTypeHelper.getURL_GUESSER().registerPathPattern("^.*[/\\\\]\\.svn$","directory/svn");
	}
	private static MenuItem createMenuItem(String command,String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name,NAME));
		item.setOnAction((e)->{Main.INSTANCE.getCommandRegistry().get(command).accept(ScmNil.NIL);});
		return item;
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		providesFileCommands();
		Registry.providesProtocol("svn",NAME);
		CoreModule.PERSISTENT_REGISTRY.put("vcs.svn/"+SETTINGS_REGISTRY_NAME);
		providesDefaultValues();
	}
	private static void providesFileCommands(){
		providesFileCommand("svn-auth");
		providesFileCommand("svn-add");
		providesFileCommand("svn-blame");
		providesFileCommand("svn-cat");
		providesFileCommand("svn-changelist");
		providesFileCommand("svn-checkout");
		providesFileCommand("svn-cleanup");
		providesFileCommand("svn-commit");
		providesFileCommand("svn-copy");
		providesFileCommand("svn-delete");
		providesFileCommand("svn-diff");
		providesFileCommand("svn-export");
		providesFileCommand("svn-import");
		providesFileCommand("svn-info");
		providesFileCommand("svn-list");
		providesFileCommand("svn-lock");
		providesFileCommand("svn-log");
		providesFileCommand("svn-merge");
		providesFileCommand("svn-mergeinfo");
		providesFileCommand("svn-mkdir");
		providesFileCommand("svn-move");
		providesFileCommand("svn-patch");
		providesFileCommand("svn-propdel");
		providesFileCommand("svn-propget");
		providesFileCommand("svn-proplist");
		providesFileCommand("svn-propset");
		providesFileCommand("svn-relocate");
		providesFileCommand("svn-resolve");
		providesFileCommand("svn-revert");
		providesFileCommand("svn-status");
		providesFileCommand("svn-switch");
		providesFileCommand("svn-unlock");
		providesFileCommand("svn-update");
		providesFileCommand("svn-upgrade");
	}
	private static void providesFileCommand(String name){
		Registry.provides(name,NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
	}
	private static void providesDefaultValues(){
		SETTINGS_REGISTRY.addChildIfNotPresent("ALLOW_MIXED_REVISIONS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("ALLOW_UNVERSIONED_OBSTRUCTIONS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("APPLY_AUTO_PROPERTIES",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("BREAK_LOCK",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("COLLECT_PARENT_EXTERNAL",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("CHANGE_LIST_FILTER",null);
		SETTINGS_REGISTRY.addChildIfNotPresent("CONFLICT_CHOICE","MERGED");
		SETTINGS_REGISTRY.addChildIfNotPresent("DELETE_FILE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("DELETE_WC_PROPERTIES",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("DEPTH_IS_STICKY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("DEPTH","INFINITY");
		SETTINGS_REGISTRY.addChildIfNotPresent("DISCOVER_CHANGED_PATHS",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("DRY_RUN",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("ENCODING","utf-8");
		SETTINGS_REGISTRY.addChildIfNotPresent("END_REVISION",SVNRevision.HEAD.getName());
		SETTINGS_REGISTRY.addChildIfNotPresent("EOL",System.getProperty("line.separator"));
		SETTINGS_REGISTRY.addChildIfNotPresent("FAIL_WHEN_DST_EXISTS",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("FETCH_LOCK",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("FORCE",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("IGNORE_ANCESTRY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("IGNORE_MIME_TYPE",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("IGNORE_UNKNOWN_NODE_TYPES",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("IGNORE_WHITESACES",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("INCLUDE_EXTERNALS",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("INCLUDE_IGNORED",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("IS_MOVE",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("KEEP_CHANGE_LISTS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("KEEP_LOCK",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("LIMIT",0);
		SETTINGS_REGISTRY.addChildIfNotPresent("MAKE_PARENT",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("METADATA_ONLY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("MKDIR",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("OVERWRITE",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("PEG_REVISION",SVNRevision.WORKING.getName());
		SETTINGS_REGISTRY.addChildIfNotPresent("PROPERTIES",null);
		SETTINGS_REGISTRY.addChildIfNotPresent("RECORD_ONLY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("RECURSIVE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("REMOTE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("REMOVE_IGNORED_ITEMS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("REMOVE_TEMP_FILE",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("REMOVE_UNVERSIONED_ITEMS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("REPORT_ALL",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("RESOLVE_CONTENT",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("RESOLVE_PROPERTIES",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("RESOLVE_TREE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("REVERSE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("REVISION",SVNRevision.WORKING.getName());
		SETTINGS_REGISTRY.addChildIfNotPresent("REVISION_PROPERTIES",null);
		SETTINGS_REGISTRY.addChildIfNotPresent("SKIP_CHECKS",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("START_REVISION",0);
		SETTINGS_REGISTRY.addChildIfNotPresent("STOP_ON_COPY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("STEAL_LOCK",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("STRIP_COUNT",0);
		SETTINGS_REGISTRY.addChildIfNotPresent("USE_ANCESTRY",false);
		SETTINGS_REGISTRY.addChildIfNotPresent("USE_GLOBAL_IGNORE",true);
		SETTINGS_REGISTRY.addChildIfNotPresent("VACUUM_PRISTINES",true);

	}
	private static Argument createArgument(String name){
		return new Argument(MessageRegistry.getString(name,NAME),()->SETTINGS_REGISTRY.get(name));
	}
	private static void addCommand(String name,List<Argument> args,ThrowableFunction<Object[],Object> proc){
		FileSystemEditor.INSTANCE.getCommandRegistry().put(name,new Command(name,args,(a)->SchemeConverter.toScheme(proc.accept(toArgumentList(a))),FileSystemModule.NAME));
	}
	private static Object[] toArgumentList(ScmPairOrNil args){
		return ScmList.asStream(args).map(SchemeConverter::toJava).toArray();
	}
}
