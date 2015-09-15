package be.nabu.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class ClassPathURIResolver implements URIResolver {

	public Source resolve(String arg0, String arg1) throws TransformerException {
		try {
			URI uri = new URI(encodeURI(arg0));
			InputStream input = null;
			if (uri.getScheme().equals(ClassPathResourceResolver.SCHEME))
				input = Thread.currentThread().getContextClassLoader().getResourceAsStream(arg0);
			else
				input = uri.toURL().openStream();
			// do nothing if the input is not found, returning null may start the default search mode for the xsl engine
			return input == null ? null : new StreamSource(input);
		}
		catch (URISyntaxException e) {
			throw new TransformerException(e);
		}
		catch (MalformedURLException e) {
			throw new TransformerException(e);
		}
		catch (IOException e) {
			throw new TransformerException(e);
		}
	}

	public static String encodeURI(String uri) {
		if (uri != null) {
			uri = uri.replace("%", "%25");
			uri = uri.replace(" ", "%20");
			uri = uri.replace("{", "%7B");
			uri = uri.replace("}", "%7D");
			uri = uri.replace("|", "%7C");
			uri = uri.replace("^", "%5E");
			uri = uri.replace("[", "%5B");
			uri = uri.replace("]", "%5D");
			// as per 2.4.3 of RFC 2396 characters "`" (%60) and "\" (%5C) should also be encoded
			uri = uri.replace("\\", "%5C");
			uri = uri.replace("`", "%60");
		}
		return uri;
	}

}