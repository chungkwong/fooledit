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
package cc.fooledit.spi;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ProviderRegistry{
	private static final Map<String,Map<String,String>> serviceProviders=new HashMap<>();
	public static void register(String service,String provider,String module){
		Map<String,String> providers=serviceProviders.get(service);
		if(providers==null){
			providers=new HashMap<>();
			serviceProviders.put(service,providers);
		}
		providers.put(provider,module);
	}
	public static String get(String service,String provider){
		Map<String,String> providers=serviceProviders.get(service);
		return providers!=null?providers.get(provider):null;
	}
}
