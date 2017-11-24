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
package cc.fooledit.model;
import cc.fooledit.util.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FoolURLConnection extends URLConnection{
	private final URLConnection base;
	private FoolURLConnection(URLConnection base){
		super(base.getURL());
		this.base=base;
	}
	public static FoolURLConnection open(URL url) throws IOException{
		return new FoolURLConnection(url.openConnection());
	}
	@Override
	public boolean getAllowUserInteraction(){
		return base.getAllowUserInteraction();
	}
	@Override
	public int getConnectTimeout(){
		return base.getConnectTimeout();
	}
	@Override
	public Object getContent() throws IOException{
		return base.getContent();//TODO
	}
	@Override
	public Object getContent(Class[] classes) throws IOException{
		return base.getContent(classes);//TODO
	}
	@Override
	public String getContentEncoding(){
		return base.getContentEncoding();
	}
	@Override
	public int getContentLength(){
		return base.getContentLength();
	}
	@Override
	public long getContentLengthLong(){
		return base.getContentLengthLong();
	}
	@Override
	public String getContentType(){
		return base.getContentType();
		//List<String> types=ContentTypeDetectorRegistry.guess(this);
		//return types.isEmpty()?null:types.get(0);
	}
	@Override
	public long getDate(){
		return base.getDate();
	}
	@Override
	public boolean getDefaultUseCaches(){
		return base.getDefaultUseCaches();
	}
	@Override
	public boolean getDoInput(){
		return base.getDoInput();
	}
	@Override
	public boolean getDoOutput(){
		return base.getDoOutput();
	}
	@Override
	public long getExpiration(){
		return base.getExpiration();
	}
	@Override
	public String getHeaderField(String name){
		return base.getHeaderField(name);
	}
	@Override
	public String getHeaderField(int n){
		return base.getHeaderField(n);
	}
	@Override
	public long getHeaderFieldDate(String name,long Default){
		return base.getHeaderFieldDate(name,Default);
	}
	@Override
	public int getHeaderFieldInt(String name,int Default){
		return base.getHeaderFieldInt(name,Default);
	}
	@Override
	public String getHeaderFieldKey(int n){
		return base.getHeaderFieldKey(n);
	}
	@Override
	public long getHeaderFieldLong(String name,long Default){
		return base.getHeaderFieldLong(name,Default);
	}
	@Override
	public Map<String,List<String>> getHeaderFields(){
		return base.getHeaderFields();
	}
	@Override
	public long getIfModifiedSince(){
		return base.getIfModifiedSince();
	}
	private InputStream cachedInputStream;
	@Override
	public InputStream getInputStream() throws IOException{
		if(cachedInputStream==null)
			cachedInputStream=MarkableInputStream.wrap(base.getInputStream());
		return cachedInputStream;
	}
	@Override
	public long getLastModified(){
		return base.getLastModified();
	}
	@Override
	public OutputStream getOutputStream() throws IOException{
		return base.getOutputStream();
	}
	@Override
	public Permission getPermission() throws IOException{
		return base.getPermission();
	}
	@Override
	public int getReadTimeout(){
		return base.getReadTimeout();
	}
	@Override
	public Map<String,List<String>> getRequestProperties(){
		return base.getRequestProperties();
	}
	@Override
	public String getRequestProperty(String key){
		return base.getRequestProperty(key);
	}
	public URLConnection getBase(){
		return base;
	}
	@Override
	public URL getURL(){
		return base.getURL();
	}
	@Override
	public void connect() throws IOException{
		base.connect();
	}
}
