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
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
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
		ImageContext context=new ImageContext(canvas);
		return new BorderPane(new ScrollPane(canvas),getEffectBar(context),null,getDrawingBar(context),null);
	}
	private Node getEffectBar(ImageContext context){
		Canvas canvas=context.canvas;
		ComboBox<ImageEffect> effectChooser=new ComboBox<>();
		effectChooser.getItems().setAll(ImageEffect.values());
		effectChooser.getSelectionModel().selectFirst();
		effectChooser.getSelectionModel().selectedItemProperty().addListener((e,o,n)->canvas.setEffect(n.getEffect()));
		Spinner<Double> scaleChooser=new Spinner<>(0.0,4.0,1.0);
		scaleChooser.valueProperty().addListener((e,o,n)->{canvas.setScaleX(n);canvas.setScaleY(n);});
		Spinner<Double> rotateChooser=new Spinner<>(-180.0,180.0,0.0);
		rotateChooser.valueProperty().addListener((e,o,n)->{canvas.setRotate(n);});
		return new HBox(effectChooser,scaleChooser,rotateChooser);
	}
	private enum ImageEffect{
		NONE(()->null),
		BLOOM(()->new Bloom()),
		SHADOW(()->new Shadow()),
		GLOW(()->new Glow()),
		BLUR(()->new GaussianBlur()),
		TONE(()->new SepiaTone());
		private final Supplier<Effect> supplier;
		private ImageEffect(Supplier<Effect> supplier){
			this.supplier=supplier;
		}
		Effect getEffect(){
			return supplier.get();
		}
	}
	private enum Shape{
		LINE((e,c)->((Canvas)e.getSource()).getGraphicsContext2D().strokeLine(c.lastx,c.lasty,e.getX(),e.getY())),
		RECTANGLE((e,c)->((Canvas)e.getSource()).getGraphicsContext2D().strokeRect(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty)),
		OVAL((e,c)->((Canvas)e.getSource()).getGraphicsContext2D().strokeOval(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty)),
		TEXT((e,c)->((Canvas)e.getSource()).getGraphicsContext2D().strokeText("hello",e.getX(),e.getY()));
		private final BiConsumer<MouseEvent,ImageContext> drawer;
		private Shape(BiConsumer<MouseEvent,ImageContext> drawer){
			this.drawer=drawer;
		}
		void draw(MouseEvent e,ImageContext c){
			drawer.accept(e,c);
		}
	}
	private Node getDrawingBar(ImageContext context){
		Canvas canvas=context.canvas;
		GraphicsContext g2d=canvas.getGraphicsContext2D();
		canvas.setOnMouseClicked((e)->{
			if(Double.isNaN(context.lastx)){
				context.lastx=e.getX();
				context.lasty=e.getY();
			}else{
				configure(context);
				context.shapeChooser.getValue().draw(e,context);
				context.lastx=Double.NaN;
				context.lasty=Double.NaN;
			}
		});
		context.shapeChooser.getItems().setAll(Shape.values());
		context.joinChooser.getItems().setAll(StrokeLineJoin.values());
		context.joinChooser.getSelectionModel().select(g2d.getLineJoin());
		context.capChooser.getItems().setAll(StrokeLineCap.values());
		context.capChooser.getSelectionModel().select(g2d.getLineCap());
		Label dashLabel=new Label("Dash:");
		context.dashChooser.setText(fromDashArray(g2d.getLineDashes()));
		Label thickLabel=new Label("Thick:");
		context.thickChooser.setText(Double.toString(g2d.getLineWidth()));
		return new HBox(context.shapeChooser,context.joinChooser,context.capChooser,
				dashLabel,context.dashChooser,thickLabel,context.thickChooser,
				context.strokeChooser,context.fillChooser);
	}
	private static double[] toDashArray(String str){
		return Arrays.stream(str.split(":")).mapToDouble((s)->Double.parseDouble(s)).toArray();
	}
	private static String fromDashArray(double[] array){
		return array==null?"1":Arrays.stream(array).mapToObj((d)->Double.toString(d)).collect(Collectors.joining(":"));
	}
	private void configure(ImageContext context){
		GraphicsContext g2d=context.canvas.getGraphicsContext2D();
		g2d.setLineJoin(context.joinChooser.getValue());
		g2d.setLineCap(context.capChooser.getValue());
		g2d.setLineDashes(toDashArray(context.dashChooser.getText()));
		g2d.setLineWidth(Double.parseDouble(context.thickChooser.getText()));
		g2d.setStroke(context.strokeChooser.getValue());
		g2d.setFill(context.fillChooser.getValue());
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
	private class ImageContext{
		private final ComboBox<Shape> shapeChooser=new ComboBox<>();
		private final ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
		private final ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
		private final TextField dashChooser=new TextField();
		private final TextField thickChooser=new TextField();
		private final ColorPicker strokeChooser=new ColorPicker(Color.BLACK);
		private final ColorPicker fillChooser=new ColorPicker(Color.WHITE);
		private double lastx=Double.NaN,lasty=Double.NaN;
		private final Canvas canvas;
		public ImageContext(Canvas canvas){
			this.canvas=canvas;
		}
	}
}
