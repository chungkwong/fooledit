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
import java.text.*;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Notifier extends Handler{
	private static final Formatter USER_FORMATTER=new UserLogFormatter();
	private final Node bar;
	private final Label label;
	private final HBox other;
	public Notifier(){
		label=new Label();
		label.setAlignment(Pos.BASELINE_LEFT);
		other=new HBox();
		bar=new BorderPane(label,null,other,null,null);
	}
	public void notify(String msg){
		Platform.runLater(()->label.setText(msg));
	}
	public void notifyStarted(String task){
		notify(MessageRegistry.getString("EXECUTING",CoreModule.NAME)+task);
	}
	public void notifyFinished(String task){
		notify(MessageRegistry.getString("EXECUTED",CoreModule.NAME)+task);
	}
	@Override
	public void publish(LogRecord record){
		notify(USER_FORMATTER.format(record));
	}
	@Override
	public void flush(){

	}
	@Override
	public void close() throws SecurityException{

	}
	public Node getStatusBar(){
		return bar;
	}
	public void addItem(Node node){
		other.getChildren().add(node);
	}
	public void removeItem(Node node){
		other.getChildren().remove(node);
	}
	public static class UserLogFormatter extends Formatter{
		private final StringBuilder buf=new StringBuilder();
		@Override
		public String format(LogRecord record){
			buf.setLength(0);
			buf.append("[").append(record.getLevel().getLocalizedName()).append("] ");
			buf.append(formatMessage(record)).append('\n');
			return buf.toString();
		}
	}
	public static class SystemLogFormatter extends Formatter{
		private static final DateFormat TIME_FORMAT=new SimpleDateFormat("y-M-d H:m:s");
		private final Date date=new Date(0L);
		private final StringBuilder buf=new StringBuilder();
		@Override
		public String format(LogRecord record){
			date.setTime(record.getMillis());
			buf.setLength(0);
			buf.append(TIME_FORMAT.format(new Date(record.getMillis()))).append(" [");
			buf.append(record.getLevel().getName()).append("] [");
			buf.append(record.getSourceClassName()).append('.').append(record.getSourceMethodName()).append("] ");
			if(record.getMessage()!=null)
				buf.append(MessageFormat.format(record.getMessage(),record.getParameters()));
			else if(record.getThrown()!=null)
				buf.append(record.getThrown().getClass().getName()).append(':').append(record.getThrown().getMessage());
			return buf.append('\n').toString();
		}
	}
	public static Node createTimeField(DateFormat format){
		Label time=new Label();
		new Timer(true).scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run(){
				Platform.runLater(()->time.setText(format.format(new Date())));
			}
		},0,1000);
		return time;
	}
	public static void main(String[] args){
		Logger.getGlobal().addHandler(new StreamHandler(System.err,USER_FORMATTER));
		Logger.getGlobal().log(Level.SEVERE,null,new RuntimeException("hello"));
		Logger.getGlobal().severe("world");Logger.getGlobal().setResourceBundle(null);
	}
}