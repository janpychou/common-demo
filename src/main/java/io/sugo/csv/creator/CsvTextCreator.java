package io.sugo.csv.creator;

import com.google.common.base.Joiner;
import org.joda.time.DateTime;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CsvTextCreator {
  public static final Random rand = new Random();
  public static final DateTime nowTime = new DateTime();
  public static final int strColCnt = 40;
  public static final int floatColCnt = 5;
  public static final int doubleColCnt = 5;

  public static void main(String[] args) throws IOException {

    List<List<String>> colValues = new ArrayList<>();

    List<String> strSet;
    for (int i = 0; i < strColCnt; i++) {
      strSet = new ArrayList();
      colValues.add(strSet);
      int randLength = rand.nextInt(800) + 10;
      for (int idx = 0; idx < randLength; idx++) {
        strSet.add(getRandomString(rand.nextInt(30)));
      }
    }
    for (int i = 0; i < floatColCnt; i++) {
      strSet = new ArrayList();
      colValues.add(strSet);
      int randLength = rand.nextInt(10000);
      for (int idx = 0; idx < randLength; idx++) {
        strSet.add(getRandomFloat(rand.nextInt(3), rand.nextInt(3)));
      }
    }
    for (int i = 0; i < doubleColCnt; i++) {
      strSet = new ArrayList();
      colValues.add(strSet);
      int randLength = rand.nextInt(10000);
      for (int idx = 0; idx < randLength; idx++) {
        strSet.add(getRandomFloat(rand.nextInt(6), 8));
      }
    }
    FileOutputStream fos = new FileOutputStream("/work/tmp/200w-last.csv");
    BufferedOutputStream bos = new BufferedOutputStream(fos);
    DataOutputStream dos = new DataOutputStream(bos);
    for (int i = 0; i < 2000000; i++) {
      String row = generateRow(colValues);
//      System.out.println(row);
      dos.writeBytes(row);
      if (i % 100000 == 0) {
        System.out.println(i);
      }
    }
    dos.close();
  }

  public static final Joiner joiner = Joiner.on(',');
  public static final String[] values = new String[strColCnt + floatColCnt + doubleColCnt + 1 * doubleColCnt];

  private static String generateRow(List<List<String>> colValues) {
    List<String> colValueSet;
    int length = colValues.size();
    StringBuilder builder = new StringBuilder().append(nowTime.minusDays(rand.nextInt(50) + 10).getMillis());
    String val;
    for (int i = 0; i < length; i++) {
      colValueSet = colValues.get(i);
      val = colValueSet.get(rand.nextInt(colValueSet.size()));
      values[i] = val;
      builder.append(',').append(val);
    }
    for (int i = length - doubleColCnt; i < length; i++) {
      values[i + doubleColCnt] = values[i];
      builder.append(',').append(values[i]);
    }
//    for (int i = length - doubleColCnt; i < length; i++) {
//      values[i + doubleColCnt] = values[i];
//      builder.append(',').append(values[i]);
//    }
    builder.append(System.lineSeparator());
    return builder.toString();
    //    return String.format("%d,%s%s", nowTime, joiner.join(values), System.lineSeparator());
  }

  public static String getRandomFloat(int length, int point) { //length表示生成字符串的长度
    if (length == 0 && point == 0) {
      return "";
    }
    String base = "0123456789";
    StringBuffer sb = new StringBuffer();
    if (length < 1) {
      sb.append('0');
    } else {
      sb.append(base.charAt(rand.nextInt(base.length() - 1) + 1));
      for (int i = 0; i < length - 1; i++) {
        int number = rand.nextInt(base.length());
        sb.append(base.charAt(number));
      }
    }

    sb.append('.');

    if (point < 1) {
      sb.append('0');
    } else {
      for (int i = 0; i < point; i++) {
        int number = rand.nextInt(base.length());
        sb.append(base.charAt(number));
      }
    }
    return sb.toString();
  }

  public static String getRandomString(int length) { //length表示生成字符串的长度
    if (length < 1) {
      return "";
    }
    String base = "abcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      int number = random.nextInt(base.length());
      sb.append(base.charAt(number));
    }
    return sb.toString();
  }
}
