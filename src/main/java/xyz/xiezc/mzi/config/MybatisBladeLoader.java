package xyz.xiezc.mzi.config;

import com.blade.Blade;
import com.blade.ioc.DynamicContext;
import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.Scanner;
import com.blade.loader.BladeLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MybatisBladeLoader implements BladeLoader {

    EntityResolver entityResolver = new XMLMapperEntityResolver();

    boolean validation = true;

    public void init(Blade blade) {
        String mapperPath = getMapperPackage(blade);
        //获取 xyz.xiezc.mzi.dao.xml 接口(集成BaseMapper)的类
        Set<ClassInfo> baseMapperClazz = getBaseMapperClazz(mapperPath);
        //获取mapper文件的路径
        String xmlMapperPath = blade.env("mybatis.mappers").orElse("mapper");
        ResourceReader resourceReader=null;
        if(DynamicContext.isJarPackage(xmlMapperPath)){
            resourceReader=new JarResourcesReaderImpl();
        }else{
            resourceReader=new ResourcesReaderImpl();
        }
        Set<InputStream> inputStreams = resourceReader.readResources(xmlMapperPath, ".xml", false);
        for(InputStream inputStream:inputStreams){
            InputSource inputSource=new InputSource(inputStream);
            inputSource.setEncoding("utf8");
            Document document = createDocument(inputSource);

        }






        SqlSessionFactory sqlSessionFactory;
        SqlSession session = null;
        String resource = "mybatis-config.xml";
        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
            XPathParser xPathParser = new XPathParser(inputStream, validation, null, entityResolver);
            XNode xNode = xPathParser.evalNode("/configuration/mappers");


            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);


            Configuration configuration = parser.parse();
            configuration.addMappers();

            //   sqlSessionFactory = new SqlSessionFactoryBuilder().build();
            //     session = sqlSessionFactory.openSession(true);
//            albumMapper = session.getMapper(AlbumMapper.class);
//            photoMapper = session.getMapper(PhotoMapper.class);
//            tagMapper = session.getMapper(TagMapper.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Set<ClassInfo> getBaseMapperClazz(String mapperPath) {
        Scanner scanner = Scanner.builder().packageName(mapperPath).recursive(false).build();
        Set<ClassInfo> classInfos = DynamicContext.getClassReader(mapperPath).readClasses(scanner);

        return classInfos.stream().filter(classInfo -> {
            Class<?> clazz = classInfo.getClazz();
            if (!clazz.isInterface()) {
                return false;
            }
            return BaseMapper.class.isAssignableFrom(clazz);

        }).collect(Collectors.toSet());
    }

    private String getMapperPackage(Blade blade) {
        String mapperPath = null;
        MapperScan mapperScan = blade.bootClass().getAnnotation(MapperScan.class);
        if (mapperScan != null) {
            mapperPath = mapperScan.value();
        }
        mapperPath = blade.environment().get("mybatis.mappers.package").orElse(mapperPath);
        if (mapperPath == null) {
            throw new RuntimeException("你需要配置mapper的位置, 两种方式, 一种在启动类上加上MapperScan注解,  另一种方式是在配置文件中mybatis.mappers.package ");
        }
        return mapperPath;
    }


    private Document createDocument(InputSource inputSource) {
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

    @Override
    public void preLoad(Blade blade) {
        this.init(blade);

    }

    @Override
    public void load(Blade blade) {

    }
}
