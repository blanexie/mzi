package xyz.xiezc.mzi.config;

import com.blade.ioc.bean.Scanner;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Set;

public interface ResourceReader {

    Set<Reader> readResources(String packageName, String endWith, boolean recursive);

}
