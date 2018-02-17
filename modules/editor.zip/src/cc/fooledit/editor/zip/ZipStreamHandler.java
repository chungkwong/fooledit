/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.compress.compressors.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipStreamHandler extends URLStreamHandler{
	protected URLConnection openConnection(URL u)throws IOException{
		return new ZipConnection(u,this);
	}
	@Override
	protected boolean sameFile(URL u1,URL u2){
		if(!u1.getProtocol().equals("compressed")||!u2.getProtocol().equals("compressed")){
			return false;
		}
		String file1=u1.getFile();
		String file2=u2.getFile();
		URL enclosedURL1=null, enclosedURL2=null;
		try{
			enclosedURL1=new URL(file1);
			enclosedURL2=new URL(file2);
		}catch(MalformedURLException unused){
			return super.sameFile(u1,u2);
		}
		if(!super.sameFile(enclosedURL1,enclosedURL2)){
			return false;
		}
		return true;
	}
	@Override
	protected int hashCode(URL u){
		int h=0;
		String protocol=u.getProtocol();
		if(protocol!=null){
			h+=protocol.hashCode();
		}
		String file=u.getFile();
		URL enclosedURL=null;
		try{
			enclosedURL=new URL(file);
			h+=enclosedURL.hashCode();
		}catch(MalformedURLException unused){
			h+=file.hashCode();
		}
		return h;
	}
	@Override
	@SuppressWarnings("deprecation")
	protected void parseURL(URL url,String spec,
			int start,int limit){
		String file=null;
		String ref=null;
		// first figure out if there is an anchor
		int refPos=spec.indexOf('#',limit);
		boolean refOnly=refPos==start;
		if(refPos>-1){
			ref=spec.substring(refPos+1,spec.length());
			if(refOnly){
				file=url.getFile();
			}
		}
        // then figure out if the spec is
		// 1. absolute (jar:)
		// 2. relative (i.e. url + foo/bar/baz.ext)
		// 3. anchor-only (i.e. url + #foo), which we already did (refOnly)
		boolean absoluteSpec=false;
		if(spec.length()>=8){
			absoluteSpec=spec.substring(0,11).equalsIgnoreCase("compressed:");
		}
		spec=spec.substring(start,limit);
		if(absoluteSpec){
			file=parseAbsoluteSpec(spec);
		}else if(!refOnly){
			file=parseContextSpec(url,spec);
		}
		setURL(url,"compressed","",-1,file,ref);
	}
	private String parseAbsoluteSpec(String spec){
		try{
			URL url=new URL(spec);
		}catch(MalformedURLException e){
			throw new NullPointerException("invalid url: "+spec+" ("+e+")");
		}
		return spec;
	}
	private String parseContextSpec(URL url,String spec){
		String ctxFile=url.getFile();
		if(!ctxFile.endsWith("/")&&(!spec.startsWith("/"))){
			// chop up the last component
			int lastSlash=ctxFile.lastIndexOf('/');
			if(lastSlash==-1){
				throw new NullPointerException("malformed context url:"+url);
			}
			ctxFile=ctxFile.substring(0,lastSlash+1);
		}
		return (ctxFile+spec);
	}
}
class ZipConnection extends URLConnection{
	private URL compressedFileURL;
	private URLConnection compressedFileURLConnection;
	private CompressorInputStream compressedFileInputStream;
	private CompressorOutputStream compressedFileOutputStream;
	private HashMap<String,List<String>> headers;
	public ZipConnection(URL url,ZipStreamHandler handler)
			throws MalformedURLException,IOException{
		super(url);
		parseSpecs(url);
		compressedFileURL=getCompressedFileURL();
		compressedFileURLConnection=compressedFileURL.openConnection();
		headers=new HashMap<>(compressedFileURLConnection.getHeaderFields());
		setField("content-length","-1");
		String file=compressedFileURL.getFile();
		int delim=file.lastIndexOf('.');
		if(delim>0&&file.charAt(delim-1)!='/'&&file.charAt(delim-1)!='\\')
			setField("content-name",file.substring(0,delim));
	}
	private void setField(String key,String value){
		List<String> values=headers.get(key);
		if(values==null){
			headers.put(key,Collections.singletonList(value));
		}else{
			values=new ArrayList<>(values);
			values.add(value);
			headers.put(key,values);
		}
	}
	@Override
	public void connect() throws IOException{
		if(!connected){
			compressedFileURLConnection=getCompressedFileURL().openConnection();
			connected=true;
		}
	}
	@Override
	public InputStream getInputStream() throws IOException{
		if(compressedFileInputStream==null){
			connect();
			try{
				compressedFileInputStream=new CompressorStreamFactory().createCompressorInputStream(compressedFileURLConnection.getInputStream());
			}catch(CompressorException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		return compressedFileInputStream;
	}
	@Override
	public OutputStream getOutputStream() throws IOException{
		if(compressedFileOutputStream==null){
			connect();
			try{
				String archiver=mime2compressor.get(compressedFileURLConnection.getContentType());
				compressedFileOutputStream=new CompressorStreamFactory().createCompressorOutputStream(archiver,compressedFileURLConnection.getOutputStream());
			}catch(CompressorException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		return compressedFileOutputStream;
	}
	@Override
	public int getContentLength(){
		long result=getContentLengthLong();
		if(result>Integer.MAX_VALUE){
			return -1;
		}
		return (int)result;
	}
	@Override
	public long getContentLengthLong(){
		return -1;
	}
	@Override
	public String getHeaderField(String name){
		List<String> values=headers.get(name);
		return values!=null&&!values.isEmpty()?values.get(values.size()-1):null;
	}
	@Override
	public void setRequestProperty(String key,String value){
		compressedFileURLConnection.setRequestProperty(key,value);
	}
	@Override
	public String getRequestProperty(String key){
		return compressedFileURLConnection.getRequestProperty(key);
	}
	@Override
	public void addRequestProperty(String key,String value){
		compressedFileURLConnection.addRequestProperty(key,value);
	}
	@Override
	public Map<String,List<String>> getRequestProperties(){
		return compressedFileURLConnection.getRequestProperties();
	}
	@Override
	public void setAllowUserInteraction(boolean allowuserinteraction){
		compressedFileURLConnection.setAllowUserInteraction(allowuserinteraction);
	}
	@Override
	public boolean getAllowUserInteraction(){
		return compressedFileURLConnection.getAllowUserInteraction();
	}
	@Override
	public void setUseCaches(boolean usecaches){
		compressedFileURLConnection.setUseCaches(usecaches);
	}
	@Override
	public boolean getUseCaches(){
		return compressedFileURLConnection.getUseCaches();
	}
	@Override
	public void setIfModifiedSince(long ifmodifiedsince){
		compressedFileURLConnection.setIfModifiedSince(ifmodifiedsince);
	}
	@Override
	public void setDefaultUseCaches(boolean defaultusecaches){
		compressedFileURLConnection.setDefaultUseCaches(defaultusecaches);
	}
	@Override
	public boolean getDefaultUseCaches(){
		return compressedFileURLConnection.getDefaultUseCaches();
	}
	@Override
	public Map<String,List<String>> getHeaderFields(){
		return headers;
	}
	@Override
	public String getHeaderFieldKey(int n){
		Iterator<String> iterator=headers.keySet().iterator();
		while(n>0&&iterator.hasNext()){
			iterator.next();
		}
		if(iterator.hasNext()){
			return iterator.next();
		}else{
			return null;
		}
	}

	@Override
	public String getHeaderField(int n){
		return getHeaderField(getHeaderFieldKey(n));
	}
    protected URLConnection jarFileURLConnection;
	private void parseSpecs(URL url) throws MalformedURLException {
        compressedFileURL=new URL(url.getFile());
    }
	public URL getCompressedFileURL(){
		return compressedFileURL;
	}
	private static String getCompressor(String mime){
		return mime2compressor.get(mime);
	}
	private static final Map<String,String> mime2compressor=new HashMap<>();
	static{
		mime2compressor.put("application/x-bzip","BZIP2");
		mime2compressor.put("application/x-bzip2","BZIP2");
		mime2compressor.put("application/gzip","GZIP");
		mime2compressor.put("application/x-gzip","GZIP");
		mime2compressor.put("application/x-java-pack200","PACK200");
		mime2compressor.put("application/x-lz4","LZ4_BLOCK");
		mime2compressor.put("application/x-lzma","LZMA");
		mime2compressor.put("application/x-xz","XZ");
		mime2compressor.put("application/zlib","Z");
	}
}