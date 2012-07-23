package edu.uccs.ecgs.utility;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class DirCleaner
{
  /**
   * Delete the files in the path, and optionally also delete the path. If the
   * directory represented by the path is to be deleted, the directory must be
   * empty after all the files in the directory have been processed. If the
   * exceptions list is not empty, and if the directory contains one or more
   * files in the exceptions list, then those files will not be deleted and the
   * directory will not be deleted. in addition, if the file delete fails for
   * any file or files, deleting the directory will fail.
   * 
   * @param path The path to delete files from
   * @param exceptions Any files that should not be deleted
   * @param deleteDir Whether to delete or save the directory
   */
  public static void deleteFilesInPath(Path path,
      final ArrayList<Path> exceptions, final DirActions deleteDir)
  {
    try {
      Files.walkFileTree(path, new FileVisitor<Path>()
      {

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
            BasicFileAttributes attrs) throws IOException
        {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException
        {
          if (!exceptions.contains(file))
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
          if (deleteDir == DirActions.DELETE_DIR)
            Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Delete all files in the path and its subpaths, and delete the path
   * @param dir The directory to delete
   */
  public static void deleteDir(Path dir)
  {
    deleteFilesInPath(dir, new ArrayList<Path>(), DirActions.DELETE_DIR);
  }

  public enum DirActions
  {
    DELETE_DIR,
    SAVE_DIR;
  }

}
