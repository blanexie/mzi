package xyz.xiezc.mzi.common.resourceReader;

import java.nio.file.Path;
import java.util.Set;

public interface ResourceReader {

    Set<Path> readResources(String packageName, String endWith, boolean recursive);

}
