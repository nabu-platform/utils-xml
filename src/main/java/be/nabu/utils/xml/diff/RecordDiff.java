package be.nabu.utils.xml.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.nabu.utils.xml.XPath;
import be.nabu.utils.xml.diff.Change.Type;

public class RecordDiff implements XMLDiffAlgorithm {

	private List<DiffKey> keys;
	
	private List<Node> matchedNodes;
	
	private List<Comparison> matches;
	
	public RecordDiff(List<DiffKey> keys) {
		this.keys = keys;
	}
	
	@Override
	public List<Change> diff(Document source, Document target) {
		// keeps track of nodes that were already matched
		matchedNodes = new ArrayList<Node>();
		// keeps track of the successful matches (necessary for patch generation)
		matches = new ArrayList<Comparison>();
		return diffKeys(source.getDocumentElement(), target.getDocumentElement(), this.keys);
	}

	private List<Change> diffKeys(Element source, Element target, List<DiffKey> keys) {
		List<Change> changes = new ArrayList<Change>();
		for (DiffKey key : keys) {
			NodeList sourceElements = new XPath(key.getPath()).query(source).asNodeList();
			NodeList targetElements = new XPath(key.getPath()).query(target).asNodeList();

			// only calculate the keys once for each target element
			Map<Integer, Map<String, String>> cachedTargetKeys = new HashMap<Integer, Map<String, String>>();
			
			// it is possible that elements can be part of other definitions and standalone at the same time
			// for example you could have an "artist" per track on the cd
			// but also an "artist" for the entire cd (to avoid repetition)
			// the first match wins, so write the most specific definitions first, the more generic later on
			for (int i = 0; i < sourceElements.getLength(); i++) {
				Element sourceElement = (Element) sourceElements.item(i);
				if (!matchedNodes.contains(sourceElement)) {
					// mark the source element as used
					matchedNodes.add(sourceElement);
					
					Map<String, String> sourceKeys = getKeys(sourceElement, key);
					boolean foundMatch = false;
					for (int j = 0; j < targetElements.getLength(); j++) {
						Element targetElement = (Element) targetElements.item(j);
						if (!matchedNodes.contains(targetElement)) {
							if (!cachedTargetKeys.containsKey(j))
								cachedTargetKeys.put(j, getKeys(targetElement, key));
							Map<String, String> targetKeys = cachedTargetKeys.get(j);
							// if the keys match, we have found the record, let's match values
							if (sourceKeys.equals(targetKeys)) {
								// mark the target element as used
								matchedNodes.add(targetElement);
								// indicate that a match was found
								foundMatch = true;
								Change change = new Change(sourceElement, targetElement);
								// add the value comparisons
								change.addChildren(diffValues(sourceElement, targetElement, key));
								// add any child key comparisons
								change.addChildren(diffKeys(sourceElement, targetElement, key.getChildren()));
								if (change.size() > 0)
									changes.add(change);
								// otherwise, add it as a successful match
								else
									matches.add(new Comparison(sourceElement, targetElement));
								break;
							}
						}
					}
					if (!foundMatch) {
						Change change = new Change(sourceElement, null);
						change.setType(Type.CREATE);
						changes.add(change);
					}
				}
			}
			// target elements that were not matched need to be marked as deleted
			for (int i = 0; i < targetElements.getLength(); i++) {
				Element targetElement = (Element) targetElements.item(i);
				if (!matchedNodes.contains(targetElement)) {
					// mark as used
					matchedNodes.add(targetElement);
					// add as deleted
					Change change = new Change(null, targetElement);
					change.setType(Type.DELETE);
					changes.add(change);
				}
			}
		}
		return changes;
	}
	
	private List<Change> diffValues(Element source, Element target, DiffKey key) {
		List<Change> changes = new ArrayList<Change>();
		for (String field : key.getFields()) {
			NodeList sourceValue = new XPath(field).query(source).asNodeList();
			NodeList targetValue = new XPath(field).query(target).asNodeList();
			
			if (sourceValue.getLength() > 1 || targetValue.getLength() > 1)
				throw new RuntimeException("The field " + field + " returns multiple matches, this is not allowed");
			
			// it does not exist in either
			if (sourceValue.getLength() == 0 && targetValue.getLength() == 0)
				continue;
			
			// it exists in both
			else if (sourceValue.getLength() == 1 && targetValue.getLength() == 1) {
				// they are equal, no need to add the change
				if (sourceValue.item(0).isEqualNode(targetValue.item(0)))
					continue;
				// otherwise we need to add it
				else {
					if (sourceValue.item(0).getNodeType() == Node.ELEMENT_NODE && targetValue.item(0).getNodeType() == Node.ELEMENT_NODE) {
						Element sourceElement = (Element) sourceValue.item(0);
						Element targetElement = (Element) targetValue.item(0);
						Change change = new Change(sourceElement, targetElement);
						change.setType(Type.UPDATE);
						changes.add(change);
					}
				}
			}
			else if (sourceValue.getLength() == 1 && sourceValue.item(0).getNodeType() == Node.ELEMENT_NODE) {
				Element sourceElement = (Element) sourceValue.item(0);
				Change change = new Change(sourceElement, null);
				change.setType(Type.CREATE);
				changes.add(change);
			}
			else if (targetValue.getLength() == 1 && targetValue.item(0).getNodeType() == Node.ELEMENT_NODE) {
				Element targetElement = (Element) targetValue.item(0);
				Change change = new Change(null, targetElement);
				change.setType(Type.DELETE);
				changes.add(change);
			}
		}
		return changes;
	}
	
	private Map<String, String> getKeys(Element source, DiffKey key) {
		Map<String, String> values = new HashMap<String, String>();
		for (String path : key.getKeys()) {
			values.put(path, new XPath(path).query(source).asString());
		}
		return values;
	}

	@Override
	public List<Comparison> getMatches() {
		return matches;
	}
	
	public static boolean returnsString(String xpath) {
		return xpath.endsWith("name()") || xpath.endsWith("text()");
	}
}
