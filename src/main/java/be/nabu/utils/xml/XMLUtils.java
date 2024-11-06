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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import be.nabu.libs.validator.api.Validation;
import be.nabu.libs.validator.api.ValidationMessage;
import be.nabu.libs.validator.api.ValidationMessage.Severity;

public class XMLUtils {
	/**
	 * Use a proper null value in case it is missing
	 */
	public static String getAttribute(Node node, String attribute) {
		NamedNodeMap map = node.getAttributes();
		if (map.getNamedItem(attribute) == null)
			return null;
		if (map.getNamedItem(attribute).getNodeValue() == null || map.getNamedItem(attribute).getNodeValue().equals(""))
			return null;
		return map.getNamedItem(attribute).getNodeValue();
	}
	
	public static boolean hasAttribute(Node node, String attribute) {
		return getAttribute(node, attribute) != null;
	}
	
	public static Integer getIntegerAttribute(Node node, String attribute) {
		String value = getAttribute(node, attribute);
		return value == null ? null : new Integer(value);
	}
	
	public static XPath query(Node node, String xpath) {
		BaseNamespaceResolver resolver = new BaseNamespaceResolver();
		resolver.setScanRecursively(true);
		return new XPath(xpath).setNamespaceContext(resolver).query(node);
	}
		
	public static Document toDocument(InputStream xml, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// no DTD
		factory.setValidating(false);
		factory.setNamespaceAware(namespaceAware);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		// allow no external access, as defined http://docs.oracle.com/javase/7/docs/api/javax/xml/XMLConstants.html#FEATURE_SECURE_PROCESSING an empty string means no protocols are allowed
		try {
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		}
		catch (Exception e) {
			// not supported in later versions..............
		}
		return factory.newDocumentBuilder().parse(xml);
	}
	
	public static Document toDocument(String xml, Charset encoding, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {
		InputStream input = new ByteArrayInputStream(xml.getBytes(encoding));
		return toDocument(input, namespaceAware);
	}
	
	public static Document newDocument(boolean namespaceAware) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(namespaceAware);
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			return factory.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Schema toSchema(String baseURI, InputStream...schemas) throws SAXException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		factory.setResourceResolver(new ClassPathResourceResolver(baseURI));
		Source [] constraints = new Source[schemas.length];
		for (int i = 0; i < schemas.length; i++)
			constraints[i] = new StreamSource(schemas[i]);
		return factory.newSchema(constraints);
	}
	
	public static List<Validation<?>> validate(Node node, Schema schema) throws IOException {
		final List<Validation<?>> validations = new ArrayList<Validation<?>>();
		try {
			validate(node, schema, new ErrorHandler() {
				@Override
				public void error(SAXParseException e) throws SAXException {
					validations.add(new ValidationMessage(Severity.ERROR, e.getMessage()));
				}
				@Override
				public void fatalError(SAXParseException e) throws SAXException {
					validations.add(new ValidationMessage(Severity.CRITICAL, e.getMessage()));
				}
				@Override
				public void warning(SAXParseException e) throws SAXException {
					validations.add(new ValidationMessage(Severity.WARNING, e.getMessage()));
				}
			});
		}
		catch (SAXException e) {
			validations.add(new ValidationMessage(Severity.CRITICAL, e.getMessage()));
		}
		return validations;
	}
	
	public static DOMResult validate(Node node, Schema schema, ErrorHandler errorHandler) throws SAXException, IOException {
		Validator validator = schema.newValidator();
		DOMResult result = new DOMResult();
		validator.validate(new DOMSource(node), result);
		return result;
	}
	
	public static Schema loadSchemaFromClassPath(String...xsds) throws IOException, SAXException {
		InputStream [] inputs = new InputStream[xsds.length];
		try {
			for (int i = 0; i < xsds.length; i++) {
				System.out.println("Found schema " + Thread.currentThread().getContextClassLoader().getResource(xsds[i]));
				inputs[i] = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsds[i]);
				if (inputs[i] == null)
					throw new IOException("Could not find the resource: " + xsds[i]);
			}
			return XMLUtils.toSchema(null, inputs);
		}
		finally {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null)
					inputs[i].close();
			}
		}
	}
	
	public static Document loadDocumentFromClassPath(String xml, boolean namespaceAware) throws IOException, SAXException, ParserConfigurationException {
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(xml);
		try {
			return toDocument(input, namespaceAware);
		}
		finally {
			input.close();
		}
	}
	
	public static Document toDocument(URL url, boolean namespaceAware) throws IOException, SAXException, ParserConfigurationException {
		InputStream input = url.openStream();
		try {
			return toDocument(input, namespaceAware);
		}
		finally {
			input.close();
		}
	}
	
	public static String toString(Node node) throws TransformerException {
		return toString(node, false);
	}
	
	public static String toString(Node node, boolean omitXMLDeclaration) throws TransformerException {
		return toString(node, omitXMLDeclaration, false);
	}
	
	public static String toString(Node node, boolean omitXMLDeclaration, boolean prettyPrint) throws TransformerException {
        StringWriter string = new StringWriter();
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        if (omitXMLDeclaration) {
        	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        if (prettyPrint) {
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        transformer.transform(new DOMSource(node), new StreamResult(string));
        return string.toString();
	}
	
	public static Transformer newTransformer(Source xsl, Map<String, Object> parameters) throws TransformerConfigurationException {
		TransformerFactory factory = TransformerFactory.newInstance();
		// allow includes through the classpath
		factory.setURIResolver(new ClassPathURIResolver());
		// create a new transformer
		Transformer transformer = factory.newTransformer(xsl);
		// set parameters if necessary
		if (parameters != null) {
			for(String key : parameters.keySet())
				transformer.setParameter(key, parameters.get(key));
		}
		transformer.setURIResolver(new ClassPathURIResolver());
		return transformer;
	}
	// in a usecase we had a tag <configuration/> it was first correctly parsed as null, then an empty array was added so it became {configuration:{property:[]}}. we marshalled it _with_ pretty print which ended up in <configuration>\n</configuration> (see xml marshaller for details). second parse it failed because it was no longer an empty string....so we trim! this is very dynamic code anyway
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, ?> toMap(Element element) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			// we handle xsi:nil in a dedicated fashion
			if (((Attr) attributes.item(i)).getName().startsWith("xmlns:") || ((Attr) attributes.item(i)).getName().equals("xsi:nil")) {
				continue;
			}
			map.put("@" + ((Attr) attributes.item(i)).getName(), attributes.item(i).getTextContent());
		}
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) element.getChildNodes().item(i);
				String name = child.getNodeName().replaceAll("^.*:", "");
				if (map.containsKey(name)) {
					if (!(map.get(name) instanceof List)) {
						List<Object> objects = new ArrayList<Object>();
						objects.add(map.get(name));
						map.put(name, objects);
					}
					if (isText(child)) {
						if (isContainer(child)) {
							Map childMap = toMap(child);
							if (isNil(child)) {
								// explicitly set it to null
								childMap.put("$value", null);
							}
							else { 
								String textContent = child.getTextContent();
								childMap.put("$value", textContent == null || textContent.trim().isEmpty() ? null : textContent);
							}
							((List<Object>) map.get(name)).add(childMap);
						}
						else if (isNil(child)) {
							// explicitly set it to null
							((List<Object>) map.get(name)).add(null);
						}
						else {
							String textContent = child.getTextContent();
							((List<Object>) map.get(name)).add(textContent == null || textContent.trim().isEmpty() ? null : textContent);
						}
					}
					else {
						((List<Object>) map.get(name)).add(toMap(child));
					}
				}
				else {
					if (isText(child)) {
						if (isContainer(child)) {
							Map childMap = toMap(child);
							if (isNil(child)) {
								// explicitly set it to null
								childMap.put("$value", null);
							}
							else { 
								String textContent = child.getTextContent();
								childMap.put("$value", textContent == null || textContent.trim().isEmpty() ? null : textContent);
							}
							map.put(name, childMap);
						}
						else if (isNil(child)) {
							// explicitly set it to null
							map.put(name, null);
						}
						else {
							String textContent = child.getTextContent();
							map.put(name, textContent == null || textContent.trim().isEmpty() ? null : textContent);
						}
					}
					else {
						map.put(name, toMap(child));
					}
				}
			}
		}
		return map;
	}
	
	// some attributes should be ignored for parsing reasons, e.g.:
	// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:nil="true"
	private static boolean hasSignificantAttribute(Element element) {
		boolean hasSignificantAttribute = false;
		NamedNodeMap attributes = element.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			if (((Attr) attributes.item(i)).getName().startsWith("xmlns:")) {
				continue;
			}
			if (((Attr) attributes.item(i)).getName().startsWith("xsi:")) {
				continue;
			}
			hasSignificantAttribute = true;
			break;
		}
		return hasSignificantAttribute;
	}
	
	private static boolean isNil(Element element) {
		String attribute = element.getAttribute("xsi:nil");
		return attribute != null && attribute.equalsIgnoreCase("true");
	}
	
	private static boolean isContainer(Element element) {
		return !isText(element) || hasSignificantAttribute(element);
	}
	
	private static boolean isText(Element element) {
		for (int i = 0; i < element.getChildNodes().getLength(); i++) {
			if (element.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
				return false;
			}
		}
		return true;
	}
}