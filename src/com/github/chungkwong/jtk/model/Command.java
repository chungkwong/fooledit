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
package com.github.chungkwong.jtk.model;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class Command{
	public abstract void execute();
	public abstract String getDisplayName();
	public static Command create(String name,Runnable action){
		return new SimpleCommand(name,action);
	}
	private static class SimpleCommand extends Command{
		private final String name;
		private final Runnable action;
		public SimpleCommand(String name,Runnable action){
			this.action=action;
			this.name=name;
		}
		@Override
		public void execute(){
			action.run();
		}
		@Override
		public String getDisplayName(){
			return name;
		}

	}
}
