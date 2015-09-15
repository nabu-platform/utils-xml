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
