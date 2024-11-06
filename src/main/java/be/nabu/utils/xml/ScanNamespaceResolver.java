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

package be.nabu.utils.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScanNamespaceResolver extends BaseNamespaceResolver {

	@Override
	public String getNamespaceURI(String prefix) {
		String namespace = super.getNamespaceURI(prefix);
		// perform a scan of the node
		if (namespace == null) {
			scan(getNode());
			namespace = super.getNamespaceURI(prefix);
		}
		return namespace;
	}
	
	private void scan(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node child = childNodes.item(i);
			String prefix = child.getPrefix();
			String namespace = child.getNamespaceURI();
			if (prefix != null) {
				registerPrefix(prefix, namespace);
			}
			scan(child);
		}
	}
}
