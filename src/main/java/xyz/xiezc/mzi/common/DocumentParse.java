package xyz.xiezc.mzi.common;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.PropertyParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.*;
import xyz.xiezc.mzi.dao.AlbumMapper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Properties;
import java.util.Set;

import static com.sun.tools.doclint.Entity.and;

public class DocumentParse {
    EntityResolver entityResolver = new XMLMapperEntityResolver();

    boolean validation = true;


    MapperDefine mapperDefine;


    public DocumentParse(MapperDefine mapperDefine, InputSource inputSource) {
        Document document = this.createDocument(inputSource);
        mapperDefine.setDocument(document);
        this.mapperDefine = mapperDefine;
    }

    public DocumentParse(MapperDefine mapperDefine) {
        Document document = this.createDocument();
        mapperDefine.setDocument(document);
        this.mapperDefine = mapperDefine;
    }

    public DocumentParse() {

    }

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


    public Document createDocument() {
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
            return builder.newDocument();

        } catch (Exception e) {
            throw new BuilderException("Error creating document instance.  Cause: " + e, e);
        }
    }


    public void createMapper() {

        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();

        Document doc = mapperDefine.getDocument();

        Element mapper = doc.createElement("mapper");
        mapper.setAttribute("namespace", mapperDefine.getMapperInterface().getName());

        Element resultMap = doc.createElement("resultMap");
        resultMap.setAttribute("id", "BaseResultMap");
        resultMap.setAttribute("type", "xyz.xiezc.mzi.entity.Album");
        mapper.appendChild(resultMap);

        Element id = doc.createElement("id");

        //   column="id" jdbcType="INTEGER" property="id" />
        id.setAttribute("column", entityTableDefine.getId().getColumn());
        id.setAttribute("property", entityTableDefine.getId().getProperty());
        resultMap.appendChild(id);
        //column
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            Element result = doc.createElement("result");
            result.setAttribute("column", columnProp.getColumn());
            result.setAttribute("property", columnProp.getProperty());
        }
        Element sql = doc.createElement("sql");
        sql.setAttribute("id", "XZCExample_Where_Clause");
        Element where = doc.createElement("where");
        sql.appendChild(where);
        Element foreach = doc.createElement("foreach");
        where.appendChild(foreach);
        foreach.setAttribute("collection", "oredCriteria");
        foreach.setAttribute("item", "criteria");
        foreach.setAttribute("separator", "or");
        Element anIf = doc.createElement("if");
        foreach.appendChild(anIf);
        anIf.setAttribute("test", "criteria.valid");
        Element trim = doc.createElement("trim");
        anIf.appendChild(trim);
        trim.setAttribute( "prefix","(");
        trim.setAttribute( "prefixOverrides","and");
        trim.setAttribute( "suffix",")");
        Element foreach1 = doc.createElement("foreach");
        trim.appendChild(foreach1);
        foreach1.setAttribute("collection","criteria.criteria");
        foreach1.setAttribute("item","criterion");
        Element choose = doc.createElement("choose");
        foreach1.appendChild(choose);
        Element when = doc.createElement("when");
        choose.appendChild(when);
        when.setAttribute( "test","criterion.noValue");
        Text textNode = doc.createTextNode("and ${criterion.condition}");
        when.appendChild(textNode);
        Element when1 = doc.createElement("when");
        when1.setAttribute("test","criterion.singleValue");
        when1.appendChild(doc.createTextNode("  and ${criterion.condition} #{criterion.value}"))       ;



                                <when test="criterion.betweenValue">
                and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}
                                </when>
                                <when test="criterion.listValue">
                and ${criterion.condition}
                                    <foreach close=")" collection="criterion.value" item="listItem" open="(" separator=",">
                                        #{listItem}
                                    </foreach>
                                </when>
                            </choose>


    }


    public void createResultMap() {
        Node mapper = mapperDefine.getDocument().getElementsByTagName("mapper").item(0);


    }


    public static void main(String[] args) {
        MapperDefine mapperDefine = new MapperDefine();
        mapperDefine.init(AlbumMapper.class);
        DocumentParse documentParse = new DocumentParse(mapperDefine);
        documentParse.createMapper();
        documentParse.createResultMap();

    }
}
