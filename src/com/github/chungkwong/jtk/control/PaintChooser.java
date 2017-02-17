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
package com.github.chungkwong.jtk.control;
import java.io.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PaintChooser extends BorderPane{
	private final ComboBox<MinorChooser> methods=new ComboBox<>();
	public PaintChooser(Color def){
		methods.getItems().add(new ColorChooser(def));
		methods.getItems().add(new ImageChooser());
		methods.getSelectionModel().selectedItemProperty().addListener((e,o,n)->setCenter(n.getNode()));
		setLeft(methods);
		methods.getSelectionModel().selectFirst();
	}
	public Paint getPaint(){
		return methods.getSelectionModel().getSelectedItem().getPaint();
	}
	private interface MinorChooser{
		Node getNode();
		Paint getPaint();
	}
	private class ColorChooser implements MinorChooser{
		private final ColorPicker picker;
		public ColorChooser(Color def){
			this.picker=new ColorPicker(def);
		}
		@Override
		public Node getNode(){
			return picker;
		}
		@Override
		public Paint getPaint(){
			return picker.getValue();
		}
		@Override
		public String toString(){
			return "Color";
		}
	}
	private class ImageChooser implements MinorChooser{
		private final FileChooser picker;
		private final Button browse=new Button("Browse");
		private File file;
		public ImageChooser(){
			this.picker=new FileChooser();
			browse.setOnAction((e)->file=picker.showOpenDialog(null));
		}
		@Override
		public Node getNode(){
			return browse;
		}
		@Override
		public Paint getPaint(){
			try{
				return new ImagePattern(new Image(new FileInputStream(file)));
			}catch(FileNotFoundException ex){
				return null;
			}
		}
		@Override
		public String toString(){
			return "Image";
		}
	}

}
