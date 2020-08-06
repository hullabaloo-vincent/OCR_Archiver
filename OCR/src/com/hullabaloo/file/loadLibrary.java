package com.hullabaloo.file;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class loadLibrary implements Serializable{
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public ArrayList<String> name;

    public loadLibrary() {}

    public ArrayList<String> loadMe(String data){
        try{
        String programPath = System.getProperty("user.dir") + "/libdata";
        programPath = programPath.replace("\\", "/");
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(programPath + "/" + data));

        name = (ArrayList<String>) objectInputStream.readObject();
        objectInputStream.close();
        }catch(Exception ex){

        }
        return name;
    }
}