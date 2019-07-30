package xyz.xiezc.mzi;

import com.blade.Blade;
import xyz.xiezc.mzi.config.MapperScan;
import xyz.xiezc.mzi.config.MybatisBladeLoader;


@MapperScan("xyz.xiezc.mzi.dao")
public class App {
    public static void main(String[] args) {
        Blade.of().addLoader(new MybatisBladeLoader()).gzip(true).start(App.class, args);
    }
}
