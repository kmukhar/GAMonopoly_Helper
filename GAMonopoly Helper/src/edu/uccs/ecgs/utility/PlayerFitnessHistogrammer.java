package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;

public class PlayerFitnessHistogrammer
{
  public static void main(String[] args)
  {
    PlayerFitnessHistogrammer processor = new PlayerFitnessHistogrammer();

    Path path = FileSystems.getDefault().getPath(".");
    path = path.toAbsolutePath().getParent();
    path = path.resolve("src/edu/uccs/ecgs/utility");
    path = path.resolve("histodirs.txt");

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
    String[] fileList = basedir.list();
    ArrayList<String> files = new ArrayList<String>();
    addAll(fileList, basedir.getAbsolutePath(), files);

    while (files.size() > 0) {
      // get the first file name off the list
      String fname = files.remove(0);
      File file = new File(fname);

      if (file.isDirectory()) {
        fileList = file.list();
        addAll(fileList, file.getAbsolutePath(), files);

      } else if (file.getName().matches("player_fitness.csv")) {
        // otherwise, if it's name is player.fitness, then get its data
        Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
        List<String> result = new ArrayList<String>();
        try {
          result = Files.readAllLines(path, Charset.defaultCharset());
          dumpScores(file, result);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void addAll(String[] fs, String basedir, ArrayList<String> files)
  {
    for (String name : fs) {
      files.add(basedir + "/" + name);
    }
  }

  private void dumpScores(File basedir,
      List<String> result)
  {
    Path sourcePath = FileSystems.getDefault().getPath(
        basedir.getAbsolutePath());
    Path path = sourcePath.getParent().getParent().getParent().getParent()
        .getParent().resolve("player_fitness.xlsx");

    File file = path.toFile();

    SpreadSheet2 ss = new SpreadSheet2();
    try {
      ss.writeData(file, result, basedir.getAbsolutePath());
    } catch (InvalidFormatException | IOException e) {
      e.printStackTrace();
    }
  }
}

class SpreadSheet2 {
  public void writeData(File file,
                        List<String> data,
                        String basedir) throws InvalidFormatException,
      IOException
  {
    InputStream inp = new FileInputStream(file);

    XSSFWorkbook wb = new XSSFWorkbook(inp);

    Sheet sheet = wb.getSheetAt(0);
    int rowIndex = 1;

    for (String s : data) {
      String[] vals = s.split(",");
      
      if (vals.length < 2) {
        System.out.println("file: "+file.getAbsolutePath());
        System.out.println("[" + s + "]");
        continue;
      }

      // but only process the line if it has numbers
      if (vals[0].matches("\\d*")) {
        Integer score = Integer.parseInt(vals[0]);
        Integer playerID = Integer.parseInt(vals[1]);

        Row row = sheet.getRow(rowIndex);
        Cell cell = row.getCell(0);
        cell.setCellValue(score);
        
        cell = row.getCell(1);
        cell.setCellValue(playerID);
        ++rowIndex;
      }
    }

    XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(wb);

    for (int sheetNum = 0; sheetNum < 2; sheetNum++) {
      sheet = wb.getSheetAt(sheetNum);
      for (Row r : sheet) {
        for (Cell c : r) {
          try {
            evaluator.evaluateFormulaCell(c);
          } catch (NotImplementedException ignored) {
          }
        }
      }
    }
    
    // Write the output to a file
    Path path = FileSystems.getDefault().getPath(basedir);
    path = path.getParent().resolve("player_fitness.xlsx");

    sheet = wb.getSheetAt(0);
    ++rowIndex;
    // path to data
//    System.out.print(path.toString()+"\n");

    // output the population
    String[] elems = path.toString().split("\\\\");
    String population = elems[7] + "-" + elems[5].substring(1, 5) + "-"
        + elems[5].substring(7, 10);
    if (elems[8].equalsIgnoreCase("finish_order")) {
      population += "-FO";
    } else if (elems[8].equalsIgnoreCase("finish_order")) {
      population += "-FO";
    } else if (elems[8].equalsIgnoreCase("net_worth")) {
      population += "-NetW";
    } else if (elems[8].equalsIgnoreCase("num_monopolies")) {
      population += "-NM";
    } else if (elems[8].equalsIgnoreCase("num_properties")) {
      population += "-NP";
    } else if (elems[8].equalsIgnoreCase("num_wins")) {
      population += "-NW";
    } else {
      System.out.println("STOP");
      System.exit(-1);
    }
    System.out.print(population + "," + elems[9].substring(11, 16) + ",");
    
    // min
    Cell cell = sheet.getRow(rowIndex++).getCell(1);
    System.out.print(cell.getNumericCellValue()+",");
    //max
    cell = sheet.getRow(rowIndex++).getCell(1);
    System.out.print(cell.getNumericCellValue()+",");
    // average
    cell = sheet.getRow(rowIndex++).getCell(1);
    System.out.print(cell.getNumericCellValue()+",");
    // variance
    cell = sheet.getRow(rowIndex++).getCell(1);
    System.out.print(cell.getNumericCellValue()+",");
    // std dev
    cell = sheet.getRow(rowIndex++).getCell(1);
    System.out.println(cell.getNumericCellValue());

    File outfile = path.toFile();
    FileOutputStream fileOut = new FileOutputStream(outfile);
    wb.write(fileOut);
    fileOut.flush();
    fileOut.close();
  }
}
