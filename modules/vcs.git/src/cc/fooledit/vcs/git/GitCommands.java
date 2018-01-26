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
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.*;
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
	public static Git clone(String uri,String branch,File dir)throws Exception{
		return Git.cloneRepository().setDirectory(dir).setURI(uri).setBranch(branch).call();//TODO: Progress
	}
	public static Iterable<PushResult> push(String remote,Git git)throws Exception{
		return git.push().setRemote(remote).call();
	}
	public static PullResult pull(String remote,String branch,Git git)throws Exception{
		return git.pull().setRemoteBranchName(branch).setRemote(remote).call();
	}
	public static FetchResult fetch(String remote,String branch,Git git)throws Exception{
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
	public static void addRemote(Git git){
		/*TextInputDialog dialog=new TextInputDialog();
		dialog.setTitle(MessageRegistry.getString("CHOOSE A NAME FOR THE NEW REMOTE CONFIGURE",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE NAME OF THE NEW REMOTE CONFIGURE:",GitModuleReal.NAME));
		Optional<String> name=dialog.showAndWait();
		dialog.setTitle(MessageRegistry.getString("CHOOSE A URI FOR THE NEW REMOTE CONFIGURE",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE URI OF THE NEW REMOTE CONFIGURE:",GitModuleReal.NAME));
		Optional<String> uri=dialog.showAndWait();
		if(name.isPresent())
			try{
				RemoteAddCommand command=((Git)getValue()).remoteAdd();
				command.setName(name.get());
				command.setUri(new URIish(uri.get()));
				getChildren().add(new RemoteTreeItem(command.call()));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}*/
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
	static void add(Object object,Git git){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	static void remove(Object object,Git git){
		/*item.setOnAction((e)->{
			RmCommand command=((Git)getValue()).rm().setCached(true);
			list.getSelectionModel().getSelectedItems().stream().forEach((path)->{
				command.addFilepattern(path);
			});
			list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
			try{
				command.call();
			}catch(Exception ex){
				Logger.getLogger(StageTreeItem.class.getName()).log(Level.SEVERE,null,ex);
			}
		});*/
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
