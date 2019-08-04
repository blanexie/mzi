package xyz.xiezc.mzi.common;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class DocumentUtil {

    public static DocumentBuilder getDocumentBuilder(boolean validation) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validation);

        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setCoalescing(false);
        factory.setExpandEntityReferences(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(new XMLMapperEntityResolver());
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void warning(SAXParseException exception) throws SAXException {
            }
        });
        return builder;
    }

    public static void writeDoc(Document document) {
        // 把xml内容输出到具体的文件中
        TransformerFactory formerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = formerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        // 换行
        transformer.setOutputProperty(OutputKeys.INDENT, "YES");
        // 文档字符编码
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        // 可随意指定文件的后缀,效果一样,但xml比较好解析,比如: E:\\person.txt等
        try {
            transformer.transform(new DOMSource(document), new StreamResult(new File(document.hashCode() + ".xml")));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
