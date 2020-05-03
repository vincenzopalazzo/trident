package org.trident;

import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.JMarsDarkTheme;
import mdlaf.themes.MaterialLiteTheme;
import org.trident.model.RecentsTracker;
import org.trident.util.Constant;
import org.trident.view.ITridentComponentView;
import org.trident.view.mainpanel.TridentMainPanel;
import org.trident.control.ActionsMediator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import java.util.ArrayList;

/**
 * @author https://github.com/vincenzopalazzo
 */
public class TridentApp extends JFrame implements ITridentComponentView {

    static {
        //only for moment
        try {
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialLiteTheme()));
            UIManager.put("ToolBar.rolloverBorder", new BorderUIResource(BorderFactory.createEmptyBorder(5,5,5,5)));
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    //This code is a design patter called Singleton, look this inside the web
    private static final TridentApp SINGLETON = new TridentApp();
    //This value is only for log
    private static final Class TAG = TridentApp.class;

    public static TridentApp getInstance() {
        return SINGLETON;
    }

    /**
     * This class should be contains only the JMenu and the action about the JMenu
     */

    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu settingsMenu;
    private JMenu toolsMenu;
    private JMenu aboutMenu;
    private JMenu clipMenu;
    private JMenu newSource;
    private JMenu openRecent;
    private JMenuBar tridentMenuBar;
    private JMenuItem newFile;
    private JMenuItem newWindow;
    private JMenuItem openFile;
    private JMenuItem saveFile;
    private JMenuItem saveAs;
    private JMenuItem exit;
    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem copy;
    private JMenuItem cut;
    private JMenuItem paste;
    private JMenuItem goTo;
    private JMenuItem pCopy;
    private JMenuItem pCut;
    private JMenuItem pPaste;
    private JMenuItem showClipboard;
    private JMenuItem eraseClipboard;
    private JMenuItem find;
    private JMenuItem replace;
    private JMenuItem styleEditor;
    private JMenuItem configs;
    private JMenuItem compile;
    private JMenuItem run;
    private JMenuItem compileAndRun;
    private JMenuItem console;
    private JMenuItem aboutFile;
    private JMenuItem help;
    private JMenuItem aboutTrident;
    private JMenuItem updates;
    private JMenuItem pyFile;
    private JMenuItem javaFile;
    private JMenuItem cFile;
    private JMenuItem cppFile;
    private JMenuItem htmlFile;
    private JMenuItem bstrp;
    private JMenuItem pboil;
    private JMenuItem[] recentlyOpened;
    private JCheckBoxMenuItem wordWrap;
    private JCheckBoxMenuItem autoSave;

    //MainPanel propriety, add this inside the frame
    private TridentMainPanel mainPanel;


    @Override
    public void initView() {
        initComponent();
        initLayout();
        initActions();

        super.setTitle("Trident Text Editor - "/* + Paths.get(path).getFileName().toString()*/);
        super.setSize(800, 550);
        super.setContentPane(mainPanel);
        super.setResizable(true);
        super.setLocationRelativeTo(null);
        super.setDefaultCloseOperation(super.EXIT_ON_CLOSE);
        super.setVisible(true);
    }

    @Override
    public void initComponent() {
        //Init the component, like
        initMenu();
        mainPanel = new TridentMainPanel();
    }

    @Override
    public void initLayout() {
        //do nothing
    }

    @Override
    public void initActions() {
        //Very simple :) and very professional
        Action exist = (Action) ActionsMediator.getInstance().getAction(Constant.ACTION_EXIT);
        exit.setAction(exist);
    }

    private void initMenu() {
        // * Menu Bar Setup
        // > File Menu
        fileMenu = new JMenu("File");

        newFile = new JMenuItem("New");
        fileMenu.add(newFile);

        newWindow = new JMenuItem("New Window");
        fileMenu.add(newWindow);

        newSource = new JMenu("New Source File");
        cFile = new JMenuItem("C Source File");
        newSource.add(cFile);

        cppFile = new JMenuItem("C++ Source File");
        newSource.add(cppFile);

        javaFile = new JMenuItem("Java Source File");
        newSource.add(javaFile);

        pyFile = new JMenuItem("Python Source File");
        newSource.add(pyFile);

        htmlFile = new JMenuItem("HTML File");
        newSource.add(htmlFile);

        bstrp = new JMenuItem("Bootstrap");
        newSource.add(bstrp);

        pboil = new JMenuItem("Open PowerBoil");
        newSource.add(pboil);
        fileMenu.add(newSource);

        openFile = new JMenuItem("Open");
        fileMenu.add(openFile);

        openRecent = new JMenu("Open Recent");
        ArrayList<String> recents = RecentsTracker.getRecords();
        int num = 0;
        recentlyOpened = new JMenuItem[5];
        for (String rec : recents) {
            recentlyOpened[num] = new JMenuItem(rec);
            openRecent.add(recentlyOpened[num]);
            num++;
        }
        fileMenu.add(openRecent);

        saveFile = new JMenuItem("Save");
        fileMenu.add(saveFile);

        saveAs = new JMenuItem("Save As");
        fileMenu.add(saveAs);

        exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        // < File Menu

        // > Edit Menu
        editMenu = new JMenu("Edit");

        undo = new JMenuItem("Undo");
        editMenu.add(undo);

        redo = new JMenuItem("Redo");
        editMenu.add(redo);

        copy = new JMenuItem("Copy");
        editMenu.add(copy);

        cut = new JMenuItem("Cut");
        editMenu.add(cut);

        paste = new JMenuItem("Paste");
        editMenu.add(paste);

        find = new JMenuItem("Find");
        editMenu.add(find);

        replace = new JMenuItem("Replace");
        editMenu.add(replace);

        goTo = new JMenuItem("Go To");
        editMenu.add(goTo);

        clipMenu = new JMenu("Clipboard");
        editMenu.add(clipMenu);

        showClipboard = new JMenuItem("Show Contents");
        clipMenu.add(showClipboard);

        eraseClipboard = new JMenuItem("Erase Contents");
        clipMenu.add(eraseClipboard);

        // < Edit Menu

        // > Settings Menu
        settingsMenu = new JMenu("Settings");

        wordWrap = new JCheckBoxMenuItem("Word Wrap", false);
        settingsMenu.add(wordWrap);

        autoSave = new JCheckBoxMenuItem("Auto Save", true);
        settingsMenu.add(autoSave);

        styleEditor = new JMenuItem("Style Editor");
        settingsMenu.add(styleEditor);

        configs = new JMenuItem("Configurations");
        settingsMenu.add(configs);
        // < Settings Menu

        // > Run Menu
        toolsMenu = new JMenu("Tools");

        compile = new JMenuItem("Compile");
        toolsMenu.add(compile);

        run = new JMenuItem("Run");
        toolsMenu.add(run);

        compileAndRun = new JMenuItem("Compile and Run");

        // > About Menu
        aboutMenu = new JMenu("About");
        aboutFile = new JMenuItem("File Properties");
        aboutMenu.add(aboutFile);

        help = new JMenuItem("Help");
        aboutMenu.add(help);

        aboutTrident = new JMenuItem("About Trident");
        aboutMenu.add(aboutTrident);

        updates = new JMenuItem("Updates");
        aboutMenu.add(updates);
        // < About Menu

        tridentMenuBar = new JMenuBar();
        tridentMenuBar.add(fileMenu);
        tridentMenuBar.add(editMenu);
        tridentMenuBar.add(toolsMenu);
        tridentMenuBar.add(settingsMenu);
        tridentMenuBar.add(aboutMenu);
        tridentMenuBar.setBorder(new EmptyBorder(0, 0, 0, 0));

        //if you call an father method (in this cases JFrame) add every time the world super.method
        super.setJMenuBar(tridentMenuBar);
    }

    //Getter !! ONLY getter method, you use the method getter to get the component in other place of code
    //like an action

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SINGLETON.initView();
            }
        });
    }
}
