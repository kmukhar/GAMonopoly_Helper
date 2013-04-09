package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

import javax.swing.JFileChooser;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ValidationProcessor
{
  public static void main(String[] args)
  {
    ValidationProcessor processor = new ValidationProcessor();

    JFileChooser fc = new JFileChooser("C:/Users/kmukhar/Documents/Validation/RGA");
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int result = fc.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) {
      System.exit(0);
    }

    File f = fc.getSelectedFile();
    List<String> datanames = new ArrayList<String>();
    Path path = null;
    
    if (f.isDirectory()) {
      datanames.add(f.getPath());
    } else { 
      path = FileSystems.getDefault().getPath(".");
      path = path.toAbsolutePath().getParent();
      path = path.resolve("GAMonopoly Helper/src/edu/uccs/ecgs/utility");
      path = path.resolve("basedirs.txt");
      try {
        datanames = Files.readAllLines(path, Charset.defaultCharset());
      } catch (IOException e) {
        e.printStackTrace();
      }
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
      // get the first file name off the list
      String fname = files.remove(0);
      File file = new File(fname);

      if (file.isDirectory()) {
        // if it's a directory, add all its files to list
        addAll(file.list(), file.getAbsolutePath(), files);
      } else {
        // otherwise, if it's name is player.fitness, then get its data
        if (file.getName().matches("player_fitness.csv")) {

          Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
          List<String> result = new ArrayList<String>();
          try {
            result = Files.readAllLines(path, Charset.defaultCharset());
          } catch (IOException e) {
            e.printStackTrace();
          }

          for (String s : result) {
            String[] vals = s.split(",");
            // but only process the line if it has numbers
            if (vals[0].matches("\\d*")) {
              Integer score = Integer.parseInt(vals[0]);
              Integer playerID = Integer.parseInt(vals[1].replaceAll("Player ", ""));
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

  private void dumpScores(File basedir,
      TreeMap<Integer, ArrayList<Integer>> allScores)
  {
    Path path = FileSystems.getDefault().getPath(".");
    path = path.toAbsolutePath().getParent();
    path = path.resolve("GAMonopoly Helper/src/edu/uccs/ecgs/utility");

    Path sourcePath = FileSystems.getDefault().getPath(
        basedir.getAbsolutePath());
    int indexFO = sourcePath.toString().indexOf("finish_order");
    int indexNW = sourcePath.toString().indexOf("num_wins");

    if (indexFO >= 0) {
      path = path.resolve("allscores_finish_order.xlsx");
    } else if (indexNW >= 0) {
      path = path.resolve("allscores_num_wins.xlsx");
    } else {
      System.out.println("Unable to determine correct fit evalator from dir name");
      return;
    }

    File file = path.toFile();

    SpreadSheet ss = new SpreadSheet();
    try {
      ss.writeData(file, allScores, basedir.getAbsolutePath());
    } catch (InvalidFormatException | IOException e) {
      e.printStackTrace();
    }
  }
}
