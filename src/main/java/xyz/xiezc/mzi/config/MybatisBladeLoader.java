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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
import xyz.xiezc.mzi.common.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MybatisBladeLoader implements BladeLoader {

    List<MapperDefine> mapperDefines = new ArrayList<>();

    DocumentParse documentParse = new DocumentParse();

    public void init(Blade blade) throws IOException {
        String mapperPath = getMapperPackage(blade);
        //获取 xyz.xiezc.mzi.dao.xml 接口(集成BaseMapper)的类
        Set<ClassInfo> baseMapperClazz = getBaseMapperClazz(mapperPath);
        //获取mapper文件的路径
        String xmlMapperPath = blade.env("mybatis.mappers").orElse("mapper");
        ResourceReader resourceReader = null;
        if (DynamicContext.isJarPackage(xmlMapperPath)) {
            resourceReader = new JarResourcesReaderImpl();
        } else {
            resourceReader = new ResourcesReaderImpl();
        }
        //获取所有已经存在的xml
        Set<Path> paths = resourceReader.readResources(xmlMapperPath, ".xml", false);
        //过滤文档与接口对应的文档
        for (Path path : paths) {
            InputSource inputSource = new InputSource(Files.newBufferedReader(path));
            inputSource.setEncoding("utf8");
            Document document = documentParse.createDocument(inputSource);
            NodeList mapper = document.getElementsByTagName("mapper");
            if (mapper.getLength() > 0) {
                Node node = mapper.item(0);
                NamedNodeMap attributes = node.getAttributes();
                String mapperNamespace = attributes.getNamedItem("namespace").getNodeValue();
                baseMapperClazz = baseMapperClazz.stream()
                        .filter(m -> {
                            boolean equals = Objects.equals(m.getClassName(), mapperNamespace);
                            if (equals) {
                                MapperDefine mapperDefine = new MapperDefine();
                                mapperDefine.setMapperClzzInfo(m);
                                mapperDefine.setDocument(document);
                                mapperDefines.add(mapperDefine);
                            }
                            return !equals;
                        })
                        .collect(Collectors.toSet());
            }
            //剩下的都是没有文档与之对应的接口
            baseMapperClazz.stream()




            XPathParser xPathParser = new XPathParser(document);


            // XMLConfigBuilder parser = new XMLConfigBuilder(xPathParser, null,null);
            //TODO

            mapperDefine.setDocument(document);

        }


//
//        SqlSessionFactory sqlSessionFactory;
//        SqlSession session = null;
//        String resource = "mybatis-config.xml";
//        try (InputStream inputStream = Resources.getResourceAsStream(resource)) {
//            XPathParser xPathParser = new XPathParser(inputStream, validation, null, entityResolver);
//            XNode xNode = xPathParser.evalNode("/configuration/mappers");
//
//
//            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);
//
//
//            Configuration configuration = parser.parse();

        // configuration.addMappers();

        //   sqlSessionFactory = new SqlSessionFactoryBuilder().build();
        //     session = sqlSessionFactory.openSession(true);
//            albumMapper = session.getMapper(AlbumMapper.class);
//            photoMapper = session.getMapper(PhotoMapper.class);
//            tagMapper = session.getMapper(TagMapper.class);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private Set<ClassInfo> getBaseMapperClazz(String mapperPath) {
        Scanner scanner = Scanner.builder().packageName(mapperPath).recursive(false).build();
        Set<ClassInfo> classInfos = DynamicContext.getClassReader(mapperPath).readClasses(scanner);

        return classInfos;

//        return classInfos.stream().filter(classInfo -> {
//            Class<?> clazz = classInfo.getClazz();
//            if (!clazz.isInterface()) {
//                return false;
//            }
//            return BaseMapper.class.isAssignableFrom(clazz);
//
//        }).collect(Collectors.toSet());
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


    @Override
    public void preLoad(Blade blade) {
        try {
            this.init(blade);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void load(Blade blade) {

    }
}
