package xyz.xiezc.mzi.common;

import com.blade.ioc.bean.ClassInfo;
import lombok.Data;
import org.w3c.dom.Document;
import xyz.xiezc.mzi.config.BaseMapper;
import xyz.xiezc.mzi.config.Column;
import xyz.xiezc.mzi.config.Id;
import xyz.xiezc.mzi.config.Table;
import xyz.xiezc.mzi.entity.Album;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    Class<?> mapperInterface;

    /**
     * 关联的对应maper.xml文件
     */
    Document document;

    /**
     * 对应mapper.xml的名称
     */
    String xmlFileName;


    public void init(Class<?> mapperInterface){
        this.mapperInterface=mapperInterface;
        this.dealEntityClazz();
        this.dealEntityClazzFiled();
    }

    /**
     * 获取到Mapper接口配置的泛型对象
     */
    private void dealEntityClazz() {
        Class<?> clazz = mapperInterface;
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
         entityTableDefine = new EntityTableDefine();
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
        entityTableDefine.getTable().setProperty(this.entityClazz.getSimpleName());

        Field[] declaredFields = this.entityClazz.getDeclaredFields();
        //搜索出id, 如果搜不出来, 后面默认搜索字段名等于id的字段作为主键id
        List<Field> fields = Arrays.asList(declaredFields);


        fields = fields.stream()
                .filter(field -> {
                    Class<?> type = field.getType();
                    if ("serialVersionUID".equals(field.getName()) && (type == Long.class || type == long.class)) {
                        return false;
                    }
                    Column column = field.getAnnotation(Column.class);
                    return column==null||column.exist();
                })
                .filter(field -> {
                    Id id = field.getAnnotation(Id.class);
                    String columnName;
                    if (id != null) {
                        if (id.value().equals("")) {
                            columnName = StringUtil.underscoreName(field.getName());
                        } else {
                            columnName = id.value();
                        }
                        entityTableDefine.setId(new EntityTableDefine.ColumnProp());
                        entityTableDefine.getId().setColumn(columnName);
                        entityTableDefine.getId().setClazz(field.getType());
                        entityTableDefine.getId().setProperty(field.getName());
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());

        EntityTableDefine.ColumnProp id = entityTableDefine.getId();

        fields.stream().forEach(field -> {
            Column column = field.getAnnotation(Column.class);
            String columnName;
            if (column == null || column.value().equals("")) {
                String simpleName = field.getName();
                columnName = StringUtil.underscoreName(simpleName);
            } else {
                columnName = column.value();
            }
            Set<EntityTableDefine.ColumnProp> columns = entityTableDefine.getColumns();
            if (columns == null) {
                columns = new HashSet<>();
                entityTableDefine.setColumns(columns);
            }

            EntityTableDefine.ColumnProp columnProp = new EntityTableDefine.ColumnProp();
            columnProp.setColumn(columnName);
            columnProp.setClazz(field.getType());
            columnProp.setProperty(field.getName());
            if (id == null && columnName.equals("id")) {
                entityTableDefine.setId(columnProp);
            } else {
                columns.add(columnProp);
            }
        });
    }


    public static void main(String[] args) {
        MapperDefine mapperDefine = new MapperDefine();
        mapperDefine.entityClazz = Album.class;
        mapperDefine.dealEntityClazzFiled();
    }


}
