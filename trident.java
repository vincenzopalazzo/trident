
// * AWT ELEMENTS
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// * CLIPBOARD ELEMENTS AND UNDO HANDLERS
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.lang.ProcessBuilder.Redirect;

// * IO ELEMENTS
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;

// * SWING ELEMENTS
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

class Trident {
  protected static JTextArea textarea;
  protected static JFrame frame;
  public static JLabel status1, status2, status3, status4;
  public static String fileType, path, uitheme, configFilePath;
  public static Boolean warned;
  public static JMenuBar mb;
  public static JScrollPane editor;
  public static JPanel statusBar, commentPanel, othersPanel;
  public static JMenu fileMenu, editMenu, formatMenu, runMenu, about, ClipMenu;
  public static JMenuItem newFile, OpenFile, SaveFile, SaveAs, Exit, Undo, Redo, Copy, Cut, Paste, pCopy, pCut, pPaste,
      ShowClipboard, EraseClipboard, fontOptions, themes, settings, Compile, Run, CRun, console, AboutFile, visit, help,
      AboutTrident, updates;
  public static UndoManager undoManager;
  public static JPopupMenu editorMenu;

  public static void ErrorDialog(String code, Exception e) {
    try {
      File logFile = new File("logs/log.txt");
      logFile.createNewFile();
      FileWriter logWriter = new FileWriter(logFile, true);
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
        status1.setText("Thanks for your positive intent.");
      } else {
        status1.setText("Please report errors to help improve Trident.");
      }
      writer.close();
      logWriter.close();
    } catch (IOException ioException) {
      // ErrorDialog("LOG_IO_ERR", ioException);
    }
  }

  public static final int checkOS() throws UnsupportedOperatingSystemException {
    String operatingSystem = System.getProperty("os.name").toLowerCase();
    if (operatingSystem.contains("windows")) {
      return 1;
    } else if (operatingSystem.contains("linux")) {
      return 2;
    } else {
      throw new UnsupportedOperatingSystemException();
    }
  }

  public static String fileTypeParser(String fileName) {
    String extension = "";

    int i = fileName.lastIndexOf('.');
    if (i > 0) {
      extension = fileName.substring(i + 1);
    }

    return (extension.toUpperCase() + " File");
  }

  public static void applyTheme() {
    try {
      if (checkOS() == 1) {
        uitheme = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; // TODO: Will be read from file
        UIManager.setLookAndFeel(uitheme);
      } else
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception themeError) {
      ErrorDialog("ERR_LOOK_AND_FEEL", themeError);
    }
  }

  public static boolean applyConfigs() {
    // * Default configs
    // TODO: These will be configurable by the user
    textarea.setLineWrap(false);
    textarea.setFont(new Font("Consolas", Font.PLAIN, 14));
    textarea.setTabSize(4);
    textarea.setBorder(new EmptyBorder(4, 4, 0, 0));
    editor.setBackground(Color.white);
    textarea.setBackground(Color.white);
    textarea.setForeground(Color.black);
    textarea.setCaretColor(Color.black);
    textarea.setSelectedTextColor(Color.white);
    textarea.setSelectionColor(new Color(23, 135, 227));

    Color statusColor = new Color(210, 210, 210);
    Color statusTextColor = Color.BLACK;
    statusBar.setBackground(statusColor);
    commentPanel.setBackground(statusColor);
    othersPanel.setBackground(statusColor);

    status1.setForeground(statusTextColor);
    status2.setForeground(statusTextColor);
    status3.setForeground(statusTextColor);
    status4.setForeground(statusTextColor);

    mb.setBackground(Color.white);
    mb.setForeground(Color.black);

    Color menuColor = Color.DARK_GRAY;
    fileMenu.setForeground(menuColor);
    editMenu.setForeground(menuColor);
    formatMenu.setForeground(menuColor);
    runMenu.setForeground(menuColor);
    about.setForeground(menuColor);

    return true;
  }

  public static void main(String[] args) {
    try {
      // * Listener Variable declarations
      FileMenuListener fml = new FileMenuListener();
      EditMenuListener eml = new EditMenuListener();
      FormatMenuListener oml = new FormatMenuListener();
      RunMenuListener rml = new RunMenuListener();
      AboutMenuListener aml = new AboutMenuListener();

      // * Global variable inits
      warned = false;
      fileType = " File";
      textarea = new JTextArea();
      mb = new JMenuBar();
      configFilePath = "configurations.json";
      path = "New File";

      // * Themeing
      applyTheme();

      // * Frame Setup
      frame = new JFrame();
      frame.setTitle("Trident Text Editor - " + Paths.get(path).getFileName().toString());
      frame.setSize(800, 550);
      frame.setResizable(true);
      WindowListener WindowCloseListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          status1.setText("Exiting Trident...");
          if (warned) {
            int opt = FileMenuListener.warningDialog();
            if (opt == JOptionPane.NO_OPTION) {
              System.exit(0);
            } else {
              Trident.status1.setText("Ready.");
            }
          } else {
            System.exit(0);
          }
        }
      };
      frame.addWindowListener(WindowCloseListener);
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.setLayout(new BorderLayout());
      ImageIcon ic = new ImageIcon("raw/trident.png");
      frame.setIconImage(ic.getImage());

      // * Menu Bar Setup
      // > File Menu
      fileMenu = new JMenu("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      newFile = new JMenuItem("New");
      newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      fileMenu.add(newFile);
      newFile.addActionListener(fml);

      OpenFile = new JMenuItem("Open");
      OpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      fileMenu.add(OpenFile);
      OpenFile.addActionListener(fml);

      SaveFile = new JMenuItem("Save");
      SaveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      fileMenu.add(SaveFile);
      SaveFile.addActionListener(fml);

      SaveAs = new JMenuItem("Save As");
      fileMenu.add(SaveAs);
      SaveAs.addActionListener(fml);

      Exit = new JMenuItem("Exit");
      Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      fileMenu.add(Exit);
      Exit.addActionListener(fml);
      // > File Menu

      // > Edit Menu
      editMenu = new JMenu("Edit");
      editMenu.setMnemonic(KeyEvent.VK_E);
      Undo = new JMenuItem("Undo");
      Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      Undo.addActionListener(eml);
      editMenu.add(Undo);

      Redo = new JMenuItem("Redo");
      Redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      Redo.addActionListener(eml);
      editMenu.add(Redo);

      Copy = new JMenuItem("Copy");
      Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      Copy.addActionListener(eml);
      editMenu.add(Copy);

      Cut = new JMenuItem("Cut");
      Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      Cut.addActionListener(eml);
      editMenu.add(Cut);

      Paste = new JMenuItem("Paste");
      Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
      editMenu.add(Paste);
      Paste.addActionListener(eml);

      ClipMenu = new JMenu("Clipboard");
      editMenu.add(ClipMenu);
      ShowClipboard = new JMenuItem("Show Contents");
      ClipMenu.add(ShowClipboard);
      ShowClipboard.addActionListener(eml);
      EraseClipboard = new JMenuItem("Erase Contents");
      ClipMenu.add(EraseClipboard);
      EraseClipboard.addActionListener(eml);

      Thread eal = new EditActionsListener();
      eal.start();

      // < Edit Menu

      // > Format Menu
      formatMenu = new JMenu("Format");
      formatMenu.setMnemonic(KeyEvent.VK_O);

      fontOptions = new JMenuItem("Fonts");
      formatMenu.add(fontOptions);
      fontOptions.addActionListener(oml);

      themes = new JMenuItem("Themes");
      themes.addActionListener(oml);
      formatMenu.add(themes);

      settings = new JMenuItem("Settings");
      settings.addActionListener(oml);
      formatMenu.add(settings);
      // < Format Menu

      // > Run Menu
      runMenu = new JMenu("Tools");
      runMenu.setMnemonic(KeyEvent.VK_T);
      Compile = new JMenuItem("Compile");
      Compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_DOWN_MASK));
      runMenu.add(Compile);
      Compile.addActionListener(rml);
      Run = new JMenuItem("Run");
      Run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_DOWN_MASK));
      runMenu.add(Run);
      Run.addActionListener(rml);
      CRun = new JMenuItem("Compile and Run");
      CRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_DOWN_MASK));
      runMenu.add(CRun);
      CRun.addActionListener(rml);
      console = new JMenuItem("Open Console");
      runMenu.add(console);
      console.addActionListener(rml);
      // < Run Menu

      // > About Menu
      about = new JMenu("About");
      AboutFile = new JMenuItem("File Properties");
      AboutFile.addActionListener(aml);
      about.add(AboutFile);

      visit = new JMenuItem("Visit our site");
      visit.addActionListener(aml);
      about.add(visit);

      help = new JMenuItem("Help");
      help.addActionListener(aml);
      about.add(help);

      AboutTrident = new JMenuItem("About Trident");
      about.add(AboutTrident);
      AboutTrident.addActionListener(aml);

      updates = new JMenuItem("Updates");
      about.add(updates);
      updates.addActionListener(aml);
      // < About Menu

      mb.add(fileMenu);
      mb.add(editMenu);
      mb.add(formatMenu);
      mb.add(runMenu);
      mb.add(about);
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

      // * Uses Edit Menu items */

      // * Text Area setup
      editor = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      ChangeListener textAreaListeners = new ChangeListener();
      textarea.getDocument().addDocumentListener(textAreaListeners);
      textarea.setComponentPopupMenu(editorMenu);
      editor.setBorder(new EmptyBorder(-1, 0, -1, 0));
      textarea.addCaretListener(textAreaListeners);
      undoManager = new UndoManager();
      textarea.getDocument().addUndoableEditListener(undoManager);
      Undo.setEnabled(false);
      Redo.setEnabled(false);

      // * Status bar setup
      statusBar = new JPanel();
      status1 = new JLabel("Ready.");
      status2 = new JLabel("Unsaved");
      status3 = new JLabel(fileType);
      status4 = new JLabel("Line: 1");
      CommentPaneListener cpl = new CommentPaneListener();
      cpl.start();

      statusBar.setSize(30, 2500);
      statusBar.setBorder(new EmptyBorder(2, 3, 2, 2));
      statusBar.setLayout(new GridLayout(1, 2, 2, 2));

      commentPanel = new JPanel();
      othersPanel = new JPanel();
      commentPanel.setLayout(new GridLayout(1, 1, 0, 0));
      othersPanel.setLayout(new GridLayout(1, 3, 0, 0));

      commentPanel.add(status1);
      othersPanel.add(status2);
      othersPanel.add(status3);
      othersPanel.add(status4);

      statusBar.add(commentPanel);
      statusBar.add(othersPanel);
      // * Status bar setup ends here

      // * Apply settings
      applyConfigs();

      frame.getContentPane().add(mb, BorderLayout.NORTH);
      frame.getContentPane().add(editor, BorderLayout.CENTER);
      frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
      frame.setVisible(true);

    } catch (

    Exception e) {
      ErrorDialog("MAIN_THREAD_CRASH", e);
      System.err.println("Unexpected crash...");
      e.printStackTrace();
      System.exit(0);
    }
  }
}

class FileMenuListener extends Trident implements ActionListener {
  public void FileOpenener() {
    try {
      JFileChooser openDialog = new JFileChooser(FileSystemView.getFileSystemView());
      int command = openDialog.showOpenDialog(frame);

      if (command == JFileChooser.APPROVE_OPTION)
        path = openDialog.getSelectedFile().getAbsolutePath();
      else if (command == JFileChooser.CANCEL_OPTION) {
        status1.setText("Operation cancelled by the user.");
        return;
      }

      File OpenedFile = new File(path);
      FileReader fr = new FileReader(OpenedFile);
      BufferedReader br = new BufferedReader(fr);
      String contents = "";
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        contents += line + System.lineSeparator();
      }
      textarea.setText(contents);
      status1.setText("Editing existing file.");
      status2.setText("Saved");
      status3.setText(fileTypeParser(Paths.get(path).getFileName().toString()));
      warned = false;
      Undo.setEnabled(false);
      Redo.setEnabled(false);

      contents = null;
      fr.close();
      br.close();
      System.gc();
    } catch (Exception ioe) {
      ErrorDialog("FILE_OPENER", ioe);
      status1.setText("Ready.");
    }
  }

  public static void FileSaver(String filepath) {
    try {
      if (!path.equals("New File")) {
        File f1 = new File(filepath);
        if (!f1.exists()) {
          f1.createNewFile();
        }
        String contents = textarea.getText();
        FileWriter fileWritter = new FileWriter(f1, false);
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write(contents);
        bw.close();
        warned = false;
        frame.setTitle("Trident Text Editor - " + Paths.get(path).getFileName().toString());
        status1.setText("File saved successfully.");
        status2.setText("Saved");
        status3.setText(fileTypeParser(Paths.get(path).getFileName().toString()));
      } else
        FileSaveAs();
      Undo.setEnabled(false);
      Redo.setEnabled(false);
    } catch (IOException ioe) {
      ErrorDialog("FILE_SAVE_IO", ioe);
      status1.setText("Error saving the file.");
    } catch (Exception unknownException) {
      ErrorDialog("FILE_SAVE_UNKNOWN", unknownException);
      status1.setText("Error saving the file.");
    }
  }

  public static void FileSaveAs() {
    JFileChooser saveAsDialog = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    int command = saveAsDialog.showSaveDialog(frame);

    if (command == JFileChooser.APPROVE_OPTION) {
      path = (saveAsDialog.getSelectedFile().getAbsolutePath());
      FileSaver(path);
    } else if (command == JFileChooser.CANCEL_OPTION) {
      status1.setText("File is not saved.");
    }
  }

  public static int warningDialog() {
    int opt = JOptionPane.showConfirmDialog(frame,
        "There are some unsaved changes in the file. Do you want to save the changes and continue?",
        "Warning: Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
        (new ImageIcon("raw/warning.png")));
    if (opt == JOptionPane.YES_OPTION) {
      FileSaver(path);
    }
    return opt;
  }

  public void actionPerformed(ActionEvent e) {
    try {
      switch (e.getActionCommand()) {
      case "New":
        if (warned) {
          int opt = warningDialog();
          if (opt == JOptionPane.CANCEL_OPTION) {
            status1.setText("Ready.");
            break;
          }
        }
        path = "New File";
        textarea.setText("");
        status1.setText("Ready.");
        status2.setText("Unsaved");
        status3.setText(" File");
        frame.setTitle("Trident Text Editor - New File");
        warned = false;
        Undo.setEnabled(false);
        Redo.setEnabled(false);
        break;

      case "Open":
        if (warned) {
          int opt = warningDialog();
          if (opt == JOptionPane.CANCEL_OPTION) {
            status1.setText("Ready.");
            break;
          }
        }
        FileOpenener();
        frame.setTitle("Trident Text Editor - " + Paths.get(path).getFileName().toString());
        break;

      case "Exit":
        status1.setText("Exiting Trident...");
        if (warned) {
          int opt = warningDialog();
          if (opt == JOptionPane.NO_OPTION) {
            System.exit(0);
          } else {
            status1.setText("Ready.");
            break;
          }
        } else {
          System.exit(0);
        }

      case "Save":
        FileSaver(path);
        break;

      case "Save As":
        FileSaveAs();
        break;
      }
    } catch (Exception exp) {
      ErrorDialog("FILE_MENU_CRASH", exp);
    }
  }
}

class EditMenuListener extends Trident implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    try {
      switch (e.getActionCommand()) {
      case "Show Contents":
        Clipboard clipboard;
        try {
          clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          JDialog cbviewer = new JDialog();
          cbviewer.setSize(450, 350);
          cbviewer.setTitle("Clipboard Viewer");
          JPanel TextViewer = new JPanel();
          JTextArea cta = new JTextArea(clipboard.getData(DataFlavor.stringFlavor).toString());
          JScrollPane spv = new JScrollPane(cta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
          spv.setBorder(new EmptyBorder(-1, 0, -1, 0));
          TextViewer.setLayout(new GridLayout(1, 1, 1, 1));
          cbviewer.setLayout(new BorderLayout());
          TextViewer.add(spv);
          cbviewer.getContentPane().add(TextViewer, BorderLayout.CENTER);
          cbviewer.setVisible(true);
        } catch (UnsupportedFlavorException ufe) {
          ErrorDialog("FLAVOR_ERR", ufe);
        } catch (IOException ioe) {
          ErrorDialog("IOE_CLIPBOARD", ioe);
        }
        break;

      case "Erase Contents":
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(""), null);
        break;

      case "Cut":
        textarea.cut();
        break;

      case "Copy":
        textarea.copy();
        break;

      case "Paste":
        textarea.paste();
        break;

      case "Undo":
        undoManager.undo();
        status1.setText("Ready.");
        Redo.setEnabled(true);
        break;

      case "Redo":
        undoManager.redo();
        Undo.setEnabled(true);
        status1.setText("Ready.");
        break;
      }
    } catch (CannotRedoException redoErr) {
      status1.setText("No more Redos available.");
      Redo.setEnabled(false);
    } catch (CannotUndoException undoErr) {
      status1.setText("No more Undos available.");
      Undo.setEnabled(false);
    } catch (HeadlessException noHead) {
      ErrorDialog("HEADLESS_ERR", noHead);
    } catch (Exception oopsErr) {
      ErrorDialog("EDIT_MENU_CRASH", oopsErr);
    }
  }
}

class FormatMenuListener extends FileMenuListener implements ActionListener {
  protected void SettingsEditor() {
    try {
      JDialog jsonEditor = new JDialog(frame, "Style Editor");
      jsonEditor.setSize(450, 350);
      jsonEditor.setIconImage((new ImageIcon("raw/trident.png")).getImage());
      JPanel TextViewer = new JPanel();
      File jsonFile = new File("configurations.json");
      FileReader fr = new FileReader(jsonFile);
      BufferedReader br = new BufferedReader(fr);
      String jsonContents = "";
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        jsonContents += line + System.lineSeparator();
      }
      fr.close();
      br.close();
      JTextArea jsonViewer = new JTextArea(jsonContents);
      JScrollPane jsonScrollController = new JScrollPane(jsonViewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      jsonScrollController.setBorder(new EmptyBorder(-1, 0, -1, 0));
      TextViewer.setLayout(new GridLayout(1, 1, 1, 1));
      jsonEditor.setLayout(new BorderLayout());
      TextViewer.add(jsonScrollController);
      jsonEditor.getContentPane().add(TextViewer, BorderLayout.CENTER);
      jsonViewer.getDocument().addDocumentListener(new DocumentListener() {
        private void saveSettings() {
          try {
            String jsonContents = jsonViewer.getText();
            File jsonFile = new File("configurations.json");
            FileWriter fileWritter = new FileWriter(jsonFile, false);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(jsonContents);
            bw.close();
          } catch (IOException fIoException) {
            ErrorDialog("JSON_THREAD_IO", fIoException);
          }
        }

        public void changedUpdate(DocumentEvent e) {
          saveSettings();
        }

        public void removeUpdate(DocumentEvent e) {
          saveSettings();
        }

        public void insertUpdate(DocumentEvent e) {
          saveSettings();
        }
      });
      jsonEditor.setVisible(true);
    } catch (Exception unknownException) {
      ErrorDialog("UNKNOWN_JSON_ERR", unknownException);
    }
  }

  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
    case "Fonts":
      SettingsEditor();
      break;
    case "Themes":
      SettingsEditor();
      break;
    case "Settings":
      SettingsEditor();
      break;
    }
  }
}

class RunMenuListener extends Trident implements ActionListener {
  public final void openTerminal(int os) throws UnsupportedOperatingSystemException {
    try {
      if (os == 1) {
        String[] processArgs = new String[] { "cmd.exe", "/c", "Start" };
        Process proc = new ProcessBuilder(processArgs).start();
      } else if (os == 2) {
        String[] processArgs = new String[] { "bash", "-c", "Start" };
        Process proc = new ProcessBuilder(processArgs).start();
      } else
        throw new UnsupportedOperatingSystemException();
    } catch (UnsupportedOperatingSystemException unOS) {
      throw new UnsupportedOperatingSystemException();
    } catch (Exception uos) {
      ErrorDialog("TERMINAL_ERROR", uos);
    }
  }

  public void actionPerformed(ActionEvent e) {
    try {
      switch (e.getActionCommand()) {
      case "Compile":
        (new TridentCompiler(path)).compile();
        status1.setText("Compilation ended.");
        break;

      case "Run":
        (new TridentCompiler(path)).execute();
        status1.setText("Execution ended.");
        break;

      case "Compile and Run":
        TridentCompiler compiler = (new TridentCompiler(path));
        compiler.compile();
        status1.setText("Compilation ended.");
        compiler.execute();
        status1.setText("Execution ended.");
        break;

      case "Open Console":
        openTerminal(checkOS());
        break;
      }
    } catch (InterruptedException interruptedException) {
      ErrorDialog("PROCESS_BUILD_INTERRUPTED", interruptedException);
    } catch (IOException ioException) {
      ErrorDialog("PROCESS_BUILD_FILEIO", ioException);
    } catch (UnsupportedOperatingSystemException unOs) {
      ErrorDialog("OS_UNSUPPORTED", unOs);
    } catch (UnsupportedFileException fileNS) {
      ErrorDialog("FILE_UNSUPPORTED", fileNS);
    } catch (Exception unknownException) {
      unknownException.printStackTrace();
      ErrorDialog("TOOLS_MENU_CRASH", unknownException);
    }
  }
}

class AboutMenuListener extends Trident implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    try {
      switch (e.getActionCommand()) {
      case "About Trident":
        // TODO: Add link to version.config
        JDialog aboutDialog = new JDialog(frame, "About Trident");
        JPanel infoPanel = new JPanel();
        ImageIcon ic = new ImageIcon("raw/trident_logo.png");
        JLabel icon = new JLabel(ic);
        icon.setSize(50, 50);
        JLabel l1 = new JLabel(
            "<html><style> h1 {font-family: \"Segoe UI\", monospace; color:blue;} </style> <center><h1> <br/><i>- Trident Text Editor -</i></h1> <h4><br/> Version 1.2 <br/>STABLE<br/><h4></html>");
        JLabel l2 = new JLabel(
            "<html><style>h3 {font-family: \"Segoe UI\", monospace; color:blue; border:2px solid blue; padding: 5px;}</style><h3>Visit Home Page</h3></html>");
        l2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l2.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent me) {
            try {
              Desktop.getDesktop().browse(java.net.URI.create("https://krishnamoorthy12.github.io/trident"));
            } catch (Exception de) {
              ErrorDialog("DESKTOP_ERR", de);
            }
          }
        });

        infoPanel.add(icon);
        infoPanel.add(l1);
        infoPanel.add(l2);
        l2.setBounds(120, 400, 50, 10);
        aboutDialog.add(infoPanel);
        aboutDialog.setSize(350, 500);
        aboutDialog.setResizable(false);
        aboutDialog.setVisible(true);
        break;

      case "File Properties":
        String fileName = Paths.get(path).getFileName().toString();
        JDialog aboutFileDialog = new JDialog(frame, "File Properties");
        JPanel leftPane = new JPanel();
        JPanel rightPane = new JPanel();

        leftPane.setLayout(new GridLayout(5, 1, 2, 2));
        rightPane.setLayout(new GridLayout(5, 1, 2, 2));
        aboutFileDialog.setLayout(new BorderLayout());

        leftPane.setBackground(new Color(230, 230, 230));
        leftPane.setBorder(new EmptyBorder(1, 5, 1, 5));
        rightPane.setBackground(new Color(240, 240, 240));
        rightPane.setBorder(new EmptyBorder(1, 5, 1, 5));

        File theFile = new File(path);
        JLabel filenameLabel = new JLabel("File Name :", SwingConstants.RIGHT);
        JLabel fileLocationLabel = new JLabel("File Location :", SwingConstants.RIGHT);
        JLabel fileTypeLabel = new JLabel("File Type :", SwingConstants.RIGHT);
        JLabel fileSizeLabel = new JLabel("File Size :", SwingConstants.RIGHT);
        JLabel lastModifiedLabel = new JLabel("Last modified :", SwingConstants.RIGHT);

        JLabel filenameProperty = new JLabel(fileName);
        JLabel fileLocationProperty = new JLabel(path);
        JLabel fileTypeProperty = new JLabel(fileTypeParser(path));
        JLabel fileSizeProperty = new JLabel((theFile.length() / 1024) + "KB (" + theFile.length() + " B)");
        JLabel lastModifiedProperty = new JLabel(new Date(theFile.lastModified()) + "");

        leftPane.add(filenameLabel);
        rightPane.add(filenameProperty);

        leftPane.add(fileLocationLabel);
        fileLocationProperty.setBorder(new EmptyBorder(2, 5, 0, 2));
        JScrollPane flsp = new JScrollPane(fileLocationProperty, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rightPane.add(flsp);
        flsp.setBorder(new EmptyBorder(-1, -1, -1, -1));

        leftPane.add(fileTypeLabel);
        rightPane.add(fileTypeProperty);

        leftPane.add(fileSizeLabel);
        rightPane.add(fileSizeProperty);

        leftPane.add(lastModifiedLabel);
        rightPane.add(lastModifiedProperty);

        aboutFileDialog.getContentPane().add(leftPane, BorderLayout.WEST);
        aboutFileDialog.getContentPane().add(rightPane, BorderLayout.CENTER);
        aboutFileDialog.setSize(450, 300);
        aboutFileDialog.setResizable(false);
        aboutFileDialog.setVisible(true);
        break;

      case "Visit our site":
        Desktop.getDesktop().browse(java.net.URI.create("https://krishnamoorthy12.github.io/trident/"));
        break;

      case "Help":
        Desktop.getDesktop().browse(java.net.URI.create("https://www.github.com/KrishnaMoorthy12/trident/issues"));
        break;

      case "Updates":
        Desktop.getDesktop().browse(java.net.URI.create("https://github.com/KrishnaMoorthy12/trident/releases/latest"));
        break;
      }
    } catch (Exception exc) {
      ErrorDialog("ABOUT_MENU_CRASH", exc);
    }
  }
}

class ChangeListener extends Trident implements DocumentListener, CaretListener {
  private static void warn() {
    if (!warned) {
      status2.setText("Unsaved");
      warned = true;
      frame.setTitle(frame.getTitle() + " - Unsaved");
      Undo.setEnabled(true);
    }
  }

  public void caretUpdate(CaretEvent ce) {
    try {
      int offset = textarea.getCaretPosition();
      int line = textarea.getLineOfOffset(offset) + 1;
      status4.setText("Line: " + line);
    } catch (BadLocationException badexp) {
      ErrorDialog("CARET_LOCATION_ERR", badexp);
      status4.setText("Aw snap!");
    }

  }

  public void changedUpdate(DocumentEvent e) {
    warn();
  }

  public void removeUpdate(DocumentEvent e) {
    warn();
  }

  public void insertUpdate(DocumentEvent e) {
    warn();
  }
}

class CommentPaneListener extends Thread {
  @Override
  public void run() {
    try {
      while (true) {
        Trident.status1.setText("Ready.");
        Thread.sleep(20000);
      }
    } catch (InterruptedException strangeException) {
      Trident.ErrorDialog("STATUS_THREAD_KILLED", strangeException);
    } catch (Exception died) {
      died.printStackTrace();
    }
  }
}

class EditActionsListener extends Thread {
  @Override
  public void run() {
    try {
      while (true) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String contents = clipboard.getData(DataFlavor.stringFlavor).toString();
        try {
          if (contents.equals("")) {
            Trident.Paste.setEnabled(false);
            Trident.pPaste.setEnabled(false);
            Trident.ShowClipboard.setEnabled(false);
            Trident.EraseClipboard.setEnabled(false);
          } else {
            Trident.pPaste.setEnabled(true);
            Trident.Paste.setEnabled(true);
            Trident.ShowClipboard.setEnabled(true);
            Trident.EraseClipboard.setEnabled(true);
          }
        } catch (NullPointerException npe) {
          Trident.Paste.setEnabled(false);
          Trident.pPaste.setEnabled(false);
          Trident.ShowClipboard.setEnabled(false);
          Trident.EraseClipboard.setEnabled(false);
        }

        try {
          if (Trident.textarea.getSelectedText().equals("")) {
            Trident.Copy.setEnabled(false);
            Trident.pCopy.setEnabled(false);
            Trident.Cut.setEnabled(false);
            Trident.pCut.setEnabled(false);
          } else {
            Trident.Copy.setEnabled(true);
            Trident.pCopy.setEnabled(true);
            Trident.Cut.setEnabled(true);
            Trident.pCut.setEnabled(true);
          }
        } catch (NullPointerException npe) {
          Trident.Copy.setEnabled(false);
          Trident.pCopy.setEnabled(false);
          Trident.Cut.setEnabled(false);
          Trident.pCut.setEnabled(false);
        }
        Thread.sleep(100);
      }
    } catch (InterruptedException inte) {
      Trident.ErrorDialog("EAL_INTERRUPTION", inte);
    } catch (UnsupportedFlavorException ufe) {
      Trident.ErrorDialog("EAL_FLAVOR_ERR", ufe);
    } catch (Exception some) {
      Trident.ErrorDialog("UNKNOWN_ERR_EAL", some);
    }
  }
}
