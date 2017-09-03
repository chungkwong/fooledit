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
package cc.fooledit.example.text;
import cc.fooledit.model.*;
import java.io.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextObjectType implements DataObjectType<TextObject>{
	public static final TextObjectType INSTANCE=new TextObjectType();
	private TextObjectType(){

	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return true;
	}
	@Override
	public void writeTo(TextObject data,OutputStream out) throws Exception{
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
		writer.write(data.getText().get());
		writer.flush();
	}
	@Override
	public TextObject readFrom(InputStream in) throws Exception{
		StringBuilder buf=new StringBuilder();
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		return new TextObject(reader.lines().collect(Collectors.joining("\n")));
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public TextObject create(){
		return new TextObject("");
	}
	@Override
	public String getName(){
		return "text";
	}
}
