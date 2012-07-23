package edu.uccs.ecgs.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

public class DataCleaner2 {
  public static void main(String[] args) {
    Path path = FileSystems.getDefault().getPath("D:/Documents and Data/workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/dir2.txt");
    List<String> datanames;
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
      for (String dataname : datanames) {
        System.out.println("Cleaning " + dataname);
        cleanFiles(dataname);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void cleanFiles(String dirName)
  {
    Path path = FileSystems.getDefault().getPath(dirName);
    String dir = path.getFileName().toString();
    if (dir.matches("Generation_[0-9][0-9][0-9][0-9][0-9]") && !dir.endsWith("00000")) {
      DirCleaner.deleteDir(path);
    }
  }
}
