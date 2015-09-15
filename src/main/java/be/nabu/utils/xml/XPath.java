package be.nabu.utils.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * You can either query with a resulttype qname of string or you can query for a nodelist and dump the content to string
 * This takes the latter approach for historic reasons but in the future the other approach may be implemented
 * The only difference is where the exception is thrown.
 */
public class XPath {
	
	private String query;
	
	private Object result;
	
	private NamespaceContext context;
	
	public XPath(String query) {
		this.query = query;
	}
	
	public XPath(String query, NamespaceContext context) {
		this.query = query;
		this.context = context;
	}
	
	public XPath query(Node node) {
		return query(node, XPathConstants.NODESET);
	}
	public XPath query(Node node, QName returnType) {
		XPathFactory factory = XPathFactory.newInstance();
		javax.xml.xpath.XPath xpath = factory.newXPath();
		NamespaceContext context = this.context == null ? new BaseNamespaceResolver() : this.context;
		// if it requires the node to evaluate, add the current node
		if (context instanceof NodeNamespaceContext)
			((NodeNamespaceContext) context).setNode(node);
		xpath.setNamespaceContext(context);
		XPathExpression expression;
		try {
			expression = xpath.compile(query);
			result = expression.evaluate(node, returnType);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
	
	public Node asNode() {
		if (size() == 0)
			return null;
		else if (result instanceof Node)
			return (Node) result;
		else if (result instanceof NodeList)
			return ((NodeList) result).item(0);
		else
			throw new ClassCastException("Could not cast " + result.getClass().getName() + " to org.w3c.dom.Node");
	}
	
	public Element asElement() {
		if (size() == 0)
			return null;
		else if (result instanceof Node)
			return (Element) result;
		else if (result instanceof NodeList)
			return (Element) ((NodeList) result).item(0);
		else
			throw new ClassCastException("Could not cast " + result.getClass().getName() + " to org.w3c.dom.Element");
	}
	
	public String asString() {
		if (size() == 0)
			return null;
		else if (result instanceof String)
			return (String) result;
		else if (result instanceof Node)
			return ((Node) result).getTextContent();
		else if (result instanceof NodeList)
			return ((NodeList) result).item(0).getTextContent();
		else
			throw new ClassCastException("Could not cast " + result.getClass().getName() + " to java.lang.String");
	}
	
	public String asString(String defaultValue) {
		String returnValue = asString();
		return returnValue == null ? defaultValue : returnValue;
	}
	
	public NodeList asNodeList() {
		if (result == null)
			return null;
		else if (result instanceof NodeList)
			return (NodeList) result;
		else
			throw new ClassCastException("Could not cast " + result.getClass().getName() + " to org.w3c.dom.NodeList");
	}
	
	public List<Element> asElementList() {
		List<Element> elements = new ArrayList<Element>();
		NodeList list = asNodeList();
		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					elements.add((Element) list.item(i));
				}
			}
		}
		return elements;
	}
	
	public int size() {
		if (result == null)
			return 0;
		else if (result instanceof NodeList)
			return ((NodeList) result).getLength();
		else
			return 1;
	}
	
	public XPath setNamespaceContext(NamespaceContext context) {
		this.context = context;
		return this;
	}
}
