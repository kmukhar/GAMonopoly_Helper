package edu.uccs.ecgs.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import com.mukhar.commons.LineDataReader;

public class DataCleaner {
  public static void main(String[] args) {
    File data = new File("D:/Documents and Data/workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/dir.txt");
    LineDataReader ldr = new LineDataReader();
    ldr.openFile(data);
    ArrayList<String> datanames = ldr.readFile(2048);
    ldr.closeFile();
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
      File pfFile = new File(pfName);
      LineDataReader ldr = new LineDataReader();
      ldr.openFile(pfFile);
      ArrayList<String> vals = ldr.readFile(2048);
      ldr.closeFile();
      String lastLine = vals.get(vals.size() - 1);
      String[] score_id = lastLine.split(",");
      idToSave = score_id[1];
      StringBuilder sb = new StringBuilder(idToSave);
      while (sb.length() < 4)
        sb.insert(0, '0');
      idToSave = sb.toString();
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
