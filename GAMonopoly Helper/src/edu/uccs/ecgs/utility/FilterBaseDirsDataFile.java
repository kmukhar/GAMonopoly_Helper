package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

public class FilterBaseDirsDataFile
{
  public static void main(String[] args)
  {
    Path path = FileSystems.getDefault().getPath(".");
    path = path.toAbsolutePath().getParent();
    path = path.resolve("GAMonopoly Helper/src/edu/uccs/ecgs/utility");
    path = path.resolve("basedirs.txt");

    List<String> datanames = new ArrayList<String>();
    List<String> filteredNames = new ArrayList<String>();

    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    Hashtable<String, String> matches = new Hashtable<String, String>();
    for (FitEvalTypes evalType : FitEvalTypes.values()) {
      if (evalType != FitEvalTypes.TOURNAMENT)
        matches.put(evalType.name().toLowerCase(), 
            evalType.name().toLowerCase());
    }

    for (String dataname : datanames) {
      Path datapath = FileSystems.getDefault().getPath(dataname);
      String strPath = datapath.getFileName().toString();
      if (matches.get(strPath) != null) {
        filteredNames.add(dataname);
      }
    }

    File file = path.toFile();
    file.delete();

    BufferedWriter bw = null;
    try {
      FileWriter fw = new FileWriter(file);
      bw = new BufferedWriter(fw);
      
      for (String s : filteredNames) {
        bw.write(s);
        bw.newLine();
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
