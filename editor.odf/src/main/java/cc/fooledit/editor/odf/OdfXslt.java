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
import java.net.*;
import java.nio.file.*;
import java.util.*;
import org.odftoolkit.odfxsltrunner.*;
import org.odftoolkit.simple.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdfXslt{
	public static URI transform(Document document)throws Exception{
		Path working=Files.createTempDirectory("extract");
		Files.copy(OdfXslt.class.getResourceAsStream("body.xsl"),working.resolve("body.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("body_common.xsl"),working.resolve("body_common.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table_common.xsl"),working.resolve("table_common.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table_cells.xsl"),working.resolve("table_cells.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table_columns.xsl"),working.resolve("table_columns.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table_rows.xsl"),working.resolve("table_rows.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table_of_content.xsl"),working.resolve("table_of_content.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("header.xsl"),working.resolve("header.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("opendoc2xhtml.xsl"),working.resolve("opendoc2xhtml.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("table.xsl"),working.resolve("table.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("style_collector.xsl"),working.resolve("style_collector.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("style_mapping_css.xsl"),working.resolve("style_mapping_css.xsl"));
		Files.copy(OdfXslt.class.getResourceAsStream("measure_conversion.xsl"),working.resolve("measure_conversion.xsl"));
		document.save(Files.newOutputStream(working.resolve("org.odt")));
		new ODFXSLTRunner().runXSLT(working.resolve("opendoc2xhtml.xsl").toFile(),Collections.emptyList(),
			working.resolve("org.odt").toFile(),ODFXSLTRunner.INPUT_MODE_PACKAGE,
			working.resolve("content.xhtml").toFile(),ODFXSLTRunner.OUTPUT_MODE_FILE,"content.xml",
			null,Collections.singletonList("Pictures/"),new CommandLineLogger(System.out,org.odftoolkit.odfxsltrunner.Logger.ERROR));
		return working.resolve("content.xhtml").toUri();
	}
}
