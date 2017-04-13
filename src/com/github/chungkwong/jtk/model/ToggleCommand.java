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
import com.github.chungkwong.jtk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class ToggleCommand extends Command{
	private boolean on;
	public ToggleCommand(boolean on){
		this.on=on;
	}
	@Override
	public void accept(Main main){
		setStatus(!on);
	}
	public boolean getStatus(){
		return on;
	}
	public void setStatus(boolean on){
		if(on!=this.on){
			if(on)
				turnOn();
			else
				turnOff();
			this.on=on;
		}
	}
	protected abstract void turnOn();
	protected abstract void turnOff();
}
