package com.hullabaloo.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Vincent
 */
public class directorySearch {
    
    public directorySearch(String directory){
        final ArrayList<String> allFiles = new ArrayList<String>();
        final ArrayList<String> allFiles_SOURCE = new ArrayList<String>();
        
        try {
            Path startPath = Paths.get(directory);
                Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {
                    String readF = new Scanner(new File("filter.fl")).useDelimiter("\\A").next();
                    String splitHere = "[,]";
                    int l = 0;
                    String[] tokens = readF.split(splitHere); //Split up contents of filter.fl every ',' into a string array
                    while (l != tokens.length) { //Tests each file with the filters found in the string array (tokens)
                        String value = tokens[l];
                        if (file.toString().endsWith(value)) {
                            String whole = file.toString().replace("\\", "/");
                            String name = file.getFileName().toString();
                            if (allFiles.contains(name)) {

                            } else {
                                allFiles.add(name);
                                allFiles_SOURCE.add(whole);
                            }
                        }
                        l += 1;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex){
                //
        }
    }
}