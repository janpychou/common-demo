package io.sugo.compression.zip;

import com.google.common.io.Files;
import org.joda.time.DateTime;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressionTest {
  public static void main(String[] args) throws IOException {
    String src = "/work/win7/movies/徐小湛《线性代数》";
    String dest = "/work/win7/movies/math.zip";
    long start = System.currentTimeMillis();
    for (int i = 0; i < 1; i++) {
      zip(src, dest);
    }
    System.out.println("not buffered:" + (System.currentTimeMillis() - start));
    start = System.currentTimeMillis();
    for (int i = 0; i < 1; i++) {
      zip1(src, dest);
    }
    System.out.println("buffered:" + (System.currentTimeMillis() - start));
  }

  private static void zip(String src, String dest) throws IOException {
    File srcDir = new File(src);
    File dFile = new File(dest);
    if (dFile.exists()) {
      dFile.delete();
    }
    FileOutputStream fos = new FileOutputStream(dFile);

    long start = System.currentTimeMillis();
    System.out.println(new DateTime());
    File[] files = srcDir.listFiles();
    long totalSize = 0;
    final ZipOutputStream zipOut = new ZipOutputStream(fos);
    try {
      zipOut.setLevel(1);
      long s1 = System.currentTimeMillis();
      for (File file : files) {
        if (file.length() >= Long.MAX_VALUE) {
          zipOut.finish();
          throw new IOException(String.format("file[%s] too large [%,d]", file, file.length()));
        }
        zipOut.putNextEntry(new ZipEntry(file.getName()));
        totalSize += Files.asByteSource(file).copyTo(zipOut);
        long s2 = System.currentTimeMillis();
        if (s2 > s1) {
          info("size[%,d]. \t  Time:%d, \t speed:%d \t Total size[%,d], \t %s",
              file.length(), s2 - s1, file.length() / (s2 - s1), totalSize, file);
        }
        s1 = s2;
      }
      zipOut.closeEntry();
      // Workarround for http://hg.openjdk.java.net/jdk8/jdk8/jdk/rev/759aa847dcaf
      zipOut.flush();
    } finally {
      zipOut.close();
    }
    System.out.println(new DateTime());
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }

  private static void zip1(String src, String dest) throws IOException {
    File srcDir = new File(src);
    File dFile = new File(dest);
    if (dFile.exists()) {
      dFile.delete();
    }
    FileOutputStream fos = new FileOutputStream(dFile);
    long start = System.currentTimeMillis();
    System.out.println(new DateTime());
    File[] files = srcDir.listFiles();
    long totalSize = 0;
    final ZipOutputStream zipOut = new ZipOutputStream(fos);
    try {
      zipOut.setLevel(1);
      long s1 = System.currentTimeMillis();
      for (File file : files) {
        if (file.length() >= Long.MAX_VALUE) {
          zipOut.finish();
          throw new IOException(String.format("file[%s] too large [%,d]", file, file.length()));
        }
        zipOut.putNextEntry(new ZipEntry(file.getName()));
        //        totalSize += Files.asByteSource(file).copyTo(zipOut);
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        byte[] datas = new byte[4096];
        int len = 0;
        while ((len = bis.read(datas)) != -1) {
          zipOut.write(datas, 0, len);
          totalSize += len;
        }
        long s2 = System.currentTimeMillis();
        if (s2 > s1) {
          info("size[%,d]. \t  Time:%d, \t speed:%d \t Total size[%,d], \t %s",
              file.length(), s2 - s1, file.length() / (s2 - s1), totalSize, file);
        }
        s1 = s2;
      }
      zipOut.closeEntry();
      // Workarround for http://hg.openjdk.java.net/jdk8/jdk8/jdk/rev/759aa847dcaf
      zipOut.flush();
    } finally {
      zipOut.close();
    }
    System.out.println(new DateTime());
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }

  private static void info(String s, Object... args) {
//    System.out.println(String.format(s, args));
  }
}
