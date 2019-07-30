package xyz.xiezc.mzi.config;

import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class AbstractResourceReader implements ResourceReader {
    @Override
    public Set<InputStream> readResources(String packageName, String endWith, boolean recursive) {
        Set<InputStream> resourcesByAnnotation = this.getResourcesByAnnotation(packageName, endWith, recursive);
        return resourcesByAnnotation;
    }

    /**
     * Get class by condition
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @return
     */
    private Set<File> findResourcesByPackage(final String packageName, final String packagePath,
                                             final String endWith, final boolean recursive) {
        Set<File> resourceSet = new HashSet<>();
        // Get the directory of this package to create a File
        File dir = new File(packagePath);
        // If not exist or is not a direct return to the directory
        if ((!dir.exists()) || (!dir.isDirectory())) {
            log.warn("The package [{}] not found.", packageName);
        }
        // If present, get all the files under the package include the directory
        File[] dirFiles = accept(dir, endWith, recursive);
        // Loop all files
        if (null != dirFiles && dirFiles.length > 0) {
            for (File file : dirFiles) {
                // If it is a directory, continue scanning
                if (file.isDirectory()) {
                    Set<File> ret = findResourcesByPackage(packageName + '.' + file.getName(), file.getAbsolutePath(), endWith, recursive);
                    resourceSet.addAll(ret);
                } else {
                    if (file.exists() && file.canRead()) {
                        resourceSet.add(file);
                        continue;
                    }
                }
            }
        }
        return resourceSet;
    }

    /**
     * Filter the file rules
     *
     * @param file
     * @param recursive
     * @return
     */
    private File[] accept(File file, final String endWith, final boolean recursive) {
        if (StringUtil.isNullOrEmpty(endWith)) {
            // Custom filtering rules If you can loop (include subdirectories) or is the end of the file. Class (compiled java class file)
            return file.listFiles(file1 -> (recursive && file1.isDirectory()));
        }
        if (endWith.startsWith(".")) {
            // Custom filtering rules If you can loop (include subdirectories) or is the end of the file. Class (compiled java class file)
            return file.listFiles(file1 -> (recursive && file1.isDirectory()) || (file1.getName().endsWith(endWith)));
        } else {
            // Custom filtering rules If you can loop (include subdirectories) or is the end of the file. Class (compiled java class file)
            return file.listFiles(file1 -> (recursive && file1.isDirectory()) || (file1.getName().endsWith("." + endWith)));
        }
    }

    public Set<InputStream> getResourcesByAnnotation(String packageName, final String endWith, boolean recursive) {
        Set<InputStream> ret = new HashSet<>();
        // Get the name of the package and replace it
        String packageDirName = packageName.replace('.', '/');
        // Defines an enumerated collection and loops to process the URL in this directory
        Enumeration<URL> dirs;
        try {
            Set<File> resourceSet = new HashSet<>();
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            // Loop iterations down
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String filePath = new URI(url.getFile()).getPath();
                Set<File> subResources = findResourcesByPackage(packageName, filePath, endWith, recursive);
                resourceSet.addAll(subResources);
            }
            for (File file : resourceSet) {
                ret.add(new FileInputStream(file));
            }
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }

        return ret;
    }

}
