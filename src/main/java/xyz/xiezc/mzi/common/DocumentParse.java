package xyz.xiezc.mzi.common;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.PropertyParser;
import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Properties;

public class DocumentParse {
    EntityResolver entityResolver = new XMLMapperEntityResolver();

    boolean validation = true;

    public Document createDocument(InputSource inputSource) {
        // important: this must only be called AFTER common constructor
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(validation);

            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
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
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }





    public static void main(String[] args) {


        String x="  select\n" +
                "        <if test=\"distinct\">\n" +
                "    ${a}        distinct\n" +

                "        </if>\n" +
                "        <include refid=\"Base_Column_List\" />\n" +
                "\n" +
                "        from ${t_table}";
        Properties properties=new Properties();
        properties.setProperty("t_table","xxx");
        String parse = PropertyParser.parse(x, properties);
        System.out.println(parse);
    }
}
