import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Desktop;
import java.io.File;

public class TridentCompiler {
  public static void compile(String filepath)
      throws UnsupportedOperatingSystemException, UnsupportedFileException, IOException {
    if (Trident.checkOS() != 1) {
      throw new UnsupportedOperatingSystemException();
    }
    String fileType = FileTypeParser.getType(filepath);
    switch (fileType) {
    case "Python Source File":
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\"echo && python " + filepath + "\"");
      break;

    case "Java Source File":
      Runtime.getRuntime()
          .exec("cmd /c start cmd.exe /K" + "\"echo && javac " + filepath + " && echo Compilation ended.\"");
      break;

    case "C Source File":
      String fileLocation = (new File(filepath)).getParent();
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\"cd " + fileLocation + " && echo && gcc " + filepath
          + "-std=c99 && echo Compilation ended.\"");
      // using C99 to avoid irritating forbidden errors
      break;

    case "C++ Source File":
      fileLocation = (new File(filepath)).getParent();
      System.out.println(fileLocation);
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\"cd " + fileLocation + " && echo && g++ " + filepath
          + " && echo Compilation ended.\"");
      break;

    default:
      throw new UnsupportedFileException(filepath);
    }
  }

  public static void execute(String filepath)
      throws UnsupportedOperatingSystemException, UnsupportedFileException, IOException {
    if (Trident.checkOS() != 1) {
      throw new UnsupportedOperatingSystemException();
    }

    String exec;

    String fileType = FileTypeParser.getType(filepath);
    switch (fileType) {
    case "Python Source File":
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\"echo && python " + filepath + "\"");
      break;

    case "Java Source File":
    case "Java Class File":
      Path name = Paths.get(filepath);
      String classFile = name.getFileName().toString();
      exec = classFile.replaceFirst("[.][^.]+$", "");
      String location = new File(filepath).getParent().toString();
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\" cd " + location + "&& echo && java " + exec + "\"");
      break;

    case "C Source File":
    case "C++ Source File":
      exec = (new File(filepath)).getParent() + "/a.exe";
      Runtime.getRuntime().exec("cmd /c start cmd.exe /K" + "\"echo && " + exec + "\"");
      break;

    case "HTML File":
      try {
        filepath = filepath.replace('\\', '/');
        Desktop.getDesktop().browse(java.net.URI.create("file:///" + filepath));
      } catch (IOException ioe) {
        Trident.ErrorDialog("COMPILER_BROSWER_ERR", ioe);
      }
      break;

    default:
      throw new UnsupportedFileException(filepath);
    }
  }
}