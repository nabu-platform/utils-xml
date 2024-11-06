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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Change extends Comparison {
	
	public enum Type {
		UPDATE,
		DELETE,
		CREATE,
		UNCHANGED
	}
	
	private Type type;
	
	/**
	 * Nested changes
	 */
	private List<Change> children = new ArrayList<Change>();

	public Change(Node source, Node target) {
		super(source, target);
	}
	public Change(Node source, Node target, Type type) {
		super(source, target);
		this.type = type;
	}
	
	public List<Change> getChildren() {
		return children;
	}
	public void addChild(Change comparison) {
		children.add(comparison);
	}
	public void setChildren(List<Change> children) {
		this.children = children;
	}
	public void addChildren(List<Change> children) {
		this.children.addAll(children);
	}
	public Type getType() {
		return type == null ? Type.UNCHANGED : type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public int size() {
		return this.children.size();
	}
	
	@Override
	public boolean equals(Object object) {
		return object instanceof Change &&
			((Change) object).getSource() == getSource() &&
			((Change) object).getTarget() == getTarget();
	}
}
