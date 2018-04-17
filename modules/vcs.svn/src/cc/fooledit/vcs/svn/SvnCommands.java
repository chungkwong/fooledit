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
import cc.fooledit.editor.text.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.*;
import org.tmatesoft.svn.core.wc.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SvnCommands{
	static final SVNClientManager SVN=SVNClientManager.newInstance();

	static{
		ISVNAuthenticationManager authManager=SVNWCUtil.createDefaultAuthenticationManager();
		authManager.setAuthenticationProvider(new ISVNAuthenticationProvider() {
			@Override
			public SVNAuthentication requestClientAuthentication(String kind,SVNURL svnurl,String realm,SVNErrorMessage msg,SVNAuthentication last,boolean cache){
				String old=last!=null?last.getUserName():"";
				switch(kind){
					case ISVNAuthenticationManager.PASSWORD:
						return SVNPasswordAuthentication.newInstance(input(MessageRegistry.getString("USERNAME",SvnModule.NAME)+realm,old),
								input(MessageRegistry.getString("PASSWORD",SvnModule.NAME)+realm,old).toCharArray(),true,svnurl,false);
					case ISVNAuthenticationManager.USERNAME:
						return SVNUserNameAuthentication.newInstance(input(kind+":"+realm,old),true,svnurl,false);
					case ISVNAuthenticationManager.SSH:
						return SVNSSHAuthentication.newInstance(input(MessageRegistry.getString("USERNAME",SvnModule.NAME)+realm,old),
								input(MessageRegistry.getString("PASSWORD",SvnModule.NAME)+realm,old).toCharArray(),22,true,svnurl,false);
					case ISVNAuthenticationManager.SSL:
						return SVNSSLAuthentication.newInstance(kind,input(MessageRegistry.getString("ALIAS",SvnModule.NAME),old),true,svnurl,false);
					default:
						return null;
				}
			}
			@Override
			public int acceptServerAuthentication(SVNURL svnurl,String string,Object o,boolean bln){
				return ACCEPTED;
			}
		});
		SVN.setAuthenticationManager(authManager);
	}
	private static String input(String prompt,String def){
		String[] result=new String[1];
		Thread lock=new Thread(()->{
			synchronized(result){
				try{
					result.wait();
				}catch(InterruptedException ex){

				}
			}
		});
		lock.setDaemon(true);
		lock.start();
		Main.INSTANCE.getMiniBuffer().setMode((text)->{
			synchronized(result){
				result[0]=text;
				result.notifyAll();
			}
		},null,def,null,null);
		try{
			lock.join();
		}catch(InterruptedException ex){
			lock.interrupt();
		}
		return result[0];
	}
	public static void add(Object file,Object force,Object mkdir,Object depth,
			Object depthIsSticky,Object includeIgnored,Object makeParent) throws SVNException{
		SVN.getWCClient().doAdd(toFiles(file),toBoolean(force),toBoolean(mkdir),true,toDepth(file),
				toBoolean(depthIsSticky),toBoolean(includeIgnored),toBoolean(makeParent));
	}
	public static void blame(Object url,Object pegRevision,Object startRevision,Object endRevision,
			Object ignoreMimeType,Object includeMergedRevisions,Object inputEncodin) throws SVNException{
		TextObject text=createAndShowDataObject(Objects.toString(url));
		ISVNAnnotateHandler handler=new ISVNAnnotateHandler(){
			@Override
			public void handleLine(Date date,long revision,String author,String line) throws SVNException{
				text.getText().set(text.getText().getValue()+"["+date+"r"+revision+"by"+author+"]"+line+"\n");
			}
			@Override
			public void handleLine(Date date,long revision,String author,String line,Date mergedDate,long mergedRevision,String mergedAuthor,String mergedPath,int lineNumber) throws SVNException{
				text.getText().set(text.getText().getValue()+"["+date+"r"+revision+"by"+author+"]"+line+"\n");
			}
			@Override
			public boolean handleRevision(Date date,long revision,String author,File contents) throws SVNException{
				return true;
			}
			@Override
			public void handleEOF() throws SVNException{

			}
		};
		SVN.getLogClient().doAnnotate(toURL(url),toRevision(pegRevision),toRevision(startRevision),
				toRevision(endRevision),toBoolean(ignoreMimeType),toBoolean(includeMergedRevisions),handler,toString(inputEncodin));
	}
	public static void cat(Object path,Object rev) throws Exception{
		Pair<File,String> file=splitWorkingCopy(toFile(path));
		File root=file.getKey();
		String loc=file.getValue();
		SVNRevision r=toRevision(rev);
		Main.INSTANCE.show(DataObjectRegistry.readFrom(new SvnConnection(root,loc,r).getURL()));
	}
	private static Pair<File,String> splitWorkingCopy(File file) throws SVNException{
		File root=SVNWCUtil.getWorkingCopyRoot(file,false);
		String loc=root.toPath().relativize(file.toPath()).toString();
		return new Pair<>(root,loc);
	}
	public static void changelistAdd(Object file,Object name,Object depth,Object filter) throws SVNException{
		SVN.getChangelistClient().doAddToChangelist(toFiles(file),toDepth(file),toString(name),toStringArray(filter));
	}
	public static void changelistGet(Object file,Object name,Object depth) throws SVNException{
		TextObject text=createAndShowDataObject(Objects.toString(file));
		ISVNChangelistHandler handler=new ISVNChangelistHandler() {
			@Override
			public void handle(File path,String changelistName){
				text.getText().set(text.getText().getValue()+path.toString()+":"+changelistName+"\n");
			}
		};
		SVN.getChangelistClient().doGetChangeLists(toFile(file),toStringSet(name),toDepth(depth),handler);
	}
	public static void changelistRemove(Object file,Object changeLists,Object depth) throws SVNException{
		SVN.getChangelistClient().doRemoveFromChangelist(toFiles(file),toDepth(depth),toStringArray(changeLists));
	}
	public static void checkout(Object url,Object dest,Object pegRev,Object rev,Object depth,Object recursive,Object force) throws SVNException{
		SVN.getUpdateClient().doCheckout(toURL(url),toFile(dest),toRevision(pegRev),toRevision(rev),toDepth(depth),true);
	}
	public static void cleanup(Object path,Object deleteWCProperties,Object breakLocks,
			Object vacuumPristines,Object removeUnversionedItems,Object removeIgnoredItems,
			Object includeExternals) throws SVNException{
		SVN.getWCClient().doCleanup(toFile(path),toBoolean(deleteWCProperties),toBoolean(breakLocks),toBoolean(vacuumPristines),
				toBoolean(removeUnversionedItems),toBoolean(removeIgnoredItems),toBoolean(includeExternals));
	}
	public static SVNCommitInfo commit(Object file,Object keepLocks,Object commitMessage,
			Object revProp,Object changelists,Object keepChangelist,
			Object force,Object depth) throws SVNException{
		return SVN.getCommitClient().doCommit(toFiles(file),toBoolean(keepLocks),toString(commitMessage),toProperties(revProp),
				toStringArray(changelists),toBoolean(keepChangelist),toBoolean(force),toDepth(depth));
	}
	public static void copy(Object from,Object pegRev,Object rev,Object to,Object isMove,Object makeParents,
			Object failWhenDstExists,Object allowMixedRevisions,Object metadataOnly)throws SVNException{
		SVNCopySource[] src=toCopySources(pegRev,rev,from);
		File dest=toFile(to);
		SVN.getCopyClient().doCopy(src,dest,toBoolean(isMove),toBoolean(makeParents),toBoolean(failWhenDstExists),
				toBoolean(allowMixedRevisions),toBoolean(metadataOnly));
	}
	public static void delete(Object file,Object force,Object deleteFile,Object dryrun) throws SVNException{
		SVN.getWCClient().doDelete(toFile(file),toBoolean(force),toBoolean(deleteFile),toBoolean(dryrun));
	}
	public static void diff(Object file,Object pegRev,Object rN,Object rM,Object depth,
			Object useAncestry,Object changeLists) throws SVNException{
		SVN.getDiffClient().doDiff(toFile(file),toRevision(pegRev),toRevision(rN),toRevision(rM),
				toDepth(depth),toBoolean(useAncestry),null,toStringSet(changeLists));
	}
	public static void export(Object src, Object dst,Object pegRev,Object rev,
			Object eolStyle,Object overwrite,Object depth) throws SVNException{
		SVN.getUpdateClient().doExport(toURL(src),toFile(dst),toRevision(pegRev),toRevision(rev),
				toString(eolStyle),toBoolean(overwrite),toDepth(depth));
	}
	public static void im(Object path,Object dstURL,Object commitMessage,Object revProp,
			Object useGlobalIgnores,Object ignoreUnknownNodeTypes,Object depth, Object applyAutoProperties) throws SVNException{
		SVN.getCommitClient().doImport(toFile(path),toURL(dstURL),toString(commitMessage),toProperties(revProp),
				toBoolean(useGlobalIgnores),toBoolean(ignoreUnknownNodeTypes),toDepth(depth),toBoolean(applyAutoProperties));
	}
	public static long info(Object url) throws SVNException{
		return SVN.getAdminClient().doInfo(toURL(url)).getLastMergedRevision();
	}
	public static void list(Object url,Object pegRev,Object rev,Object fetchLocks,Object depth) throws SVNException{
		TextObject text=createAndShowDataObject(Objects.toString(url));
		ISVNDirEntryHandler handler=new ISVNDirEntryHandler(){
			@Override
			public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException{
				text.getText().set(text.getText().getValue()+dirEntry.toString()+"\n");
			}
		};
		SVN.getLogClient().doList(toURL(url),toRevision(pegRev),toRevision(rev),toBoolean(fetchLocks),
				toDepth(rev),SVNDirEntry.DIRENT_ALL,handler);
	}
	public static void lock(Object url,Object stealLock,Object msg) throws SVNException{
		SVN.getWCClient().doLock(toURLs(url),toBoolean(stealLock),toString(msg));
	}
	public static void log(Object url,Object path, Object pegRev,Object startRev,Object endRev,
			Object stopOnCopy,Object discoverChangedPaths,Object includeMergedRevisions,Object limit,Object revisionProperties) throws SVNException{
		TextObject text=createAndShowDataObject(Objects.toString(url)+':'+Objects.toString(path));
		ISVNLogEntryHandler handler=new ISVNLogEntryHandler() {
			@Override
			public void handleLogEntry(SVNLogEntry logEntry) throws SVNException{
				text.getText().set(text.getText().getValue()+logEntry.toString()+"\n\n");
			}
		};
		SVN.getLogClient().doLog(toURL(url),new String[]{toString(path)},toRevision(pegRev),toRevision(startRev),
				toRevision(endRev),toBoolean(stopOnCopy),toBoolean(discoverChangedPaths),toBoolean(includeMergedRevisions),
				toLong(limit),toStringArray(revisionProperties),handler);
	}
	public static void merge(Object url1,Object rev1,Object url2,Object rev2,Object dstPath,
			Object depth,Object useAncestry,Object force,Object dryRun,Object recordOnly) throws SVNException{
		SVN.getDiffClient().doMerge(toURL(url1),toRevision(rev1),toURL(url2),toRevision(rev2),toFile(dstPath),
				toDepth(depth),toBoolean(useAncestry),toBoolean(force),toBoolean(dryRun),toBoolean(recordOnly));
	}
	public static Map<SVNURL,SVNMergeRangeList> mergeinfo(Object url,Object pegRev)throws SVNException{
		return SVN.getDiffClient().doGetMergedMergeInfo(toURL(url),toRevision(pegRev));
	}
	public static void mkdir(Object url,Object msg,Object prop,Object makeParent) throws SVNException{
		SVN.getCommitClient().doMkDir(toURLs(url),toString(msg),toProperties(prop),toBoolean(makeParent));
	}
	public static void move(Object from,Object to) throws SVNException{
		SVN.getMoveClient().doMove(toFile(from),toFile(to));
	}
	public static void patch(Object absPatchPath,Object localAbsPath,Object dryRun,Object stripCount,
			Object ignoreWhitespace,Object removeTempFiles,Object reverse) throws SVNException{
		SVN.getDiffClient().doPatch(toFile(absPatchPath),toFile(localAbsPath),toBoolean(dryRun),toInt(stripCount),
				toBoolean(ignoreWhitespace),toBoolean(removeTempFiles),toBoolean(reverse));
	}
	public static void propdel(Object path, Object propName,Object skipChecks,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doSetProperty(toFile(path),toString(propName),null,toBoolean(skipChecks),toDepth(depth),
				ISVNPropertyHandler.NULL,toStringSet(changes));
	}
	public static void propget(Object path,Object propName,Object pegRev,Object rev,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doGetProperty(toFile(path),toString(propName),toRevision(pegRev),toRevision(rev),
				toDepth(depth),ISVNPropertyHandler.NULL,toStringSet(changes));
	}
	public static void proplist(Object path,Object pegRev,Object rev,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doGetProperty(toFile(path),null,toRevision(pegRev),toRevision(rev),
				toDepth(depth),ISVNPropertyHandler.NULL,toStringSet(changes));
	}
	public static void propset(Object path, Object propName, Object propValue,Object skipChecks,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doSetProperty(toFile(path),toString(propName),toValue(propValue),toBoolean(skipChecks),toDepth(depth),
				ISVNPropertyHandler.NULL,toStringSet(changes));
	}
	public static void relocate(Object dst,Object oldURL,Object newURL,Object recursive) throws SVNException{
		SVN.getUpdateClient().doRelocate(toFile(dst),toURL(oldURL),toURL(newURL),toBoolean(recursive));
	}
	public static void resolve(Object path,Object depth,Object resolveContents,
			Object resolveProperties,Object resolveTree,Object conflictChoice)throws SVNException{
		SVN.getWCClient().doResolve(toFile(path),toDepth(depth),toBoolean(resolveContents),
				toBoolean(resolveProperties),toBoolean(resolveTree),toConflictChoice(conflictChoice));
	}
	public static void revert(Object path,Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doRevert(toFiles(path),toDepth(depth),toStringSet(changes));
	}
	public static void status(Object path,Object rev,Object depth,Object remote,Object reportAll,
			Object includeIgnored, Object collectParentExternal,Object changes) throws SVNException{
		ISVNStatusHandler handler=new ISVNStatusHandler() {
			@Override
			public void handleStatus(SVNStatus status) throws SVNException{
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		};
		SVN.getStatusClient().doStatus(toFile(path),toRevision(rev),toDepth(depth),toBoolean(remote),toBoolean(reportAll),
				toBoolean(includeIgnored),toBoolean(collectParentExternal),handler,toStringSet(changes));
	}
	public static void sw(Object path,Object url,Object pegRev,Object rev,Object depth,
			Object allowUnversionedObstructions,Object depthIsSticky,Object ignoreAncestry) throws SVNException{
		SVN.getUpdateClient().doSwitch(toFile(path),toURL(url),toRevision(pegRev),toRevision(rev),
				toDepth(depth),toBoolean(allowUnversionedObstructions),toBoolean(depthIsSticky),toBoolean(ignoreAncestry));
	}
	public static void unlock(Object url,Object breakLock) throws SVNException{
		SVN.getWCClient().doUnlock(toURLs(url),toBoolean(breakLock));
	}
	public static void update(Object file,Object rev,Object depth,
			Object allowUnversionedObstructions,Object depthIsSticky,Object makeParents) throws SVNException{
		SVN.getUpdateClient().doUpdate(toFiles(file),toRevision(rev),toDepth(depth),
				toBoolean(allowUnversionedObstructions),toBoolean(depthIsSticky),toBoolean(makeParents));
	}
	public static void upgrade(Object root) throws SVNException{
		SVN.getAdminClient().doUpgrade(toFile(root));
	}
	private static File[] toFiles(Object files) throws SVNException{
		if(files instanceof Collection){
			ArrayList<SVNURL> list=new ArrayList<>(((Collection)files).size());
			for(Object file:((Collection)files)){
				list.add(toURL(file));
			}
			return list.toArray(new File[0]);
		}else if(files==null){
			return new File[0];
		}else{
			return new File[]{toFile(files)};
		}
	}
	private static SVNCopySource[] toCopySources(Object pegRev,Object rev,Object froms) throws SVNException{
		if(froms instanceof Collection){
			ArrayList<SVNURL> list=new ArrayList<>(((Collection)froms).size());
			for(Object from:((Collection)froms)){
				list.add(toURL(from));
			}
			return list.toArray(new SVNCopySource[0]);
		}else if(froms==null){
			return new SVNCopySource[0];
		}else{
			return new SVNCopySource[]{toCopySource(pegRev,rev,froms)};
		}
	}
	private static SVNURL[] toURLs(Object urls) throws SVNException{
		if(urls instanceof Collection){
			ArrayList<SVNURL> list=new ArrayList<>(((Collection)urls).size());
			for(Object url:((Collection)urls)){
				list.add(toURL(url));
			}
			return list.toArray(new SVNURL[0]);
		}else if(urls==null){
			return new SVNURL[0];
		}else{
			return new SVNURL[]{toURL(urls)};
		}
	}
	private static File toFile(Object obj){
		if(obj instanceof File){
			return (File)obj;
		}else if(obj instanceof Path){
			return ((Path)obj).toFile();
		}else{
			return new File(Objects.toString(obj));
		}
	}
	private static SVNCopySource toCopySource(Object pegRev,Object rev,Object obj) throws SVNException{
		return new SVNCopySource(toRevision(pegRev),toRevision(rev),toURL(obj));
	}
	private static SVNURL toURL(Object obj) throws SVNException{
		if(obj instanceof Path)
			obj=((Path)obj).toFile();
		if(obj instanceof File){
			return SVNURL.fromFile((File)obj);
		}else{
			return SVNURL.parseURIEncoded(Objects.toString(obj));
		}
	}
	private static SVNProperties toProperties(Object obj){
		return SVNProperties.wrap(null);
	}
	private static SVNDepth toDepth(Object obj){
		if(obj instanceof Number){
			return SVNDepth.fromID(((Number)obj).intValue());
		}else if(obj instanceof Boolean){
			return SVNDepth.fromRecurse((Boolean)obj);
		}else{
			return SVNDepth.fromString(Objects.toString(obj));
		}
	}
	private static SVNRevision toRevision(Object obj){
		if(obj instanceof Number){
			return SVNRevision.create(((Number)obj).longValue());
		}else if(obj instanceof Date){
			return SVNRevision.create((Date)obj);
		}else{
			return SVNRevision.parse(Objects.toString(obj));
		}
	}
	private static String[] toStringArray(Object obj){
		if(obj instanceof Collection){
			return ((Collection<String>)obj).toArray(new String[0]);
		}else{
			return new String[0];
		}
	}
	private static Collection<String> toStringSet(Object obj){
		if(obj instanceof Collection){
			return (Collection<String>)obj;
		}else{
			return Collections.emptySet();
		}
	}
	private static SVNConflictChoice toConflictChoice(Object conflictChoice){
		switch(Objects.toString(conflictChoice).toUpperCase()){
			case "POSTPONE":return SVNConflictChoice.POSTPONE;
			case "MINE_CONFLICT":return SVNConflictChoice.MINE_CONFLICT;
			case "MINE_FULL":return SVNConflictChoice.MINE_FULL;
			case "THEIRS_CONFLICT":return SVNConflictChoice.THEIRS_CONFLICT;
			case "THEIRS_FULL":return SVNConflictChoice.THEIRS_FULL;
			case "BASE":return SVNConflictChoice.BASE;
			default:return SVNConflictChoice.MERGED;
		}
	}
	private static SVNPropertyValue toValue(Object propValue){
		return SVNPropertyValue.create(Objects.toString(propValue));
	}
	private static boolean toBoolean(Object obj){
		if(obj instanceof Boolean){
			return (Boolean)obj;
		}else{
			return Boolean.valueOf(Objects.toString(obj));
		}
	}
	private static String toString(Object obj){
		return Objects.toString(obj);
	}
	private static long toLong(Object obj){
		if(obj instanceof Number){
			return ((Number)obj).longValue();
		}else{
			return Long.parseLong(Objects.toString(obj));
		}
	}
	private static int toInt(Object obj){
		if(obj instanceof Number){
			return ((Number)obj).intValue();
		}else{
			return Integer.parseInt(Objects.toString(obj));
		}
	}
	private static TextObject createAndShowDataObject(String name){
		TextObject text=new TextObject("");
		RegistryNode<String,Object> obj=new SimpleRegistryNode<>();
		obj.put(DataObject.DEFAULT_NAME,name);
		obj.put(DataObject.DATA,text);
		DataObjectRegistry.addDataObject(obj);
		Main.INSTANCE.show(obj);
		return text;
	}
}