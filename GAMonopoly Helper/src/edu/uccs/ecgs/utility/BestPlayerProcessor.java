package edu.uccs.ecgs.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import edu.uccs.ecgs.utility.DirCleaner.DirActions;

/**
 * Read a list of directories, find the best player in each directory, and
 * copy the data file to a new directory.
 *
 */
public class BestPlayerProcessor {

  static int newId = 1;
  
  public static void main(String[] args)
  {
    // read a data file that contains a list of all the directories to clean
    Path path = FileSystems.getDefault().getPath(".");
    path = path.toAbsolutePath().getParent();
    path = path.resolve("src/edu/uccs/ecgs/utility");
    path = path.resolve("gen999.txt");

    // read all the lines in the data file
    List<String> datanames = new ArrayList<String>();
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // process each directory in the data file
    for (String dataname : datanames) {
      System.out.println("Processing " + dataname);
      processFiles(dataname);
    }
  }

  public static void processFiles(String dirName)
  {
    // get the path for the directory
    Path path = FileSystems.getDefault().getPath(dirName);

    // find and read the player fitness data file
    List<String> vals = new ArrayList<String>();
    try {
      vals = Files.readAllLines(path.resolve("player_fitness.csv"),
          Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    String idToSave = "";

    // the last line in the file is the best player of that generation
    // (disregarding ties for best player)
    if (vals.size() > 0) {
      String lastLine = vals.get(vals.size() - 1);
      String[] score_id = lastLine.split(",");
      // this is the id of the best player
      idToSave = score_id[1];
      StringBuilder sb = new StringBuilder(idToSave);
      // convert it to a 4 char string
      while (sb.length() < 4)
        sb.insert(0, '0');
      idToSave = sb.toString();
    }

    // get a path to the data file we are saving
    Path pathToSave = path.resolve("player" + idToSave + ".dat");
    
    // get the new file name
    if (!idToSave.equals("9999")) {
      Path newPath = FileSystems.getDefault().getPath(
          "D:/Documents and Data/Kevin/Monopoly/gamedata/bestplayers");

      StringBuilder sb = new StringBuilder();
      sb.append(newId);
      // convert it to a 4 char string
      while (sb.length() < 4)
        sb.insert(0, '0');
      
      ++newId;
      
      newPath = newPath.resolve("player" + sb.toString() + ".dat");
      System.out.println("Player " + sb.toString() + " :: "
          + path.subpath(4, 5) + " :: " + path.subpath(7, 8));

      // move the data file to the new directory with new name
      // then delete the directory
      try {
        Files.copy(pathToSave, newPath, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
