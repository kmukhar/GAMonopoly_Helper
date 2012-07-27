package edu.uccs.ecgs.utility;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
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

    ArrayList<Integer> g250 = allScores.get(250);
    ArrayList<Integer> g500 = allScores.get(500);
    ArrayList<Integer> g750 = allScores.get(750);
    ArrayList<Integer> g999 = allScores.get(999);

    for (Integer val : g250) {
      Row row = sheet.getRow(rowIndex);
      Cell cell250 = row.getCell(0);
      cell250.setCellValue(g250.get(index));

      Cell cell500 = row.getCell(1);
      cell500.setCellValue(g500.get(index));

      Cell cell750 = row.getCell(2);
      cell750.setCellValue(g750.get(index));
      
      Cell cell999 = row.getCell(3);
      cell999.setCellValue(g999.get(index));

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
