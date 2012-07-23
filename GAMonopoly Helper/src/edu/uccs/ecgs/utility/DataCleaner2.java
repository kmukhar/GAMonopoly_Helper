package edu.uccs.ecgs.utility;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DataCleaner2 {
  public static void main(String[] args) {
    Path path = FileSystems.getDefault().getPath("D:/Documents and Data/workspace/GAMonopoly Helper/src/edu/uccs/ecgs/utility/dir2.txt");
    List<String> datanames;
    try {
      datanames = Files.readAllLines(path, Charset.defaultCharset());
      for (String dataname : datanames) {
        System.out.println("Cleaning " + dataname);
        cleanFiles(dataname);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void cleanFiles(String dirName)
  {
    Path path = FileSystems.getDefault().getPath(dirName);
    String dir = path.getFileName().toString();
    if (dir.matches("Generation_[0-9][0-9][0-9][0-9][0-9]") && !dir.endsWith("00000")) {
      try {
        Files.walkFileTree(path, new FileVisitor<Path>(){

          @Override
          public FileVisitResult preVisitDirectory(Path dir,
                                                   BasicFileAttributes attrs)
              throws IOException
          {
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException
          {
            if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS))
              Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc)
              throws IOException
          {
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc)
              throws IOException
          {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
          });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
