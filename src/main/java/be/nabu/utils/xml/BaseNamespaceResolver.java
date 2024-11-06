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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.Node;

/**
 * Based on http://www.ibm.com/developerworks/xml/library/x-nmspccontext/index.html?ca=drs-
 * Notes:
 *	* The lookup for namespaces or prefixes is done in the ancestors of the used node
 * 	* The lookup is called when XPath evaluates, so it consumes some extra time.
 */
public class BaseNamespaceResolver implements NodeNamespaceContext {

	/**
	 * Allows users to register fixed prefixes
	 */
	private Map<String, String> fixedPrefixes = new HashMap<String, String>();
	
	/**
	 * Used to "cache" resulting prefixes
	 */
	private Map<String, String> prefixes = new HashMap<String, String>();
	
	/**
	 * A reverse map from namespace to prefix
	 */
	private Map<String, String> namespaces = new HashMap<String, String>();
	
	/**
	 * Whether or not to cache results
	 */
	private boolean cacheResults = true;
	
	/**
	 * Whether or not to scan recursively to find the prefix (assumes the document is not too big and the prefix is unique!)
	 */
	private boolean scanRecursively = false;
	
	// all lookups are delegated to this node
	private Node node = null;
	
	@Override
	public String getNamespaceURI(String prefix) {
		if ("&default".equalsIgnoreCase(prefix)) {
			return node.lookupNamespaceURI(null);
		}
		// registered
		if (fixedPrefixes.containsKey(prefix))
			return fixedPrefixes.get(prefix);
		// cached
		else if (prefixes.containsKey(prefix))
			return prefixes.get(prefix);
		
		String namespace = prefix.equals(XMLConstants.DEFAULT_NS_PREFIX) 
			? node.lookupNamespaceURI(null)
			: node.lookupNamespaceURI(prefix);
			
		if (namespace == null && scanRecursively) {
			return getNamespaceURIRecursively(node, prefix);
		}
			
		if (cacheResults && namespace != null)
			prefixes.put(prefix, namespace);
		
		return namespace;
	}
	
	private String getNamespaceURIRecursively(Node node, String prefix) {
		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
			String lookupNamespaceURI = node.getChildNodes().item(i).lookupNamespaceURI(prefix);
			if (lookupNamespaceURI != null) {
				return lookupNamespaceURI;
			}
			else {
				lookupNamespaceURI = getNamespaceURIRecursively(node.getChildNodes().item(i), prefix);
				if (lookupNamespaceURI != null) {
					return lookupNamespaceURI;
				}
			}
		}
		return null;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		// cached
		if (namespaces.containsKey(namespaceURI))
			return namespaces.get(namespaceURI);
		
		String prefix = node.lookupPrefix(namespaceURI);
		
		if (cacheResults)
			namespaces.put(namespaceURI, prefix);
		
		return prefix;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		// one namespaceURI can be bound to multiple prefixes, this returns an iterator over those prefixes
		// the Node class however does not allow lookup of all the prefixes, so this was not implemented
		return null;
	}
	
	public void setPrefixes(Map<String, String> fixedPrefixes) {
		this.fixedPrefixes = fixedPrefixes;
	}
	
	public void registerPrefix(String prefix, String namespace) {
		fixedPrefixes.put(prefix, namespace);
	}

	@Override
	public void setNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	public boolean isScanRecursively() {
		return scanRecursively;
	}

	public void setScanRecursively(boolean scanRecursively) {
		this.scanRecursively = scanRecursively;
	}
}