/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.core;
import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.url.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	static BundleContext bundleContext;
	@Override
	public void start(BundleContext context) throws Exception{
		bundleContext=context;
		Hashtable dataProperties=new Hashtable();
		dataProperties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"data"});
		context.registerService(URLStreamHandlerService.class.getName(),new DataStreamHandler(),dataProperties);
		Hashtable appProperties=new Hashtable();
		appProperties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"application"});
		context.registerService(URLStreamHandlerService.class.getName(),new DataStreamHandler(),appProperties);
		new Thread(()->Main.launch(Main.class)).start();
	}
	@Override
	public void stop(BundleContext context) throws Exception{
		Registry.ROOT.syncPersistent();
	}
	class ApplicationRegistry extends AbstractURLStreamHandlerService{
		@Override
		public URLConnection openConnection(URL u) throws IOException{
			return new ApplicationURLConnection(u);
		}
		private class ApplicationURLConnection extends URLConnection{
			public ApplicationURLConnection(URL url){
				super(url);
			}
			@Override
			public void connect() throws IOException{
			}
			@Override
			public String getContentType(){
				return CoreModule.APPLICATION_REGISTRY.get(getURL().getPath());
			}
		}
	}
}
