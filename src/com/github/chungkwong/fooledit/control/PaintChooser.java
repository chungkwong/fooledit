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
package com.github.chungkwong.fooledit.control;
import com.github.chungkwong.fooledit.api.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;
import javafx.scene.Node;
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
		methods.getItems().add(new LinearChooser());
		methods.getItems().add(new RadialChooser());
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
			return MessageRegistry.getString("COLOR");
		}
	}
	private class ImageChooser implements MinorChooser{
		private final FileChooser picker;
		private final Button browse=new Button("BROWSE");
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
			return MessageRegistry.getString("IMAGE");
		}
	}
	private class LinearChooser implements MinorChooser{
		private final HBox bar=new HBox();
		private File file;
		private final Spinner angle=new Spinner(-Math.PI,Math.PI,0,0.1);
		private ComboBox<CycleMethod> cycleMethod=new ComboBox<>();
		public LinearChooser(){
			Button add=new Button("+");
			add.setOnAction((e)->bar.getChildren().add(new StopEditor()));
			angle.setEditable(true);
			cycleMethod.getItems().addAll(CycleMethod.values());
			cycleMethod.getSelectionModel().select(CycleMethod.NO_CYCLE);
			bar.getChildren().add(angle);
			bar.getChildren().add(cycleMethod);
			bar.getChildren().add(add);
		}
		@Override
		public Node getNode(){
			return bar;
		}
		@Override
		public Paint getPaint(){
			List<Stop> stops=bar.getChildren().stream().filter((c)->c instanceof StopEditor).
					map((c)->((StopEditor)c).getStop()).collect(Collectors.toList());
			double theta=((Number)angle.getValue()).doubleValue();
			return new LinearGradient(0,0,Math.cos(theta),Math.sin(theta),true,cycleMethod.getValue(),stops);
		}
		@Override
		public String toString(){
			return MessageRegistry.getString("LINEAR_GRADIENT");
		}
		private class StopEditor extends HBox{
			private final Button remove=new Button("X");
			private final ColorPicker colorPicker=new ColorPicker();
			private final Spinner posPicker=new Spinner(0.0,1.0,0.5,0.1);
			public StopEditor(){
				getChildren().add(posPicker);
				getChildren().add(colorPicker);
				getChildren().add(remove);
				posPicker.setEditable(true);
				remove.setOnAction((e)->bar.getChildren().remove(this));
			}
			Stop getStop(){
				return new Stop(((Number)posPicker.getValue()).doubleValue(),colorPicker.getValue());
			}
		}
	}
	private class RadialChooser implements MinorChooser{
		private final HBox bar=new HBox();
		private File file;
		private final Spinner angle=new Spinner(-Math.PI,Math.PI,0,0.1);
		private ComboBox<CycleMethod> cycleMethod=new ComboBox<>();
		public RadialChooser(){
			Button add=new Button("+");
			add.setOnAction((e)->bar.getChildren().add(new StopEditor()));
			angle.setEditable(true);
			cycleMethod.getItems().addAll(CycleMethod.values());
			cycleMethod.getSelectionModel().select(CycleMethod.NO_CYCLE);
			bar.getChildren().add(angle);
			bar.getChildren().add(cycleMethod);
			bar.getChildren().add(add);
		}
		@Override
		public Node getNode(){
			return bar;
		}
		@Override
		public Paint getPaint(){
			List<Stop> stops=bar.getChildren().stream().filter((c)->c instanceof StopEditor).
					map((c)->((StopEditor)c).getStop()).collect(Collectors.toList());
			double theta=((Number)angle.getValue()).doubleValue();
			return new RadialGradient(0,0,0.5,0.5,1,true,cycleMethod.getValue(),stops);
		}
		@Override
		public String toString(){
			return MessageRegistry.getString("RADIAL_GRADIENT");
		}
		private class StopEditor extends HBox{
			private final Button remove=new Button("X");
			private final ColorPicker colorPicker=new ColorPicker();
			private final Spinner posPicker=new Spinner(0.0,1.0,0.5,0.1);
			public StopEditor(){
				getChildren().add(posPicker);
				getChildren().add(colorPicker);
				getChildren().add(remove);
				posPicker.setEditable(true);
				remove.setOnAction((e)->bar.getChildren().remove(this));
			}
			Stop getStop(){
				return new Stop(((Number)posPicker.getValue()).doubleValue(),colorPicker.getValue());
			}
		}
	}

}
