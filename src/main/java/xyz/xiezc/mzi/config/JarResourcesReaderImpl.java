package xyz.xiezc.mzi.config;

import com.blade.ioc.bean.ClassInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class JarResourcesReaderImpl extends AbstractResourceReader {
    private static final String JAR_FILE   = "jar:file:";
    private static final String WSJAR_FILE = "wsjar:file:";

    @Override
    public Set<File> getResourcesByAnnotation(String packageName, String endWith, boolean recursive) {
        Set<ClassInfo> classes = new HashSet<>();
        // Get the name of the package and replace it
        String packageDirName = packageName.replace('.', '/');
        // Defines an enumerated collection and loops to process the URL in this directory
        Enumeration<URL> dirs;
        try {
            dirs = this.getClass().getClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                // Next
                URL            url        = dirs.nextElement();
                Set<ClassInfo> subClasses = this.getClasses(url, packageDirName, packageName, parent, annotation, recursive, classes);
                if (subClasses.size() > 0) {
                    classes.addAll(subClasses);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return classes;
    }

    private Set<ClassInfo> getClasses(final URL url, final String packageDirName, String packageName, final String endWith,
                                      final Class<? extends Annotation> annotation, final boolean recursive, Set<ClassInfo> classes) {
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
                    String   name  = entry.getName();
                    // if start with '/'
                    if (name.charAt(0) == '/') {
                        name = name.substring(1);
                    }
                    // If the first half is the same as the defined package name
                    if (!name.startsWith(packageDirName)) {
                        continue;
                    }
                    int idx = name.lastIndexOf('/');
                    // If the end of "/" is a package
                    if (idx != -1) {
                        // Get the package name and replace "/" with "."
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    // If it can be iterated and is a package
                    if (idx == -1 && !recursive) {
                        continue;
                    }
                    if(endWith!=null&&!endWith.equals("")){
                        // If it is a .class file and not a directory
                        if (!name.endsWith(endWith) || entry.isDirectory()) {
                            continue;
                        }
                    }


                }
            }
        } catch (IOException e) {
            log.error("The scan error when the user to define the view from a jar package file.", e);
        } catch (ClassNotFoundException e) {
            log.error("", e);
        }
        return classes;
    }


}
