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
package cc.fooledit.editor.zip;
import com.github.junrar.*;
import com.github.junrar.exception.*;
import com.github.junrar.rarfile.*;
import java.io.*;
import java.util.*;
import org.apache.commons.compress.archivers.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RarStreamProvider implements ArchiveStreamProvider{
	private static final String RAR="RAR";
	@Override
	public ArchiveInputStream createArchiveInputStream(String name,InputStream in,String encoding) throws ArchiveException{
		return new RarInputStream(in);
	}
	@Override
	public ArchiveOutputStream createArchiveOutputStream(String string,OutputStream out,String string1) throws ArchiveException{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Set<String> getInputStreamArchiveNames(){
		return Collections.singleton(RAR);
	}
	@Override
	public Set<String> getOutputStreamArchiveNames(){
		return Collections.emptySet();
	}
}
class RarInputStream extends ArchiveInputStream{
	private final Archive archive;
	private FileHeader header;
	private InputStream curr;
	public RarInputStream(InputStream in) throws ArchiveException{
		try{
			File tmp=File.createTempFile("rar","");
			tmp.deleteOnExit();
			FileOutputStream out=new FileOutputStream(tmp);
			byte[] buf=new byte[4096];
			int c;
			while((c=in.read(buf))!=-1){
				out.write(buf,0,c);
			}
			out.close();
			this.archive=new Archive(tmp);
		}catch(RarException|IOException ex){
			throw new ArchiveException(ex.getMessage(),ex);
		}
	}
	@Override
	public int read() throws IOException{
		if(ensureStreamLoaded()){
			return -1;
		}
		return curr.read();
	}
	@Override
	public int read(byte[] b) throws IOException{
		if(ensureStreamLoaded()){
			return -1;
		}
		return curr.read(b);
	}
	@Override
	public int read(byte[] b,int off,int len) throws IOException{
		if(ensureStreamLoaded()){
			return -1;
		}
		return curr.read(b,off,len);
	}
	private boolean ensureStreamLoaded() throws IOException{
		if(curr==null){
			if(header==null){
				return true;
			}else{
				try{
					curr=archive.getInputStream(header);
				}catch(RarException ex){
					throw new IOException(ex);
				}
			}
		}
		return false;
	}
	@Override
	public ArchiveEntry getNextEntry() throws IOException{
		header=archive.nextFileHeader();
		return header!=null?new RarEntry(header):null;
	}
}
class RarEntry implements ArchiveEntry{
	private final FileHeader header;
	public RarEntry(FileHeader header){
		this.header=header;
	}
	@Override
	public String getName(){
		return header.getFileNameString();
	}
	@Override
	public long getSize(){
		return header.getFullUnpackSize();
	}
	@Override
	public boolean isDirectory(){
		return header.isDirectory();
	}
	@Override
	public Date getLastModifiedDate(){
		return header.getMTime();
	}
}
