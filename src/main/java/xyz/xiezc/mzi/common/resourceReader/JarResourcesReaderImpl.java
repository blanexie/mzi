package xyz.xiezc.mzi.common.resourceReader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class JarResourcesReaderImpl extends AbstractResourceReader {
    private static final String JAR_FILE = "jar:file:";
    private static final String WSJAR_FILE = "wsjar:file:";


    @Override
    public Set<Path> getResourcesByAnnotation(String packageName, String endWith, boolean recursive) {
        Set<Path> classes = new HashSet<>();
        // Get the name of the package and replace it
        String packageDirName = packageName.replace('.', '/');
        // Defines an enumerated collection and loops to process the URL in this directory
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                // Next
                URL url = dirs.nextElement();
                Set<Path> subClasses = this.getClasses(packageDirName, url, endWith);
                if (subClasses.size() > 0) {
                    classes.addAll(subClasses);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return classes;

    }

    private Set<Path> getClasses(final String packageDirName, final URL url, final String endWith) {
        Set<Path> set = new HashSet<>();
        try {
            if (url.toString().startsWith(JAR_FILE) || url.toString().startsWith(WSJAR_FILE)) {
                // Get jar file
                JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                // From the jar package to get an enumeration class
                Enumeration<JarEntry> eje = jarFile.entries();
                while (eje.hasMoreElements()) {
                    // Get an entity in jar can be a directory and some other documents in the jar package
                    // such as META-INF and other documents
                    JarEntry entry = eje.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith(packageDirName)) {
                        continue;
                    }
                    if (endWith != null && !endWith.equals("")) {
                        // If it is a .class file and not a directory
                        if (!name.endsWith(endWith) || entry.isDirectory()) {
                            continue;
                        }
                    }

                    set.add(Paths.get(name));
                }
            }
        } catch (IOException e) {
            log.error("The scan error when the user to define the view from a jar package file.", e);
        }
        return set;
    }


}
