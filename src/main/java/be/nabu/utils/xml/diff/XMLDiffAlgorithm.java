package be.nabu.utils.xml.diff;

import java.util.List;

import org.w3c.dom.Document;

public interface XMLDiffAlgorithm {
	
	/**
	 * This annotates the source document with the diff changes using attributes:
	 */
	public List<Change> diff(Document source, Document target);

	/**
	 * Once you call the diff method, you can call this method to list all the matched elements
	 */
	public List<Comparison> getMatches();
}
