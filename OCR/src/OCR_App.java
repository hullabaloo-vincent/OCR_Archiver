
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hullabaloo.gui.AppWindow;
import com.hullabaloo.file.createFileChooser;
import com.hullabaloo.file.directorySearch;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR_App {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
        IllegalAccessException, UnsupportedLookAndFeelException {
        JFrame window =  new AppWindow("Document Analyzer", 800, 700);

        JPanel centerPanel = new JPanel();
        GridLayout frameLayout = new GridLayout(0,2);
        centerPanel.setLayout(frameLayout);
 
        JMenuBar menu = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem buttonChooseDirectory = new JMenuItem("Choose Directory");
        JMenuItem buttonInterpret = new JMenuItem("Interpret Documents");

        menu.add(menuFile);
        menuFile.add(buttonChooseDirectory);
        menuFile.add(buttonInterpret);

        JTextField searchBar = new JTextField("Search");
        searchBar.setPreferredSize(new Dimension(150,50));

        JPanel bottomPanel = new JPanel();
        JTextArea searchedCriteria = new JTextArea();
        bottomPanel.add(searchedCriteria);
        window.setJMenuBar(menu);
        window.add(BorderLayout.NORTH, searchBar);
        window.add(BorderLayout.CENTER, centerPanel);
        window.add(BorderLayout.SOUTH, bottomPanel);

        window.invalidate();
        window.validate();
        window.repaint();


        Tesseract tesseract = new Tesseract(); 
        
        buttonChooseDirectory.addActionListener((ActionEvent e) -> {
            createFileChooser fileChooser = new createFileChooser("Select a starting location", "FILES_AND_DIRECTORIES");
             directorySearch ds = new directorySearch(fileChooser.getSelection());
        });
        
       /* try { 
  
            tesseract.setDatapath("D:/OneDrive/OO/OO_Software/ORC/OCR_Organizer/OCR_Archiver/OCR/tessdata"); 
  
            String text 
                = tesseract.doOCR(new File("D:/OneDrive/Word Docs/Aliquo Stories/Consumption/NYT_Consumption_01.pdf")); 

            System.out.print(text); 
        } 
        catch (TesseractException e) { 
            e.printStackTrace(); 
        } */

        /*
         } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Problem opening file");
                }
        */
    }
} 
