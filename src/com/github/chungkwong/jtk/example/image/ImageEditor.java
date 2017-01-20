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
import com.github.chungkwong.jtk.model.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageEditor implements DataEditor<ImageObject>{
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
	private Node getDrawingBar(Canvas canvas){
		Button line=new Button("Line");
		line.setOnAction((e)->{canvas.getGraphicsContext2D().fillOval(50,50,50,50);});
		return new HBox(line);
	}
}
