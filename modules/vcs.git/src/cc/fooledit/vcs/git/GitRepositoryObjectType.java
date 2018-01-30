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
import cc.fooledit.spi.*;
import java.io.File;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.util.FS;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitRepositoryObjectType implements DataObjectType<GitRepositoryObject>{
	public static final GitRepositoryObjectType INSTANCE=new GitRepositoryObjectType();
	private GitRepositoryObjectType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return false;
	}
	@Override
	public boolean canCreate(){
		return false;
	}
	@Override
	public GitRepositoryObject create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("GIT_REPOSITORY",GitModuleReal.NAME);
	}
	@Override
	public void writeTo(GitRepositoryObject data,URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public GitRepositoryObject readFrom(URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		Git git;
		if(connection.getURL().getProtocol().equals("file")){
			git=Git.open(new File(connection.getURL().toURI()));
		}else if(connection.getURL().getProtocol().equals("git")){
			git=Git.open(new File(new URI(connection.getURL().getFile())));
		}else{
			git=Git.cloneRepository().setURI(connection.getURL().toString()).setDirectory(Files.createTempDirectory("git").toFile()).call();
		}
		return new GitRepositoryObject(git);
	}
}
