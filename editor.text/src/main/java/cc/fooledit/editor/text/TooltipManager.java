/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.text;
import java.time.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.fxmisc.richtext.event.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class TooltipManager{
	private final CodeEditor area;
	private TooltipProvider provider;
	private final Popup popup=new Popup();
	public TooltipManager(CodeEditor area){
		this.area=area;
		area.getArea().setMouseOverTextDelay(Duration.ofSeconds(1));
		area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN,e->{
			Point2D pos=e.getScreenPosition();
			if(provider!=null&&provider.updateNode(e.getCharacterIndex(),area,popup.getContent().get(0))){
				popup.show(area,pos.getX(),pos.getY()-Font.getDefault().getSize());
			}
		});
		area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END,e->{
			popup.hide();
		});
	}
	public void setProvider(TooltipProvider provider){
		this.provider=provider;
		Node tooltip=provider.createNode();
		tooltip.getStyleClass().add("tooltip");
		popup.getContent().setAll(tooltip);
	}
	public void showTooltip(){
		Bounds b=area.getArea().caretBoundsProperty().getValue().get();
		popup.show(area,b.getMinX(),b.getMinY());
	}
}
