import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.awt.event.ActionEvent;
import java.awt.*;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;

import com.hullabaloo.gui.AppWindow;
import com.hullabaloo.file.createFileChooser;
import com.hullabaloo.file.directorySearch;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OCR_App {

    static boolean hasInterpretedDocs = false;
    static final DefaultListModel<String> calcJobs = new DefaultListModel<>();
    static final JList<String> calcJobsList = new JList<String>(calcJobs);

    static final DefaultListModel<String> fileArchive = new DefaultListModel<>();
    static final JList<String> fileArchiveList = new JList<String>(fileArchive);

    static Dictionary<String, String> interpretedDocs = new Hashtable<String, String>();
    static final ArrayList<String> foundFiles_SOURCE = new ArrayList<String>();
    static final ArrayList<String> foundFiles_NAME = new ArrayList<String>();

    static JMenuBar menu;
    static JMenu menuFile;
    static JMenuItem buttonChooseDirectory;
    static JMenuItem buttonInterpret;
    static JProgressBar jobProgress;
    static JPanel jobPanel;
    static JSplitPane contentJobPane;
    static JSplitPane splitPane;

    static JTextArea fileContents;
    static JScrollPane contentScroll;
    static JScrollPane scroll;
    static JScrollPane jobScroll;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException {

        /*---GUI INIT---*/
        JFrame window = new AppWindow("Document Analyzer", 800, 700);
        initComp();

        // Set window components
        window.setJMenuBar(menu);
        window.add(splitPane);

        // Refresh the window after adding new components
        window.invalidate();
        window.validate();
        window.repaint();

        /*---END OF GUI INIT---*/

        Tesseract tesseract = new Tesseract();

        buttonChooseDirectory.addActionListener((ActionEvent e) -> {
            createFileChooser fileChooser = new createFileChooser("Select a starting location",
                    "FILES_AND_DIRECTORIES");
            directorySearch ds = new directorySearch(fileChooser.getSelection());

            for (int fileIndex = 0; fileIndex < ds.getAllFileNames().size() - 1; fileIndex++) {
                fileArchive.addElement(ds.getAllFileNames().get(fileIndex));
                // foundFiles.put(ds.getAllFileNames().get(fileIndex),ds.getAllFilesSource().get(fileIndex));
                foundFiles_SOURCE.add(ds.getAllFilesSource().get(fileIndex));
                foundFiles_NAME.add(ds.getAllFileNames().get(fileIndex));
            }
        });

        buttonInterpret.addActionListener((ActionEvent e) -> {
            String programPath = System.getProperty("user.dir") + "/tessdata";
            programPath = programPath.replace("\\", "/");

            tesseract.setDatapath(programPath);

            SwingWorker worker = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() {
                    jobProgress.setMaximum(foundFiles_SOURCE.size());
                    for (int fileIndex = 0; fileIndex < foundFiles_SOURCE.size(); fileIndex++) {
                        calcJobs.addElement("Queued: " + foundFiles_NAME.get(fileIndex));
                        calcJobs.set(fileIndex, "Running: " + foundFiles_NAME.get(fileIndex));
                        String text = "";
                        try {
                            text = tesseract.doOCR(new File(foundFiles_SOURCE.get(fileIndex)));
                        } catch (TesseractException e) {
                            e.printStackTrace();
                        }
                        interpretedDocs.put(foundFiles_NAME.get(fileIndex), text);
                        calcJobs.set(fileIndex, "Completed: " + foundFiles_NAME.get(fileIndex));
                        jobProgress.setValue(fileIndex + 1);
                    }
                    return "";
                }

                @Override
                public void done() {
                    System.out.println("DONE");
                }
            };
            worker.execute();
            hasInterpretedDocs = true;
        });

        fileArchiveList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if (hasInterpretedDocs) {
                        fileContents.setText(interpretedDocs.get(fileArchiveList.getSelectedValue().toString()));
                    }
                }
            }
        });
    }

    public static void initComp() {
        menu = new JMenuBar();
        /*---FILE BUTTONS---*/
        menuFile = new JMenu("File");
        buttonChooseDirectory = new JMenuItem("Choose Directory");
        buttonInterpret = new JMenuItem("Interpret Documents");
        menu.add(menuFile);
        menuFile.add(buttonChooseDirectory);
        menuFile.add(buttonInterpret);

        scroll = new JScrollPane(fileArchiveList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jobScroll = new JScrollPane(calcJobsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        fileContents = new JTextArea();
        fileContents.setEditable(false);
        fileContents.setWrapStyleWord(true);
        contentScroll = new JScrollPane(fileContents, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jobProgress = new JProgressBar(0, 10);
        jobPanel = new JPanel();
        jobPanel.setLayout(new GridLayout(2, 0));
        jobPanel.add(BorderLayout.NORTH, jobScroll);
        jobPanel.add(BorderLayout.SOUTH, jobProgress);

        contentJobPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contentScroll, jobPanel);
        contentJobPane.setDividerLocation(500);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, contentJobPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);
    }
} 
