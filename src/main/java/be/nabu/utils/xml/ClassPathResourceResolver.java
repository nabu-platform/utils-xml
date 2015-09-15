package be.nabu.utils.xml;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class ClassPathResourceResolver implements LSResourceResolver {
	
	/**
	 * The scheme used to indicate a classpath include
	 */
	public static final String SCHEME = "classpath";
	private String rootDirectory;
	
	public ClassPathResourceResolver(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		if (baseURI == null) {
			baseURI = this.rootDirectory;
		}
		// the system gives the full URI of the parent resource as base uri for the new one, remove the actual filename though for relative lookup
		else {
			baseURI = baseURI.replaceAll("/[^/]+$", "");
		}
		DOMImplementationRegistry registry;
		try {
	    	registry = DOMImplementationRegistry.newInstance();
	        DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry.getDOMImplementation("LS 3.0");
	
	        LSInput lsInput = domImplementationLS.createLSInput();
	        lsInput.setSystemId(systemId);
	        // resolve it against the base uri (if any)
	        URI uri = new URI(baseURI == null ? systemId : baseURI + (baseURI.endsWith("/") ? "" : "/") + systemId);
	        lsInput.setBaseURI(uri.normalize().toString());
	        if (uri.getScheme() == null || SCHEME.equals(uri.getScheme())) {
	        	String path = uri.getPath().replaceAll("^[/]*", "");
	        	lsInput.setByteStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
	        }
	        else {
	        	// try classic url
	        	URL url = uri.toURL();
	        	lsInput.setByteStream(url.openStream());
	        }
	        return lsInput;
		}
		catch (ClassCastException e) {
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}        
	}
}