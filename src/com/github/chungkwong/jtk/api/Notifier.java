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
package com.github.chungkwong.jtk.api;
import com.github.chungkwong.jtk.*;
import java.text.*;
import java.util.*;
import java.util.logging.Formatter;
import java.util.logging.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Notifier extends Handler{
	private static final Formatter FORMATTER=new LogFormatter();
	private final Main app;
	private final Node bar;
	private final Label label;
	private final HBox other;
	public Notifier(Main app){
		this.app=app;
		label=new Label();
		other=new HBox();
		bar=new BorderPane(label,null,other,null,null);
	}
	public void notify(String msg){
		label.setText(msg);
	}
	@Override
	public void publish(LogRecord record){
		notify(FORMATTER.format(record));
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
	private static class LogFormatter extends Formatter{
		@Override
		public String format(LogRecord record){
			return "["+record.getLevel().getLocalizedName()+"]:"+formatMessage(record);
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
}