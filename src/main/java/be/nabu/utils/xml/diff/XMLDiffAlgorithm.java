/*
* Copyright (C) 2015 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.utils.xml.diff;

import java.util.List;

import org.w3c.dom.Document;

public interface XMLDiffAlgorithm {
	
	/**
	 * This annotates the source document with the diff changes using attributes:
	 */
	public List<Change> diff(Document source, Document target);

	/**
	 * Once you call the diff method, you can call this method to list all the matched elements
	 */
	public List<Comparison> getMatches();
}
