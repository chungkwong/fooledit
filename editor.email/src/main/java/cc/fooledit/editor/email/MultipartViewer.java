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
package cc.fooledit.editor.email;
import java.net.*;
import javafx.scene.layout.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MultipartViewer extends BorderPane{
	private final Multipart object;
	public MultipartViewer(Multipart object){
		this.object=object;
	}
	public static void main(String[] args) throws MalformedURLException{
		String str=new URLName("smtp","mail.sysu.edu.cn",25,"hello","kwong","888888").toString();
//		URL url=new URL(str);
		System.out.println(str);
		/*		System.out.println(url.getProtocol());
		System.out.println(url.getUserInfo());
		System.out.println(url.getHost());
		System.out.println(url.getPort());
		System.out.println(url.getPath());*/
	}
}
