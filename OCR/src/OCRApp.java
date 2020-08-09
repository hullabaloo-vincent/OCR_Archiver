import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hullabaloo.file.CreateFileChooser;
import com.hullabaloo.file.directorySearch;
import com.hullabaloo.file.LoadLibrary;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


/**
 *
 * @author Vincent Aliquo
 * http://www.aliquodigitalportfolio.com/
 * https://github.com/hullabaloo-vincent
 *
 */
public class OCRApp extends JFrame {

    private static final long serialVersionUID = 1L;

    static boolean hasInterpretedDocs = false;
    static final DefaultListModel<String> calcJobs = new DefaultListModel<>();
    static final DefaultListModel<String> fileArchive = new DefaultListModel<>();
    static Dictionary<String, String> interpretedDocs = new Hashtable<String, String>();
    static final ArrayList<String> foundFiles_SOURCE = new ArrayList<String>();
    static ArrayList<String> foundFiles_NAME = new ArrayList<String>();

    public OCRApp() {
        initComponents();
    }

    private void initComponents() {
        menubar = new JMenuBar();
        menu = new JMenu("File");
        loadLibrary = new JMenuItem("Load Library");
        clearLibrary = new JMenuItem("Clear Library");
        langaugeModels = new JMenuItem("Choose Language Models");
        scrollFileList = new JScrollPane();
        calcJobsList = new JList<String>(calcJobs);
        fileArchiveList = new JList<String>(fileArchive);
        tabbedContent = new JTabbedPane();
        jScrollPane2 = new JScrollPane();
        fileContents = new JTextArea();
        jobPanel = new JPanel();
        jobProgress = new JProgressBar();
        jScrollPane3 = new JScrollPane();
        labelLibrarySection = new JLabel();
        searchFiles = new JTextField();
        labelSearch = new JLabel();
        chooseDirectory = new JButton();
        buttonInterpret = new JButton();
        clarifyImage = new JCheckBox();

        /*---SET UP WINDOW PARAMETERS---*/
        setJMenuBar(menubar);
        setTitle("Document Analyzer");
        setMinimumSize(new Dimension(700, 450));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /*----------------------------- */

        menubar.add(menu);
        menu.add(loadLibrary);
        menu.add(clearLibrary);
        menu.addSeparator();
        menu.add(langaugeModels);

        scrollFileList.setViewportView(fileArchiveList);
        scrollFileList.setPreferredSize(new Dimension(scrollFileList.getPreferredSize().height, 400));
        fileContents.setColumns(20);
        fileContents.setRows(5);
        fileContents.setEditable(false);
        jScrollPane2.setViewportView(fileContents);

        tabbedContent.addTab("File Content", jScrollPane2);
        jScrollPane3.setViewportView(calcJobsList);

        GroupLayout jobPanelLayout = new GroupLayout(jobPanel);
        jobPanel.setLayout(jobPanelLayout);
        jobPanelLayout.setHorizontalGroup(
            jobPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addComponent(jobProgress,
                GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
        );
        jobPanelLayout.setVerticalGroup(
            jobPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jobPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane3,
                    GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jobProgress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        tabbedContent.addTab("Job List", jobPanel);

        labelLibrarySection.setFont(new java.awt.Font("Tahoma", 1, 10));
        labelLibrarySection.setText("Library");

        searchFiles.setToolTipText("");

        labelSearch.setFont(new java.awt.Font("Tahoma", 1, 10));
        labelSearch.setText("Search");

        Tesseract tesseract = new Tesseract();

        chooseDirectory.setText("Load Library");
        chooseDirectory.addActionListener((ActionEvent e) -> {
            CreateFileChooser fileChooser = new CreateFileChooser("Select a starting location",
                    "FILES_AND_DIRECTORIES");
            directorySearch ds = new directorySearch(fileChooser.getSelection());

            for (int fileIndex = 0; fileIndex < ds.getAllFileNames().size() - 1; fileIndex++) {
                fileArchive.addElement(ds.getAllFileNames().get(fileIndex));
                foundFiles_SOURCE.add(ds.getAllFilesSource().get(fileIndex));
                foundFiles_NAME.add(ds.getAllFileNames().get(fileIndex));
            }
            buttonInterpret.setEnabled(true);
            saveLibrary("names");
            saveLibrary("sources");
        });

        buttonInterpret.setText("Analyze Documents");
        buttonInterpret.setEnabled(false);
        buttonInterpret.addActionListener((ActionEvent e) -> {
            tabbedContent.setSelectedIndex(1); //set to job content panel
            buttonInterpret.setEnabled(false);
            String programPath = System.getProperty("user.dir") + "/tessdata";
            programPath = programPath.replace("\\", "/");

            tesseract.setDatapath(programPath);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
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
                    saveLibrary("content");
                }
            };
            worker.execute();
            hasInterpretedDocs = true;
        });

        fileArchiveList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if (hasInterpretedDocs) {
                        fileContents.setText(interpretedDocs.get(
                            fileArchiveList.getSelectedValue().toString()));
                    }
                }
            }
        });

        loadLibrary.addActionListener((ActionEvent e) -> {
            ArrayList<String> temp = new LoadLibrary().loadMe("names.libdata");
            for (int i = 0; i < temp.size(); i++){
                fileArchive.addElement(temp.get(i));
            }
        });

        clarifyImage.setText("Clarify Images");
        clarifyImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                clarifyImageActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(
                    GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollFileList)
                    .addComponent(labelLibrarySection,
                    GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(
                    GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chooseDirectory)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonInterpret)
                        .addPreferredGap(
                            LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(clarifyImage,
                            GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                        .addGap(42, 42, 42)
                        .addComponent(labelSearch)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchFiles,
                            GroupLayout.PREFERRED_SIZE, 187,
                            GroupLayout.PREFERRED_SIZE))
                    .addComponent(tabbedContent,
                        GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(
                    GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseDirectory)
                    .addComponent(buttonInterpret)
                    .addComponent(clarifyImage)
                    .addComponent(labelLibrarySection, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelSearch, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchFiles))
                .addPreferredGap(
                    LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(
                    GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollFileList)
                    .addComponent(tabbedContent)))
        );

        pack();
    }

    /**
     * @param type
     * Saves the currently loaded file names, source path, and content
     */
    public void saveLibrary(final String type) {
        try {
            String programPath = System.getProperty("user.dir") + "/libdata";
            programPath = programPath.replace("\\", "/");
            //NAMES
            if (type == "names") {
                FileOutputStream fosNames = new FileOutputStream(
                    programPath + "/names.libdata");
                ObjectOutputStream oosNames = new ObjectOutputStream(
                    fosNames);
                oosNames.writeObject(foundFiles_NAME);
                oosNames.close();
            }
            //SOURCES
            if (type == "sources") {
                FileOutputStream fosSources = new FileOutputStream(
                    programPath + "/source.libdata");
                ObjectOutputStream oosSources = new ObjectOutputStream(
                    fosSources);
                oosSources.writeObject(foundFiles_SOURCE);
                oosSources.close();
            }
            //CONTENT
            if (type == "content") {
                FileOutputStream fosContent = new FileOutputStream(
                    programPath + "/content.libdata");
                ObjectOutputStream oosContent = new ObjectOutputStream(
                    fosContent);
                oosContent.writeObject(interpretedDocs);
                oosContent.close();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * @param evt
     * Whether the program will take extra time to enhance images for
     * better readability
     */
    private void clarifyImageActionPerformed(
        final java.awt.event.ActionEvent evt) {
    }

    /**
     * @param args
     * Runs the main program and sets UI style
     */
    public static void main(final String[] args) {
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        try {
            for (UIManager.LookAndFeelInfo info
             : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OCRApp().setVisible(true);
            }
        });
    }

    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem loadLibrary;
    private JMenuItem clearLibrary;
    private JMenuItem langaugeModels;
    private JButton chooseDirectory;
    private JCheckBox clarifyImage;
    private JLabel labelLibrarySection;
    private JLabel labelSearch;
    private JList<String> calcJobsList;
    private JList<String> fileArchiveList;
    private JPanel jobPanel;
    private JProgressBar jobProgress;
    private JScrollPane scrollFileList;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTabbedPane tabbedContent;
    private JTextArea fileContents;
    private JTextField searchFiles;
    private JButton buttonInterpret;
}
