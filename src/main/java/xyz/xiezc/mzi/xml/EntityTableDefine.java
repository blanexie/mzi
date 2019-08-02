package xyz.xiezc.mzi.xml;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class EntityTableDefine {



public static final String ExampleName="xyz.xiezc.mzi.common.Example";
    private ColumnProp table;
    private ColumnProp id;
    private Set<ColumnProp> columns;


    @Setter
    @Getter
    @EqualsAndHashCode
    public static class ColumnProp {
        Class<?> clazz;
        String column;
        String property;
    }

}
