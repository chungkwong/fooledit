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
package com.github.chungkwong.jtk.example.image;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.model.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageEditor  extends Application implements DataEditor<ImageObject>{
	@Override
	public Node edit(ImageObject data){
		Canvas canvas=data.getCanvas();
		return new BorderPane(new ScrollPane(canvas),getEffectBar(canvas),null,getDrawingBar(canvas),null);
	}
	private Node getEffectBar(Canvas canvas){
		Button bloom=new Button("Bloom");
		bloom.setOnAction((e)->canvas.setEffect(new javafx.scene.effect.Bloom()));
		Button shadow=new Button("DropShadow");
		shadow.setOnAction((e)->canvas.setEffect(new javafx.scene.effect.DropShadow()));
		Button glow=new Button("Glow");
		glow.setOnAction((e)->canvas.setEffect(new javafx.scene.effect.Glow()));
		Button blur=new Button("Blur");
		blur.setOnAction((e)->canvas.setEffect(new javafx.scene.effect.GaussianBlur()));
		Spinner<Double> spinner=new Spinner<>(0.0,4.0,1.0);
		spinner.setEditable(true);
		spinner.valueProperty().addListener((e,o,n)->{canvas.setScaleX(n);canvas.setScaleY(n);});
		return new HBox(bloom,shadow,glow,blur,spinner);
	}
	private enum Shape{
		LINE((e)->((Canvas)e.getSource()).getGraphicsContext2D().strokeLine(lastx,lasty,e.getX(),e.getY())),
		Oval((e)->((Canvas)e.getSource()).getGraphicsContext2D().strokeOval(lastx,lasty,e.getX(),e.getY()));
		private final Consumer<MouseEvent> drawer;
		private Shape(Consumer<MouseEvent> drawer){
			this.drawer=drawer;
		}
		void draw(MouseEvent e){
			drawer.accept(e);
		}
	}
	private final ComboBox<Shape> shapeChooser=new ComboBox<>();
	private final ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
	private final ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
	private final TextField dashChooser=new TextField();
	private final TextField thickChooser=new TextField();
	private static double lastx=Double.NaN,lasty=Double.NaN;
	private Node getDrawingBar(Canvas canvas){
		GraphicsContext g2d=canvas.getGraphicsContext2D();
		canvas.setOnMouseClicked((e)->{
			if(Double.isNaN(lastx)){
				lastx=e.getX();
				lasty=e.getY();
			}else{
				configure(canvas);
				shapeChooser.getValue().draw(e);
				lastx=Double.NaN;
				lasty=Double.NaN;
			}
		});
		shapeChooser.getItems().setAll(Shape.values());
		joinChooser.getItems().setAll(StrokeLineJoin.values());
		joinChooser.getSelectionModel().select(g2d.getLineJoin());
		capChooser.getItems().setAll(StrokeLineCap.values());
		capChooser.getSelectionModel().select(g2d.getLineCap());
		Label dashLabel=new Label("Dash:");
		dashChooser.setText(fromDashArray(g2d.getLineDashes()));
		Label thickLabel=new Label("Thick:");
		thickChooser.setText(Double.toString(g2d.getLineWidth()));
		return new HBox(shapeChooser,joinChooser,capChooser,dashLabel,dashChooser,thickLabel,thickChooser);
	}
	private static double[] toDashArray(String str){
		return Arrays.stream(str.split(":")).mapToDouble((s)->Double.parseDouble(s)).toArray();
	}
	private static String fromDashArray(double[] array){
		return array==null?"1":Arrays.stream(array).mapToObj((d)->Double.toString(d)).collect(Collectors.joining(":"));
	}
	private void configure(Canvas canvas){
		GraphicsContext g2d=canvas.getGraphicsContext2D();
		g2d.setLineJoin(joinChooser.getValue());
		g2d.setLineCap(capChooser.getValue());
		g2d.setLineDashes(toDashArray(dashChooser.getText()));
		g2d.setLineWidth(Double.parseDouble(thickChooser.getText()));
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("IMAGE_EDITOR");
	}
	@Override
	public void start(Stage stage) throws Exception{
		stage.setScene(new Scene(new BorderPane(new ImageEditor().edit(new ImageObject(new WritableImage(200,200))))));
		stage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
