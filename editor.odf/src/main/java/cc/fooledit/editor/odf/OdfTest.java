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
package cc.fooledit.editor.odf;
import cc.fooledit.editor.odf.writer.*;
import java.io.*;
import java.nio.file.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.odftoolkit.simple.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdfTest extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		TextDocument document=TextDocument.loadDocument(Files.newInputStream(new File("/home/kwong/sysu_learning/生物人类学/eassy.odt").toPath()));
		primaryStage.setScene(new Scene(new BorderPane(new OdtViewer(document))));
//		PresentationDocument document=PresentationDocument.loadDocument(Files.newInputStream(new File("/home/kwong/sysu_learning/大学语文/数学与中国文学.odp").toPath()));
//		primaryStage.setScene(new Scene(new BorderPane(new OdpViewer(document))));
//		SpreadsheetDocument document=SpreadsheetDocument.loadDocument(Files.newInputStream(new File("/home/kwong/sysu_learning/misc/classlist.ods").toPath()));
//		primaryStage.setScene(new Scene(new BorderPane(new OdsViewer(document))));
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
