package com.fsggs.server.utils;

import com.fsggs.server.Application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    static public Path getApplicationPath() {
        Path path;
        if (isRunnedInJar()) {
            path = Paths.get(System.getProperty("java.class.path"));
            return path.toAbsolutePath().getParent();
        } else {
            path = Paths.get(System.getProperty("user.dir"));
            return path.toAbsolutePath();
        }
    }

    static public boolean isRunnedInJar() {
        URL path = Application.class.getResource("Application.class");
        return path.toString().startsWith("jar:");
    }

    public static List<Path> fileList(Path directory) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
