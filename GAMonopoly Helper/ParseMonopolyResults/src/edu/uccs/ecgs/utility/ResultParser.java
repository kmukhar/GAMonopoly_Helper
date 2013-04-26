package edu.uccs.ecgs.utility;

import java.io.*;
import javax.swing.JFileChooser;

/**
 * A class to parse the emails that contain Monopoly results from human versus
 * computer competitions.
 */
public class ResultParser {
  public static void main(String[] args)
  {
    ResultParser rp = new ResultParser();
    try {
      rp.parseResults();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseResults() throws IOException
  {
    String lf = System.getProperty("line.separator");

    JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int result = fc.showOpenDialog(null);
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File dir = fc.getSelectedFile();
    File[] files = dir.listFiles();

    BufferedReader br = null;
    BufferedWriter bw = null;

    bw = new BufferedWriter(new FileWriter(dir.getPath() + "/results.csv"));

    for (File file : files) {
      br = new BufferedReader(new FileReader(file));
      String line = br.readLine();

      while (line != null) {
        if (line.trim().startsWith("BEGIN")) {
          for (int i = 0; i < 4; i++) {
            line = br.readLine().trim();
            bw.write(line);
            System.out.println(line);
            bw.write(lf);
          }
          bw.write(lf);
          System.out.println();
          break;
        } else if (line.trim().contains("BEGIN")) {
          StringBuilder sb = new StringBuilder(line);
          do {
            line = br.readLine();
            sb.append(line);
          } while (!line.contains("END"));

          String strSb = sb.toString();
          strSb = strSb.trim().replaceAll("=0A", lf).replaceAll("=A0", lf);
          strSb = strSb.replaceAll("=", "");
          String[] lines = strSb.split(lf);

          int count = 0;
          for (String s : lines) {
            if (s.contains("BEGIN"))
              continue;

            bw.write(s);
            bw.write(lf);
            System.out.println(s);
            count++;
            if (count == 5)
              break;
          }

          break;
        }
        line = br.readLine();
      }

      br.close();
    }

    bw.close();
  }
}
