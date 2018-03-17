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
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TaskManager{
	private static final ExecutorService executor=Executors.newCachedThreadPool();
	private static final List<Task<?>> tasks=new ArrayList<>();
	public static void executeTask(Task<?> task){
		tasks.add(task);
		task.setOnCancelled(null);
		task.setOnSucceeded(null);
		task.setOnFailed(null);
		executor.submit(task);
	}
	public static void executeCommand(Command command){
		executeCommand(command,new ArrayList<>(),command.getParameters());
	}
	public static void executeCommand(Command command,List<ScmObject> collected,List<Argument> missing){
		if(missing.isEmpty()){
			Main.INSTANCE.getNotifier().notifyStarted(command.getDisplayName());
			ScmObject obj=command.accept(ScmList.toList(collected));
			if(obj!=null){
				Main.INSTANCE.getNotifier().notify(obj.toExternalRepresentation());
			}else{
				Main.INSTANCE.getNotifier().notifyFinished(command.getDisplayName());
			}
			if(!command.getName().equals("command")){
				Main.INSTANCE.getMiniBuffer().restore();
			}
		}else{
			Argument arg=missing.get(0);
			if(arg.getDef()!=null){
				try{
					collected.add(SchemeConverter.toScheme(arg.getDef().get()));
					executeCommand(command,collected,missing.subList(1,missing.size()));
					return;
				}catch(Exception ex){
					Logger.getGlobal().log(Level.FINE,null,ex);
				}
			}
			Main.INSTANCE.getMiniBuffer().setMode((String p)->{
				collected.add(new ScmString(p));
				executeCommand(command,collected,missing.subList(1,missing.size()));
			},null,"",new Label(MessageRegistry.getString(missing.get(0).getName(),command.getModule())),null);
		}
	}
}