package io.sugo.http;

import java.net.URL;
import java.util.Properties;

/**
 * Created by root on 17-4-21.
 */
public class UrlTest {
  public static void main(String[] args) {
    URL url = UrlTest.class.getClassLoader().getResource("check.sh");
    System.out.println(url);
    System.out.println(url.toString());
    System.out.println(url.getPath());
    System.out.println(url.getFile());
    Properties pros = new Properties();
    pros.put("test1", "test");
    System.out.println(pros);
  }
}
