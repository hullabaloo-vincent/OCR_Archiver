package com.hullabaloo.file;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class createFileChooser {

    private JButton open;
    private String fcName;
    private String fcSelection;
    private String selection;

    public createFileChooser(String chooserName, String fileSelection){
        fcName = chooserName;
        fcSelection = fileSelection;
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File("user.home"));
        fc.setDialogTitle(fcName);
        if (fcSelection == "FILES_AND_DIRECTORIES") {
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        if (fcSelection == "FILES_ONLY") {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if (fcSelection == "DIRECTORIES_ONLY") {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION) {
            System.out.println("Dir: "
                    + fc.getCurrentDirectory());
            System.out.println("File: "
                    + fc.getSelectedFile());
        }
        selection = fc.getSelectedFile().getAbsolutePath();
    }
    public String getSelection() {
        return selection;
    }
}