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
import java.io.*;
import java.util.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.transport.*;
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

}
