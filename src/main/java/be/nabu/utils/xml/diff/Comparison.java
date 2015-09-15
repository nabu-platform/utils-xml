package be.nabu.utils.xml.diff;

import org.w3c.dom.Node;

public class Comparison {
	private Node source;
	private Node target;

	public Comparison(Node source, Node target) {
		this.source = source;
		this.target = target;
	}
	
	public Node getSource() {
		return source;
	}
	public void setSource(Node source) {
		this.source = source;
	}
	public Node getTarget() {
		return target;
	}
	public void setTarget(Node target) {
		this.target = target;
	}
}
