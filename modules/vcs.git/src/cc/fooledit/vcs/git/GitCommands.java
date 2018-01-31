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
import cc.fooledit.*;
import cc.fooledit.core.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.dircache.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitCommands{
	public static void execute(String command){
		Main.INSTANCE.getMiniBuffer().executeCommand(GitRepositoryEditor.INSTANCE.getCommandRegistry().getChild(command));
	}
	public static Git init(File file)throws Exception{
		return Git.init().setDirectory(file).call();
	}
	public static Git clone(String uri,File dir)throws Exception{
		return Git.cloneRepository().setDirectory(dir).setURI(uri).call();//TODO: Progress
	}
	public static Iterable<PushResult> push(Object remote,String username,String password,Git git)throws Exception{
		return git.push().setRemote(toRemoteConfigName(remote)).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username,password)).call();
	}
	public static PullResult pull(Object remote,Git git)throws Exception{
		return git.pull().setRemote(toRemoteConfigName(remote)).call();
	}
	public static FetchResult fetch(Object remote,Git git)throws Exception{
		return git.fetch().setRemote(toRemoteConfigName(remote)).call();
	}
	private static String toRemoteConfigName(Object remote){
		return remote instanceof RemoteConfig?((RemoteConfig)remote).getName():Objects.toString(remote);
	}
	public static Properties gc(Git git) throws GitAPIException{
		return git.gc().call();
	}
	public static Status status(Git git) throws GitAPIException{
		return git.status().call();
	}
	public static RevCommit commit(Git git) throws GitAPIException{
		return git.commit().call();
	}
	public static Set<String> clean(Git git) throws GitAPIException{
		return git.clean().call();
	}
	public static Ref addBranch(String name,Git git) throws GitAPIException{
		return git.branchCreate().setName(name).call();
	}
	public static List<String> deleteBranch(Object branch,Git git) throws GitAPIException{
		return git.branchDelete().setBranchNames(toRefName(branch)).call();
	}
	public static Ref renameBranch(Object branch,String newname,Git git) throws GitAPIException{
		return git.branchRename().setOldName(toRefName(branch)).setNewName(newname).call();
	}
	private static String toRefName(Object ref){
		return ref instanceof Ref?((Ref)ref).getName():Objects.toString(ref.toString());
	}
	public static String diff(String v1,String v2,Git git,boolean detailed){
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
				return entry.getNewPath()+MessageRegistry.getString(" ADDED",GitModule.NAME);
			case COPY:
				return entry.getOldPath()+MessageRegistry.getString(" COPIED TO ",GitModule.NAME)+entry.getNewPath();
			case DELETE:
				return entry.getOldPath()+MessageRegistry.getString(" REMOVED",GitModule.NAME);
			case MODIFY:
				return entry.getOldPath()+MessageRegistry.getString(" MODIFIED",GitModule.NAME);
			case RENAME:
				return entry.getOldPath()+MessageRegistry.getString(" RENAMED TO ",GitModule.NAME)+entry.getNewPath();
			default:
				return "";
		}
	}
	public static MergeResult merge(Object obj,Git git) throws GitAPIException, IOException{
		obj=toBranchOrCommit(obj,git);
		if(obj instanceof AnyObjectId)
			return git.merge().include((AnyObjectId)obj).call();
		else
			return git.merge().include((Ref)obj).call();
	}
	public static Ref checkout(Object obj,Git git) throws GitAPIException, IOException{
		obj=toBranchOrCommit(obj,git);
		if(obj instanceof RevCommit)
			return git.checkout().setStartPoint((RevCommit)obj).call();
		else
			return git.checkout().setName(obj.toString()).call();
	}
	public static RevCommit revert(Object obj,Git git) throws GitAPIException, IOException{
		obj=toBranchOrCommit(obj,git);
		if(obj instanceof AnyObjectId)
			return git.revert().include((AnyObjectId)obj).call();
		else
			return git.revert().include(git.getRepository().findRef(obj.toString())).call();
	}
	private static Object toBranchOrCommit(Object obj,Git git) throws IOException{
		if(obj instanceof AnyObjectId||obj instanceof Ref)
			return obj;
		else{
			String name=Objects.toString(obj);
			ObjectId id=git.getRepository().resolve(name);
			return id!=null?git.getRepository().parseCommit(id):git.getRepository().findRef(name);
		}
	}
	public static RemoteConfig addRemote(String name,String uri,Git git) throws URISyntaxException, GitAPIException{
		RemoteAddCommand command=git.remoteAdd();
		command.setName(name);
		command.setUri(new URIish(uri));
		return command.call();
	}
	public static RemoteConfig deleteRemote(Object remote,Git git) throws URISyntaxException, GitAPIException{
		RemoteRemoveCommand command=git.remoteRemove();
		command.setName(toRemoteConfigName(remote));
		return command.call();
	}
	public static RemoteConfig setRemote(Object remote,String uri,Git git) throws URISyntaxException, GitAPIException{
		RemoteSetUrlCommand command=git.remoteSetUrl();
		String name=toRemoteConfigName(remote);
		command.setName(name);
		command.setUri(new URIish(uri));
		command.setPush(true);
		command.call();
		command=git.remoteSetUrl();
		command.setName(name);
		command.setUri(new URIish(uri));
		command.setPush(false);
		return command.call();
	}
	public static Ref addTag(String tag,Object id,Git git) throws GitAPIException, IOException{
		return git.tag().setName(tag).setObjectId((RevObject)toBranchOrCommit(id,git)).call();
	}
	public static List<String> removeTag(Object tag,Git git) throws GitAPIException{
		return git.tagDelete().setTags(toRefName(tag)).call();
	}
	public static void config(Git git){
		/*
			try{
			StoredConfig config=((Git)getValue()).getRepository().getConfig();
			Dialog dialog=new Dialog();
			dialog.setTitle(MessageRegistry.getString("CONFIGURE",GitModuleReal.NAME));
			TextArea area=new TextArea(config.toText());
			dialog.getDialogPane().setContent(area);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.APPLY);
			dialog.showAndWait();
			if(dialog.getResult().equals(ButtonType.APPLY)){
				config.fromText(area.getText());
				config.save();
			}
		}catch(Exception ex){
			Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
		*/
	}
	public static DirCache add(String filepattern,Git git) throws GitAPIException{
		return git.add().addFilepattern(filepattern).call();
	}
	public static DirCache remove(String filepattern,Git git) throws GitAPIException{
		return git.rm().addFilepattern(filepattern).call();
	}
	public static void blame(Object object,Git git){
		/*item.setOnAction((e)->{
			(e)->{
			Stage dialog=new Stage();
			dialog.setTitle(MessageRegistry.getString("BLAME",GitModuleReal.NAME));
			StringBuilder buf=new StringBuilder();
			list.getSelectionModel().getSelectedItems().stream().forEach((path)->{
				try{
					BlameResult command=git.blame().setFilePath(path).call();
					RawText contents=command.getResultContents();
					for(int i=0;i<contents.size();i++){
						buf.append(command.getSourcePath(i)).append(':');
						buf.append(command.getSourceLine(i)).append(':');
						buf.append(command.getSourceCommit(i)).append(':');
						buf.append(command.getSourceAuthor(i)).append(':');
						buf.append(contents.getString(i)).append('\n');
					}
				}catch(Exception ex){
					Logger.getLogger(StageTreeItem.class.getName()).log(Level.SEVERE,null,ex);
				}
			});
			dialog.setScene(new Scene(new TextArea(buf.toString())));
			dialog.setMaximized(true);
			dialog.show();
		}
		});*/
	}
	public static Object view(Object obj,Git git) throws Exception{
		AnyObjectId id=toObjectId(obj,git);
		Main.INSTANCE.addAndShow(DataObjectRegistry.readFrom(new GitConnection(id,git).getURL()));
		return id;
	}
	private static AnyObjectId toObjectId(Object obj,Git git) throws Exception{
		if(obj instanceof AnyObjectId)
			return (AnyObjectId)obj;
		else
			return git.getRepository().resolve(Objects.toString(obj));
	}
	public static String read(ObjectId id,Git git) throws IOException{
		ObjectLoader obj=git.getRepository().open(id);
		StringBuilder buf=new StringBuilder();
		BufferedReader in=new BufferedReader(new InputStreamReader(obj.openStream()));
		return in.lines().collect(Collectors.joining("\n"));
	}
}
