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
	private static final SVNClientManager SVN=SVNClientManager.newInstance();

	static{

		ISVNAuthenticationManager authManager=SVNWCUtil.createDefaultAuthenticationManager();
		authManager.setAuthenticationProvider(new ISVNAuthenticationProvider() {
			@Override
			public SVNAuthentication requestClientAuthentication(String kind,SVNURL svnurl,String realm,SVNErrorMessage msg,SVNAuthentication last,boolean cache){
				String old=last!=null?last.getUserName():"";
				switch(kind){
					case ISVNAuthenticationManager.PASSWORD:
						return SVNPasswordAuthentication.newInstance(input("USERNAME:"+realm,old),input("PASSWORD:"+realm,old).toCharArray(),true,svnurl,false);
					case ISVNAuthenticationManager.USERNAME:
						return SVNUserNameAuthentication.newInstance(input(kind+":"+realm,old),true,svnurl,false);
					case ISVNAuthenticationManager.SSH:
						return SVNSSHAuthentication.newInstance(input("USERNAME:"+realm,old),input("PASSWORD:"+realm,old).toCharArray(),22,true,svnurl,false);
					case ISVNAuthenticationManager.SSL:
						return SVNSSLAuthentication.newInstance(kind,input("ALIAS",old),true,svnurl,false);
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
		SVN.getWCClient().doAdd(new File[]{toFile(file)},toBoolean(force),toBoolean(mkdir),true,toDepth(file),
				toBoolean(depthIsSticky),toBoolean(includeIgnored),toBoolean(makeParent));
	}
	public static void blame() throws SVNException{
		//TODO
	}
	public static void cat(Object path,Object rev) throws SVNException{
		SVN.getLookClient().doCat(null,null,toRevision(rev),null);
	}
	public static void changelistAdd(Object file,Object name,Object depth) throws SVNException{
		SVN.getChangelistClient().doAddToChangelist(new File[]{toFile(file)},toDepth(file),toString(name),null);
	}
	public static void changelistGet(Object file,Object name,Object depth) throws SVNException{
		SVN.getChangelistClient().doGetChangeLists(toFile(file),Collections.singleton(toString(name)),toDepth(depth),null);
	}
	public static void changelistRemove(Object file,Object name,Object depth) throws SVNException{
		SVN.getChangelistClient().doRemoveFromChangelist(new File[]{toFile(file)},toDepth(depth),new String[]{toString(name)});
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
	public static void commit(File file,Object keepLocks,Object commitMessage,
			Object revProp,Object changelists,Object keepChangelist,
			Object force,Object depth) throws SVNException{
		SVN.getCommitClient().doCommit(new File[]{toFile(file)},toBoolean(keepLocks),toString(commitMessage),toProperties(revProp),
				toStringArray(changelists),toBoolean(keepChangelist),toBoolean(force),toDepth(depth));
	}
	public static void copy(Object from,Object pegRev,Object rev,Object to,Object isMove,Object makeParents,
			Object failWhenDstExists,Object allowMixedRevisions,Object metadataOnly)throws SVNException{
		SVNCopySource[] src=new SVNCopySource[]{toCopySource(pegRev,rev,from)};
		File dest=toFile(to);
		SVN.getCopyClient().doCopy(src,dest,toBoolean(isMove),toBoolean(makeParents),toBoolean(failWhenDstExists),
				toBoolean(allowMixedRevisions),toBoolean(metadataOnly));
	}
	public static void delete(Object file,Object force,Object deleteFile,Object dryrun) throws SVNException{
		SVN.getWCClient().doDelete(toFile(file),toBoolean(force),toBoolean(deleteFile),toBoolean(dryrun));
	}
	public static void diff(Object file,Object pegRev,Object rN,Object rM,Object depth,
			Object useAncestry, OutputStream result, Collection<String> changeLists) throws SVNException{
		SVN.getDiffClient().doDiff(toFile(file),toRevision(pegRev),toRevision(rN),toRevision(rM),
				toDepth(depth),toBoolean(useAncestry),null,toStringSet(changeLists));
	}
	public static void export(Object src, Object dst,Object pegRev,Object rev,
			Object eolStyle,Object overwrite,SVNDepth depth) throws SVNException{
		SVN.getUpdateClient().doExport(toFile(src),toFile(rev),toRevision(pegRev),toRevision(rev),
				toString(eolStyle),toBoolean(overwrite),toDepth(depth));
	}
	public static void im(Object path,Object dstURL,Object commitMessage,Object revProp,
			Object useGlobalIgnores,Object ignoreUnknownNodeTypes,Object depth, Object applyAutoProperties) throws SVNException{
		SVN.getCommitClient().doImport(toFile(path),toURL(dstURL),toString(commitMessage),toProperties(revProp),
				toBoolean(useGlobalIgnores),toBoolean(ignoreUnknownNodeTypes),toDepth(depth),toBoolean(applyAutoProperties));
	}
	public static void info(Object url) throws SVNException{
		SVN.getAdminClient().doInfo(toURL(url));
	}
	public static void list(Object url,Object pegRev,Object rev,Object fetchLocks,
			SVNDepth depth) throws SVNException{
		SVN.getLogClient().doList(toURL(url),toRevision(pegRev),toRevision(rev),toBoolean(fetchLocks),
				toDepth(rev),SVNDirEntry.DIRENT_ALL,null);
	}
	public static void lock(Object url,Object stealLock,Object msg) throws SVNException{
		SVN.getWCClient().doLock(new SVNURL[]{toURL(url)},toBoolean(stealLock),toString(msg));
	}
	public static void log(Object url,Object path, SVNRevision pegRev, SVNRevision startRev,SVNRevision endRev,
			Object stopOnCopy,Object discoverChangedPaths,Object includeMergedRevisions,Object limit,Object[] revisionProperties, ISVNLogEntryHandler handler) throws SVNException{
		//boolean stopOnCopy, boolean discoverChangedPaths, boolean includeMergedRevisions, long limit, String[] revisionProperties, ISVNLogEntryHandler handler
		SVN.getLogClient().doLog(toURL(url),new String[]{toString(path)},toRevision(pegRev),toRevision(startRev),
				toRevision(endRev),toBoolean(stopOnCopy),toBoolean(discoverChangedPaths),toBoolean(includeMergedRevisions),
				toLong(limit),toStringArray(revisionProperties),null);
	}
	public static void merge(Object url1,Object rev1,Object url2,Object rev2,Object dstPath,
			Object depth,Object useAncestry,Object force,Object dryRun,Object recordOnly) throws SVNException{
		SVN.getDiffClient().doMerge(toURL(url1),toRevision(rev1),toURL(url2),toRevision(rev2),toFile(dstPath),
				toDepth(depth),toBoolean(useAncestry),toBoolean(force),toBoolean(dryRun),toBoolean(recordOnly));
	}
	public static void mergeinfo(Object url,Object pegRev)throws SVNException{
		SVN.getDiffClient().doGetMergedMergeInfo(toURL(url),toRevision(pegRev));
	}
	public static void mkdir(Object url,Object msg,Object prop,Object makeParent) throws SVNException{
		SVN.getCommitClient().doMkDir(new SVNURL[]{toURL(url)},toString(msg),toProperties(prop),toBoolean(makeParent));
	}
	public static void move(Object from,Object to) throws SVNException{
		SVN.getMoveClient().doMove(toFile(from),toFile(to));
	}
	public static void patch(Object absPatchPath,Object localAbsPath,Object dryRun,Object stripCount,
			Object ignoreWhitespace,Object removeTempFiles,Object reverse) throws SVNException{
		SVN.getDiffClient().doPatch(toFile(absPatchPath),toFile(localAbsPath),toBoolean(dryRun),toInt(stripCount),
				toBoolean(ignoreWhitespace),toBoolean(removeTempFiles),toBoolean(reverse));
	}
	public static void propdel () throws SVNException{

	}
	public static void propedit () throws SVNException{

	}
	public static void propget(Object path,Object propName,Object pegRev,Object rev,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doGetProperty(toFile(path),toString(propName),toRevision(pegRev),toRevision(rev),
				toDepth(depth),ISVNPropertyHandler.NULL,toStringSet(changes));
	}
	public static void proplist () throws SVNException{

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
	public static void resolved() throws SVNException{

	}
	public static void revert(Object path,Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doRevert(new File[]{toFile(path)},toDepth(depth),toStringSet(changes));
	}
	public static void status(Object path,Object rev,Object depth,Object remote,Object reportAll,
			Object includeIgnored, Object collectParentExternal,Object changes) throws SVNException{
		SVN.getStatusClient().doStatus(toFile(path),toRevision(rev),toDepth(depth),toBoolean(remote),toBoolean(reportAll),
				toBoolean(includeIgnored),toBoolean(collectParentExternal),null,toStringSet(changes));
	}
	public static void sw(Object path,Object url,Object pegRev,Object rev,Object depth,
			Object allowUnversionedObstructions,Object depthIsSticky,Object ignoreAncestry) throws SVNException{
		SVN.getUpdateClient().doSwitch(toFile(path),toURL(url),toRevision(pegRev),toRevision(rev),
				toDepth(depth),toBoolean(allowUnversionedObstructions),toBoolean(depthIsSticky),toBoolean(ignoreAncestry));
	}
	public static void unlock(Object url,Object breakLock) throws SVNException{
		SVN.getWCClient().doUnlock(new SVNURL[]{toURL(url)},toBoolean(breakLock));
	}
	public static void update(Object file,Object rev,Object depth,
			Object allowUnversionedObstructions,Object depthIsSticky,Object makeParents) throws SVNException{
		SVN.getUpdateClient().doUpdate(new File[]{toFile(file)},toRevision(rev),toDepth(depth),
				toBoolean(allowUnversionedObstructions),toBoolean(depthIsSticky),toBoolean(makeParents));
	}
	public static void upgrade(Object root) throws SVNException{
		SVN.getAdminClient().doUpgrade(toFile(root));
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
}
