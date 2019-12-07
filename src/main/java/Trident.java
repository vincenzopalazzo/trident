
// * AWT ELEMENTS
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;

// * CLIPBOARD ELEMENTS

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Clipboard;

// * IO ELEMENTS
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
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
import org.json.simple.JSONObject;

class Trident {
  protected static JTextArea textarea;
  protected static JFrame frame;
  public static JLabel status1, status2, status3, status4;
  public static String fileType;
  public static String path;
  public static String uitheme;
  public static Boolean warned;
  public static JMenuBar mb;
  public static JScrollPane editor;
  public static JPanel statusBar;
  public static JMenu fileMenu;
  public static JMenu editMenu;
  public static JMenu formatMenu;
  public static JMenu runMenu;
  public static JMenu about;
  public static String configFilePath;
  public static UndoManager undoManager;

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
      uitheme = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"; // TODO: Will be read from file
      UIManager.setLookAndFeel(uitheme);
    } catch (Exception themeError) {
      System.err.println("Error theming the application.");
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

    statusBar.setBackground(Color.LIGHT_GRAY);
    status1.setForeground(Color.BLACK);
    status2.setForeground(Color.BLACK);
    status3.setForeground(Color.BLACK);
    status4.setForeground(Color.BLACK);

    mb.setBackground(Color.white);
    mb.setForeground(Color.black);
    fileMenu.setForeground(Color.DARK_GRAY);
    editMenu.setForeground(Color.DARK_GRAY);
    formatMenu.setForeground(Color.DARK_GRAY);
    runMenu.setForeground(Color.DARK_GRAY);
    about.setForeground(Color.DARK_GRAY);

    return true;
  }

  public static void main(String[] args) {
    try {
      // * Local Variable declarations
      FileMenuListener fml = new FileMenuListener();
      EditMenuListener eml = new EditMenuListener();
      FormatMenuListener oml = new FormatMenuListener();
      RunMenuListener rml = new RunMenuListener();
      AboutMenuListener aml = new AboutMenuListener();

      // * Global variable inits
      {
        warned = false;
        fileType = " File";
        textarea = new JTextArea();
        mb = new JMenuBar();
        configFilePath = "configurations.json";
        path = System.getProperty("java.io.tmpdir") + "New File";
      }

      // * Themeing
      applyTheme();

      // * Frame Setup
      {
        frame = new JFrame();
        frame.setTitle("Trident Text Editor - " + Paths.get(path).getFileName().toString());
        frame.setSize(800, 550);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        ImageIcon ic = new ImageIcon("raw\\trident.png");
        frame.setIconImage(ic.getImage());
      }

      // * Menu Bar Setup
      {
        fileMenu = new JMenu("File");
        {
          fileMenu.setMnemonic(KeyEvent.VK_F);
          JMenuItem newFile = new JMenuItem("New");
          newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          fileMenu.add(newFile);
          newFile.addActionListener(fml);

          JMenuItem OpenFile = new JMenuItem("Open");
          OpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          fileMenu.add(OpenFile);
          OpenFile.addActionListener(fml);

          JMenuItem SaveFile = new JMenuItem("Save");
          SaveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          fileMenu.add(SaveFile);
          SaveFile.addActionListener(fml);

          JMenuItem SaveAs = new JMenuItem("Save As");
          fileMenu.add(SaveAs);
          SaveAs.addActionListener(fml);

          JMenuItem Exit = new JMenuItem("Exit");
          Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          fileMenu.add(Exit);
          Exit.addActionListener(fml);
        }

        editMenu = new JMenu("Edit");
        {
          editMenu.setMnemonic(KeyEvent.VK_E);
          JMenuItem Undo = new JMenuItem("Undo");
          Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          editMenu.add(Undo);
          JMenuItem Redo = new JMenuItem("Redo");
          Redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          editMenu.add(Redo);
          JMenuItem Copy = new JMenuItem("Copy");
          Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          editMenu.add(Copy);
          JMenuItem Cut = new JMenuItem("Cut");
          Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          editMenu.add(Cut);
          JMenuItem Paste = new JMenuItem("Paste");
          Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
          editMenu.add(Paste);
          Undo.addActionListener(eml);
          Redo.addActionListener(eml);
          Cut.addActionListener(eml);
          Copy.addActionListener(eml);
          Paste.addActionListener(eml);

          JMenu ClipMenu = new JMenu("Clipboard");
          editMenu.add(ClipMenu);
          JMenuItem ShowClipboard = new JMenuItem("Show Contents");
          ClipMenu.add(ShowClipboard);
          ShowClipboard.addActionListener(eml);
          JMenuItem EraseClipboard = new JMenuItem("Erase Contents");
          ClipMenu.add(EraseClipboard);
          EraseClipboard.addActionListener(eml);
        }

        formatMenu = new JMenu("Format");
        {
          formatMenu.setMnemonic(KeyEvent.VK_O);
          JMenuItem fontOptions = new JMenuItem("Fonts");
          formatMenu.add(fontOptions);
          fontOptions.addActionListener(oml);
          JMenuItem themes = new JMenuItem("Themes");
          themes.addActionListener(oml);
          formatMenu.add(themes);
          JMenuItem settings = new JMenuItem("Settings");
          settings.addActionListener(oml);
          formatMenu.add(settings);
        }

        runMenu = new JMenu("Run");
        {
          runMenu.setMnemonic(KeyEvent.VK_R);
          JMenuItem Compile = new JMenuItem("Compile");
          Compile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_DOWN_MASK));
          runMenu.add(Compile);
          Compile.addActionListener(rml);
          JMenuItem Run = new JMenuItem("Run");
          Run.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_DOWN_MASK));
          runMenu.add(Run);
          Run.addActionListener(rml);
          JMenuItem CRun = new JMenuItem("Compile and Run");
          CRun.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_DOWN_MASK));
          runMenu.add(CRun);
          Run.addActionListener(rml);
          JMenuItem console = new JMenuItem("Open Console");
          runMenu.add(console);
          console.addActionListener(rml);
        }

        about = new JMenu("About");
        {
          JMenuItem AboutFile = new JMenuItem("File Properties");
          AboutFile.addActionListener(aml);
          about.add(AboutFile);

          JMenuItem visit = new JMenuItem("Visit our site");
          visit.addActionListener(aml);
          about.add(visit);

          JMenuItem help = new JMenuItem("Help");
          help.addActionListener(aml);
          about.add(help);

          JMenuItem AboutTrident = new JMenuItem("About Trident");
          about.add(AboutTrident);
          AboutTrident.addActionListener(aml);

          JMenuItem updates = new JMenuItem("Updates");
          about.add(updates);
          updates.addActionListener(aml);
        }

        mb.add(fileMenu);
        mb.add(editMenu);
        mb.add(formatMenu);
        mb.add(runMenu);
        mb.add(about);
      } // * Menu bar setup ends here

      // * Pop up menu for text area
      JPopupMenu editorMenu = new JPopupMenu();
      {
        JMenuItem Copy = new JMenuItem("Copy");
        editorMenu.add(Copy);
        Copy.addActionListener(eml);
        JMenuItem Cut = new JMenuItem("Cut");
        editorMenu.add(Cut);
        Cut.addActionListener(eml);
        JMenuItem Paste = new JMenuItem("Paste");
        editorMenu.add(Paste);
        Paste.addActionListener(eml);

      }

      // * Text Area setup
      editor = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      {
        ChangeListener textAreaListeners = new ChangeListener();
        textarea.getDocument().addDocumentListener(textAreaListeners);
        textarea.setComponentPopupMenu(editorMenu);
        editor.setBorder(new EmptyBorder(-1, 0, -1, 0));
        textarea.addCaretListener(textAreaListeners);
        undoManager = new UndoManager();
        textarea.getDocument().addUndoableEditListener(undoManager);
      }

      // * Status bar setup
      statusBar = new JPanel();
      {
        status1 = new JLabel("Ready");
        status2 = new JLabel("Unsaved");
        status3 = new JLabel(fileType);
        status4 = new JLabel("Line: 1   Offset: 1");

        statusBar.setSize(30, 2500);
        statusBar.setBorder(new EmptyBorder(2, 3, 2, 2));
        statusBar.setLayout(new GridLayout(1, 4, 2, 2));
        statusBar.add(status1);
        statusBar.add(status2);
        statusBar.add(status3);
        statusBar.add(status4);
      } // * Status bar setup ends here

      // * Apply settings
      applyConfigs();

      frame.getContentPane().add(mb, BorderLayout.NORTH);
      frame.getContentPane().add(editor, BorderLayout.CENTER);
      frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
      frame.setVisible(true);

    } catch (Exception e) {
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
      int command = openDialog.showOpenDialog(null);

      if (command == JFileChooser.APPROVE_OPTION)
        path = openDialog.getSelectedFile().getAbsolutePath();

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

      contents = null;
      fr.close();
      br.close();
      System.gc();
    } catch (IOException ioe) {
      status1.setText("Error opening file.");
    }
  }

  public void FileSaver(String filepath) {
    try {
      if (!path.equals(System.getProperty("java.io.tmpdir") + "New File")) {
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
    } catch (IOException ioe) {
      status1.setText("Error saving the file.");
    }
  }

  public void FileSaveAs() {
    JFileChooser saveAsDialog = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    int command = saveAsDialog.showSaveDialog(null);

    if (command == JFileChooser.APPROVE_OPTION) {
      path = (saveAsDialog.getSelectedFile().getAbsolutePath());
      FileSaver(path);
    } else if (command == JFileChooser.CANCEL_OPTION) {
      status1.setText("File is not saved.");
    }
  }

  public int warningDialog() {
    // ! When OptionPane is closed using 'X' button, invokes FileSaver()
    int opt = JOptionPane.showConfirmDialog(frame,
        "There are some unsaved changes in the file. Do you want to save the changes and continue?",
        "Warning: Unsaved changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
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
        textarea.setText("");
        path = System.getProperty("java.io.tmpdir") + "New File";
        status1.setText("Ready.");
        status2.setText("Unsaved");
        status3.setText(" File");
        frame.setTitle("Trident Text Editor - New File");
        warned = false;
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
          } else if (opt == JOptionPane.CANCEL_OPTION) {
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
          System.err.println("UFE");
        } catch (IOException ioe) {
          System.err.println("IOE");
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
        status1.setText("Ready");
        break;
      case "Redo":
        undoManager.redo();
        status1.setText("Ready");
        break;
      }
    } catch (CannotRedoException redoErr) {
      status1.setText("Redo could not be done.");
    } catch (CannotUndoException undoErr) {
      status1.setText("Undo could not be done.");
    } catch (HeadlessException noHead) {
    } catch (Exception oopsErr) {
    }
  }
}

class FormatMenuListener extends FileMenuListener implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
    case "Fonts":
      // break;
    case "Themes":
      // break;
    case "Settings":
      try {
        JDialog jsonEditor = new JDialog();
        jsonEditor.setSize(450, 350);
        jsonEditor.setTitle("Style Editor");
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
              // ! Huge delay in editor since we're writing file for every single change
              String jsonContents = jsonViewer.getText();
              File jsonFile = new File("configurations.json");
              FileWriter fileWritter = new FileWriter(jsonFile, false);
              BufferedWriter bw = new BufferedWriter(fileWritter);
              bw.write(jsonContents);
              Thread.sleep(1000);
              bw.close();
            } catch (IOException fIoException) {
            } catch (InterruptedException fInterruptedException) {
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
      } catch (IOException ioe) {
        System.err.println("IOE");
      }
      break;
    }
  }
}

class RunMenuListener extends Trident implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
    case "Compile":
      break;

    case "Run":
      break;

    case "Compile and Run":
      break;

    case "Open Console":
      break;
    }
  }
}

class AboutMenuListener extends Trident implements ActionListener {
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
    case "About Trident":
      // TODO: Add link to version.config
      JDialog aboutDialog = new JDialog(frame, "About Trident");
      JPanel infoPanel = new JPanel();
      ImageIcon ic = new ImageIcon("raw\\trident_logo.png");
      JLabel icon = new JLabel(ic);
      icon.setSize(50, 50);
      JLabel l1 = new JLabel(
          "<html> <center><h2> <br/>Trident Text Editor</h2> <br/> Version 0.0.6 <br/>MAVEN - ALPHA<br/> <a href=\"https://github.com/KrishnaMoorthy12/trident\">View Source Code - GitHub</a></center> </html>");
      // ! Link is not clickable
      infoPanel.setBorder(new EmptyBorder(10, 5, 5, 5));
      infoPanel.add(icon);
      infoPanel.add(l1);
      aboutDialog.add(infoPanel);
      aboutDialog.setSize(350, 500);
      aboutDialog.setResizable(false);
      aboutDialog.setVisible(true);
      break;

    case "File Properties":
      String fileName = Paths.get(path).getFileName().toString();
      JDialog aboutFileDialog = new JDialog(frame, "File Properties");
      JLabel filenameProperty = new JLabel(fileName);
      JLabel fileLocationProperty = new JLabel(path);
      JLabel fileTypeProperty = new JLabel(fileTypeParser(path));
      JPanel leftPane = new JPanel();
      JPanel rightPane = new JPanel();
      leftPane.setLayout(new GridLayout(5, 1, 2, 2));
      rightPane.setLayout(new GridLayout(5, 1, 2, 2));
      aboutFileDialog.setLayout(new FlowLayout());

      File theFile = new File(path);
      JLabel fileSizeProperty = new JLabel((theFile.length() / 1024) + "KB (" + theFile.length() + " B)");
      JLabel lastModifiedProperty = new JLabel(new Date(theFile.lastModified()) + "");

      leftPane.add(new JLabel("File Name   ", SwingConstants.RIGHT));
      rightPane.add(filenameProperty);
      leftPane.add(new JLabel("File Location   ", SwingConstants.RIGHT));
      rightPane.add(fileLocationProperty);
      leftPane.add(new JLabel("File Type   ", SwingConstants.RIGHT));
      rightPane.add(fileTypeProperty);
      leftPane.add(new JLabel("File Size   ", SwingConstants.RIGHT));
      rightPane.add(fileSizeProperty);
      leftPane.add(new JLabel("Last modified   ", SwingConstants.RIGHT));
      rightPane.add(lastModifiedProperty);
      aboutFileDialog.add(leftPane);
      aboutFileDialog.add(rightPane);
      aboutFileDialog.setSize(450, 130);
      aboutFileDialog.setResizable(false);
      aboutFileDialog.setVisible(true);
      break;

    case "Visit our site":
      break;

    case "Help":
      break;

    case "Updates":
      break;
    }
  }
}

class ChangeListener extends Trident implements DocumentListener, CaretListener {
  private static void warn() {
    if (!warned) {
      status2.setText("Unsaved");
      warned = true;
      frame.setTitle(frame.getTitle() + " - Unsaved");
    }
  }

  public void caretUpdate(CaretEvent ce) {
    try {

      int offset = textarea.getCaretPosition();
      int line = textarea.getLineOfOffset(offset) + 1;
      status4.setText("Line: " + line + "   Offset: " + (offset + 1));
    } catch (BadLocationException badexp) {
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