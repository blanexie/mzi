package xyz.xiezc.mzi.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
public class MziDataSourceFactory implements DataSourceFactory {

    private DataSource dataSource;

    @Override
    public void setProperties(Properties props) {
        try {
            dataSource = com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(props);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("init data source error", e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
