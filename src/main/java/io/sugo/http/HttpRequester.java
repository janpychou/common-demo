package io.sugo.http;


import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequester extends Thread{
  public static String postData(String url, String param){
    PrintWriter out = null;
    BufferedReader in = null;
    String result = null;
    HttpURLConnection connection = null;
    try {
      URL realUrl = null;
      realUrl = new URL(url);

      connection = (HttpURLConnection)realUrl.openConnection();
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type",
          "application/json");
      connection.connect();


      out = new PrintWriter(connection.getOutputStream());
      out.write(param);
      out.flush();

      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder sb = new StringBuilder("");
      String line;
      while (( line = in.readLine()) != null){
        sb.append(line);
        sb.append("\n");
      }
      result = sb.toString();
      connection.disconnect();
      return  result;
    } catch (MalformedURLException e){
      printErrorln("url错误！");
      e.printStackTrace();
    } catch (IOException e){
      printErrorln("post请求错误！");
      e.printStackTrace();
    } finally {

      try {
        if(null != connection)
          connection.disconnect();
        if(null != out)
          out.close();
        if(null != in)
          in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
  public static void printErrorln(String str){
    System.err.println(str);
  }

  public static String param = "{\n" +
      "  \"queryType\": \"lucene_timeseries\",\n" +
      "  \"dataSource\": \"janpychou-3\",\n" +
      "  \"intervals\": \"1000/3000\",\n" +
      "  \"granularity\": \"all\",\n" +
      "  \"context\": {\n" +
      "    \"timeout\": 180000,\n" +
      "    \"useOffheap\": true,\n" +
      "    \"groupByStrategy\": \"v2\"\n" +
      "  },\n" +
      "  \"aggregations\": [\n" +
      "    {\n" +
      "      \"name\": \"__VALUE__\",\n" +
      "      \"type\": \"lucene_count\"\n" +
      "    }\n" +
      "  ]\n" +
      "}";


  public static int sum = -1;
  public static int sumNew;
  public static String num;
  public static DateTime dt;

  public static void main(String[] args) throws InterruptedException {
    while (true) {
      String returnStr = postData("http://192.168.0.212:8082/druid/v2",param);
      String reStrs[] = returnStr.split(":");
      if(reStrs.length < 2) continue;
      num = reStrs[reStrs.length-1].substring(0,reStrs[reStrs.length-1].length()-4);

      //新的数字
      sumNew = Integer.parseInt(num);

      dt = new DateTime();
      if(sumNew < sum){
        System.out.println("wrong!"+"old="+sum+" new="+sumNew+" time="+dt+  "   差值：" + (sumNew - sum) + " -------------------------------------------------------");
      }

      if(sumNew - sum > 0) {
        System.out.println("time:" + dt + "   num:" + num + "   差值：" + (sumNew - sum));
      } else {
//        System.out.print(". ");
      }
      sum = sumNew;
      Thread.sleep(1000);
    }
  }

}