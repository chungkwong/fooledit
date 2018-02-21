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
import java.io.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SvnTest {
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws SVNException{
		SVNClientManager SVN=SVNClientManager.newInstance();
		System.out.println(SVN.getWCClient().doInfo(SVNURL.fromFile(new File("/home/kwong/projects/svntest/")),SVNRevision.HEAD,SVNRevision.HEAD).getCommittedRevision());
	}

}