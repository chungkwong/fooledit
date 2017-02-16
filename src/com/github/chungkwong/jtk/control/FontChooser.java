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
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FontChooser extends HBox{
	private final ComboBox<String> family=new ComboBox<>();
	private final ComboBox<FontWeight> weight=new ComboBox<>();
	private final ComboBox<FontSmoothingType> smoothing=new ComboBox<>();
	private final ComboBox<TextAlignment> alignment=new ComboBox<>();
	private final ComboBox<VPos> baseline=new ComboBox<>();
	private final TextField preview=new TextField();
	private final Spinner size=new Spinner(0.0,100.0,Font.getDefault().getSize(),0.5);
	private final ToggleButton italic=new ToggleButton("I");
	public FontChooser(){
		getChildren().setAll(family,italic,weight,size,smoothing,alignment,baseline,preview);
		family.getItems().setAll(Font.getFamilies());
		family.getSelectionModel().select(Font.getDefault().getFamily());
		weight.getItems().setAll(FontWeight.values());
		smoothing.getItems().setAll(FontSmoothingType.values());
		alignment.getItems().setAll(TextAlignment.values());
		baseline.getItems().setAll(VPos.values());
		weight.getSelectionModel().select(FontWeight.NORMAL);
		smoothing.getSelectionModel().select(FontSmoothingType.GRAY);
		alignment.getSelectionModel().select(TextAlignment.LEFT);
		baseline.getSelectionModel().select(VPos.BASELINE);
		preview.setFont(Font.getDefault());
		size.setEditable(true);
		family.getSelectionModel().selectedItemProperty().addListener((e,o,n)->updatePreview());
		size.valueProperty().addListener((e,o,n)->updatePreview());
		italic.selectedProperty().addListener((e,o,n)->updatePreview());
		weight.getSelectionModel().selectedItemProperty().addListener((e,o,n)->updatePreview());
		smoothing.getSelectionModel().selectedItemProperty().addListener((e,o,n)->updatePreview());
	}
	private void updatePreview(){
		preview.setFont(getFont());
	}
	public Font getFont(){
		return Font.font(family.selectionModelProperty().getValue().getSelectedItem(),weight.getSelectionModel().getSelectedItem(),
				italic.isSelected()?FontPosture.ITALIC:FontPosture.REGULAR,((Number)size.getValue()).doubleValue());
	}
	public FontSmoothingType getFontSmoothingType(){
		return smoothing.getSelectionModel().getSelectedItem();
	}
	public TextAlignment getTextAlignment(){
		return alignment.getValue();
	}
	public VPos getTextBaseline(){
		return baseline.getValue();
	}
}
