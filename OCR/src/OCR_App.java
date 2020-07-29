
import java.io.File;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.UnsupportedLookAndFeelException;

import com.hullabaloo.gui.AppWindow;
import com.hullabaloo.file.createFileChooser;
import com.hullabaloo.file.directorySearch;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR_App {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,IllegalAccessException, UnsupportedLookAndFeelException {
        
        /*---GUI INIT---*/
        JFrame window =  new AppWindow("Document Analyzer", 800, 700);
 
        //File buttons
        JMenuBar menu = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem buttonChooseDirectory = new JMenuItem("Choose Directory");
        JMenuItem buttonInterpret = new JMenuItem("Interpret Documents");
        menu.add(menuFile);
        menuFile.add(buttonChooseDirectory);
        menuFile.add(buttonInterpret);

        //List of found files
        final DefaultListModel<String> fileArchive = new DefaultListModel<>(); //Holds the locations of the files
        final JList<String> fileArchiveList = new JList<String>(fileArchive); //Displays model1
        final JScrollPane scroll = new JScrollPane(fileArchiveList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //Scrollbar for model1

        JTextArea fileContents = new JTextArea();
        fileContents.setEditable(false);

        //Display the JList with the name and the content displayed in the JTextArea
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scroll, fileContents);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        //Set window components
        window.setJMenuBar(menu);
        window.add(splitPane);

        //Refresh the window after adding new components
        window.invalidate();
        window.validate();
        window.repaint();

        /*---END OF GUI INIT---*/

        final ArrayList<String> foundFiles_SOURCE = new ArrayList<String>();

        Tesseract tesseract = new Tesseract(); 
        
        buttonChooseDirectory.addActionListener((ActionEvent e) -> {
            createFileChooser fileChooser = new createFileChooser("Select a starting location", "FILES_AND_DIRECTORIES");
            directorySearch ds = new directorySearch(fileChooser.getSelection());

            for (int fileIndex = 0; fileIndex < ds.getAllFileNames().size()-1; fileIndex++){
                fileArchive.addElement(ds.getAllFileNames().get(fileIndex));
                foundFiles_SOURCE.add(ds.getAllFilesSource().get(fileIndex));
            }
        });

        buttonInterpret.addActionListener((ActionEvent e) -> {
            try { 
                String programPath = System.getProperty("user.dir")+"/tessdata";
                programPath = programPath.replace("\\", "/");
                tesseract.setDatapath(programPath); 
                
                for (int fileIndex = 0; fileIndex < foundFiles_SOURCE.size(); fileIndex++){
                    String text 
                        = tesseract.doOCR(new File(foundFiles_SOURCE.get(fileIndex))); 
    
                    System.out.print(text); 
                }
            } 
            catch (TesseractException TE) { 
                TE.printStackTrace(); 
            } 
        });
    }
} 
