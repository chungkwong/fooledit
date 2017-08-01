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
package com.github.chungkwong.fooledit.example.image;
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.control.*;
import com.github.chungkwong.fooledit.model.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.geometry.*;
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
		canvas.setCursor(Cursor.CROSSHAIR);
		return new ImageViewer(canvas);
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
	private class ImageViewer extends BorderPane{
		private final FontChooser fontChooser=new FontChooser();
		private final ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
		private final ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
		private final ComboBox<FillRule> fillRuleChooser=new ComboBox<>();
		private final Spinner alphaChooser=new Spinner(0.0,1.0,1.0,0.1);
		private final TextField dashChooser=new TextField();
		private final TextField thickChooser=new TextField();
		private final PaintChooser strokeChooser=new PaintChooser(Color.BLACK);
		private final PaintChooser fillChooser=new PaintChooser(Color.WHITE);
		private final ToggleButton fill=new ToggleButton(MessageRegistry.getString("FILL"));
		private final ToggleButton border=new ToggleButton(MessageRegistry.getString("BORDER"));
		private final ToggleButton close=new ToggleButton(MessageRegistry.getString("CLOSE"));

		private double lastx=Double.NaN,lasty=Double.NaN;
		private double lastlastx=Double.NaN,lastlasty=Double.NaN;
		private int recorded=0;
		private final Canvas canvas,preview;
		public ImageViewer(Canvas canvas){
			this.canvas=canvas;
			preview=new Canvas(canvas.getWidth(),canvas.getHeight());
			preview.translateXProperty().bind(canvas.translateXProperty());
			preview.translateYProperty().bind(canvas.translateYProperty());
			preview.scaleXProperty().bind(canvas.scaleXProperty());
			preview.scaleYProperty().bind(canvas.scaleYProperty());
			setCenter(new ScrollPane(new StackPane(canvas,preview)));
			setTop(getEffectBar());
			setRight(getPathBar());
			setBottom(getPropertiesBar());
		}
		private Node getPathBar(){
			GraphicsContext g2d=canvas.getGraphicsContext2D();
			GraphicsContext pg2d=preview.getGraphicsContext2D();
			Button draw=new Button(MessageRegistry.getString("DRAW"));
			draw.setOnAction((e)->{
				draw(g2d);
				clearPreview();
				g2d.beginPath();
				pg2d.beginPath();
			});
			VBox bar=new VBox(draw,new Separator(Orientation.VERTICAL),border,fill,close,new Separator(Orientation.VERTICAL));
			ToggleGroup elements=new ToggleGroup();
			for(Element shape:Element.values()){
				ToggleButton button=new ToggleButton(MessageRegistry.getString(shape.name()));
				button.setUserData(shape);
				elements.getToggles().add(button);
				bar.getChildren().add(button);
			}
			elements.selectedToggleProperty().addListener((e,o,n)->recorded=0);
			preview.setOnMouseClicked((e)->{
				configure();
				preview.getGraphicsContext2D().fillOval(e.getX()-2,e.getY()-2,4,4);
				((Element)elements.getSelectedToggle().getUserData()).make(e,this);
			});
			return bar;
		}
		private Node getEffectBar(){
			ComboBox<ImageEffect> effectChooser=new ComboBox<>();
			effectChooser.getItems().setAll(ImageEffect.values());
			effectChooser.getSelectionModel().selectFirst();
			effectChooser.getSelectionModel().selectedItemProperty().addListener((e,o,n)->canvas.setEffect(n.getEffect()));
			Spinner<Double> scaleChooser=new Spinner<>(0.0,4.0,1.0);
			scaleChooser.valueProperty().addListener((e,o,n)->{
				canvas.setTranslateX(canvas.getWidth()*(n-1)/2);canvas.setScaleX(n);
				canvas.setTranslateY(canvas.getHeight()*(n-1)/2);canvas.setScaleY(n);});
			Spinner<Double> rotateChooser=new Spinner<>(-180.0,180.0,0.0);
			rotateChooser.valueProperty().addListener((e,o,n)->{canvas.setRotate(n);});
			return new HBox(effectChooser,scaleChooser,rotateChooser);
		}
		private Node getPropertiesBar(){
			TabPane tabs=new TabPane(new Tab(MessageRegistry.getString("STROKE"),getStrokePropertiesBar()),
					new Tab(MessageRegistry.getString("FILL"),getFillPropertiesBar()),
					new Tab(MessageRegistry.getString("TEXT"),getTextPropertiesBar()));
			tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
			return tabs;
		}
		private Node getStrokePropertiesBar(){
			GraphicsContext g2d=canvas.getGraphicsContext2D();
			joinChooser.setConverter(new EnumStringConvertor<>(StrokeLineJoin.class));
			joinChooser.getItems().setAll(StrokeLineJoin.values());
			joinChooser.getSelectionModel().select(g2d.getLineJoin());
			capChooser.setConverter(new EnumStringConvertor<>(StrokeLineCap.class));
			capChooser.getItems().setAll(StrokeLineCap.values());
			capChooser.getSelectionModel().select(g2d.getLineCap());
			Label dashLabel=new Label(MessageRegistry.getString("DASH"));
			dashChooser.setText(fromDashArray(g2d.getLineDashes()));
			Label thickLabel=new Label(MessageRegistry.getString("THICK"));
			thickChooser.setText(Double.toString(g2d.getLineWidth()));
			return new HBox(joinChooser,capChooser,dashLabel,dashChooser,thickLabel,thickChooser,strokeChooser);
		}
		private Node getFillPropertiesBar(){
			fillRuleChooser.setConverter(new EnumStringConvertor<>(FillRule.class));
			fillRuleChooser.getItems().setAll(FillRule.values());
			fillRuleChooser.getSelectionModel().select(FillRule.NON_ZERO);
			alphaChooser.setEditable(true);
			return new HBox(fillRuleChooser,alphaChooser,fillChooser);
		}
		private Node getTextPropertiesBar(){
			return fontChooser;
		}
		private double[] toDashArray(String str){
			return Arrays.stream(str.split(":")).mapToDouble((s)->Double.parseDouble(s)).toArray();
		}
		private String fromDashArray(double[] array){
			return array==null?"1":Arrays.stream(array).mapToObj((d)->Double.toString(d)).collect(Collectors.joining(":"));
		}
		private void configure(){
			GraphicsContext g2d=canvas.getGraphicsContext2D();
			g2d.setLineJoin(joinChooser.getValue());
			g2d.setLineCap(capChooser.getValue());
			g2d.setLineDashes(toDashArray(dashChooser.getText()));
			g2d.setLineWidth(Double.parseDouble(thickChooser.getText()));
			g2d.setStroke(strokeChooser.getPaint());
			g2d.setFill(fillChooser.getPaint());
			g2d.setFillRule(fillRuleChooser.getValue());
			g2d.setFont(fontChooser.getFont());
			g2d.setFontSmoothingType(fontChooser.getFontSmoothingType());
			g2d.setTextAlign(fontChooser.getTextAlignment());
			g2d.setTextBaseline(fontChooser.getTextBaseline());
			g2d.setGlobalAlpha(((Number)alphaChooser.getValue()).doubleValue());
		}
		private void updatePreview(){
			GraphicsContext g2d=preview.getGraphicsContext2D();
			g2d.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
			draw(g2d);
		}
		private void draw(GraphicsContext g2d){
			if(close.isSelected())
				g2d.closePath();
			if(border.isSelected())
				g2d.stroke();
			if(fill.isSelected())
				g2d.fill();
		}
		private void clearPreview(){
			preview.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
			recorded=0;
		}
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
		public String toString(){
			return MessageRegistry.getString(name());
		}
	}
	private enum Element{
		MOVE((e,c)->{
			c.canvas.getGraphicsContext2D().moveTo(e.getX(),e.getY());
			c.preview.getGraphicsContext2D().moveTo(e.getX(),e.getY());
			c.lastx=e.getX();
			c.lasty=e.getY();
		}),
		LINE((e,c)->{
			c.canvas.getGraphicsContext2D().lineTo(e.getX(),e.getY());
			c.preview.getGraphicsContext2D().lineTo(e.getX(),e.getY());
			c.updatePreview();
		}),
		RECT(keepOrMake((e,c)->{
			c.canvas.getGraphicsContext2D().rect(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty);
			c.preview.getGraphicsContext2D().rect(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty);
			c.updatePreview();
		},1)),
		QUADRATIC(keepOrMake((e,c)->{
			c.canvas.getGraphicsContext2D().quadraticCurveTo(c.lastx,c.lasty,e.getX(),e.getY());
			c.preview.getGraphicsContext2D().quadraticCurveTo(c.lastx,c.lasty,e.getX(),e.getY());
			c.updatePreview();
		},1)),
		ARC(keepOrMake((e,c)->{
			double radius=getRadius(c.lastlastx,c.lastlasty,c.lastx,c.lasty,e.getX(),e.getY());
			c.canvas.getGraphicsContext2D().arcTo(c.lastx,c.lasty,e.getX(),e.getY(),radius);
			c.preview.getGraphicsContext2D().arcTo(c.lastx,c.lasty,e.getX(),e.getY(),radius);
			c.updatePreview();
		},1)),
		BEZIER(keepOrMake((e,c)->{
			c.canvas.getGraphicsContext2D().bezierCurveTo(c.lastlastx,c.lastlasty,c.lastx,c.lasty,e.getX(),e.getY());
			c.preview.getGraphicsContext2D().bezierCurveTo(c.lastlastx,c.lastlasty,c.lastx,c.lasty,e.getX(),e.getY());
			c.updatePreview();
		},2)),
		TEXT((e,c)->{
			Main.INSTANCE.getMiniBuffer().setMode((text)->{
				c.canvas.getGraphicsContext2D().strokeText(text,e.getX(),e.getY());
				c.clearPreview();
				Main.INSTANCE.getMiniBuffer().restore();
			},null,"",null,null);
		});
		private final BiConsumer<MouseEvent,ImageViewer> drawer;
		private Element(BiConsumer<MouseEvent,ImageViewer> drawer){
			this.drawer=drawer;
		}
		void make(MouseEvent e,ImageViewer c){
			drawer.accept(e,c);
		}
		static BiConsumer<MouseEvent,ImageViewer> keepOrMake(BiConsumer<MouseEvent,ImageViewer> maker,int needPoints){
			return (e,c)->{
				if(c.recorded>=needPoints){
					maker.accept(e,c);
					c.recorded=0;
				}else{
					++c.recorded;
				}
				c.lastlastx=c.lastx;
				c.lastlasty=c.lasty;
				c.lastx=e.getX();
				c.lasty=e.getY();
			};
		}
		static double getAngle(double x0,double y0,double x1,double y1,double x2,double y2){
			double a=Math.hypot(x0-x1,y0-y1);
			double b=Math.hypot(x2-x1,y2-y1);
			double c=Math.hypot(x0-x2,y0-y2);
			return (Math.PI-Math.acos((a*a+b*b-c*c)/(2*a*b)))*2;
		}
		static double getRadius(double x0,double y0,double x1,double y1,double x2,double y2){
			double angle=getAngle(x0,y0,x1,y1,x2,y2);
			double c=Math.hypot(x0-x2,y0-y2);
			return c/Math.sqrt(2*(1-Math.cos(angle)));
		}
	}
}