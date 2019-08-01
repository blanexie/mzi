package xyz.xiezc.mzi.common;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.*;
import xyz.xiezc.mzi.dao.AlbumMapper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Set;

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
            resultMap.appendChild(result);
            result.setAttribute("column", columnProp.getColumn());
            result.setAttribute("property", columnProp.getProperty());
        }
        return resultMap;
    }

    public Element createUpdateByPrimaryKey() {
        Document doc = mapperDefine.getDocument();
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Element update = doc.createElement("update");
        update.setAttribute("id", "updateByPrimaryKey");
        update.setAttribute("parameterType", entityTableDefine.getTable().getClazz().getName());
        update.appendChild(doc.createTextNode(" update " + entityTableDefine.getTable().getColumn()));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" set ");
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            stringBuffer.append(columnProp.getColumn()).append("=#{")
                    .append(columnProp.getProperty()).append("},");
        }
        stringBuffer.append(" where " + entityTableDefine.getId().getColumn() + " = #{" + entityTableDefine.getId().getProperty() + "}");
        update.appendChild(doc.createTextNode(stringBuffer.toString()));
        return update;
    }

    public Element createUpdateByPrimaryKeySelective() {
        Document doc = mapperDefine.getDocument();
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Element update = doc.createElement("update");
        update.setAttribute("id", "updateByPrimaryKeySelective");
        update.setAttribute("parameterType", entityTableDefine.getTable().getClazz().getName());
        update.appendChild(doc.createTextNode(" update " + entityTableDefine.getTable().getColumn()));
        Element set = doc.createElement("set");
        update.appendChild(set);

        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            Element anIf = doc.createElement("if");
            set.appendChild(anIf);
            anIf.setAttribute("test", columnProp.getProperty() + " != null");
            anIf.appendChild(doc.createTextNode(columnProp.getColumn() + " = #{" + columnProp.getProperty() + "},"));
        }

        update.appendChild(doc.createTextNode("where " + entityTableDefine.getId().getColumn() + "= #{" + entityTableDefine.getId().getProperty() + "}"));
        return update;
    }

    public Element createUpdateByExample() {
        Document doc = mapperDefine.getDocument();
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Element update = doc.createElement("update");
        update.setAttribute("id", "updateByExample");
        update.setAttribute("parameterType", "map");
        update.appendChild(doc.createTextNode(" update " + entityTableDefine.getTable().getColumn()));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" set ").append(entityTableDefine.getId().getColumn())
                .append("=#{record.").append(entityTableDefine.getId().getProperty())
                .append("},");
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            stringBuffer.append(columnProp.getColumn()).append("= #{record.")
                    .append(columnProp.getProperty()).append("},");
        }
        Element anIf = doc.createElement("if");
        update.appendChild(anIf);
        anIf.setAttribute("test", "_parameter != null");
        Element include = doc.createElement("include");
        anIf.appendChild(include);
        include.setAttribute("refid", "Update_By_XZCExample_Where_Clause");
        return update;
    }

    public Element createUpdateByExampleSelective() {
        Document doc = mapperDefine.getDocument();
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Element update = doc.createElement("update");
        update.setAttribute("id", "updateByExampleSelective");
        update.setAttribute("parameterType", "map");
        update.appendChild(doc.createTextNode("update " + entityTableDefine.getTable().getColumn()));
        Element set = doc.createElement("set");
        update.appendChild(set);
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        Element anIf = doc.createElement("if");
        set.appendChild(anIf);
        anIf.setAttribute("test", "record." + entityTableDefine.getId().getProperty() + " != null");
        anIf.appendChild(doc.createTextNode(entityTableDefine.getId().getColumn() + "= #{record." + entityTableDefine.getId().getProperty() + "},"));
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            Element anIf1 = doc.createElement("if");
            set.appendChild(anIf1);
            anIf1.setAttribute("test", "record." + columnProp.getProperty() + " != null");
            anIf1.appendChild(doc.createTextNode(columnProp.getColumn() + "= #{record." + columnProp.getProperty() + "},"));
        }
        Element anIf2 = doc.createElement("if");
        set.appendChild(anIf2);
        anIf2.setAttribute("test", "_parameter  != null");
        Element include = doc.createElement("include");
        anIf2.appendChild(include);
        include.setAttribute("refid", "Update_By_XZCExample_Where_Clause");
        return update;
    }

    public Element createCountByExample() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element select = doc.createElement("select");
        select.setAttribute("id", "countByExample");
        select.setAttribute("parameterType", EntityTableDefine.ExampleName);
        select.setAttribute("resultType", "java.lang.Long");
        select.appendChild(doc.createTextNode(" select count(*) from " + entityTableDefine.getTable().getColumn()));
        Element anIf = doc.createElement("if");
        select.appendChild(anIf);
        anIf.setAttribute("test", "_parameter != null");
        Element include = doc.createElement("include");
        anIf.appendChild(include);
        include.setAttribute("refid", "Example_Where_Clause");
        return select;

    }

    public Element createInsertSelective() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element insert = doc.createElement("insert");
        insert.setAttribute("id", "insertSelective");
        insert.setAttribute("keyColumn", entityTableDefine.getId().getColumn());
        insert.setAttribute("keyProperty", entityTableDefine.getId().getProperty());
        insert.setAttribute("parameterType", entityTableDefine.getTable().getClazz().getName());
        insert.setAttribute("useGeneratedKeys", "true");
        insert.appendChild(doc.createTextNode(" insert into " + entityTableDefine.getTable().getColumn()));
        Element trim = doc.createElement("trim");
        insert.appendChild(trim);
        trim.setAttribute("prefix", "(");
        trim.setAttribute("suffix", ")");
        trim.setAttribute("suffixOverrides", ",");
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            Element anIf = doc.createElement("if");
            trim.appendChild(anIf);
            anIf.setAttribute("test", columnProp.getProperty() + " != null");
            anIf.appendChild(doc.createTextNode(columnProp.getColumn()));
        }
        Element trim1 = doc.createElement("trim");
        insert.appendChild(trim1);
        trim1.setAttribute("prefix", "values (");
        trim1.setAttribute("suffix", ")");
        trim1.setAttribute("suffixOverrides", ",");
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            Element anIf = doc.createElement("if");
            trim1.appendChild(anIf);
            anIf.setAttribute("test", columnProp.getProperty() + " != null");
            anIf.appendChild(doc.createTextNode("   #{" + columnProp.getProperty() + "},"));
        }
        return insert;
    }


    public Element createInsert() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element insert = doc.createElement("insert");
        insert.setAttribute("id", "insert");
        insert.setAttribute("keyColumn", entityTableDefine.getId().getColumn());
        insert.setAttribute("keyProperty", entityTableDefine.getId().getProperty());
        insert.setAttribute("parameterType", entityTableDefine.getTable().getClazz().getName());
        insert.setAttribute("useGeneratedKeys", "true");
        StringBuffer stringBuffer = new StringBuffer();
//
//    EntityTableDefine.ColumnProp id = entityTableDefine.getId();
//    stringBuffer.append(id.getColumn());
        stringBuffer.append("  insert into t_album (");
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            stringBuffer.append(columnProp.getColumn()).append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1).append(" )   values (");
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            stringBuffer.append("#{");
            stringBuffer.append(columnProp.getProperty()).append("},");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1).append(" )");
        insert.appendChild(doc.createTextNode(stringBuffer.toString()));
        return insert;

    }

    public Element createDeleteByExample() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element select = doc.createElement("select");
        select.setAttribute("id", "deleteByExample");
        select.setAttribute("parameterType", EntityTableDefine.ExampleName);
        select.appendChild(doc.createTextNode("  delete from " + entityTableDefine.getTable().getColumn()));
        Element anIf = doc.createElement("if");
        select.appendChild(anIf);
        anIf.setAttribute("test", "_parameter != null");
        Element include = doc.createElement("include");
        anIf.appendChild(include);
        include.setAttribute("refid", "XZCExample_Where_Clause");
        return select;
    }

    public Element createDeleteByPrimaryKey() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element select = doc.createElement("select");
        select.setAttribute("id", "deleteByPrimaryKey");
        select.setAttribute("parameterType", entityTableDefine.getId().getClazz().getName());
        select.appendChild(doc.createTextNode("delete from " + entityTableDefine.getTable().getColumn()));
        select.appendChild(doc.createTextNode("where " + entityTableDefine.getId().getColumn() + "= #{" + entityTableDefine.getId().getProperty() + "}"));
        return select;
    }

    public Element createSelectByPrimaryKey() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element select = doc.createElement("select");
        select.setAttribute("id", "selectByPrimaryKey");
        select.setAttribute("parameterType", entityTableDefine.getId().getClazz().getName());
        select.setAttribute("resultMap", "XZCBaseResultMap");
        select.appendChild(doc.createTextNode("select"));
        Element include = doc.createElement("include");
        select.appendChild(include);
        include.setAttribute("refid", "XZCBase_Column_List");
        select.appendChild(doc.createTextNode(" from " + entityTableDefine.getTable().getColumn() +
                "   where" + entityTableDefine.getId().getColumn() + " = #{" + entityTableDefine.getId().getProperty() + "}"));
        return select;
    }

    public Element createSelectByExample() {
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        Document doc = mapperDefine.getDocument();
        Element select = doc.createElement("select");
        select.setAttribute("id", "selectByExample");
        select.setAttribute("parameterType", EntityTableDefine.ExampleName);
        select.setAttribute("resultMap", "XZCBaseResultMap");
        select.appendChild(doc.createTextNode("select"));
        Element anIf = doc.createElement("if");
        anIf.setAttribute("test", "distinct");
        select.appendChild(anIf);
        anIf.appendChild(doc.createTextNode("distinct"));
        Element include = doc.createElement("include");
        select.appendChild(include);
        include.setAttribute("refid", "XZCBase_Column_List");
        select.appendChild(doc.createTextNode(" from " + entityTableDefine.getTable().getColumn()));

        Element anIf1 = doc.createElement("if");
        select.appendChild(anIf1);
        anIf1.setAttribute("test", "_parameter != null");
        Element include1 = doc.createElement("include");
        anIf1.appendChild(include1);
        include1.setAttribute("refid", "XZCExample_Where_Clause");

        Element anIf2 = doc.createElement("if");
        select.appendChild(anIf2);
        anIf2.setAttribute("test", "orderByClause != null");
        anIf2.appendChild(doc.createTextNode(" order by ${orderByClause}"));

        Element anIf3 = doc.createElement("if");
        select.appendChild(anIf3);
        anIf3.setAttribute("test", "limit != null");
        Element anIf4 = doc.createElement("if");
        anIf3.appendChild(anIf4);
        anIf4.setAttribute("test", "offset != null");
        anIf4.appendChild(doc.createTextNode("  limit ${offset}, ${limit}"));
        Element anIf5 = doc.createElement("if");
        anIf3.appendChild(anIf5);
        anIf5.setAttribute("test", "offset == null");
        anIf5.appendChild(doc.createTextNode("  limit ${limit}"));
        return select;
    }


    public Element createAllColumnSql() {
        Document doc = mapperDefine.getDocument();
        Element sql = doc.createElement("sql");
        sql.setAttribute("id", "XZCBase_Column_List");
        EntityTableDefine entityTableDefine = mapperDefine.getEntityTableDefine();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(entityTableDefine.getId().getColumn());
        Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
        for (EntityTableDefine.ColumnProp columnProp : columns) {
            stringBuffer.append(",");
            stringBuffer.append(columnProp.getColumn());
        }
        sql.appendChild(doc.createTextNode(stringBuffer.toString()));
        return sql;
    }

    public Element createUpdateWhereSql() {
        Document doc = mapperDefine.getDocument();
        Element sql = doc.createElement("sql");
        sql.setAttribute("id", "Update_By_XZCExample_Where_Clause");
        Element where = doc.createElement("where");
        sql.appendChild(where);
        Element foreach = doc.createElement("foreach");
        where.appendChild(foreach);
        foreach.setAttribute("collection", "example.oredCriteria"); //collection="example.oredCriteria" item="criteria" separator="or"
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
        when.appendChild(doc.createTextNode("and ${criterion.condition}"));
        Element when1 = doc.createElement("when");
        choose.appendChild(when1);
        when1.setAttribute("test", "criterion.singleValue");
        when1.appendChild(doc.createTextNode("and ${criterion.condition} #{criterion.value}"));
        Element when2 = doc.createElement("when");
        choose.appendChild(when2);
        when2.setAttribute("test", "criterion.betweenValue");
        when2.appendChild(doc.createTextNode("and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"));
        Element when3 = doc.createElement("when");
        choose.appendChild(when3);
        when3.setAttribute("test", "criterion.listValue");
        when3.appendChild(doc.createTextNode("and ${criterion.condition}"));
        Element foreach2 = doc.createElement("foreach");
        when3.appendChild(foreach2);
        foreach2.setAttribute("close", ")");
        foreach2.setAttribute("collection", "criterion.value");
        foreach2.setAttribute("item", "listItem");
        foreach2.setAttribute("open", "(");
        foreach2.setAttribute("separator", ",");
        foreach2.appendChild(doc.createTextNode(" #{listItem}"));
        return sql;
    }

    public Element createWhereSql() {
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


    public static void main(String[] args) throws TransformerException {
        MapperDefine mapperDefine = new MapperDefine();
        mapperDefine.init(AlbumMapper.class);
        DocumentParse documentParse = new DocumentParse(mapperDefine);

        Document doc = mapperDefine.getDocument();
        // 添加根节点
        Element mapper = documentParse.createMapper();
        mapper.appendChild(documentParse.createResultMap());
        mapper.appendChild(documentParse.createWhereSql());
        mapper.appendChild(documentParse.createUpdateWhereSql());
        mapper.appendChild(documentParse.createAllColumnSql());
        mapper.appendChild(documentParse.createSelectByExample());
        mapper.appendChild(documentParse.createSelectByPrimaryKey());
        mapper.appendChild(documentParse.createDeleteByPrimaryKey());
        mapper.appendChild(documentParse.createDeleteByExample());
        mapper.appendChild(documentParse.createInsert());
        mapper.appendChild(documentParse.createInsertSelective());
        mapper.appendChild(documentParse.createCountByExample());
        mapper.appendChild(documentParse.createUpdateByExampleSelective());
        mapper.appendChild(documentParse.createUpdateByExample());
        mapper.appendChild(documentParse.createUpdateByPrimaryKeySelective());
        mapper.appendChild(documentParse.createUpdateByPrimaryKey());

        doc.appendChild(mapper);

        // 把xml内容输出到具体的文件中
        TransformerFactory formerFactory = TransformerFactory.newInstance();
        Transformer transformer = formerFactory.newTransformer();
        // 换行
        transformer.setOutputProperty(OutputKeys.INDENT, "YES");
        // 文档字符编码
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");

        // 可随意指定文件的后缀,效果一样,但xml比较好解析,比如: E:\\person.txt等
        transformer.transform(new DOMSource(doc), new StreamResult(new File("C:\\Users\\86187\\Desktop\\src\\AlbumMapper.xml")));

    }
}
