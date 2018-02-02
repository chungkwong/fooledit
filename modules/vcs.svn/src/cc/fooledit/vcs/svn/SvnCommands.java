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
	public static void add(Object file) throws SVNException{
		SVN.getWCClient().doAdd(new File[]{toFile(file)},true,true,true,SVNDepth.EMPTY,true,true,true,true);
	}
	public static void auth() throws SVNException{

	}
	public static void blame () throws SVNException{

	}
	public static void cat(Object path,Object rev) throws SVNException{
		//(File repositoryRoot, String path, SVNRevision revision, OutputStream out)
		SVN.getLookClient().doCat(null,null,SVNRevision.HEAD,null);
	}
	public static void changelist () throws SVNException{
		//Collection<String> changeLists, Collection<File> targets, SVNDepth depth, ISVNChangelistHandler handler
		SVN.getChangelistClient().doGetChangeLists(null,null,SVNDepth.EMPTY,null);
	}
	public static void checkout() throws SVNException{
		SVN.getUpdateClient().doCheckout(null,null,SVNRevision.WORKING,SVNRevision.HEAD,SVNDepth.EMPTY,true);
	}
	public static void cleanup() throws SVNException{
		SVN.getWCClient().doCleanup(null,true,true,true,true,true,true);
	}
	public static void commit() throws SVNException{
		SVN.getCommitClient().doCommit(paths,true,null,null,changelists,true,true,SVNDepth.EMPTY);
	}
	public static void copy(Object from,Object to) throws SVNException{
		SVNCopySource[] src=new SVNCopySource[]{toCopySource(from)};
		File dest=toFile(to);
		SVN.getCopyClient().doCopy(src,dest,true,true,true,true,true);
	}
	public static void delete(Object file,boolean force,boolean deleteFile,boolean dryrun) throws SVNException{
		SVN.getWCClient().doDelete(toFile(file),force,deleteFile,dryrun);
	}
	public static void diff () throws SVNException{
		SVN.getDiffClient().doDiff(null,SVNRevision.HEAD,null,SVNRevision.HEAD,SVNDepth.EMPTY,true,null,null);
	}
	public static void export() throws SVNException{
		SVN.getUpdateClient().doExport(null,null,SVNRevision.WORKING,SVNRevision.HEAD,null,true,SVNDepth.EMPTY)
	}
	public static void im() throws SVNException{
		SVN.getCommitClient().doImport(null,null,null,null,true,true,SVNDepth.EMPTY,true);
	}
	public static void info(Object url) throws SVNException{
		SVN.getAdminClient().doInfo(toURL(url));
	}
	public static void list () throws SVNException{
		SVN.getLogClient().doList(null,SVNRevision.WORKING,SVNRevision.HEAD,true,SVNDepth.EMPTY,entryFields,null);
	}
	public static void lock(Object url,boolean stealLock,String msg) throws SVNException{
		SVN.getWCClient().doLock(new SVNURL[]{toURL(url)},stealLock,msg);
	}
	public static void log() throws SVNException{
		SVN.getLogClient().doLog(null,paths,SVNRevision.WORKING,SVNRevision.WORKING,SVNRevision.WORKING,true,true,true,limit,revisionProperties,null);
	}
	public static void merge() throws SVNException{
		SVN.getDiffClient().doMerge(null,SVNRevision.HEAD,null,SVNRevision.HEAD,null,SVNDepth.EMPTY,true,true,true,true);
	}
	public static void mergeinfo() throws SVNException{
		SVN.getDiffClient().doGetMergedMergeInfo(null,SVNRevision.WORKING)
	}
	public static void mkdir(Object url,String msg,Object prop,boolean makeParent) throws SVNException{
		SVN.getCommitClient().doMkDir(new SVNURL[]{toURL(url)},msg,toProperties(prop),makeParent);
	}
	public static void move(Object from,Object to) throws SVNException{
		SVN.getMoveClient().doMove(toFile(from),toFile(to));
	}
	public static void patch() throws SVNException{
		SVN.getDiffClient().doPatch(null,null,true,stripCount,true,true,true);
	}
	public static void propdel () throws SVNException{

	}
	public static void propedit () throws SVNException{

	}
	public static void propget () throws SVNException{

	}
	public static void proplist () throws SVNException{

	}
	public static void propset () throws SVNException{

	}
	public static void relocate() throws SVNException{
		SVN.getUpdateClient().doRelocate(null,null,null,true);
	}
	public static void resolve() throws SVNException{

	}
	public static void resolved() throws SVNException{

	}
	public static void revert() throws SVNException{

	}
	public static void status () throws SVNException{
		SVN.getStatusClient().doStatus(null,SVNRevision.HEAD,SVNDepth.EMPTY,true,true,true,true,null,null)
	}
	public static void sw () throws SVNException{
		SVN.getUpdateClient().doSwitch(null,null,SVNRevision.WORKING,SVNRevision.HEAD,SVNDepth.EMPTY,true,true,true)
	}
	public static void unlock(Object url,boolean breakLock) throws SVNException{
		SVN.getWCClient().doUnlock(new SVNURL[]{toURL(url)},breakLock);
	}
	public static void update () throws SVNException{
		SVN.getUpdateClient().doUpdate(paths,SVNRevision.HEAD,SVNDepth.EMPTY,true,true,true)
	}
	public static void upgrade() throws SVNException{
		SVN.getAdminClient().doUpgrade(null);
	}

	public static void checkout(String url,String file){
		SVN.getUpdateClient();
	}
	public static void commit(String url,String file){
		SVN.getCommitClient().doMkDir(urls,file);
	}
	public static void mkdir(Object url,String msg,){
		SVN.getOperationFactory().createMkDir()
		SVN.getCommitClient().doMkDir(new ,msg,null,true)r(new SVNURL[]{},file);
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
	private static SVNCopySource toCopySource(Object obj){
		SVNRevision pegRevision;
		SVNRevision revision;
		if(obj instanceof File){
			return new SVNCopySource(pegRevision,revision,(File)obj);
		}else if(obj instanceof Path){
			return new SVNCopySource(pegRevision,revision,((Path)obj).toFile());
		}else{
			return new SVNCopySource(pegRevision,revision,new SVNURL(Objects.toString(obj)));
		}SVNURL.parseURIEncoded(null);
	}
	private static SVNURL toURL(Object obj){

	}
	private static SVNProperties toProperties(Object obj){
		return SVNProperties.wrap(null);
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
}
