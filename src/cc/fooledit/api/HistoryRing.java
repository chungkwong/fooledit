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
package cc.fooledit.api;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class HistoryRing<T>{
	private final List<T> list=new ArrayList<>();
	private final Map<String,Integer> tags=new HashMap<>();
	private int currentIndex=-1;
	private int limit;
	public HistoryRing(){
		this(Integer.MAX_VALUE);
	}
	public HistoryRing(int limit){
		this.limit=limit;
	}
	public int getLimit(){
		return limit;
	}
	public void setLimit(int limit){
		this.limit=limit;
	}
	public void add(T obj){
		currentIndex=list.size();
		list.add(obj);
	}
	public void tag(String tag){
		tags.put(tag,getCurrentIndex());
	}
	public int size(){
		return list.size();
	}
	public int getCurrentIndex(){
		return currentIndex;
	}
	public void setCurrentIndex(int currentIndex){
		if(currentIndex>=0&&currentIndex<list.size())
			this.currentIndex=currentIndex;
	}
	public T get(String tag){
		return get(tags.get(tag));
	}
	public T get(int index){
		return list.get(index);
	}
	public int previous(){
		if(currentIndex==0){
			currentIndex=list.size()-1;
		}else{
			--currentIndex;
		}
		return currentIndex;
	}
	public int next(){
		++currentIndex;
		if(currentIndex==list.size()){
			currentIndex=0;
		}
		return currentIndex;
	}
	public Stream<T> stream(){
		return list.stream();
	}
	public void registerComamnds(String noun,Supplier<T> snapshotAction,Consumer<T> chooseAction,CommandRegistry registry){
		registry.put("first-"+noun,()->chooseAction.accept(get(0)),CoreModule.NAME);
		registry.put("last-"+noun,()->chooseAction.accept(get(size()-1)),CoreModule.NAME);
		registry.put("next-"+noun,()->chooseAction.accept(get(next())),CoreModule.NAME);
		registry.put("previous-"+noun,()->chooseAction.accept(get(previous())),CoreModule.NAME);
		registry.put("record-"+noun,()->add(snapshotAction.get()),CoreModule.NAME);
		registry.put("tag-"+noun,(args)->{
			tag(SchemeConverter.toString(ScmList.first(args)));
			return null;
		},CoreModule.NAME);
		registry.put(noun+"-limit",(ScmPairOrNil)->ScmInteger.valueOf(getLimit()),CoreModule.NAME);
		registry.put("set-"+noun+"-limit",(args)->{
			setLimit(SchemeConverter.toInteger(ScmList.first(args)));
			return null;
		},CoreModule.NAME);
	}
}