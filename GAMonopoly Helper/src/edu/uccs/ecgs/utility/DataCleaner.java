package edu.uccs.ecgs.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import edu.uccs.ecgs.utility.DirCleaner.DirActions;

public class DataCleaner
{
  public static void main(String[] args)
  {
    // read a data file that contains a list of all the directories to clean
    Path path = FileSystems.getDefault().getPath("C:/Users/kmukhar/workspace",
        "GAMonopoly_Helper/GAMonopoly Helper/src",
        "edu/uccs/ecgs/utility/dir.txt");
    List<String> datanames = new ArrayList<String>();
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // process each directory
    for (String dataname : datanames) {
      System.out.println("Cleaning " + dataname);
      cleanFiles(dataname);
    }
  }

  public static void cleanFiles(String dirName)
  {
    Path path = FileSystems.getDefault().getPath(dirName);

    String idToSave = "9999";
    String generation = "00000";

    // if not the initial generation
    if (!dirName.endsWith("00000")) {
      String absPath = path.toString();
      // get the generation number
      generation = absPath.substring(absPath.length() - 4);

      // find and read the player fitness data file
      List<String> vals = new ArrayList<String>();
      try {
        vals = Files.readAllLines(path.resolve("player_fitness.csv"),
            Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }

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
    } else if (dirName.endsWith("00000")) {
      DirCleaner.deleteFilesInPath(path, new ArrayList<Path>(), DirActions.SAVE_DIR);
    }

    // get a path to the data file we are saving
    Path pathToSave = path.resolve("player" + idToSave + ".dat");

    // get the new file name 
    if (!idToSave.equals("9999")) {
      Path newPath = pathToSave.getParent().getParent()
          .resolve("Generation_00000").resolve("player" + generation + ".dat");

      // move the data file to Generation_00000 with new name
      // then delete the directory
      try {
        Files.move(pathToSave, newPath);
        DirCleaner.deleteDir(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
