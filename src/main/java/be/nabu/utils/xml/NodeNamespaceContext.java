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

import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Node;

/**
 * If your namespace context resolver should be "aware" of the context (the node) it is resolving in, add this interface
 * The xpath class will feed it the node it is working on before execution
 * 
 * @author alex
 *
 */
public interface NodeNamespaceContext extends NamespaceContext {
	public void setNode(Node node);
}
