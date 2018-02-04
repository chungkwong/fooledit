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
import cc.fooledit.core.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SvnCommands{
	private static final SVNClientManager SVN=SVNClientManager.newInstance();

	static{
		//SVN.setAuthenticationManager();
		Argument a=new Argument(null);
	}
	public static void add(Object file,boolean force,boolean mkdir,Object depth,
			boolean depthIsSticky,boolean includeIgnored,boolean makeParent) throws SVNException{
		SVN.getWCClient().doAdd(new File[]{toFile(file)},force,mkdir,true,toDepth(file),
				depthIsSticky,includeIgnored,makeParent);
	}
	public static void auth() throws SVNException{
		//TODO
	}
	public static void blame() throws SVNException{
		//TODO
	}
	public static void cat(Object path,Object rev) throws SVNException{
		//(File repositoryRoot, String path, SVNRevision revision, OutputStream out)
		SVN.getLookClient().doCat(null,null,toRevision(rev),null);
	}
	public static void changelistAdd(Object file,String name,Object depth) throws SVNException{
		SVN.getChangelistClient().doAddToChangelist(new File[]{toFile(file)},toDepth(file),name,null);
	}
	public static void changelistGet(Object file,String name,Object depth) throws SVNException{
		SVN.getChangelistClient().doGetChangeLists(toFile(file),Collections.singleton(name),toDepth(depth),null);
	}
	public static void changelistRemove(Object file,String name,Object depth) throws SVNException{
		SVN.getChangelistClient().doRemoveFromChangelist(new File[]{toFile(file)},toDepth(depth),new String[]{name});
	}
	public static void checkout(Object url,Object dest,Object pegRev,Object rev,Object depth,boolean recursive,boolean force) throws SVNException{
		SVN.getUpdateClient().doCheckout(toURL(url),toFile(dest),toRevision(pegRev),toRevision(rev),toDepth(depth),true);
	}
	public static void cleanup(Object path,boolean deleteWCProperties,boolean breakLocks,
			boolean vacuumPristines,boolean removeUnversionedItems,boolean removeIgnoredItems,
			boolean includeExternals) throws SVNException{
		SVN.getWCClient().doCleanup(toFile(path),deleteWCProperties,breakLocks,vacuumPristines,
				removeUnversionedItems,removeIgnoredItems,includeExternals);
	}
	public static void commit(File file,boolean keepLocks,String commitMessage,
			Object revProp,Object changelists,boolean keepChangelist,
			boolean force,Object depth) throws SVNException{
		//File[] paths, boolean keepLocks, String commitMessage, SVNProperties revisionProperties, String[] changelists, boolean keepChangelist, boolean force, SVNDepth depth
		SVN.getCommitClient().doCommit(new File[]{toFile(file)},keepLocks,commitMessage,toProperties(revProp),
				toChangeList(changelists),keepChangelist,force,toDepth(depth));
	}
	public static void copy(Object from,Object pegRev,Object rev,Object to,boolean isMove,boolean makeParents,
			boolean failWhenDstExists,boolean allowMixedRevisions,boolean metadataOnly)throws SVNException{
		SVNCopySource[] src=new SVNCopySource[]{toCopySource(pegRev,rev,from)};
		File dest=toFile(to);
		SVN.getCopyClient().doCopy(src,dest,isMove,makeParents,failWhenDstExists,allowMixedRevisions,metadataOnly);
	}
	public static void delete(Object file,boolean force,boolean deleteFile,boolean dryrun) throws SVNException{
		SVN.getWCClient().doDelete(toFile(file),force,deleteFile,dryrun);
	}
	public static void diff(Object file,Object pegRev,Object rN,Object rM,Object depth,
			boolean useAncestry, OutputStream result, Collection<String> changeLists) throws SVNException{
		SVN.getDiffClient().doDiff(toFile(file),toRevision(pegRev),toRevision(rN),toRevision(rM),
				toDepth(depth),useAncestry,null,null);
	}
	public static void export(Object src, Object dst,Object pegRev,Object rev,
			String eolStyle,boolean overwrite,SVNDepth depth) throws SVNException{
		SVN.getUpdateClient().doExport(toFile(src),toFile(rev),toRevision(pegRev),toRevision(rev),
				eolStyle,overwrite,toDepth(depth));
	}
	public static void im(Object path,Object dstURL,String commitMessage,Object revProp,
			boolean useGlobalIgnores,boolean ignoreUnknownNodeTypes,Object depth, boolean applyAutoProperties) throws SVNException{
		SVN.getCommitClient().doImport(toFile(path),toURL(dstURL),commitMessage,toProperties(revProp),
				useGlobalIgnores,ignoreUnknownNodeTypes,toDepth(depth),applyAutoProperties);
	}
	public static void info(Object url) throws SVNException{
		SVN.getAdminClient().doInfo(toURL(url));
	}
	public static void list(Object url,Object pegRev,Object rev,boolean fetchLocks,
			SVNDepth depth) throws SVNException{
		SVN.getLogClient().doList(toURL(url),toRevision(pegRev),toRevision(rev),fetchLocks,
				toDepth(rev),SVNDirEntry.DIRENT_ALL,null);
	}
	public static void lock(Object url,boolean stealLock,String msg) throws SVNException{
		SVN.getWCClient().doLock(new SVNURL[]{toURL(url)},stealLock,msg);
	}
	public static void log(SVNURL url, String[] paths, SVNRevision pegRevision, SVNRevision startRevision, SVNRevision endRevision, boolean stopOnCopy, boolean discoverChangedPaths, boolean includeMergedRevisions, long limit, String[] revisionProperties, ISVNLogEntryHandler handler) throws SVNException{
		SVN.getLogClient().doLog(null,paths,SVNRevision.WORKING,SVNRevision.WORKING,SVNRevision.WORKING,true,true,true,limit,revisionProperties,null);
	}
	public static void merge(Object url1,Object rev1,Object url2,Object rev2,Object dstPath,
			Object depth,boolean useAncestry,boolean force,boolean dryRun,boolean recordOnly) throws SVNException{
		SVN.getDiffClient().doMerge(toURL(url1),toRevision(rev1),toURL(url2),toRevision(rev2),toFile(dstPath),
				toDepth(depth),useAncestry,force,dryRun,recordOnly);
	}
	public static void mergeinfo(Object url,Object pegRev)throws SVNException{
		SVN.getDiffClient().doGetMergedMergeInfo(toURL(url),toRevision(pegRev));
	}
	public static void mkdir(Object url,String msg,Object prop,boolean makeParent) throws SVNException{
		SVN.getCommitClient().doMkDir(new SVNURL[]{toURL(url)},msg,toProperties(prop),makeParent);
	}
	public static void move(Object from,Object to) throws SVNException{
		SVN.getMoveClient().doMove(toFile(from),toFile(to));
	}
	public static void patch(Object absPatchPath,Object localAbsPath,boolean dryRun,int stripCount,
			boolean ignoreWhitespace,boolean removeTempFiles,boolean reverse) throws SVNException{
		SVN.getDiffClient().doPatch(toFile(absPatchPath),toFile(localAbsPath),dryRun,stripCount,ignoreWhitespace,removeTempFiles,reverse);
	}
	public static void propdel () throws SVNException{

	}
	public static void propedit () throws SVNException{

	}
	public static void propget(Object path,String propName,Object pegRev,Object rev,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doGetProperty(toFile(path),propName,toRevision(pegRev),toRevision(rev),
				toDepth(depth),ISVNPropertyHandler.NULL,toChanges(changes));
	}
	public static void proplist () throws SVNException{

	}
	public static void propset(File path, String propName, Object propValue,boolean skipChecks,
			Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doSetProperty(toFile(path),propName,toValue(propValue),skipChecks,toDepth(depth),
				ISVNPropertyHandler.NULL,toChanges(changes));
	}
	public static void relocate(Object dst,Object oldURL,Object newURL,boolean recursive) throws SVNException{
		SVN.getUpdateClient().doRelocate(toFile(dst),toURL(oldURL),toURL(newURL),recursive);
	}
	public static void resolve(Object path,Object depth,boolean resolveContents,
			boolean resolveProperties,boolean resolveTree,Object conflictChoice)throws SVNException{
		SVN.getWCClient().doResolve(toFile(path),toDepth(depth),resolveContents,resolveProperties,resolveTree,toConflictChoice(conflictChoice));
	}
	public static void resolved() throws SVNException{

	}
	public static void revert(Object path,Object depth,Object changes) throws SVNException{
		SVN.getWCClient().doRevert(new File[]{toFile(path)},toDepth(depth),toChanges(changes));
	}
	public static void status(Object path,Object rev,Object depth,boolean remote,boolean reportAll,
			boolean includeIgnored, boolean collectParentExternal,Object changes) throws SVNException{
		SVN.getStatusClient().doStatus(toFile(path),toRevision(rev),toDepth(depth),remote,reportAll,
				includeIgnored,collectParentExternal,null,toChanges(changes));
	}
	public static void sw(Object path,Object url,Object pegRev,Object rev,Object depth,
			boolean allowUnversionedObstructions,boolean depthIsSticky,boolean ignoreAncestry) throws SVNException{
		SVN.getUpdateClient().doSwitch(toFile(path),toURL(url),toRevision(pegRev),toRevision(rev),
				toDepth(depth),allowUnversionedObstructions,depthIsSticky,ignoreAncestry);
	}
	public static void unlock(Object url,boolean breakLock) throws SVNException{
		SVN.getWCClient().doUnlock(new SVNURL[]{toURL(url)},breakLock);
	}
	public static void update(Object file,Object rev,Object depth,
			boolean allowUnversionedObstructions,boolean depthIsSticky,boolean makeParents) throws SVNException{
		SVN.getUpdateClient().doUpdate(new File[]{toFile(file)},toRevision(rev),toDepth(depth),allowUnversionedObstructions,depthIsSticky,makeParents);
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
	private static String[] toChangeList(Object obj){
		if(obj instanceof Collection){
			return ((Collection<String>)obj).toArray(new String[0]);
		}else{
			return new String[0];
		}
	}
	private static Collection<String> toChanges(Object obj){
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
}
