package com.hullabaloo.file;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;

public class LoadLibrary implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public ArrayList<String> name;
    public Dictionary<String, String> content;

    public LoadLibrary() {}

    /**
     * @param data
     * @return ArrayList<String>
     */
    public ArrayList<String> loadMe(final String data) {
        try {
        String programPath = System.getProperty("user.dir") + "/libdata";
        programPath = programPath.replace("\\", "/");
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(programPath + "/" + data));

        name = (ArrayList<String>) objectInputStream.readObject();
        Dictionary<String, String> name2 = (Dictionary<String, String>) objectInputStream.readObject();
        objectInputStream.close();
        } catch (Exception ex) {
            System.out.println("Error: " + ex);
        }
        return name;
    }

    public Dictionary<String, String> loadContent(final String data){
        try {
            String programPath = System.getProperty("user.dir") + "/libdata";
            programPath = programPath.replace("\\", "/");
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(programPath + "/" + data));
    
            content = (Dictionary<String, String>) objectInputStream.readObject();
            Dictionary<String, String> name2 = (Dictionary<String, String>) objectInputStream.readObject();
            objectInputStream.close();
            } catch (Exception ex) {
                System.out.println("Error: " + ex);
            }
            return content;
    }
}
