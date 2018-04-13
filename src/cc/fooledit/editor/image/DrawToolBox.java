/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.editor.image;
import cc.fooledit.*;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DrawToolBox implements ToolBox{
	public static final DrawToolBox INSTANCE=new DrawToolBox();
	private DrawToolBox(){

	}
	@Override
	public String getName(){
		return "DRAW";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("DRAW",ImageModule.NAME);
	}
	@Override
	public Node createInstance(){
		return createInstance((GraphicsObject)Main.INSTANCE.getCurrentData(),(GraphicsViewer)((ScrollPane)Main.INSTANCE.getCurrentNode()).getContent());
	}
	public Node createInstance(GraphicsObject object,GraphicsViewer viewer){
		return new DrawingToolBox(object,viewer);
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.LEFT,Side.RIGHT};
	}
}
class DrawingToolBox extends TabPane{
	private final ToggleButton fill=new ToggleButton(MessageRegistry.getString("FILL",ImageModule.NAME));
	private final ToggleButton border=new ToggleButton(MessageRegistry.getString("BORDER",ImageModule.NAME));
	private final ToggleButton close=new ToggleButton(MessageRegistry.getString("CLOSE",ImageModule.NAME));
	private final FontChooser fontChooser=new FontChooser();
	private final ComboBox<FillRule> fillRuleChooser=new ComboBox<>();
	private final Spinner alphaChooser=new Spinner(0.0,1.0,1.0,0.1);
	private final PaintChooser fillChooser=new PaintChooser(Color.TRANSPARENT);
	private final ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
	private final ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
	private final TextField dashChooser=new TextField();
	private final TextField thickChooser=new TextField();
	private final PaintChooser strokeChooser=new PaintChooser(Color.BLACK);
	private final GraphicsObject object;
	private final GraphicsViewer viewer;
	private double lastx=Double.NaN,lasty=Double.NaN;
	private double lastlastx=Double.NaN,lastlasty=Double.NaN;
	private int recorded=0;
	public DrawingToolBox(GraphicsObject object,GraphicsViewer viewer){
		getTabs().setAll(getDrawTab(),getTextTab(),getFrontgroundTab(),getBackgroundTab());
		this.object=object;
		this.viewer=viewer;
	}
	public Tab getDrawTab(){
		FlowPane pane=new FlowPane(fill,border,close);
		pane.getChildren().add(new Separator());
		ToggleGroup elements=new ToggleGroup();
		for(Element shape:Element.values()){
			ToggleButton button=new ToggleButton(MessageRegistry.getString(shape.name(),ImageModule.NAME));
			button.setUserData(shape);
			elements.getToggles().add(button);
			pane.getChildren().add(button);
		}
		elements.selectedToggleProperty().addListener((e,o,n)->recorded=0);
		Button draw=new Button(MessageRegistry.getString("DRAW",ImageModule.NAME));
		draw.setOnAction((e)->{
			GraphicsContext g2d=getGraphicsContext();
			draw(g2d);
			recorded=0;
			g2d.beginPath();
		});
		viewer.setOnMouseClicked((e)->{
			configure(getGraphicsContext());
			((Element)elements.getSelectedToggle().getUserData()).make(e,this);
		});
		pane.getChildren().add(new Separator());
		pane.getChildren().add(draw);
		//RegistryNode<String,DrawingTool,String> tools=((RegistryNode<String,DrawingTool,String>)Registry.ROOT.getChild(ImageEditorModule.NAME).getChild(DrawingTool.NAME));
		/*for(String name:tools.getChildNames()){
			ToggleButton cand=new ToggleButton(name);
			DrawingTool child=tools.getChild(name);
			cand.selectedProperty().addListener((e,o,n)->{
				if(n==true){
					viewer.setOnMouseMoved(child);
					viewer.setOnMousePressed(child);
					viewer.setOnMouseReleased(child);
					viewer.setOnMouseEntered(child);
					viewer.setOnMouseExited(child);
				}
			});
			cands.getToggles().add(cand);
			pane.getChildren().add(cand);
		}*/
		return new Tab("TOOL",pane);
	}
	public Tab getBackgroundTab(){
		fillRuleChooser.setConverter(new EnumStringConvertor<>(FillRule.class,ImageModule.NAME));
		fillRuleChooser.getItems().setAll(FillRule.values());
		fillRuleChooser.getSelectionModel().select(FillRule.NON_ZERO);
		alphaChooser.setEditable(true);
		return new Tab("Fill",new FlowPane(fillRuleChooser,alphaChooser,fillChooser));
	}
	public Tab getFrontgroundTab(){
		joinChooser.setConverter(new EnumStringConvertor<>(StrokeLineJoin.class,ImageModule.NAME));
		joinChooser.getItems().setAll(StrokeLineJoin.values());
		joinChooser.getSelectionModel().select(StrokeLineJoin.MITER);
		capChooser.setConverter(new EnumStringConvertor<>(StrokeLineCap.class,ImageModule.NAME));
		capChooser.getItems().setAll(StrokeLineCap.values());
		capChooser.getSelectionModel().select(StrokeLineCap.BUTT);
		Label dashLabel=new Label(MessageRegistry.getString("DASH",ImageModule.NAME));
		dashChooser.setText("1.0");
		Label thickLabel=new Label(MessageRegistry.getString("THICK",ImageModule.NAME));
		thickChooser.setText("1.0");
		return new Tab("STROKE",new FlowPane(joinChooser,capChooser,dashLabel,dashChooser,thickLabel,thickChooser,strokeChooser));
	}
	public Tab getTextTab(){
		return new Tab("TEXT",fontChooser);
	}
	private double[] toDashArray(String str){
		return Arrays.stream(str.split(":")).mapToDouble((s)->Double.parseDouble(s)).toArray();
	}
	private String fromDashArray(double[] array){
		return array==null?"1":Arrays.stream(array).mapToObj((d)->Double.toString(d)).collect(Collectors.joining(":"));
	}
	private void draw(GraphicsContext g2d){
		if(close.isSelected())
			g2d.closePath();
		if(border.isSelected())
			g2d.stroke();
		if(fill.isSelected())
			g2d.fill();
	}
	private GraphicsContext getGraphicsContext(){
		return ((Canvas)object.currentLayerProperty().getValue()).getGraphicsContext2D();
	}
	private void configure(GraphicsContext g2d){
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
	private enum Element{
		MOVE((e,c)->{
			c.getGraphicsContext().moveTo(e.getX(),e.getY());
			c.lastx=e.getX();
			c.lasty=e.getY();
		}),
		LINE((e,c)->{
			c.getGraphicsContext().lineTo(e.getX(),e.getY());
		}),
		RECT(keepOrMake((e,c)->{
			c.getGraphicsContext().rect(c.lastx,c.lasty,e.getX()-c.lastx,e.getY()-c.lasty);
		},1)),
		QUADRATIC(keepOrMake((e,c)->{
			c.getGraphicsContext().quadraticCurveTo(c.lastx,c.lasty,e.getX(),e.getY());
		},1)),
		ARC(keepOrMake((e,c)->{
			double radius=getRadius(c.lastlastx,c.lastlasty,c.lastx,c.lasty,e.getX(),e.getY());
			c.getGraphicsContext().arcTo(c.lastx,c.lasty,e.getX(),e.getY(),radius);
		},1)),
		BEZIER(keepOrMake((e,c)->{
			c.getGraphicsContext().bezierCurveTo(c.lastlastx,c.lastlasty,c.lastx,c.lasty,e.getX(),e.getY());
		},2)),
		TEXT((e,c)->{
			Main.INSTANCE.getMiniBuffer().setMode((text)->{
				c.getGraphicsContext().strokeText(text,e.getX(),e.getY());
				Main.INSTANCE.getMiniBuffer().restore();
			},null,"",null,null);
		});
		private final BiConsumer<MouseEvent,DrawingToolBox> drawer;
		private Element(BiConsumer<MouseEvent,DrawingToolBox> drawer){
			this.drawer=drawer;
		}
		void make(MouseEvent e,DrawingToolBox c){
			drawer.accept(e,c);
		}
		static BiConsumer<MouseEvent,DrawingToolBox> keepOrMake(BiConsumer<MouseEvent,DrawingToolBox> maker,int needPoints){
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
