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
package cc.fooledit.control;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface AutoCompleteProvider{
	Stream<AutoCompleteHint> checkForHints(String text,int pos);
	static AutoCompleteProvider createSimple(Collection<AutoCompleteHint> hints){
		return new SimpleAutoCompleteProvider(hints);
	}
}
class SimpleAutoCompleteProvider implements AutoCompleteProvider{
	private final Collection<AutoCompleteHint> hints;
	public SimpleAutoCompleteProvider(Collection<AutoCompleteHint> hints){
		this.hints=hints;
	}
	@Override
	public Stream<AutoCompleteHint> checkForHints(String text,int pos){
		String prefix=text.substring(0,pos);
		return hints.stream().filter((hint)->hint.getInputText().startsWith(prefix)&&hint.getInputText().length()>pos).
				map((hint)->AutoCompleteHint.modify(hint.getInputText().substring(pos),hint));
	}
}