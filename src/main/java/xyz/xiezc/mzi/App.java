package xyz.xiezc.mzi;

import com.blade.Blade;
import xyz.xiezc.mzi.common.annotation.MapperScan;
import xyz.xiezc.mzi.config.MybatisBladeLoader;


@MapperScan("xyz.xiezc.mzi.dao")
public class App {
    public static void main(String[] args) {
        Blade.of().gzip(true).start(App.class, args);
    }
}
