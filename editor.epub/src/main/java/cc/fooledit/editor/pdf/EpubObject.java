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
package cc.fooledit.editor.pdf;
import cc.fooledit.core.*;
import nl.siegmann.epublib.domain.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EpubObject implements DataObject<EpubObject>{
	private final Book doc;
	public EpubObject(Book doc){
		this.doc=doc;
	}
	@Override
	public DataObjectType<EpubObject> getDataObjectType(){
		return EpubObjectType.INSTANCE;
	}
	public Book getDocument(){
		return doc;
	}
}
