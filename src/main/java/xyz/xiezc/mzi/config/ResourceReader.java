package xyz.xiezc.mzi.config;

import java.io.InputStream;
import java.util.Set;

public interface ResourceReader {

    Set<InputStream> readResources(String packageName, String endWith, boolean recursive) throws Exception;

}
