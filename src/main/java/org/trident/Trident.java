package org.trident;

import org.trident.control.ActionsMediator;
import org.trident.control.listeners.*;
import org.trident.model.Configurations;
import org.trident.model.RecentsTracker;
import org.trident.util.Constant;
import org.trident.util.TridentLogger;
import org.trident.view.Toolbar;

import java.awt.event.*;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.undo.UndoManager;
import java.awt.Desktop;

/*
 * Trident Text Editor v4.0 +
 * @author: Krishna Moorthy
 */
public class Trident {


    //
    //TODO this comment after load should be remove
    //This class should be the MAIN app, should be contains only the
    //managed the class, like the frame, the mediator of the acions and nothing else.
    //So I think I start the refactoring to move all component inside this class in different component
    //Look also TridentApp in View package

    private static final Class TAG = Trident.class;

    private static Trident INSTANCE = new Trident("New File");

    private JFrame frame;
    protected JTextArea textarea;
    protected JLabel statusText, savedStatus, fileTypeStatus, positionStatus;
    protected String fileType;
    protected String path;
    protected Boolean warned;
    protected JMenuBar mb;
    protected JScrollPane editor;
    protected JPanel statusBar, commentPanel, othersPanel;
    protected JMenu fileMenu, editMenu, settingsMenu, toolsMenu, about, clipMenu, newSource, openRecent;
    protected JMenuItem newFile, newWindow, openFile, saveFile, saveAs, exit, undo, redo, copy, cut, paste, goTo,
            pCopy, pCut, pPaste, showClipboard, eraseClipboard, find, replace, styleEditor, configs, compile, run, compileAndRun,
            console, aboutFile, help, aboutTrident, updates, pyFile, javaFile, cFile, cppFile, htmlFile, bstrp, pboil;
    protected JMenuItem[] recentlyOpened;
    protected JCheckBoxMenuItem wordWrap, autoSave;
    protected JToolBar toolBar;
    protected UndoManager undoManager;
    protected JPopupMenu editorMenu;

    // * Listener Variable declarations
    private FileMenuListener fml = (FileMenuListener) ActionsMediator.getInstance().getAction(Constant.LISTENER_FILE_MENU);
    private ActionListener eml = ActionsMediator.getInstance().getAction(Constant.LISTENER_EDIT_MENU);
    private ActionListener sml = ActionsMediator.getInstance().getAction(Constant.LISTENER_SETTING_MENU);
    private ActionListener tml = ActionsMediator.getInstance().getAction(Constant.LISTENER_TOOLS_MENU);
    private ActionListener aml = ActionsMediator.getInstance().getAction(Constant.LISTENER_ABOUT_MENU);

    protected boolean isRunning = true;

    public static Trident getInstance() {
        return INSTANCE;
    }

    @Deprecated
    public void ErrorDialog(String code, Exception e) {
        try {
            /*
             * The Trident Error Handler :- * Handles Errors that occur during the runtime
             * of Trident and writes logs whenever an error occurs. * Shows Error dialog.
             *
             * @param: Error code, excpection object.
             */
            writeLog(code, e);
            int option = JOptionPane.showConfirmDialog(frame,
                    "An Unexpected error occured. \nThis may lead to a crash. Save any changes and continue. \nERROR CODE: "
                            + code + "\nERROR NAME: " + e.getClass().getName() + "\nERROR CAUSE: " + e.getCause(),
                    "Aw! Snap!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon("raw/error.png"));
            if (option == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().browse(java.net.URI.create(
                            "https://github.com/KrishnaMoorthy12/trident/issues/new?assignees=&labels=&template=bug_report.md&title="));
                } catch (Exception shit) {
                    ErrorDialog("DESKTOP_UNAVAILABLE", shit);
                }
                statusText.setText("Thanks for your positive intent.");
            } else {
                statusText.setText("Please report errors to help improve Trident.");
            }
        } catch (Exception dialogShowErr) {
            System.err.println("Trident was terminated with an unexpected error.");
            System.exit(-1);
            writeLog("CRASH_ERR_DIALOG", dialogShowErr);
        }
    }

    @Deprecated
    public static void writeLog(String code, Exception e) {
        try {
            StringWriter logWriter = new StringWriter();
            BufferedWriter writer = new BufferedWriter(logWriter);

            writer.write(System.lineSeparator());
            writer.write(LocalDateTime.now().toString());
            writer.write(System.lineSeparator());
            writer.write(code + System.lineSeparator() + e.getClass().getName() + System.lineSeparator() + e.getCause()
                    + System.lineSeparator() + e.getMessage());
            writer.write(System.lineSeparator());
            writer.write("------------------------------------------");
            writer.write(System.lineSeparator());

            for (StackTraceElement ste : e.getStackTrace()) {
                writer.write("[" + code + "] ");
                writer.write(ste.toString());
                writer.write(System.lineSeparator());
            }
            writer.write("=========================================================");
            TridentLogger.getInstance().info(TAG, "Exception to line 123 in Trident");
            writer.close();
            logWriter.close();
            e.printStackTrace();
        } catch (IOException ioException) {
            // ErrorDialog("LOG_IO_ERR", ioException);
            TridentLogger.getInstance().debug(TAG, ioException.getLocalizedMessage());
            ioException.printStackTrace();
            // System.out.println(ioException);
        }
    }

    public int doCheckOS() {
        //TODO add CONSTANT
        String operatingSystem = System.getProperty("os.name").toLowerCase();
        if (operatingSystem.contains("windows")) {
            return 1;
        } else if (operatingSystem.contains("linux")) {
            return 2;
        } else {
            return 3;
            // throw new org.trident.exception.UnsupportedOperatingSystemException();
        }
    }

    public Trident(String file) {

        try {
            // * Global variable inits
            warned = false;
            fileType = "Plain File";
            textarea = new JTextArea();
            mb = new JMenuBar();
            path = file;

            // * Themeing
            Configurations.getInstance().read();
            if (doCheckOS() == 1) {
                try {
                    UIManager.setLookAndFeel(Configurations.getInstance().getThemeName());
                } catch (Exception e) {
                    Configurations.getInstance().setThemeName(UIManager.getSystemLookAndFeelClassName());
                    UIManager.setLookAndFeel(Configurations.getInstance().getThemeName());
                    Configurations.getInstance().write();
                    ErrorDialog("UI_THEME_ERR", e);
                }
            } else {
                try {
                    if (Configurations.getInstance().getThemeName().contains("windows")) {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                } catch (Exception e) {
                    ErrorDialog("LINUX_THEME_ERR", e);
                }
            }

            // * Frame Setup
            frame = new JFrame();
            frame.setTitle("Trident Text Editor - " + Paths.get(path).getFileName().toString());
            frame.setSize(800, 550);
            frame.setResizable(true);
            WindowListener WindowCloseListener = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    statusText.setText("Exiting Trident...");
                    if (warned) {
                        int opt = fml.warningDialog();
                        if (opt == JOptionPane.NO_OPTION) {
                            System.exit(0);
                        } else {
                            Trident.getInstance().getstatusText().setText("Ready.");
                        }
                    } else {
                        System.exit(0);
                    }
                }
            };

            frame.addWindowListener(WindowCloseListener);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            ImageIcon ic = new ImageIcon("raw/trident_icon.png");
            frame.setIconImage(ic.getImage());

            // * Menu Bar Setup
            // > File Menu
            fileMenu = new JMenu("File");
            fileMenu.setMnemonic(KeyEvent.VK_F);

            newFile = new JMenuItem("New");
            newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            fileMenu.add(newFile);
            newFile.addActionListener(fml);

            newWindow = new JMenuItem("New Window");
            newWindow.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                    java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
            fileMenu.add(newWindow);
            newWindow.addActionListener(fml);

            newSource = new JMenu("New Source File");
            newSource.setMnemonic(KeyEvent.VK_O);
            cFile = new JMenuItem("C Source File");
            cFile.addActionListener(fml);
            newSource.add(cFile);
            cppFile = new JMenuItem("C++ Source File");
            cppFile.addActionListener(fml);
            newSource.add(cppFile);
            javaFile = new JMenuItem("Java Source File");
            javaFile.addActionListener(fml);
            newSource.add(javaFile);
            pyFile = new JMenuItem("Python Source File");
            pyFile.addActionListener(fml);
            newSource.add(pyFile);
            htmlFile = new JMenuItem("HTML File");
            htmlFile.addActionListener(fml);
            newSource.add(htmlFile);
            bstrp = new JMenuItem("Bootstrap");
            bstrp.addActionListener(fml);
            newSource.add(bstrp);
            pboil = new JMenuItem("Open PowerBoil");
            pboil.addActionListener(fml);
            newSource.add(pboil);
            fileMenu.add(newSource);

            openFile = new JMenuItem("Open");
            openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            fileMenu.add(openFile);
            openFile.addActionListener(fml);

            openRecent = new JMenu("Open Recent");
            ArrayList<String> recents = RecentsTracker.getRecords();
            int num = 0;
            recentlyOpened = new JMenuItem[5];
            for (String rec : recents) {
                recentlyOpened[num] = new JMenuItem(rec);
                recentlyOpened[num].addActionListener(fml);
                openRecent.add(recentlyOpened[num]);
                num++;
            }
            fileMenu.add(openRecent);

            saveFile = new JMenuItem("Save");
            saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            fileMenu.add(saveFile);
            saveFile.addActionListener(fml);

            saveAs = new JMenuItem("Save As");
            saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                    (java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK)));
            fileMenu.add(saveAs);
            saveAs.addActionListener(fml);

            exit = new JMenuItem("Exit");
            exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK));
            fileMenu.add(exit);
            exit.addActionListener(fml);
            // < File Menu

            // > Edit Menu
            editMenu = new JMenu("Edit");
            editMenu.setMnemonic(KeyEvent.VK_E);

            undo = new JMenuItem("Undo");
            undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            undo.addActionListener(eml);
            editMenu.add(undo);

            redo = new JMenuItem("Redo");
            redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            redo.addActionListener(eml);
            editMenu.add(redo);

            copy = new JMenuItem("Copy");
            copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            copy.addActionListener(eml);
            editMenu.add(copy);

            cut = new JMenuItem("Cut");
            cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            cut.addActionListener(eml);
            editMenu.add(cut);

            paste = new JMenuItem("Paste");
            paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            editMenu.add(paste);
            paste.addActionListener(eml);

            find = new JMenuItem("Find");
            find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            editMenu.add(find);
            find.addActionListener(eml);

            replace = new JMenuItem("Replace");
            replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                    java.awt.event.InputEvent.CTRL_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
            editMenu.add(replace);
            replace.addActionListener(eml);

            goTo = new JMenuItem("Go To");
            goTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
            editMenu.add(goTo);
            goTo.addActionListener(eml);

            clipMenu = new JMenu("Clipboard");
            editMenu.add(clipMenu);
            showClipboard = new JMenuItem("Show Contents");
            clipMenu.add(showClipboard);
            showClipboard.addActionListener(eml);
            eraseClipboard = new JMenuItem("Erase Contents");
            clipMenu.add(eraseClipboard);
            eraseClipboard.addActionListener(eml);

            // < Edit Menu

            // > Settings Menu
            settingsMenu = new JMenu("Settings");
            settingsMenu.setMnemonic(KeyEvent.VK_S);

            wordWrap = new JCheckBoxMenuItem("Word Wrap", false);
            wordWrap.addItemListener((ItemListener) sml);
            settingsMenu.add(wordWrap);

            autoSave = new JCheckBoxMenuItem("Auto Save", true);
            autoSave.addItemListener((ItemListener) sml);
            settingsMenu.add(autoSave);

            styleEditor = new JMenuItem("Style Editor");
            settingsMenu.add(styleEditor);
            styleEditor.addActionListener(sml);

            configs = new JMenuItem("org.trident.model.Configurations");
            configs.addActionListener(sml);
            settingsMenu.add(configs);
            // < Settings Menu

            // > Run Menu
            toolsMenu = new JMenu("Tools");
            toolsMenu.setMnemonic(KeyEvent.VK_T);

            compile = new JMenuItem("Compile");
            compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_DOWN_MASK));
            toolsMenu.add(compile);
            compile.addActionListener(tml);

            run = new JMenuItem("Run");
            run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_DOWN_MASK));
            toolsMenu.add(run);
            run.addActionListener(tml);

            compileAndRun = new JMenuItem("Compile and Run");
            compileAndRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_DOWN_MASK));
            toolsMenu.add(compileAndRun);
            compileAndRun.addActionListener(tml);

            console = new JMenuItem("Open Console");
            toolsMenu.add(console);
            console.addActionListener(tml);
            // < Run Menu

            // > About Menu
            about = new JMenu("About");

            aboutFile = new JMenuItem("File Properties");
            aboutFile.addActionListener(aml);
            about.add(aboutFile);

            help = new JMenuItem("Help");
            help.addActionListener(aml);
            about.add(help);

            aboutTrident = new JMenuItem("About Trident");
            about.add(aboutTrident);
            aboutTrident.addActionListener(aml);

            updates = new JMenuItem("Updates");
            about.add(updates);
            updates.addActionListener(aml);
            // < About Menu

            mb.add(fileMenu);
            mb.add(editMenu);
            mb.add(toolsMenu);
            mb.add(settingsMenu);
            mb.add(about);
            mb.setBorder(new EmptyBorder(0, 0, -1, 0));
            // * Menu bar setup ends here

            // * Pop up menu for text area
            editorMenu = new JPopupMenu();
            pCopy = new JMenuItem("Copy");
            pCopy.addActionListener(eml);
            pCut = new JMenuItem("Cut");
            pCut.addActionListener(eml);
            pPaste = new JMenuItem("Paste");
            pPaste.addActionListener(eml);

            editorMenu.add(pCopy);
            editorMenu.add(pCut);
            editorMenu.add(pPaste);

            // * Tool bar setup
            toolBar = new JToolBar();
            toolBar.setFloatable(false);

            // * Text Area setup
            editor = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            editor.setBorder(new EmptyBorder(0, 0, 0, 0));
            textarea.setBorder(new EmptyBorder(2, 2, 0, 0));
            textarea.setComponentPopupMenu(editorMenu);

            // > Listeners for Text Area
            AbstractDocument doc = (AbstractDocument) textarea.getDocument();
            doc.setDocumentFilter(new IndentListener());
            EditActionsListener eal = new EditActionsListener();
            undoManager = new UndoManager();
            eal.start();
            textarea.getDocument().addDocumentListener(new ChangeListener());
            textarea.getDocument().addDocumentListener(new AutoSave());
            textarea.addCaretListener(new LineNumberListener());
            textarea.getDocument().addUndoableEditListener(undoManager);
            undo.setEnabled(false);
            if (Toolbar.undoButton != null) {
                Toolbar.undoButton.setEnabled(false);
                redo.setEnabled(false);
                Toolbar.redoButton.setEnabled(false);
            }

            // * Status bar setup
            statusBar = new JPanel();
            statusText = new JLabel("Ready.");
            savedStatus = new JLabel("Unsaved");
            fileTypeStatus = new JLabel(fileType);
            positionStatus = new JLabel("Line: 1 Col: 1");

            CommentPaneListener cpl = new CommentPaneListener();
            cpl.start();

            statusBar.setBorder(new EmptyBorder(2, 3, 2, 2));
            statusBar.setLayout(new GridLayout(1, 2, 2, 2));

            commentPanel = new JPanel();
            othersPanel = new JPanel();
            commentPanel.setLayout(new GridLayout(1, 1, 0, 0));
            othersPanel.setLayout(new GridLayout(1, 3, 0, 0));

            commentPanel.add(statusText);
            othersPanel.add(savedStatus);
            othersPanel.add(fileTypeStatus);
            othersPanel.add(positionStatus);

            statusBar.add(commentPanel);
            statusBar.add(othersPanel);
            // * Status bar setup ends here

            // * Apply settings
            Configurations.getInstance().raw_apply();

            if (!path.equals("New File")) {
                fml.openFile();
                fml.saveFile();
                statusText.setText("File opened using command-line.");
            }

            frame.setJMenuBar(mb);
            frame.getContentPane().add(toolBar, BorderLayout.PAGE_START);
            frame.getContentPane().add(editor, BorderLayout.CENTER);
            frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null);
            frame.setLocationByPlatform(true);
            frame.setVisible(true);

        } catch (Exception e) {
            ErrorDialog("MAIN_THREAD_CRASH", e);
            TridentLogger.getInstance().error(TAG, "Unexpected crash...");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //getter and setter
    public JFrame getFrame() {
        return frame;
    }

    public JTextArea getTextarea() {
        return textarea;
    }

    public String getPath() {
        return path;
    }

    public JScrollPane getEditor() {
        return editor;
    }

    public JPanel getStatusBar() {
        return statusBar;
    }

    public JPanel getCommentPanel() {
        return commentPanel;
    }

    public JLabel getstatusText() {
        return statusText;
    }

    public void setToolBar(JToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public JLabel getStatus2() {
        return savedStatus;
    }

    public JLabel getStatus3() {
        return fileTypeStatus;
    }

    public JLabel getStatus4() {
        return positionStatus;
    }

    public JPanel getOthersPanel() {
        return othersPanel;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public Trident getINSTANCE() {
        return INSTANCE;
    }

    public String getFileType() {
        return fileType;
    }

    public Boolean getWarned() {
        return warned;
    }

    public void setWarned(Boolean warned) {
        this.warned = warned;
    }

    public JMenuBar getMb() {
        return mb;
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getEditMenu() {
        return editMenu;
    }

    public JMenu getSettingsMenu() {
        return settingsMenu;
    }

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public JMenu getAbout() {
        return about;
    }

    public JMenu getClipMenu() {
        return clipMenu;
    }

    public JMenu getNewSource() {
        return newSource;
    }

    public JMenu getOpenRecent() {
        return openRecent;
    }

    public JMenuItem getNewFile() {
        return newFile;
    }

    public JMenuItem getNewWindow() {
        return newWindow;
    }

    public JMenuItem getopenFile() {
        return openFile;
    }

    public JMenuItem getsaveFile() {
        return saveFile;
    }

    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public JMenuItem getsaveAs() {
        return saveAs;
    }

    public JMenuItem getExit() {
        return exit;
    }

    public JMenuItem getUndo() {
        return undo;
    }

    public JMenuItem getRedo() {
        return redo;
    }

    public JMenuItem getCopy() {
        return copy;
    }

    public JMenuItem getCut() {
        return cut;
    }

    public JMenuItem getPaste() {
        return paste;
    }

    public JMenuItem getGoTo() {
        return goTo;
    }

    public JMenuItem getpCopy() {
        return pCopy;
    }

    public JMenuItem getpCut() {
        return pCut;
    }

    public JMenuItem getpPaste() {
        return pPaste;
    }

    public JMenuItem getShowClipboard() {
        return showClipboard;
    }

    public JMenuItem getEraseClipboard() {
        return eraseClipboard;
    }

    public JMenuItem getFind() {
        return find;
    }

    public JMenuItem getReplace() {
        return replace;
    }

    public JMenuItem getStyleEditor() {
        return styleEditor;
    }

    public JMenuItem getConfigs() {
        return configs;
    }

    public JMenuItem getCompile() {
        return compile;
    }

    public JMenuItem getRun() {
        return run;
    }

    public JMenuItem getCRun() {
        return compileAndRun;
    }

    public JMenuItem getConsole() {
        return console;
    }

    public JMenuItem getAboutFile() {
        return aboutFile;
    }

    public JMenuItem getHelp() {
        return help;
    }

    public JMenuItem getAboutTrident() {
        return aboutTrident;
    }

    public JMenuItem getUpdates() {
        return updates;
    }

    public JMenuItem getPyFile() {
        return pyFile;
    }

    public JMenuItem getJavaFile() {
        return javaFile;
    }

    public JMenuItem getcFile() {
        return cFile;
    }

    public JMenuItem getCppFile() {
        return cppFile;
    }

    public JMenuItem getHtmlFile() {
        return htmlFile;
    }

    public JMenuItem getBstrp() {
        return bstrp;
    }

    public JMenuItem getPboil() {
        return pboil;
    }

    public JMenuItem[] getRecentlyOpened() {
        return recentlyOpened;
    }

    public JCheckBoxMenuItem getWordWrap() {
        return wordWrap;
    }

    public JCheckBoxMenuItem getAutoSave() {
        return autoSave;
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public JPopupMenu getEditorMenu() {
        return editorMenu;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static void main(String[] args) {

        if (args.length != 0) {
            for (String arg : args) {
                if (arg.equals("-version")) {
                    TridentLogger.getInstance().debug(TAG, "Trident Text Editor");
                    TridentLogger.getInstance().debug(TAG, "Version: 5.0");
                    TridentLogger.getInstance().debug(TAG, "\tChannel: Beta");
                    TridentLogger.getInstance().debug(TAG, "(c) 2020 Krishna Moorthy Athinarayan. All rights reserved.");
                } else {
                    INSTANCE = new Trident(arg);
                }
            }
        }
    }
}
