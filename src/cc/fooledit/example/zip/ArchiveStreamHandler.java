/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package cc.fooledit.example.zip;
import java.io.*;
import java.net.URLConnection;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.compress.archivers.*;
import sun.net.www.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveStreamHandler extends URLStreamHandler{
	private static final String separator="!/";
	protected URLConnection openConnection(URL u)throws IOException{
		return new ArchiveConnection(u,this);
	}
	private static int indexOfBangSlash(String spec){
		int indexOfBang=spec.length();
		while((indexOfBang=spec.lastIndexOf('!',indexOfBang))!=-1){
			if((indexOfBang!=(spec.length()-1))
					&&(spec.charAt(indexOfBang+1)=='/')){
				return indexOfBang+1;
			}else{
				indexOfBang--;
			}
		}
		return -1;
	}
	@Override
	protected boolean sameFile(URL u1,URL u2){
		if(!u1.getProtocol().equals("archive")||!u2.getProtocol().equals("archive")){
			return false;
		}
		String file1=u1.getFile();
		String file2=u2.getFile();
		int sep1=file1.indexOf(separator);
		int sep2=file2.indexOf(separator);
		if(sep1==-1||sep2==-1){
			return super.sameFile(u1,u2);
		}
		String entry1=file1.substring(sep1+2);
		String entry2=file2.substring(sep2+2);
		if(!entry1.equals(entry2)){
			return false;
		}
		URL enclosedURL1=null, enclosedURL2=null;
		try{
			enclosedURL1=new URL(file1.substring(0,sep1));
			enclosedURL2=new URL(file2.substring(0,sep2));
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
		int sep=file.indexOf(separator);
		if(sep==-1){
			return h+file.hashCode();
		}
		URL enclosedURL=null;
		String fileWithoutEntry=file.substring(0,sep);
		try{
			enclosedURL=new URL(fileWithoutEntry);
			h+=enclosedURL.hashCode();
		}catch(MalformedURLException unused){
			h+=fileWithoutEntry.hashCode();
		}
		String entry=file.substring(sep+2);
		h+=entry.hashCode();
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
			absoluteSpec=spec.substring(0,8).equalsIgnoreCase("archive:");
		}
		spec=spec.substring(start,limit);
		if(absoluteSpec){
			file=parseAbsoluteSpec(spec);
		}else if(!refOnly){
			file=parseContextSpec(url,spec);
			// Canonize the result after the bangslash
			int bangSlash=indexOfBangSlash(file);
			String toBangSlash=file.substring(0,bangSlash);
			String afterBangSlash=file.substring(bangSlash);
			sun.net.www.ParseUtil canonizer=new ParseUtil();
			afterBangSlash=canonizer.canonizeString(afterBangSlash);
			file=toBangSlash+afterBangSlash;
		}
		setURL(url,"archive","",-1,file,ref);
	}
	private String parseAbsoluteSpec(String spec){
		URL url=null;
		int index=-1;
		if((index=indexOfBangSlash(spec))==-1){
			throw new NullPointerException("no !/ in spec");
		}
		try{
			String innerSpec=spec.substring(0,index-1);
			url=new URL(innerSpec);
		}catch(MalformedURLException e){
			throw new NullPointerException("invalid url: "
					+spec+" ("+e+")");
		}
		return spec;
	}
	private String parseContextSpec(URL url,String spec){
		String ctxFile=url.getFile();
		// if the spec begins with /, chop up the jar back !/
		if(spec.startsWith("/")){
			int bangSlash=indexOfBangSlash(ctxFile);
			if(bangSlash==-1){
				throw new NullPointerException("malformed "
						+"context url:"
						+url
						+": no !/");
			}
			ctxFile=ctxFile.substring(0,bangSlash);
		}
		if(!ctxFile.endsWith("/")&&(!spec.startsWith("/"))){
			// chop up the last component
			int lastSlash=ctxFile.lastIndexOf('/');
			if(lastSlash==-1){
				throw new NullPointerException("malformed "
						+"context url:"
						+url);
			}
			ctxFile=ctxFile.substring(0,lastSlash+1);
		}
		return (ctxFile+spec);
	}
}
class ArchiveConnection extends URLConnection{
	private URL archiveFileURL;
	private URLConnection archiveFileURLConnection;
	private String entryName;
	private ArchiveInputStream archiveFile;
	private ArchiveEntry archiveEntry;
	private String contentType;
	public ArchiveConnection(URL url,ArchiveStreamHandler handler)
			throws MalformedURLException,IOException{
		super(url);
		parseSpecs(url);
		archiveFileURL=getArchiveFileURL();
		archiveFileURLConnection=archiveFileURL.openConnection();
		entryName=getEntryName();
	}
	@Override
	public void connect() throws IOException{
		if(!connected){
			archiveFileURLConnection=getArchiveFileURL().openConnection();
			if((entryName!=null)){
				try{
					archiveFile=new ArchiveStreamFactory().createArchiveInputStream(archiveFileURLConnection.getInputStream());
					//archiveFile=new ArchiveStreamFactory().createArchiveInputStream(getArchiver(archiveFileURLConnection.getContentType()),archiveFileURLConnection.getInputStream());
					while((archiveEntry=archiveFile.getNextEntry())!=null){
						if(archiveEntry.getName().equals(entryName))
							break;
					}
				}catch(ArchiveException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			}
			connected=true;
		}
	}
	@Override
	public InputStream getInputStream() throws IOException{
		connect();
		if(entryName==null){
			throw new IOException("no entry name specified");
		}else{
			if(archiveEntry==null){
				throw new FileNotFoundException("JAR entry "+entryName
						+" not found in "
						+ archiveFileURL);
			}
		}
		return archiveFile;
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
		long result=-1;
		try{
			connect();
			if(archiveEntry==null){
				/* if the URL referes to an archive */
				result=archiveFileURLConnection.getContentLengthLong();
			}else{
				/* if the URL referes to an archive entry */
				result=getArchiveEntry().getSize();
			}
		}catch(IOException e){
		}
		return result;
	}
	@Override
	public String getHeaderField(String name){
		return archiveFileURLConnection.getHeaderField(name);
	}
	@Override
	public void setRequestProperty(String key,String value){
		archiveFileURLConnection.setRequestProperty(key,value);
	}
	@Override
	public String getRequestProperty(String key){
		return archiveFileURLConnection.getRequestProperty(key);
	}
	@Override
	public void addRequestProperty(String key,String value){
		archiveFileURLConnection.addRequestProperty(key,value);
	}
	@Override
	public Map<String,List<String>> getRequestProperties(){
		return archiveFileURLConnection.getRequestProperties();
	}
	@Override
	public void setAllowUserInteraction(boolean allowuserinteraction){
		archiveFileURLConnection.setAllowUserInteraction(allowuserinteraction);
	}
	@Override
	public boolean getAllowUserInteraction(){
		return archiveFileURLConnection.getAllowUserInteraction();
	}
	@Override
	public void setUseCaches(boolean usecaches){
		archiveFileURLConnection.setUseCaches(usecaches);
	}
	@Override
	public boolean getUseCaches(){
		return archiveFileURLConnection.getUseCaches();
	}
	@Override
	public void setIfModifiedSince(long ifmodifiedsince){
		archiveFileURLConnection.setIfModifiedSince(ifmodifiedsince);
	}
	@Override
	public void setDefaultUseCaches(boolean defaultusecaches){
		archiveFileURLConnection.setDefaultUseCaches(defaultusecaches);
	}
	@Override
	public boolean getDefaultUseCaches(){
		return archiveFileURLConnection.getDefaultUseCaches();
	}

    /**
     * The connection to the JAR file URL, if the connection has been
     * initiated. This should be set by connect.
     */
    protected URLConnection jarFileURLConnection;

	private void parseSpecs(URL url) throws MalformedURLException {
        String spec = url.getFile();
        int separator = spec.lastIndexOf("!/");
        if (separator == -1) {
            throw new MalformedURLException("no !/ found in url spec:" + spec);
        }
        archiveFileURL = new URL(spec.substring(0, separator++));
        entryName = null;
        if (++separator != spec.length()) {
            entryName = spec.substring(separator, spec.length());
            entryName = ParseUtil.decode (entryName);
        }
    }
	public URL getArchiveFileURL(){
		return archiveFileURL;
	}
	public String getEntryName() {
        return entryName;
    }
    public ArchiveEntry getArchiveEntry() throws IOException {
        return archiveEntry;
    }
	private static String getArchiver(String mime){
		return mime2archive.get(mime);
	}
	private static final Map<String,String> mime2archive=new HashMap<>();
	static{
		mime2archive.put("application/x-7z-compressed","7Z");
		mime2archive.put("application/x-archive","AR");
		mime2archive.put("application/x-arj","ARJ");
		mime2archive.put("application/x-cpio","CPIO");
		mime2archive.put("application/x-gtar","TAR");
		mime2archive.put("application/java-archive","JAR");
		mime2archive.put("application/x-java-archive","JAR");
		mime2archive.put("application/x-jar","JAR");
		mime2archive.put("application/x-tar","TAR");
		mime2archive.put("application/zip","ZIP");
		mime2archive.put("application/x-zip-compressed","ZIP");
	}
	public static void main(String[] args) throws Exception{
		System.out.println(new URL("file:/home/kwong/javatool/bsh-2.0b4.jar").getFile());
		URL url=new URL(new URL("file:/home/kwong/javatool/bsh-2.0b4.jar"),"archive:file:/home/kwong/javatool//bsh-2.0b4.jar!/META-INF/MANIFEST.MF",new ArchiveStreamHandler());
		BufferedReader in=new BufferedReader(new InputStreamReader(url.openStream()));
		in.lines().forEach((s)->System.out.println(s));
	}
}