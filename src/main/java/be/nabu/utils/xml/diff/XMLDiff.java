package be.nabu.utils.xml.diff;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import be.nabu.utils.xml.XMLUtils;

public class XMLDiff {
	
	private XMLDiffAlgorithm algorithm;
	private List<Change> changes;
	private List<Comparison> matches;
	
	public XMLDiff(XMLDiffAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public XMLDiff diff(Document source, Document target) {
		changes = algorithm.diff(source, target);
		matches = algorithm.getMatches();
		return this;
	}
	
	public String asString() {
		StringWriter writer = new StringWriter();
		asString(writer, 0, changes);
		return writer.toString();
	}
	
	private void asString(StringWriter writer, int depth, List<Change> changes) {
		for (Change change : changes) {
			tabs(writer, depth);
			writer.write("[" + change.getType() + "]");
			// no deeper lying changes, write info about this one
			if (change.size() == 0) {
				writer.write(": ");
				switch (change.getType()) {
					case CREATE:
						writer.write(change.getSource().getNodeName());
					break;
					case DELETE:
						writer.write(change.getTarget().getNodeName());
					break;
					case UPDATE:
						writer.write(change.getSource().getNodeName());
						if (change.getSource() instanceof Attr)
							writer.write(" from '" + change.getSource().getNodeValue() + "' to '" + change.getTarget().getNodeValue() + "'");
						else if (change.getSource() instanceof Element)
							writer.write(" from '" + change.getSource().getTextContent() + "' to '" + change.getTarget().getTextContent() + "'");
						writer.write("\n");
					break;
				}
			}
			else {
				writer.write(" " + change.getSource().getNodeName() + " {\n");
				asString(writer, depth + 1, change.getChildren());
				tabs(writer, depth);
				writer.write("}\n");
			}
		}
	}
	private void tabs(StringWriter writer, int depth) {
		for (int i = 0; i < depth; i++)
			writer.write("\t");
	}
	
	public void patchTarget() {
		patchTarget(changes);
	}
	
	private void patchTarget(List<Change> changes) {
		for (Change change : changes) {
			switch(change.getType()) {
				case UNCHANGED:
					((Element) change.getTarget()).setAttribute("_change", "child");
				break;
				case CREATE:
					if (change.getSource() instanceof Attr) {
						Attr attr = (Attr) change.getSource();
						Comparison comparison = getParentInChanges(attr, this.changes);
						if (comparison == null) {
							throw new RuntimeException("Could not find a match that can situate the new attribute in the target document");
						}
						((Element) comparison.getTarget()).setAttribute(attr.getName(), attr.getValue());
					}
					else {
						Comparison comparison = getClosestNextMatch(change.getSource());
						// insertBefore next match
						Node newNode = null;
						if (comparison != null)
							newNode = comparison.getTarget().getParentNode().insertBefore(comparison.getTarget().getOwnerDocument().importNode(change.getSource(), true), comparison.getTarget());
						else {
							comparison = getParentMatch(change.getSource());
							if (comparison != null)
								newNode = comparison.getTarget().appendChild(comparison.getTarget().getOwnerDocument().importNode(change.getSource(), true));
							else {
								comparison = getClosestPreviousMatch(change.getSource());
								if (comparison != null)
									newNode = comparison.getTarget().getParentNode().appendChild(comparison.getTarget().getOwnerDocument().importNode(change.getSource(), true));	
								else
									throw new RuntimeException("Could not find match that can situate the new element in the target document");
							}
						}
						((Element) newNode).setAttribute("_change", "insert");
					}
				break;
				case DELETE:
					((Element) change.getTarget()).setAttribute("_change", "delete");
				break;
				case UPDATE:
					((Element) change.getTarget()).setAttribute("_change", "update");
					// if the source is a text field, we can set the content as an attribute
					if (change.getSource().getAttributes().getLength() == 0 && change.getSource().getChildNodes().getLength() == 1 && change.getSource().getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
						// no linefeeds though
						if (!change.getSource().getTextContent().contains("\n"))
							((Element) change.getTarget()).setAttribute("_new_value", change.getSource().getTextContent());
					}
					// if it's an attribute, definitely
					else if (change.getSource() instanceof Attr)
						((Element) change.getTarget()).setAttribute("_new_value_" + ((Attr) change.getSource()).getName(), ((Attr) change.getSource()).getNodeValue());
				break;
			}
			patchTarget(change.getChildren());
		}
	}
	
	public String asTargetPatch() {
		StringWriter writer = new StringWriter();
		asTargetPatch(writer, changes);
		return writer.toString();
	}
	
	private Change getParentInChanges(Attr attr, List<Change> changes) {
		for (Change change : changes) {
			if (attr.getOwnerElement().equals(change.getSource())) {
				return change;
			}
			else if (attr.getOwnerElement().equals(change.getTarget())) {
				return change;
			}
			if (!change.getChildren().isEmpty()) {
				Change potential = getParentInChanges(attr, change.getChildren());
				if (potential != null) {
					return potential;
				}
			}
		}
		return null;
	}
	
	private Comparison getAttributeParent(Attr attr) {
		for (Comparison match : matches) {
			if (match.getSource() == attr.getOwnerElement()) {
				return match;
			}
		}
		return getParentInChanges(attr, changes);
	}
	
	private void asTargetPatch(StringWriter writer, List<Change> changes) {
		for (Change change : changes) {
//			System.out.println("Change " + change.getType() + " " + (change.getSource() != null ? change.getSource().getNodeName() : change.getTarget().getNodeName()));
			switch(change.getType()) {
				case CREATE:
					if (change.getSource().getNodeType() == Node.ATTRIBUTE_NODE) {
						Attr attr = (Attr) change.getSource();
						Comparison comparison = getAttributeParent(attr);
						if (comparison == null)
							throw new RuntimeException("Could not find match that can situate the new attribute in the target document");
						writer.write("s:" + getPathToRoot((Element) comparison.getTarget()) + "/@" + attr.getName() + "\n");
						writer.write("> " + attr.getNodeValue() + "\n");
					}
					else {
						Comparison comparison = getClosestNextMatch(change.getSource());
						if (comparison != null)
							writer.write("i:" + getPathToRoot((Element) comparison.getTarget()) + "\n");
						else {
							comparison = getParentMatch(change.getSource());
							if (comparison != null)
								writer.write("a:" + getPathToRoot((Element) comparison.getTarget()) + "\n");	
							else {
								comparison = getClosestPreviousMatch(change.getSource());
								if (comparison != null)
									writer.write("a:" + getPathToRoot((Element) comparison.getTarget().getParentNode()) + "\n");	
								else
									throw new RuntimeException("Could not find match that can situate the new element in the target document for: " + (change.getSource() instanceof Attr ? getPathToRoot((Attr) change.getSource()) : getPathToRoot((Element) change.getSource())));
							}
						}
						try {
							writer.write(">> " + XMLUtils.toString(change.getSource(), true).replaceAll("\n", "\n>> ") + "\n");
						}
						catch (TransformerException e) {
							throw new RuntimeException(e);
						}
					}
				break;
				case DELETE:
					writer.write("d:" + (change.getTarget() instanceof Attr ? getPathToRoot((Attr) change.getTarget()) : getPathToRoot((Element) change.getTarget())) + "\n");
					if (change.getTarget() instanceof Attr || (change.getTarget().getAttributes().getLength() == 0 && change.getTarget().getChildNodes().getLength() == 1 && change.getTarget().getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
						writer.write("< " + change.getTarget().getTextContent().replaceAll("\n", "\n< ") + "\n");
					else {
						try {
							writer.write("<< " + XMLUtils.toString(change.getTarget(), true).replaceAll("\n", "\n<< ") + "\n");
						}
						catch (TransformerException e) {
							throw new RuntimeException(e);
						}
					}
				break;
				case UPDATE:
					writer.write("u:" + (change.getTarget() instanceof Attr ? getPathToRoot((Attr) change.getTarget()) : getPathToRoot((Element) change.getTarget())) + "\n");
					// it is a text node
					if (change.getTarget() instanceof Attr || (change.getTarget().getAttributes().getLength() == 0 && change.getTarget().getChildNodes().getLength() == 1 && change.getTarget().getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
						writer.write("< " + change.getTarget().getTextContent().replaceAll("\n", "\n< ") + "\n");
					else {
						try {
							writer.write("<< " + XMLUtils.toString(change.getTarget(), true).replaceAll("\n", "\n<< ") + "\n");
						}
						catch (TransformerException e) {
							throw new RuntimeException(e);
						}
					}
					if (change.getTarget() instanceof Attr || (change.getSource().getAttributes().getLength() == 0 && change.getSource().getChildNodes().getLength() == 1 && change.getSource().getChildNodes().item(0).getNodeType() == Node.TEXT_NODE))
						writer.write("> " + change.getSource().getTextContent().replaceAll("\n", "\n< ") + "\n");
					else {
						try {
							writer.write(">> " + XMLUtils.toString(change.getSource(), true).replaceAll("\n", "\n>> ") + "\n");
						}
						catch (TransformerException e) {
							throw new RuntimeException(e);
						}						
					}
				break;
			}
			asTargetPatch(writer, change.getChildren());
		}
	}
	
	public String getPathToRoot(Element element) {
		String path = "";
		// if you are not the root element, check parent path 
		if (!(element.getParentNode() instanceof Document)) {
			path = getPathToRoot((Element) element.getParentNode());
			// we need to figure out the index
			Integer index = getIndex(element);
			if (index == null) {
				throw new RuntimeException("Could not resolve element " + element.getNodeName());	
			}
			// you don't "always" need an index but without a definition it is hard to be sure when it's a list or not
			else {
				path += "/" + element.getNodeName() + "[" + index + "]";
			}
		}
		else
			path = "/" + element.getNodeName();
		return path;
	}
	
	private Integer getIndex(Element element) {
		Integer index = null;
		for (int i = 0; i < element.getParentNode().getChildNodes().getLength(); i++) {
			if (element.getParentNode().getChildNodes().item(i).getNodeName().equals(element.getNodeName())) {
				if (index == null) {
					index = 0;
				}
				else {
					index++;
				}
			}
		}
		return index;
	}
	
	public String getPathToRoot(Attr attr) {
		return getPathToRoot(attr.getOwnerElement()) + "/@" + attr.getNodeName();
	}

	/**
	 * Check if there is a close next match so we can insert before
	 * @param source
	 * @param matches
	 * @return
	 */
	private Comparison getClosestNextMatch(Node source) {
		Node next = source;
		while((next = next.getNextSibling()) != null) {
			if (next.getNodeType() == Node.ELEMENT_NODE) {
				// check if there is a match for it
				for (Comparison match : matches) {
					if (next == match.getSource())
						return match;
				}
				// otherwise check if there is a change which is not a delete/insert
				for (Change change : changes) {
					if (change.getType() != Change.Type.CREATE && change.getType() != Change.Type.DELETE && change.getSource() == next)
						return change;
				}
			}
		}
		return null;
	}

	/**
	 * Check if a PREVIOUS sibling was matched. in some cases the parent is not matched, but siblings are, in this case we need to add it to the parent of the closest previous sibling
	 * @param source
	 * @return
	 */
	private Comparison getClosestPreviousMatch(Node source) {
		Node previous = source;
		while((previous = previous.getPreviousSibling()) != null) {
			// check if there is a match for it
			for (Comparison match : matches) {
				if (previous == match.getSource())
					return match;
			}
			// otherwise check if there is a change which is not a delete/insert
			Comparison matchingChange = getMatchingChange(changes, previous);
			if (matchingChange != null)
				return matchingChange;
		}
		return null;
	}

	private Comparison getParentMatch(Node source) {
		// check if a parent was matched, if so we need to simply append it
		for (Comparison match : matches) {
			if (source.getParentNode() == match.getSource())
				return match;
		}
		return getMatchingChange(changes, source.getParentNode());
	}
	
	private Comparison getMatchingChange(List<Change> changes, Node target) {
		for (Change change : changes) {
			if (change.getType() != Change.Type.CREATE && change.getType() != Change.Type.DELETE && change.getSource() == target)
				return change;
			Comparison matchingChange = getMatchingChange(change.getChildren(), target);
			if (matchingChange != null)
				return matchingChange;
		}
		return null;
	}
	
	public void filter(String...blacklistPaths) {
		if (changes != null) {
			List<String> paths = Arrays.asList(blacklistPaths);
			filter(changes, paths);
		}
	}

	private void filter(List<Change> changes, List<String> paths) {
		Iterator<Change> iterator = changes.iterator();
		while (iterator.hasNext()) {
			Change change = iterator.next();
			if (change.getSource() != null && shouldFilter(change.getSource(), paths)) {
				iterator.remove();
			}
			else if (change.getTarget() != null && shouldFilter(change.getTarget(), paths)) {
				iterator.remove();
			}
			if (change.getChildren() != null) {
				filter(change.getChildren(), paths);
			}
		}
	}
	
	private boolean shouldFilter(Node node, List<String> paths) {
		String path = node instanceof Attr
			? getPathToRoot((Attr) node)
			: getPathToRoot((Element) node);
		String normalizedPath = path.replaceAll("\\[[^\\]]+\\]", "");
		String pathNoPrefix = path.replaceAll("[a-zA-Z0-9]+:", "");
		String normalizedPathNoPrefix = normalizedPath.replaceAll("[a-zA-Z0-9]+:", "");
		return paths.contains(path) || paths.contains(normalizedPath) || paths.contains(pathNoPrefix) || paths.contains(normalizedPathNoPrefix);
	}
}
