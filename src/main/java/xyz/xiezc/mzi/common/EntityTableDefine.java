package xyz.xiezc.mzi.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class EntityTableDefine {



  private  ColumnProp table;
  private  ColumnProp id;
    private Set<ColumnProp> columns;


    @Setter
    @Getter
    @EqualsAndHashCode
    public static class ColumnProp {
        Class<?> clazz;
        String column;
    }

}
