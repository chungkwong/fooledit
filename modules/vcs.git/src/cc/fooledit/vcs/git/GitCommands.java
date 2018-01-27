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
	public static Iterable<PushResult> push(String remote,Git git)throws Exception{
		return git.push().setRemote(remote).call();
	}
	public static PullResult pull(String remote,Git git)throws Exception{
		return git.pull().setRemote(remote).call();
	}
	public static FetchResult fetch(String remote,Git git)throws Exception{
		return git.fetch().setRemote(remote).call();
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
	public static List<String> deleteBranch(String name,Git git) throws GitAPIException{
		return git.branchDelete().setBranchNames(name).call();
	}
	public static Ref renameBranch(String name,String newname,Git git) throws GitAPIException{
		return git.branchRename().setOldName(name).setNewName(newname).call();
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
	public static RemoteConfig addRemote(String name,String uri,Git git) throws URISyntaxException, GitAPIException{
		RemoteAddCommand command=git.remoteAdd();
		command.setName(name);
		command.setUri(new URIish(uri));
		return command.call();
	}
	public static RemoteConfig deleteRemote(String name,Git git) throws URISyntaxException, GitAPIException{
		RemoteRemoveCommand command=git.remoteRemove();
		command.setName(name);
		return command.call();
	}
	public static RemoteConfig setRemote(String name,String uri,Git git) throws URISyntaxException, GitAPIException{
		RemoteSetUrlCommand command=git.remoteSetUrl();
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
	public static Ref addTag(String tag,RevObject id,Git git) throws GitAPIException{
		return git.tag().setName(tag).setObjectId(id).call();
	}
	public static List<String> removeTag(String tag,Git git) throws GitAPIException{
		return git.tagDelete().setTags(tag).call();
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
	static void blame(Object object,Git git){
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
	public static String read(ObjectId id,Git git) throws IOException{
		ObjectLoader obj=git.getRepository().open(id);
		StringBuilder buf=new StringBuilder();
		BufferedReader in=new BufferedReader(new InputStreamReader(obj.openStream()));
		return in.lines().collect(Collectors.joining("\n"));
	}
}
