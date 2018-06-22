package etech.omni.transaction;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UnmarchalHandler extends XmlAdapter<Object, String> {

	/**
	 * Factory for building DOM documents.
	 */
	private final DocumentBuilderFactory docBuilderFactory;
	/**
	 * Factory for building transformers.
	 */
	private final TransformerFactory transformerFactory;

	public UnmarchalHandler() {
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		transformerFactory = TransformerFactory.newInstance();
	}

	@Override
	public Object marshal(String v) throws Exception {
		// DOM document builder
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        // Creating a new empty document
        Document doc = docBuilder.newDocument();
/*        // Creating the <title> element
        Element titleElement = doc.createElement("title");
        // Setting as the document root
        doc.appendChild(titleElement);
        // Creating a DOMResult as output for the transformer
        DOMResult result = new DOMResult(titleElement);
        // Default transformer: identity tranformer (doesn't alter input)
        Transformer transformer = transformerFactory.newTransformer();
        // String reader from the input and source
        StringReader stringReader = new StringReader(v);
        StreamSource source = new StreamSource(stringReader);
        // Transforming input string to the DOM
        transformer.transform(source, result);
        // Return DOM root element (<title>) for JAXB marshalling to XML
*/        return doc.getDocumentElement();
	}

	@Override
	public String unmarshal(Object v) throws Exception {
		// Creating a DOMSource as input for the transformer
		DOMSource source = new DOMSource((Element) v);
		// Default transformer: identity tranformer (doesn't alter input)
		Transformer transformer = transformerFactory.newTransformer();
		// This is necessary to avoid the <?xml ...?> prolog
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		// Transform to a StringWriter
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		transformer.transform(source, result);
		// Returning result as string
		return stringWriter.toString();
	}

}
