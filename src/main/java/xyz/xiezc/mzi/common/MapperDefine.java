package xyz.xiezc.mzi.common;

import com.blade.ioc.bean.ClassInfo;
import lombok.Data;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Data
public class MapperDefine {

    /**
     * 关联的实体对象类
     */
    Class<?> entityClazz;
    /**
     * 关联的mapper 接口类
     */
    ClassInfo mapperClzzInfo;

    /**
     * 关联的对应maper.xml文件
     */
    Document document;

    /**
     * 对应mapper.xml的名称
     */
    String xmlName;


}
