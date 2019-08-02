package xyz.xiezc.mzi.config;

import com.blade.Blade;
import com.blade.ioc.DynamicContext;
import com.blade.ioc.annotation.Bean;
import com.blade.ioc.bean.ClassInfo;
import com.blade.ioc.bean.Scanner;
import com.blade.loader.BladeLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.io.ResolverUtil;
import xyz.xiezc.mzi.common.BaseMapper;
import xyz.xiezc.mzi.common.annotation.MapperScan;
import xyz.xiezc.mzi.common.annotation.Table;
import xyz.xiezc.mzi.common.resourceReader.JarResourcesReaderImpl;
import xyz.xiezc.mzi.common.resourceReader.ResourceReader;
import xyz.xiezc.mzi.common.resourceReader.ResourcesReaderImpl;
import xyz.xiezc.mzi.xml.DocumentMapperDefine;
import xyz.xiezc.mzi.xml.MapperDefine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@Bean
public class MybatisBladeLoader implements BladeLoader {


    /**
     * 处理mapper 接口和mapper.xml配置文件
     *
     * @param blade
     */
    public void dealMapperXml(Blade blade) {

        //获取 xyz.xiezc.mzi.dao.xml 接口(继承BaseMapper)的类
        Set<Class<?>> baseMapperClazz = getBaseMapperClazz(blade);
        //获取mapper文件的路径
        Set<Path> paths = getMapperXmlPath(blade);
        List<DocumentMapperDefine> documentPars = paths.stream()
                .map(path -> {
                    try {
                        return new DocumentMapperDefine(path);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                        return null;
                    }
                })
                .filter(documentMapperDefine -> documentMapperDefine != null)
                .collect(Collectors.toList());

        List<MapperDefine> mapperDefines = baseMapperClazz.stream()
                .map(mapperClazz ->
                        new MapperDefine(mapperClazz)
                )
                .collect(Collectors.toList());

        //找到mapper接口对应的xml文档
        for (MapperDefine mapperDefine : mapperDefines) {
            boolean findDoc = false;
            for (DocumentMapperDefine documentMapperDefine : documentPars) {
                String nameSpace = documentMapperDefine.getNameSpace();
                String name = mapperDefine.getMapperInterface().getName();
                if (Objects.equals(name, nameSpace)) {
                    documentMapperDefine.setMapperDefine(mapperDefine);
                    findDoc = true;
                }
            }
            if (!findDoc) {
                documentPars.add(new DocumentMapperDefine(mapperDefine));
            }
        }

        for (DocumentMapperDefine documentMapperDefine : documentPars) {
            documentMapperDefine.checkDoc();
        }


    }


    private Set<Path> getMapperXmlPath(Blade blade) {
        String xmlMapperPath = blade.env("mybatis.mappers").orElse("mapper");
        ResourceReader resourceReader = null;
        if (DynamicContext.isJarPackage(xmlMapperPath)) {
            resourceReader = new JarResourcesReaderImpl();
        } else {
            resourceReader = new ResourcesReaderImpl();
        }
        //获取所有已经存在的xml
        return resourceReader.readResources(xmlMapperPath, ".xml", false);
    }

    private Set<Class<?>> getBaseMapperClazz(Blade blade) {
        String mapperPath = getMapperPackage(blade);
        ResolverUtil resolverUtil = new ResolverUtil();
        ResolverUtil implementations = resolverUtil.findImplementations(BaseMapper.class, mapperPath);
        Set<Class<?>> classes = implementations.getClasses();
        return classes;
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
        this.dealMapperXml(blade);

    }

    @Override
    public void load(Blade blade) {

    }

}
