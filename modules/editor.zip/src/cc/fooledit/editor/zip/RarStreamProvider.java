/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.fooledit.editor.zip;
import com.github.junrar.*;
import com.github.junrar.exception.*;
import com.github.junrar.rarfile.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
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
	private Archive archive;
	public RarInputStream(InputStream in){
		try{
			File tmp=File.createTempFile("rar","");
			FileOutputStream out=new FileOutputStream(tmp);
			byte[] buf=new byte[4096];
			int c;
			while((c=in.read(buf))!=-1){
				out.write(buf,0,c);
			}
			out.close();
			this.archive=new Archive(tmp);
		}catch(RarException|IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			this.archive=null;
		}
	}
	@Override
	public ArchiveEntry getNextEntry() throws IOException{
		return new RarEntry(archive.nextFileHeader());
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