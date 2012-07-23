package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ValidationProcessor {
  public static void main(String[] args)
  {
    ValidationProcessor processor = new ValidationProcessor();

    Path path = FileSystems.getDefault().getPath("D:/Documents and Data",
        "workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/basedirs.txt");
    List<String> datanames = new ArrayList<String>();
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (String dataname : datanames) {
      File file = new File(dataname);
      processor.processData(file);
    }
  }

  private void processData(File basedir)
  {
    TreeMap<Integer, ArrayList<Integer>> allScores = new TreeMap<Integer, ArrayList<Integer>>();

    String[] fileList = basedir.list();
    ArrayList<String> files = new ArrayList<String>();
    addAll(fileList, basedir.getAbsolutePath(), files);

    while (files.size() > 0) {
      //get the first file name off the list
      String fname = files.remove(0);
      File file = new File(fname);

      if (file.isDirectory()) {
        //if it's a directory, add all its files to list
        addAll(file.list(), file.getAbsolutePath(), files);
      } else {
        // otherwise, if it's name is player.fitness, then get its data
        if (file.getName().matches("player_fitness.csv")) {
          
          Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
          List<String> result = new ArrayList<String>();
          try {
            result = Files.readAllLines(path,
                Charset.defaultCharset());
          } catch (IOException e) {
            e.printStackTrace();
          }

          for (String s : result) {
            String[] vals = s.split(",");
            // but only process the line if it has numbers
            if (vals[0].matches("\\d*")) {
              Integer score = Integer.parseInt(vals[0]);
              Integer playerID = Integer.parseInt(vals[1]);
              ArrayList<Integer> playerscore = allScores.get(playerID);
              if (playerscore == null) {
                playerscore = new ArrayList<Integer>();
                allScores.put(playerID, playerscore);
              }
              playerscore.add(score);
            }
          }
        }
      }
    }
    dumpScores(basedir, allScores);
  }

  private void addAll(String[] fs, String basedir, ArrayList<String> files)
  {
    for (String name : fs) {
      files.add(basedir + "/" + name);
    }
  }
  
  private void dumpScores(File basedir, TreeMap<Integer, ArrayList<Integer>> allScores) {
    File file = new File("D:/Documents and Data/workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/allscores.xlsx");
    SpreadSheet ss = new SpreadSheet();
    try {
      ss.writeData(file, allScores, basedir.getAbsolutePath());
    } catch (InvalidFormatException | IOException e) {
      e.printStackTrace();
    }
  }
}
