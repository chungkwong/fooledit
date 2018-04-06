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
import java.util.stream.*;
import javafx.scene.*;
import javafx.scene.control.*;
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
		return MessageRegistry.getString("DRAW",ImageEditorModule.NAME);
	}
	@Override
	public Node createInstance(){
		return createInstance((GraphicsObject)Main.INSTANCE.getCurrentData());
	}
	Node createInstance(GraphicsObject object){
		return new TabPane(getDrawTab(),getFrontgroundTab(),getBackgroundTab());
	}
	public Tab getDrawTab(){
		return new Tab("TOOL");
	}
	public Tab getBackgroundTab(){
		ComboBox<FillRule> fillRuleChooser=new ComboBox<>();
		fillRuleChooser.setConverter(new EnumStringConvertor<>(FillRule.class,ImageEditorModule.NAME));
		fillRuleChooser.getItems().setAll(FillRule.values());
		fillRuleChooser.getSelectionModel().select(FillRule.NON_ZERO);
		Spinner alphaChooser=new Spinner(0.0,1.0,1.0,0.1);
		alphaChooser.setEditable(true);
		PaintChooser fillChooser=new PaintChooser(Color.WHITE);
		return new Tab("Fill",new FlowPane(fillRuleChooser,alphaChooser,fillChooser));
	}
	public Tab getFrontgroundTab(){
		ComboBox<StrokeLineJoin> joinChooser=new ComboBox<>();
		ComboBox<StrokeLineCap> capChooser=new ComboBox<>();
		ComboBox<FillRule> fillRuleChooser=new ComboBox<>();
		TextField dashChooser=new TextField();
		TextField thickChooser=new TextField();
		PaintChooser strokeChooser=new PaintChooser(Color.BLACK);
		joinChooser.setConverter(new EnumStringConvertor<>(StrokeLineJoin.class,ImageEditorModule.NAME));
		joinChooser.getItems().setAll(StrokeLineJoin.values());
		joinChooser.getSelectionModel().select(StrokeLineJoin.MITER);
		capChooser.setConverter(new EnumStringConvertor<>(StrokeLineCap.class,ImageEditorModule.NAME));
		capChooser.getItems().setAll(StrokeLineCap.values());
		capChooser.getSelectionModel().select(StrokeLineCap.BUTT);
		Label dashLabel=new Label(MessageRegistry.getString("DASH",ImageEditorModule.NAME));
		dashChooser.setText("1.0");
		Label thickLabel=new Label(MessageRegistry.getString("THICK",ImageEditorModule.NAME));
		thickChooser.setText("1.0");
		return new Tab("STROKE",new FlowPane(joinChooser,capChooser,dashLabel,dashChooser,thickLabel,thickChooser,strokeChooser));
	}
	private double[] toDashArray(String str){
		return Arrays.stream(str.split(":")).mapToDouble((s)->Double.parseDouble(s)).toArray();
	}
	private String fromDashArray(double[] array){
		return array==null?"1":Arrays.stream(array).mapToObj((d)->Double.toString(d)).collect(Collectors.joining(":"));
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public SideBar.Side[] getPerferedSides(){
		return new SideBar.Side[]{SideBar.Side.LEFT,SideBar.Side.LEFT};
	}
}
