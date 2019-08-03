package xyz.xiezc.mzi.xml;

import com.blade.Blade;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.io.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xyz.xiezc.mzi.common.DocumentUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class MybatisConfigDefine {
    @Setter
    @Getter
    Document configDocument;

    @Setter
    @Getter
    Blade blade;

    public MybatisConfigDefine(Blade blade) {
        this.blade = blade;
    }


    public void checkMybatisConfig() throws ParserConfigurationException, IOException, SAXException {
        Properties properties = blade.environment().props();

        DocumentBuilder documentBuilder = DocumentUtil.getDocumentBuilder(true);
        InputStream resourceAsStream = Resources.getResourceAsStream(properties.getProperty("mybatis.config"));
        configDocument = documentBuilder.parse(resourceAsStream);
        //获得配置的envName
        checkEnvironments(properties);
        checkMappers();
    }


    private void checkMappers() {
        StringBuffer stringBuffer = new StringBuffer();
        //如果用户配置了
        NodeList mappers = configDocument.getElementsByTagName("mappers");
        int length = mappers.getLength();
        if (length == 0) {
            return;
        }
        if (length > 1) {
            throw new RuntimeException("请配置一个mappers节点就够了");
        }
        Node item = mappers.item(0);
        NodeList childNodes = item.getChildNodes();
        int length1 = childNodes.getLength();
        for (int i = 0; i < length1; i++) {
            Node item1 = childNodes.item(i);
            String nodeName = item1.getNodeName();
            if (Objects.equals(nodeName, "mapper")) {
                Node resource = item1.getAttributes().getNamedItem("resource");
                String nodeValue = resource.getNodeValue();
                int i1 = nodeValue.lastIndexOf("/");
                String substring = nodeValue.substring(0, i1);
                stringBuffer.append(substring).append(",");
            }
            if (Objects.equals(nodeName, "package")) {
                //还有url和class属性
                //TODO
                Node nameNode = item1.getAttributes().getNamedItem("name");
                String nodeValue = nameNode.getNodeValue();
                stringBuffer.append(nodeValue.replaceAll(".", "/")).append(",");
            }
        }
        blade.environment().add("mybatis.mappers.xml.config", stringBuffer.toString());
    }

    /**
     * 获得默认的 配置环境的 id
     */
    private void checkEnvironments(Properties properties) {
        String envName = properties.getProperty("mybatis.environment");

        NodeList environments = configDocument.getElementsByTagName("environments");
        int length = environments.getLength();
        if (length == 0) {
            Element environments1 = createEnviorments();
            configDocument.appendChild(environments1);
            return;
        }
        if (length > 1) {
            throw new RuntimeException("请配置一个environments 就够了, 请注意environments 和environment 的区别");
        }

        Node item = environments.item(0);
        Node development = item.getAttributes().getNamedItem("default");
        if (development != null && Objects.equals(development.getNodeValue(), envName)) {
            return;
        } else {
            Element environments1 = (Element) item;
            environments1.setAttribute("default", envName);
        }
    }


    private Element createEnviorments() {
        Element environments1 = configDocument.createElement("environments");
        environments1.setAttribute("default", "development");
        configDocument.appendChild(environments1);
        Element environment = configDocument.createElement("environment");
        environment.setAttribute("id", "development");
        environments1.appendChild(environment);
        Element transactionManager = configDocument.createElement("transactionManager");
        transactionManager.setAttribute("type", "JDBC");
        environment.appendChild(transactionManager);
        Element dataSource = configDocument.createElement("dataSource");
        environment.appendChild(dataSource);
        dataSource.setAttribute("type", "xyz.xiezc.mzi.common.MziDataSourceFactory");
        Element property = configDocument.createElement("property");
        dataSource.appendChild(property);
        property.setAttribute("name", "driver");
        property.setAttribute("value", "${mybatis.driver}");
        Element property1 = configDocument.createElement("property");
        dataSource.appendChild(property1);
        property1.setAttribute("name", "url");
        property1.setAttribute("value", "${mybatis.url}");
        Element property2 = configDocument.createElement("property");
        dataSource.appendChild(property2);
        property2.setAttribute("name", "username");
        property2.setAttribute("value", "${mybatis.username}");
        Element property3 = configDocument.createElement("property");
        dataSource.appendChild(property3);
        property3.setAttribute("name", "password");
        property3.setAttribute("value", "${mybatis.password}");
        return environments1;
    }

}
