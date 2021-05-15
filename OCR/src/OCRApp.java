import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import java.awt.Desktop;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hullabaloo.file.CreateFileChooser;
import com.hullabaloo.file.directorySearch;
import com.hullabaloo.file.LoadLibrary;
import com.hullabaloo.file.OpenFileLocation;

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
    static Point compCoords;

    public OCRApp() {
        initComponents();
    }

    private void initComponents() {
        menubar = new JMenuBar();
        menu = new JMenu("File");
        loadLibrary = new JMenuItem("Load Library");
        clearLibrary = new JMenuItem("Clear Library");
        langaugeModels = new JMenuItem("Choose Language Models");
        exitProgram = new JMenuItem("Exit");
        minimizeProgram = new JMenuItem("Minimize");
        scrollFileList = new JScrollPane();
        calcJobsList = new JList<String>(calcJobs);
        fileArchiveList = new JList<String>(fileArchive);
        searchResultsList = new JEditorPane();
        tabbedContent = new JTabbedPane();
        jScrollPane2 = new JScrollPane();
        fileContents = new JTextArea();
        jobPanel = new JPanel();
        jobProgress = new JProgressBar();
        jScrollPane3 = new JScrollPane();
        searchScrollPane = new JScrollPane(searchResultsList,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
        setUndecorated(true);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /*----------------------------- */

        menubar.add(menu);
        menu.add(loadLibrary);
        menu.add(clearLibrary);
        menu.addSeparator();
        menu.add(langaugeModels);
        menu.addSeparator();
        menu.add(minimizeProgram);
        menu.add(exitProgram);

        scrollFileList.setViewportView(fileArchiveList);
        scrollFileList.setPreferredSize(new Dimension(
            scrollFileList.getPreferredSize().height, 400));
        fileContents.setColumns(20);
        fileContents.setRows(5);
        fileContents.setEditable(false);
        jScrollPane2.setViewportView(fileContents);

        tabbedContent.addTab("File Content", jScrollPane2);
        jScrollPane3.setViewportView(calcJobsList);

        final GroupLayout jobPanelLayout = new GroupLayout(jobPanel);
        jobPanel.setLayout(jobPanelLayout);
        jobPanelLayout.setHorizontalGroup(jobPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3).addComponent(jobProgress, GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE));
        jobPanelLayout.setVerticalGroup(jobPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jobPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jobProgress,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));

        tabbedContent.addTab("Job List", jobPanel);

        labelLibrarySection.setFont(new java.awt.Font("Tahoma", 1, 10));
        labelLibrarySection.setText("Library");

        searchFiles.setToolTipText("");
        searchResultsList.setContentType("text/html");
        labelSearch.setFont(new java.awt.Font("Tahoma", 1, 10));
        labelSearch.setText("Search");
        tabbedContent.addTab("Search Results", searchScrollPane);

        final Action action = new AbstractAction() {
            /**
            *
            */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println("some action");
                List<String> tokens = new ArrayList<String>();
                tokens.add(searchFiles.getText());
                System.out.println(tokens);
                String patternString = "\\b(" + StringUtils.join(tokens, "|") + ")\\b";
                Pattern pattern = Pattern.compile(patternString);
                String results = "";
                for (String item_name : foundFiles_NAME){
                    String text = interpretedDocs.get(item_name);
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        int startIndex = 0;
                        int endIndex = 0;
                        if (matcher.start() > 200){
                            startIndex = matcher.start()-200;
                        }
                        if (text.length() > (matcher.end() + 200)){
                            endIndex = matcher.end() + 200;
                        } else {
                            endIndex = text.length();
                        }
                        results = results + "<h3>FOUND: " + matcher.group(1) + " in " + item_name + "</h3></br>-----------</br></br>" + text.substring(startIndex, matcher.start()) + "<b style='color:blue;'>" + text.substring(matcher.start(), matcher.end()) + "</b>" + text.substring(matcher.end(), endIndex) +  "</b></br>";
                    }
                }
                searchResultsList.setText(results);
            }
        };
        searchFiles.addActionListener(action);
        searchFiles.setEditable(false);
        searchFiles.setToolTipText("Press enter after you are finished typing");

        /*
         * Window moving
         */
        compCoords = null;
        menubar.addMouseListener(new MouseListener() {
            public void mouseReleased(final MouseEvent e) {
                compCoords = null;
            }

            public void mousePressed(final MouseEvent e) {
                compCoords = e.getPoint();
            }

            public void mouseExited(final MouseEvent e) {
            }

            public void mouseEntered(final MouseEvent e) {
            }

            public void mouseClicked(final MouseEvent e) {
            }
        });
        menubar.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(final MouseEvent e) {
            }

            public void mouseDragged(final MouseEvent e) {
                final Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - compCoords.x, currCoords.y - compCoords.y);
            }
        });

        final Tesseract tesseract = new Tesseract();

        chooseDirectory.setText("Load Library");
        chooseDirectory.addActionListener((final ActionEvent e) -> {
            final CreateFileChooser fileChooser = new CreateFileChooser("Select a starting location",
                    "FILES_AND_DIRECTORIES");
            final directorySearch ds = new directorySearch(fileChooser.getSelection());

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
        buttonInterpret.addActionListener((final ActionEvent e) -> {
            tabbedContent.setSelectedIndex(1); // set to job content panel
            buttonInterpret.setEnabled(false);
            String programPath = System.getProperty("user.dir") + "/tessdata";
            programPath = programPath.replace("\\", "/");

            tesseract.setDatapath(programPath);
            final SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() {
                    jobProgress.setMaximum(foundFiles_SOURCE.size());
                    for (int fileIndex = 0; fileIndex < foundFiles_SOURCE.size(); fileIndex++) {
                        calcJobs.addElement("Queued: " + foundFiles_NAME.get(fileIndex));
                        calcJobs.set(fileIndex, "Running: " + foundFiles_NAME.get(fileIndex));
                        String text = "";
                        try {
                            text = tesseract.doOCR(new File(foundFiles_SOURCE.get(fileIndex)));
                        } catch (final TesseractException e) {
                            e.printStackTrace();
                        } catch (final Exception ee) {
                            ee.printStackTrace();
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
                    searchFiles.setEditable(true);
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
                        fileContents.setText(interpretedDocs.get(fileArchiveList.getSelectedValue().toString()));  
                    }
                }
            }
        });

        loadLibrary.addActionListener((final ActionEvent e) -> {
            final ArrayList<String> loadName = new LoadLibrary().loadMe("names.libdata");
            final ArrayList<String> loadSource = new LoadLibrary().loadMe("source.libdata");
            final Dictionary<String, String> loadContent = new LoadLibrary().loadContent("content.libdata");

            for (int i = 0; i < loadName.size(); i++) {
                fileArchive.addElement(loadName.get(i));
                foundFiles_NAME.add(i, loadName.get(i));
                foundFiles_SOURCE.add(i, loadSource.get(i));
            }
            for (int i = 0; i < loadContent.size(); i++) {
                interpretedDocs.put(loadName.get(i), loadContent.get(loadName.get(i)));
            }
            if (loadContent.size() > 0){
                hasInterpretedDocs = true;
            }
            searchFiles.setEditable(true);
            buttonInterpret.setEnabled(true);
        });

        clarifyImage.setText("Clarify Images");
        clarifyImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                clarifyImageActionPerformed(evt);
            }
        });

        exitProgram.addActionListener((final ActionEvent e) -> {
            System.exit(0);
        });

        minimizeProgram.addActionListener((final ActionEvent e) -> {
            setState(JFrame.ICONIFIED);
        });

        fileArchiveList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    final String selection = fileArchiveList.getSelectedValue().toString();
                    final JPopupMenu popup = new JPopupMenu();
                    JMenuItem openFileLocation = null;
                    if (Desktop.isDesktopSupported()) {
                        try {
                            openFileLocation = new OpenFileLocation(foundFiles_SOURCE.get(foundFiles_NAME.indexOf(selection)));
                            final ImageIcon openFL_icon = new ImageIcon("Img/open-sm.png");
                            openFileLocation.setIcon(openFL_icon);
                        } catch (final IOException ex) {
                            System.out.println("IO Exception: " + ex);
                        }
                    }
                    popup.add(openFileLocation);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        final GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(scrollFileList)
                        .addComponent(labelLibrarySection, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addComponent(chooseDirectory)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(buttonInterpret)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(clarifyImage, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                                .addGap(42, 42, 42).addComponent(labelSearch)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchFiles, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
                        .addComponent(tabbedContent, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(chooseDirectory)
                        .addComponent(buttonInterpret).addComponent(clarifyImage)
                        .addComponent(labelLibrarySection, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(labelSearch, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchFiles))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(scrollFileList)
                        .addComponent(tabbedContent))));

        pack();
    }

    /**
     * @param type Saves the currently loaded file names, source path, and content
     */
    public void saveLibrary(final String type) {
        try {
            String programPath = System.getProperty("user.dir") + "/libdata";
            programPath = programPath.replace("\\", "/");
            // NAMES
            if (type == "names") {
                final FileOutputStream fosNames = new FileOutputStream(programPath + "/names.libdata");
                final ObjectOutputStream oosNames = new ObjectOutputStream(fosNames);
                oosNames.writeObject(foundFiles_NAME);
                oosNames.close();
            }
            // SOURCES
            if (type == "sources") {
                final FileOutputStream fosSources = new FileOutputStream(programPath + "/source.libdata");
                final ObjectOutputStream oosSources = new ObjectOutputStream(fosSources);
                oosSources.writeObject(foundFiles_SOURCE);
                oosSources.close();
            }
            // CONTENT
            if (type == "content") {
                final FileOutputStream fosContent = new FileOutputStream(programPath + "/content.libdata");
                final ObjectOutputStream oosContent = new ObjectOutputStream(fosContent);
                oosContent.writeObject(interpretedDocs);
                oosContent.close();
            }
        } catch (final Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * @param evt Whether the program will take extra time to enhance images for
     *            better readability
     */
    private void clarifyImageActionPerformed(final java.awt.event.ActionEvent evt) {
    }

    /**
     * @param args Runs the main program and sets UI style
     * @throws InstantiationException
     */
    public static void main(final String[] args) throws InstantiationException {
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        try {
            for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (final IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(OCRApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (final UnsupportedLookAndFeelException ex) {
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
    private JMenuItem exitProgram;
    private JMenuItem minimizeProgram;
    private JButton chooseDirectory;
    private JCheckBox clarifyImage;
    private JLabel labelLibrarySection;
    private JLabel labelSearch;
    private JList<String> calcJobsList;
    private JList<String> fileArchiveList;
    private JEditorPane searchResultsList;
    private JPanel jobPanel;
    private JProgressBar jobProgress;
    private JScrollPane scrollFileList;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JScrollPane searchScrollPane;
    private JTabbedPane tabbedContent;
    private JTextArea fileContents;
    private JTextField searchFiles;
    private JButton buttonInterpret;
}