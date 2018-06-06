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
package cc.fooledit.editor.terminal;
import com.kodedu.terminalfx.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class TerminalTest extends Application{
	private TerminalBuilder terminalBuilder;
	private TerminalTab terminal;
	@Override
	public void start(Stage primaryStage) throws Exception{
		terminalBuilder = new TerminalBuilder();
		terminal=terminalBuilder.newTerminal();
		terminal.onTerminalFxReady(()->Platform.runLater(()->primaryStage.setScene(new Scene(new BorderPane(terminal.getContent())))));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
	@Override
	public void stop() throws Exception{
		System.exit(0);
	}
	public static void main(String[] args){
		launch(args);
	}
}
