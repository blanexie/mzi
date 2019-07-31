package xyz.xiezc.mzi.common;

import com.blade.ioc.bean.ClassInfo;
import lombok.Data;
import org.w3c.dom.Document;
import xyz.xiezc.mzi.config.BaseMapper;
import xyz.xiezc.mzi.config.Id;
import xyz.xiezc.mzi.config.Table;
import xyz.xiezc.mzi.entity.Album;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MapperDefine {

    /**
     * 关联的实体对象类
     */
    Class<?> entityClazz;

    EntityTableDefine entityTableDefine;

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

    /**
     * 获取到Mapper接口配置的泛型对象
     */
    private void dealEntityClazz() {
        Class<?> clazz = mapperClzzInfo.getClazz();
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Class rawType = (Class) parameterizedType.getRawType();
            if (rawType == BaseMapper.class) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    this.entityClazz = (Class) actualTypeArguments[0];
                    return;
                }
            }
        }
        throw new RuntimeException(clazz.getTypeName() + "接口没有指定正确的泛型对象类");
    }


    public void dealEntityClazzFiled() {
        EntityTableDefine entityTableDefine = new EntityTableDefine();
        //表名
        Table table = this.entityClazz.getAnnotation(Table.class);
        String tableName;
        if (table == null || table.value().equals("")) {
            String simpleName = this.entityClazz.getSimpleName();
            tableName = StringUtil.underscoreName(simpleName);
        } else {
            tableName = table.value();
        }
        entityTableDefine.setTable(new EntityTableDefine.ColumnProp());
        entityTableDefine.getTable().setColumn(tableName);
        entityTableDefine.getTable().setClazz(this.entityClazz);


        Field[] declaredFields = this.entityClazz.getDeclaredFields();
        List<Field> fields = Arrays.asList(declaredFields);
        fields = fields.stream().filter(field -> {
            Id id = field.getAnnotation(Id.class);
            Class<?> clazz = field.getType();
            String columnName;
            if (id != null ) {
                if(id.value().equals("")){
                    columnName = StringUtil.underscoreName(clazz.getSimpleName());
                }else{
                    columnName =id.value();
                }
                entityTableDefine.setId(new EntityTableDefine.ColumnProp());
                entityTableDefine.getId().setColumn(columnName);
                entityTableDefine.getId().setClazz(clazz);
                return false;
            }
            return true;

        }).collect(Collectors.toList());

    }


    public static void main(String[] args) {
        MapperDefine mapperDefine=new MapperDefine();
        mapperDefine.entityClazz= Album.class;
        mapperDefine.dealEntityClazzFiled();
    }


}
