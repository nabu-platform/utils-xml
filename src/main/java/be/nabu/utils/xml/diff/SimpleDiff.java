package be.nabu.utils.xml.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.nabu.utils.xml.diff.Change.Type;

public class SimpleDiff implements XMLDiffAlgorithm {

	private List<Comparison> comparisons;
	
	@Override
	public List<Change> diff(Document source, Document target) {
		comparisons = new ArrayList<Comparison>();
		return diff(source.getDocumentElement(), target.getDocumentElement());
	}
	
	private List<Change> diff(Element source, Element target) {
		List<Change> changes = new ArrayList<Change>();
		List<Element> sourceChildren = getChildElements(source);
		List<Element> targetChildren = getChildElements(target);

		// for speed sake: if there are not the same amount of children, just stop
		if (sourceChildren.size() != targetChildren.size()) {
			changes.add(new Change(source, target, Type.UPDATE));
		}
		// both are text elements at best, compare like that
		else if (sourceChildren.size() == 0 && targetChildren.size() == 0) {
			if (!source.getTextContent().trim().equals(target.getTextContent().trim())) {
				changes.add(new Change(source, target, Type.UPDATE));
			}
			else {
				comparisons.add(new Comparison(source, target));
			}
		}
		else {
			for (int i = 0; i < sourceChildren.size(); i++) {
				Element sourceElement = sourceChildren.get(i);
				Element targetElement = targetChildren.get(i);
				// same namespace
				boolean sameNamespace = (sourceElement.getNamespaceURI() == null && targetElement.getNamespaceURI() == null) 
					|| (sourceElement.getNamespaceURI() != null && sourceElement.getNamespaceURI().equals(targetElement.getNamespaceURI()));
				boolean sameName;
				if (sourceElement.getLocalName() == null || targetElement.getLocalName() == null) {
					sameName = (sourceElement.getNodeName() == null && targetElement.getNodeName() == null) 
						|| (sourceElement.getNodeName() != null && sourceElement.getNodeName().equals(targetElement.getNodeName()));
				}
				else {
					sameName = (sourceElement.getLocalName() == null && targetElement.getLocalName() == null) 
						|| (sourceElement.getLocalName() != null && sourceElement.getLocalName().equals(targetElement.getLocalName()));
				}
				// it's the same element
				if (sameNamespace && sameName) {
					Change change = new Change(sourceElement, targetElement);
					change.addChildren(diff(sourceElement, targetElement));
					// also check the attributes (if any)
					Map<String, String> sourceAttributes = getAttributes(sourceElement);
					Map<String, String> targetAttributes = getAttributes(targetElement);
					// forward check
					for (String key : sourceAttributes.keySet()) {
						if (!targetAttributes.containsKey(key)) {
							change.addChild(new Change(sourceElement.getAttributeNode(key), null, Type.CREATE));
						}
						else if (!sourceAttributes.get(key).equals(targetAttributes.get(key))) {
							change.addChild(new Change(sourceElement.getAttributeNode(key), targetElement.getAttributeNode(key), Type.UPDATE));
						}
					}
					// reverse check
					for (String key : targetAttributes.keySet()) {
						if (!sourceAttributes.containsKey(key)) {
							change.addChild(new Change(null, targetElement.getAttributeNode(key), Type.DELETE));
						}
						else if (!targetAttributes.get(key).equals(sourceAttributes.get(key))) {
							change.addChild(new Change(sourceElement.getAttributeNode(key), targetElement.getAttributeNode(key), Type.UPDATE));
						}
					}
					if (!change.getChildren().isEmpty()) {
						changes.add(change);
					}
					else {
						comparisons.add(new Comparison(source, target));
					}
				}
				else {
					changes.add(new Change(null, targetElement, Type.DELETE));
					changes.add(new Change(sourceElement, null, Type.CREATE));
				}
			}
		}
		return changes;
	}
	
	private List<Element> getChildElements(Element element) {
		List<Element> children = new ArrayList<Element>();
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				children.add((Element) childNodes.item(i));
			}
		}
		return children;
	}
	
	private Map<String, String> getAttributes(Element element) {
		Map<String, String> attributes = new HashMap<String, String>();
		NamedNodeMap nodeMap = element.getAttributes();
		for (int i = 0; i < nodeMap.getLength(); i++) {
			Node child = nodeMap.item(i);
			attributes.put(child.getNodeName(), child.getTextContent());
		}
		return attributes;
	}

	@Override
	public List<Comparison> getMatches() {
		return comparisons;
	}

}
