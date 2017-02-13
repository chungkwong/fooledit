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
import com.github.chungkwong.jtk.control.*;
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
		return new BorderPane(new ScrollPane(new StackPane(canvas,context.preview)),getEffectBar(context),getPathBar(context),getDrawingBar(context),null);
	}
	private Node getPathBar(ImageContext context){
		Canvas canvas=context.canvas;
		GraphicsContext g2d=canvas.getGraphicsContext2D();
		Button start=new Button("Start");
		start.setOnAction((e)->g2d.beginPath());
		Button close=new Button("Close");
		close.setOnAction((e)->g2d.closePath());
		Button draw=new Button("Draw");
		draw.setOnAction((e)->g2d.stroke());
		Button fill=new Button("Fill");
		fill.setOnAction((e)->g2d.fill());
		ComboBox<Element> elementChooser=new ComboBox<>();
		elementChooser.getItems().setAll(Element.values());
		context.preview.setOnMouseClicked((e)->{
			configure(context);
			elementChooser.getValue().make(e,context);
		});
		return new VBox(start,close,draw,fill,elementChooser);
	}
	private enum Element{
		MOVE((e,c)->c.getGraphics().moveTo(e.getX(),e.getY())),
		LINE((e,c)->c.getGraphics().lineTo(e.getX(),e.getY())),
		RECT(keepOrMake((e,c)->c.getGraphics().rect(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty))),
		QUADRATIC(keepOrMake((e,c)->c.getGraphics().quadraticCurveTo(c.lastx,c.lasty,e.getX(),e.getY()))),
		ARC(keepOrMake((e,c)->c.getGraphics().arcTo(c.lastx,c.lasty,e.getX(),e.getY(),Math.PI))),
		BEZIER(keepOrMake((e,c)->c.getGraphics().bezierCurveTo(c.lastx,c.lasty,c.lastx,c.lasty,e.getX(),e.getY()))),
		TEXT((e,c)->c.getGraphics().strokeText(OptionDialog.showInputDialog("","Text:"),e.getX(),e.getY()));
		private final BiConsumer<MouseEvent,ImageContext> drawer;
		private Element(BiConsumer<MouseEvent,ImageContext> drawer){
			this.drawer=drawer;
		}
		void make(MouseEvent e,ImageContext c){
			drawer.accept(e,c);
		}
		static BiConsumer<MouseEvent,ImageContext> keepOrMake(BiConsumer<MouseEvent,ImageContext> maker){
			return (e,c)->{
				if(Double.isNaN(c.lastx)){
					c.lastx=e.getX();
					c.lasty=e.getY();
				}else{
					maker.accept(e,c);
					c.lastx=Double.NaN;
					c.lasty=Double.NaN;
				}
			};
		}
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
	private Node getDrawingBar(ImageContext context){
		Canvas canvas=context.canvas;
		GraphicsContext g2d=canvas.getGraphicsContext2D();
		context.joinChooser.getItems().setAll(StrokeLineJoin.values());
		context.joinChooser.getSelectionModel().select(g2d.getLineJoin());
		context.capChooser.getItems().setAll(StrokeLineCap.values());
		context.capChooser.getSelectionModel().select(g2d.getLineCap());
		Label dashLabel=new Label("Dash:");
		context.dashChooser.setText(fromDashArray(g2d.getLineDashes()));
		Label thickLabel=new Label("Thick:");
		context.thickChooser.setText(Double.toString(g2d.getLineWidth()));
		return new HBox(context.joinChooser,context.capChooser,
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
		private final ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
		private final ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
		private final TextField dashChooser=new TextField();
		private final TextField thickChooser=new TextField();
		private final ColorPicker strokeChooser=new ColorPicker(Color.BLACK);
		private final ColorPicker fillChooser=new ColorPicker(Color.WHITE);
		private double lastx=Double.NaN,lasty=Double.NaN;
		private final Canvas canvas,preview;
		public ImageContext(Canvas canvas){
			this.canvas=canvas;
			preview=new Canvas(canvas.getWidth(),canvas.getHeight());
		}
		GraphicsContext getGraphics(){
			return canvas.getGraphicsContext2D();
		}
	}
}