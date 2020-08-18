package com.hullabaloo.file;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

/**
 *
 * @author Vincent
 */
public class OpenFileLocation extends JMenuItem {

    /**
     *
     */
    private static final long serialVersionUID = 2907029936241494739L;

    public OpenFileLocation(String loc) throws IOException {
        setText("Open file location");
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File dir = new File(loc.replace("\\", "/"));
                File dir2 = new File(loc.replace(dir.getName(), ""));
                System.out.println(dir2.toString());
                try {
                    Desktop.getDesktop().open(dir2);
                } catch (IOException ex) {
                    System.out.println("IO Exception: " + ex);
                }
            }
        });
    }
}
