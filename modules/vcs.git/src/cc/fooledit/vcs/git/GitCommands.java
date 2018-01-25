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
package cc.fooledit.vcs.git;
import cc.fooledit.core.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitCommands{
	public Git init(File file)throws Exception{
		return Git.init().setDirectory(file).call();
	}
	public Git clone(String uri,String branch,File dir)throws Exception{
		return Git.cloneRepository().setDirectory(dir).setURI(uri).setBranch(branch).call();//TODO: Progress
	}
	public Iterable<PushResult> push(String remote,Git git)throws Exception{
		return git.push().setRemote(remote).call();
	}
	public PullResult pull(String remote,String branch,Git git)throws Exception{
		return git.pull().setRemoteBranchName(branch).setRemote(remote).call();
	}
	public FetchResult fetch(String remote,String branch,Git git)throws Exception{
		return git.fetch().setRemote(remote).call();
	}
	public Properties gc(Git git) throws GitAPIException{
		return git.gc().call();
	}
	public Status status(Git git) throws GitAPIException{
		return git.status().call();
	}
	public String diff(String v1,String v2,Git git,boolean detailed){
		try(ObjectReader reader=git.getRepository().newObjectReader()){
			List<DiffEntry> entries;
			if(v1.isEmpty()&&v2.isEmpty()){
				entries=git.diff().setCached(true).call();
			}else{
				CanonicalTreeParser oldTreeIter=new CanonicalTreeParser();
				oldTreeIter.reset(reader,git.getRepository().resolve(v1+"^{tree}"));
				CanonicalTreeParser newTreeIter=new CanonicalTreeParser();
				newTreeIter.reset(reader,git.getRepository().resolve(v2+"^{tree}"));
				entries=git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
			}
			if(detailed){
				PipedInputStream in=new PipedInputStream();
				PipedOutputStream out=new PipedOutputStream(in);
				DiffFormatter formatter=new DiffFormatter(out);
				formatter.setRepository(git.getRepository());
				formatter.format(entries);
				out.close();
				return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
			}else{
				return entries.stream().map((o)->toString(o)).collect(Collectors.joining("\n"));
			}
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return "";
		}
	}
	private static String toString(DiffEntry entry){
		switch(entry.getChangeType()){
			case ADD:
				return entry.getNewPath()+MessageRegistry.getString(" ADDED",GitModuleReal.NAME);
			case COPY:
				return entry.getOldPath()+MessageRegistry.getString(" COPIED TO ",GitModuleReal.NAME)+entry.getNewPath();
			case DELETE:
				return entry.getOldPath()+MessageRegistry.getString(" REMOVED",GitModuleReal.NAME);
			case MODIFY:
				return entry.getOldPath()+MessageRegistry.getString(" MODIFIED",GitModuleReal.NAME);
			case RENAME:
				return entry.getOldPath()+MessageRegistry.getString(" RENAMED TO ",GitModuleReal.NAME)+entry.getNewPath();
			default:
				return "";
		}
	}

}
