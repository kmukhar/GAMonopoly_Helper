package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SpreadSheet {
  public void writeData(File file,
                        TreeMap<Integer, ArrayList<Integer>> allScores,
                        String basedir) throws InvalidFormatException,
      IOException
  {
    InputStream inp = new FileInputStream(file);

    XSSFWorkbook wb = new XSSFWorkbook(inp);

    Sheet sheet = wb.getSheetAt(0);
    int rowIndex = 1;
    int index = 0;

    Iterator<Integer> keys = allScores.keySet().iterator();
    ArrayList<ArrayList<Integer>> scores = new ArrayList<ArrayList<Integer>>();
    Row row = sheet.getRow(0);
    while (keys.hasNext()) {
      Integer key = keys.next();
      row.getCell(scores.size()).setCellValue(key.intValue());
      ArrayList<Integer> vals = allScores.get(key);
      scores.add(vals);
    }

    for (Integer val : scores.get(0)) {
      row = sheet.getRow(rowIndex);
      Cell cell250 = row.getCell(0);
      cell250.setCellValue(scores.get(0).get(index));

      Cell cell500 = row.getCell(1);
      cell500.setCellValue(scores.get(1).get(index));

      Cell cell750 = row.getCell(2);
      cell750.setCellValue(scores.get(2).get(index));
      
      Cell cell999 = row.getCell(3);
      cell999.setCellValue(scores.get(3).get(index));

      ++index;
      ++rowIndex;
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
    String fit = path.getFileName().toString();
    String chromo = path.getParent().getFileName().toString();
    String nm = path.getParent().getParent().getParent().getFileName().toString();
    nm = "n" + nm.substring(nm.length() - 3);

    path = path.resolve("allscores_" + nm + "_" + chromo + "_"
        + fit + ".xlsx");

    System.out.println("Writing file " + path.toString());

    File outfile = path.toFile();
    FileOutputStream fileOut = new FileOutputStream(outfile);
    wb.write(fileOut);
    fileOut.flush();
    fileOut.close();
  }
}
