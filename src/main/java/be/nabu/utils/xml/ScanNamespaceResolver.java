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
