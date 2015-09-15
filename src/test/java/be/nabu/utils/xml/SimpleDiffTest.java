package be.nabu.utils.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import be.nabu.utils.xml.XMLUtils;
import be.nabu.utils.xml.diff.SimpleDiff;
import be.nabu.utils.xml.diff.XMLDiff;

public class SimpleDiffTest extends TestCase {
	
	public void testSimpleDiff() throws IOException, SAXException, ParserConfigurationException, TransformerException {
		XMLDiff diff = new XMLDiff(new SimpleDiff());
		Document actual, expected;
		InputStream actualInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("actual.xml");
		try {
			actual = XMLUtils.toDocument(actualInput, true);
		}
		finally {
			actualInput.close();
		}
		InputStream expectedInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("expected.xml");
		try {
			expected = XMLUtils.toDocument(expectedInput, true);
		}
		finally {
			expectedInput.close();
		}
		String expectedPatch = "u:/soap:Envelope/soap:Body[0]/m:GetStockPriceResponse[0]/m:Price[0]\n" + 
				"< 34.5\n" + 
				"> 55.5\n" + 
				"s:/soap:Envelope/soap:Body[0]/m:GetStockPriceResponse[0]/@a\n" + 
				"> b";
		XMLDiff result = diff.diff(actual, expected);
		assertEquals(expectedPatch, result.asTargetPatch().replaceAll("\r", "").trim());
		result.patchTarget();
		String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><soap:Envelope xmlns:soap=\"http://www.w3.org/2001/12/soap-envelope\" soap:encodingStyle=\"http://www.w3.org/2001/12/soap-encoding\">\n" + 
				"	<soap:Body xmlns:m=\"http://www.example.org/stock\" _change=\"child\">\n" + 
				"		<m:GetStockPriceResponse _change=\"child\" a=\"b\">\n" + 
				"			<m:Price _change=\"update\" _new_value=\"55.5\">34.5</m:Price>\n" + 
				"		</m:GetStockPriceResponse>\n" + 
				"	</soap:Body>\n" + 
				"</soap:Envelope>";
		assertEquals(expectedResult, XMLUtils.toString(expected).replaceAll("\r",  "").trim());
	}
}
