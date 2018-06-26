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
package cc.fooledit.core;
import cc.fooledit.*;
import cc.fooledit.util.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TaskManager{
	private static final ExecutorService executor=Executors.newCachedThreadPool();
	public static void executeTask(Task<?> task){
		String id=getTitle(task);
		CoreModule.TASK_REGISTRY.put(id,task);
		task.setOnRunning((e)->{
			Logger.getGlobal().log(Level.INFO,
					MessageFormat.format(MessageRegistry.getString("EXECUTING",CoreModule.NAME),id));
		});
		task.setOnFailed((e)->{
			Logger.getGlobal().log(Level.SEVERE,
					MessageFormat.format(MessageRegistry.getString("FAILED",CoreModule.NAME),id),
					task.getException());
			CoreModule.TASK_REGISTRY.remove(id);
		});
		task.setOnCancelled((e)->{
			Logger.getGlobal().log(Level.INFO,
					MessageFormat.format(MessageRegistry.getString("CANCELLED",CoreModule.NAME),id));
			CoreModule.TASK_REGISTRY.remove(id);
		});
		task.setOnSucceeded((e)->{
			Logger.getGlobal().log(Level.INFO,
					MessageFormat.format(MessageRegistry.getString("EXECUTED",CoreModule.NAME),id,task.getValue()));
			CoreModule.TASK_REGISTRY.remove(id);
		});
		executor.submit(task);
	}
	private static String getTitle(Task<?> task){
		String name=task.getTitle();
		if(name==null){
			name="";
		}
		if(CoreModule.TASK_REGISTRY.containsKey(name)){
			for(int i=1;;i++){
				String tmp=name+":"+i;
				if(!CoreModule.TASK_REGISTRY.containsKey(tmp)){
					name=tmp;
					break;
				}
			}
		}
		return name;
	}
	public static void executeCommand(Command command){
		executeCommand(command,new ArrayList<>(),command.getParameters());
	}
	public static void executeCommand(Command command,List<Object> collected,List<Argument> missing){
		if(missing.isEmpty()){
			executeTask(new UserTask<>(command.getName(),()->{
				if(!command.getName().equals("command")){
					Platform.runLater(()->Main.INSTANCE.getMiniBuffer().restore());
				}
				return command.accept(collected.toArray());
			}));//FIXME
		}else{
			Argument arg=missing.get(0);
			if(arg.getDef()!=null){
				try{
					collected.add(arg.getDef().get());
					executeCommand(command,collected,missing.subList(1,missing.size()));
					return;
				}catch(Exception ex){
					Logger.getGlobal().log(Level.FINE,null,ex);
				}
			}
			Platform.runLater(()->Main.INSTANCE.getMiniBuffer().setMode((String p)->{
				collected.add(p);
				executeCommand(command,collected,missing.subList(1,missing.size()));
			},null,"",new Label(MessageRegistry.getString(missing.get(0).getName(),command.getModule())),null));
		}
	}
}
class UserTask<T> extends Task<T>{
	private final ThrowableSupplier<T> workload;
	public UserTask(String title,ThrowableSupplier<T> workload){
		this.workload=workload;
		updateTitle(title);
	}
	@Override
	protected T call() throws Exception{
		return workload.get();
	}
}
