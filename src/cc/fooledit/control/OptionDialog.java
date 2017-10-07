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
package cc.fooledit.control;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OptionDialog{
	public static void showConfirmDialog(String title,String content){
		Dialog dialog=new Dialog();
		dialog.setTitle(title);
		dialog.setContentText(content);
		dialog.showAndWait();
	}
	public static String showInputDialog(String title,String prompt){
		TextInputDialog dialog=new TextInputDialog();
		dialog.setTitle(title);
		dialog.setHeaderText(prompt);
		return dialog.showAndWait().get();
	}
	public static void showDialog(Node node){
		Dialog dialog=new Dialog();
		dialog.setResizable(true);
		dialog.getDialogPane().setContent(node);
		dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
		dialog.show();
	}
}
