package be.nabu.utils.xml.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A diff key identifies a record, it consists of:
 * - a path to the root of the record
 * - a series of paths which indicate a (optionally complex) key
 * - a series of paths which indicate the values of said record
 * - a series of nested records
 *  
 * @author alex
 *
 */
@XmlRootElement(name="record")
public class DiffKey {
	/**
	 * The xpath to the element, if it is a root diff key, this is from the root of the document, for children keys this is from the root of the parent
	 */
	private String path;
	
	private List<DiffKey> children = new ArrayList<DiffKey>();
	private List<DiffKeyReference> childReferences = new ArrayList<DiffKeyReference>();
	
	@XmlElement(name="reference")
	public List<DiffKeyReference> getChildReferences() {
		return childReferences;
	}

	public void setChildReferences(List<DiffKeyReference> childReferences) {
		this.childReferences = childReferences;
	}

	public DiffKey() {
		
	}
	
	public DiffKey(String path, String [] keys, String [] fields, DiffKey...children) {
		this.path = path;
		this.keys = Arrays.asList(keys);
		this.fields = Arrays.asList(fields);
		this.children = Arrays.asList(children);
	}
	
	/**
	 * Relative paths (relative to the key element itself) that determine the "key" for this element, use XPath
	 * Each path must result in a string value
	 */
	private List<String> keys = new ArrayList<String>();
	
	/**
	 * Relative paths to determine additional "fields" for the element
	 * Each path must result in a string value
	 */
	private List<String> fields = new ArrayList<String>();

	public String getPath() {
		return path;
	}
	
	@XmlElement(name="key")
	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	@XmlElement(name="field")
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	@XmlElement(name="record")
	public List<DiffKey> getChildren() {
		return children;
	}

	@XmlAttribute(name="path")
	public void setPath(String path) {
		this.path = path;
	}

	public void setChildren(List<DiffKey> children) {
		this.children = children;
	}
	
	@Override
	public String toString() {
		return "KEY[" + path + "]";
	}
}
