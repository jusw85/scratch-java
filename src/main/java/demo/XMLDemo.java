package demo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

public class XMLDemo {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerException, XPathExpressionException {
        String xml = "<text>Hello World!</text>";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource inputSource = new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        Document doc = db.parse(inputSource);

        XPathFactory xf = XPathFactory.newInstance();
        XPath xpath = xf.newXPath();
        String expression = "/text";
        XPathExpression expr = xpath.compile(expression);
        String value = expr.evaluate(doc);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        System.out.println(value);
        System.out.println(node);

        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        System.out.println(sw.toString());
    }

}
