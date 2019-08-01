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

import javax.print.Doc;
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


    public Element createMapper() {


        Document doc = mapperDefine.getDocument();

        Element mapper = doc.createElement("mapper");
        mapper.setAttribute("namespace", mapperDefine.getMapperInterface().getName());

        return mapper;


    }

    public Element createResultMap() {
        Document doc = mapperDefine.getDocument();
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Element resultMap = doc.createElement("resultMap");
        resultMap.setAttribute("id", "BaseResultMap");
        resultMap.setAttribute("type", "xyz.xiezc.mzi.entity.Album");
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
        return resultMap;
    }

    public Element createSql() {
        Document doc = mapperDefine.getDocument();
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
        trim.setAttribute("prefix", "(");
        trim.setAttribute("prefixOverrides", "and");
        trim.setAttribute("suffix", ")");
        Element foreach1 = doc.createElement("foreach");
        trim.appendChild(foreach1);
        foreach1.setAttribute("collection", "criteria.criteria");
        foreach1.setAttribute("item", "criterion");
        Element choose = doc.createElement("choose");
        foreach1.appendChild(choose);
        Element when = doc.createElement("when");
        choose.appendChild(when);
        when.setAttribute("test", "criterion.noValue");
        Text textNode = doc.createTextNode("and ${criterion.condition}");
        when.appendChild(textNode);

        Element when1 = doc.createElement("when");
        choose.appendChild(when1);
        when1.setAttribute("test", "criterion.singleValue");
        when1.appendChild(doc.createTextNode("  and ${criterion.condition} #{criterion.value}"));

        Element when2 = doc.createElement("when");
        choose.appendChild(when2);
        when2.setAttribute("test", "riterion.betweenValue");
        when2.appendChild(doc.createTextNode("    and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"));


        Element when3 = doc.createElement("when");
        choose.appendChild(when3);
        when3.setAttribute("test", "criterion.listValue");
        when3.appendChild(doc.createTextNode("    and ${criterion.condition}"));

        Element foreach2 = doc.createElement("foreach");
        when3.appendChild(foreach2);
        foreach2.setAttribute("close", ")");
        foreach2.setAttribute("collection", "criterion.value");
        foreach2.setAttribute("item", "listItem");

        foreach2.setAttribute("separator", ",");
        foreach2.setAttribute("open", "(");
        foreach2.appendChild(doc.createTextNode("   #{listItem}"));
        return sql;
    }


    public static void main(String[] args) {
        MapperDefine mapperDefine = new MapperDefine();
        mapperDefine.init(AlbumMapper.class);
        DocumentParse documentParse = new DocumentParse(mapperDefine);
        documentParse.createMapper();
        documentParse.createResultMap();

    }
}
