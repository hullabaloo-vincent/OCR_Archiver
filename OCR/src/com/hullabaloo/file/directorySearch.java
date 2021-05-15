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

    final ArrayList<String> allFiles = new ArrayList<String>();
    final ArrayList<String> allFiles_SOURCE = new ArrayList<String>();

    public directorySearch(String directory){

        try {
            Path startPath = Paths.get(directory);
                Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path dir,
                            final BasicFileAttributes attrs) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file,
                final BasicFileAttributes attrs)
                            throws FileNotFoundException {
                    File file_loc = new File("filter.fl");
                    Scanner sc = new Scanner(file_loc);
                    String readF = sc.useDelimiter("\\A").next();
                    sc.close();
                    String splitHere = "[,]";
                    int l = 0;
//Split up contents of filter.fl every ',' into a string array
                    String[] tokens = readF.split(splitHere);
//Tests each file with the filters found in the string array (tokens)
                    while (l != tokens.length) {
                        String value = tokens[l];
                        if (file.toString().endsWith(value)) {
                            String whole = file.toString().replace("\\", "/");
                            String name = file.getFileName().toString();
                            if (!allFiles.contains(name)) {
                                allFiles.add(name);
                                allFiles_SOURCE.add(whole);
                            }
                        }
                        l += 1;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file,
                final IOException e) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception ex) {
                System.out.println("Error: " + ex);
        }
    }

    /**
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllFilesSource() {
        return allFiles_SOURCE;
    }

    /**
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllFileNames() {
        return allFiles;
    }
}