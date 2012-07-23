package edu.uccs.ecgs.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DataCleaner {
  public static void main(String[] args) {
    Path path = FileSystems.getDefault().getPath("D:/Documents and Data",
        "workspace", "GAMonopoly Helper/src/edu/uccs/ecgs/utility/dir.txt");
    List<String> datanames = new ArrayList<String>();
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }
    for (String dataname : datanames) {
      System.out.println("Cleaning " + dataname);
      cleanFiles(dataname);
    }
  }

  public static void cleanFiles(String dirName)
  {
    File dir = new File(dirName);
    String idToSave = "9999";
    String generation = "00000";

    if (!dirName.endsWith("00000")) {
      String pfName = dir.getAbsolutePath() + "/player_fitness.csv";
      String absPath = dir.getAbsolutePath();
      generation = absPath.substring(absPath.length() - 4);

      Path path = FileSystems.getDefault().getPath(pfName);
      List<String> vals = new ArrayList<String>();
      try {
        vals = Files.readAllLines(path, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }

      if (vals.size() > 0) {
        String lastLine = vals.get(vals.size() - 1);
        String[] score_id = lastLine.split(",");
        idToSave = score_id[1];
        StringBuilder sb = new StringBuilder(idToSave);
        while (sb.length() < 4)
          sb.insert(0, '0');
        idToSave = sb.toString();
      }
    }
    
    String[] files = dir.list();
    for (String name : files) {
      if (!name.endsWith(idToSave + ".dat")) {
        File fileToDel = new File(dir.getAbsolutePath() + "/" + name);
        fileToDel.delete();
      }
    }

    if (!idToSave.equals("9999")) {
      Path oldPath = FileSystems.getDefault().getPath(dir.getAbsolutePath(),
          "player" + idToSave + ".dat");
      Path newPath = oldPath.getParent().getParent().resolve("Generation_00000");
      newPath= newPath.resolve("player" + generation + ".dat");

      try {
        Files.move(oldPath, newPath);
        Files.delete(oldPath.getParent());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
