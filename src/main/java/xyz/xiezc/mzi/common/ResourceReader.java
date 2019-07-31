package xyz.xiezc.mzi.common;

import java.nio.file.Path;
import java.util.Set;

public interface ResourceReader {

    Set<Path> readResources(String packageName, String endWith, boolean recursive);

}
