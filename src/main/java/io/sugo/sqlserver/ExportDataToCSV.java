package io.sugo.sqlserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class ExportDataToCSV {
  private final static ObjectMapper jsonMapper = new ObjectMapper();
  final static String cfn = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  final static String url = "jdbc:sqlserver://192.168.0.45:1433;DatabaseName=deviceInfo";

  public static void main(String[] args) {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.0.220:9092,192.168.0.221:9092,192.168.0.222:9092");
    props.put("client.id", "DemoProducer1");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    final String mytopic = "fuli";
    final KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);
//    producer.send(new ProducerRecord<>(mytopic, ""));

    long s1 = System.currentTimeMillis();
    System.out.println(new DateTime());
    Connection con = null;
    int cnt = 0;
    PreparedStatement statement = null;
    ResultSet res = null;
    try {
      Class.forName(cfn);
      con = DriverManager.getConnection(url, "sa", "123456");

      String sql = "select \n" +
          "      detail.TDR_Guid,\n" +
          "      detail.TDR_Title,\n" +
          "      detail.TDR_MinVal,\n" +
          "      detail.TDR_MaxVal,\n" +
          "      detail.TDR_StandVal,\n" +
          "      detail.TDR_Val,\n" +  //（转为浮点数，遇到浮点数以外的字符，统一变为0.1111）
          "      detail.TDR_Unit,\n" +
          "      detail.TDR_ISProblem,\n" +
          "      detail.TDR_Deal_Result,\n" +
          "      detail.TDR_Deal_State,\n" +  //（转为字符串格式）
          "      detail.TDR_Deal_EmpName,\n" +
          "      detail.TDR_Deal_Time,\n" +   //（遇到空值，变为1900-01-01 00:00；若有秒数或毫秒数，舍去）
          "      detail.TDR_Org_Val,\n" +
          "      title.TTR_Tsk_Code,\n" +
          "      title.TTR_Area,\n" +
          "      title.TTR_Project, \n" +
          "      title.TTR_Ped_One,\n" +
          "      title.TTR_Ped_Two,\n" +
          "      title.TTR_Ped_Three,\n" +
          "      title.TTR_DeviceNo,\n" +
          "      title.TTR_DeviceNumber,\n" +
          "      title.TTR_StartTime,\n" +    //（遇到空值，变为1900-01-01 00:00；若有秒数或毫秒数，舍去）
          "      title.TTR_EndTime,\n" +      //（遇到空值，变为1900-01-01 00:00；若有秒数或毫秒数，舍去）
          "      title.TTR_EmpName,\n" +
          "      title.TTR_CompleteTime,\n" +   //（遇到空值，变为1900-01-01 00:00；若有秒数或毫秒数，舍去）
          "      title.TTR_Complete,\n" +
          "      title.TTR_IsProblem\n" +
          "    from dbo.S_Task_Detail_Report detail\n" +
          "    left join dbo.S_Task_Title_Report title\n" +
          "    on detail.TDR_TTR_Guid=title.TTR_Guid";

      statement = con.prepareStatement(sql);
      res = statement.executeQuery();
      Map<String, Object> map = new HashMap<>();
      DateTime now = new DateTime();
      Random rand = new Random();
      int dealTimeNull = 0;
      int startTimeNull = 0;
      int endTimeNull = 0;
      int completeTimeNull = 0;
      while (res.next()) {
        map.put("d|insert_time", now.plusSeconds(rand.nextInt(30000)).getMillis());
        if (res.getString("TDR_Guid") != null) map.put("s|TDR_Guid", res.getString("TDR_Guid"));
        if (res.getString("TDR_Title") != null) map.put("s|TDR_Title", res.getString("TDR_Title"));
        if (res.getString("TDR_MinVal") != null) map.put("s|TDR_MinVal", res.getString("TDR_MinVal"));
        if (res.getString("TDR_MaxVal") != null) map.put("s|TDR_MaxVal", res.getString("TDR_MaxVal"));
        if (res.getString("TDR_StandVal") != null) map.put("s|TDR_StandVal", res.getString("TDR_StandVal"));
        float TDR_Val;
        try {
          TDR_Val = Float.parseFloat(res.getString("TDR_Val"));
        } catch (Exception e){
          TDR_Val = 0.1111f;
        }
        map.put("f|TDR_Val", TDR_Val);
        if (res.getString("TDR_Unit") != null) map.put("s|TDR_Unit", res.getString("TDR_Unit"));
        if (res.getString("TDR_ISProblem") != null) map.put("i|TDR_ISProblem", res.getInt("TDR_ISProblem"));
        if (res.getString("TDR_Deal_Result") != null) map.put("s|TDR_Deal_Result", res.getString("TDR_Deal_Result"));
        if (res.getString("TDR_Deal_State") != null) map.put("s|TDR_Deal_State", res.getString("TDR_Deal_State"));
        if (res.getString("TDR_Deal_EmpName") != null) map.put("s|TDR_Deal_EmpName", res.getString("TDR_Deal_EmpName"));
        String dealTimeStr = res.getString("TDR_Deal_Time");
        if(dealTimeStr == null){
          dealTimeNull++;
        } else {
          Date date = res.getDate("TDR_Deal_Time");
          DateTime dt = new DateTime(date);
          map.put("d|TDR_Deal_Time", dt.getMillis());
        }
        float TDR_Org_Val;
        try {
          TDR_Org_Val = Float.parseFloat(res.getString("TDR_Val"));
          map.put("f|TDR_Org_Val", TDR_Org_Val);
        } catch (Exception e){
//          System.out.println(e.getMessage());
        }
//        if (res.getString("TDR_Org_Val") != null) map.put("s|TDR_Org_Val", res.getString("TDR_Org_Val"));
        if (res.getString("TTR_Tsk_Code") != null) map.put("s|TTR_Tsk_Code", res.getString("TTR_Tsk_Code"));
        if (res.getString("TTR_Area") != null) map.put("s|TTR_Area", res.getString("TTR_Area"));
        if (res.getString("TTR_Project") != null) map.put("s|TTR_Project", res.getString("TTR_Project"));
        if (res.getString("TTR_Ped_One") != null) map.put("s|TTR_Ped_One", res.getString("TTR_Ped_One"));
        if (res.getString("TTR_Ped_Two") != null) map.put("s|TTR_Ped_Two", res.getString("TTR_Ped_Two"));
        if (res.getString("TTR_Ped_Three") != null) map.put("s|TTR_Ped_Three", res.getString("TTR_Ped_Three"));
        if (res.getString("TTR_DeviceNo") != null) map.put("s|TTR_DeviceNo", res.getString("TTR_DeviceNo"));
        if (res.getString("TTR_DeviceNumber") != null) map.put("s|TTR_DeviceNumber", res.getString("TTR_DeviceNumber"));
        String startTimeStr = res.getString("TTR_StartTime");
        if(startTimeStr == null){
          startTimeNull++;
        } else {
          Date date = res.getDate("TTR_StartTime");
          DateTime dt = new DateTime(date);
          map.put("d|TTR_StartTime", dt.getMillis());
        }
        String endTimeStr = res.getString("TTR_EndTime");
        if(endTimeStr == null){
          endTimeNull++;
        } else {
          Date date = res.getDate("TTR_EndTime");
          DateTime dt = new DateTime(date);
          map.put("d|TTR_EndTime", dt.getMillis());
        }
        String completeTimeStr = res.getString("TTR_CompleteTime");
        if(completeTimeStr == null){
          completeTimeNull++;
        } else {
          Date date = res.getDate("TTR_CompleteTime");
          DateTime dt = new DateTime(date);
          map.put("d|TTR_CompleteTime", dt.getMillis());
        }
        if (res.getString("TTR_Complete") != null) map.put("i|TTR_Complete", res.getInt("TTR_Complete"));
        if (res.getString("TTR_IsProblem") != null) map.put("i|TTR_IsProblem", res.getInt("TTR_IsProblem"));

        if(rand.nextInt(100000) == 0) {
          System.out.println(jsonMapper.writeValueAsString(map));
        }


        //        System.out.println("姓名：" + title);
        producer.send(new ProducerRecord<>(mytopic, jsonMapper.writeValueAsString(map)));
        cnt++;
        map.clear();
        if(cnt % 100000 == 0) {
          System.out.println("row:" + cnt);
        }
      }
      System.out.println(String.format("deal:%d, start:%d, end:%d, complete:%d", dealTimeNull, startTimeNull, endTimeNull, completeTimeNull));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (res != null) {
          res.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (con != null) {
          con.close();
        }
      } catch (Exception e2) {
        // TODO: handle exception
        e2.printStackTrace();
      }
      producer.close();
    }
    long s2 = System.currentTimeMillis();
    System.out.println(new DateTime());
    System.out.println("spend time:" + (s2 - s1));
    System.out.println("row count:" + cnt);

  }
}
