package edu.uccs.ecgs.utility;

import java.io.*;
import java.util.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.mukhar.commons.FilePicker;
import com.mukhar.commons.LineDataReader;

public class ValidationProcessor {
  public static void main(String[] args)
  {
    ValidationProcessor processor = new ValidationProcessor();

    File data = new File(
        "D:/Documents and Data/workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/basedirs.txt");
    LineDataReader ldr = new LineDataReader();
    ldr.openFile(data);
    ArrayList<String> datanames = ldr.readFile(2048);
    ldr.closeFile();
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
          LineDataReader r = new LineDataReader();
          r.openFile(file);
          ArrayList<String> result = r.readFile(5);
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
