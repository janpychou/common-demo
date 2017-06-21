package io.sugo.sqlserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class ExportSqlServerDataToTindex {
  private final static ObjectMapper jsonMapper = new ObjectMapper();
  final static String cfn = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  final static String url = "jdbc:sqlserver://192.168.0.45:1433;DatabaseName=deviceInfo";

  public static void main(String[] args) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter millisformatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    DateTime date = DateTime.parse("2016-03-01 00:00:00", formatter);
    System.out.println(date);
    DateTime dt1 = new DateTime("2017-05-01T02:02:05+08:00");
    System.out.println(String.format("%02d", dt1.getHourOfDay()));
    if(dt1.getMillis() > 0){
//      return;
    }

    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.0.220:9092,192.168.0.221:9092,192.168.0.222:9092");
    props.put("client.id", "DemoProducer1");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    final String mytopic = "fuli0526";
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
          "      title.TTR_Ped_Name,\n" +
          "      title.TTR_EmpName,\n" +
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
      System.out.println("fetching");
      res = statement.executeQuery();
      System.out.println("fetched");
      Map<String, Object> map = new HashMap<>();
      DateTime now = new DateTime("2017-05-26T");
      Random rand = new Random();
      int randTime = 60 * 60 * 24 * 6;
      int dealTimeNull = 0;
      int startTimeNull = 0;
      int endTimeNull = 0;
      int completeTimeNull = 0;
      System.out.println("getFetchSize:" + res.getFetchSize());
      while (res.next()) {
        map.put("d|insert_time", now.plusSeconds(rand.nextInt(randTime)).getMillis());
        if (res.getString("TDR_Guid") != null) map.put("s|TDR_Guid", res.getString("TDR_Guid"));
        if (res.getString("TDR_Title") != null) map.put("s|TDR_Title", res.getString("TDR_Title"));
        float TDR_MinVal = 0f;
        boolean hasMinVal = false;
        try {
          TDR_MinVal = Float.parseFloat(res.getString("TDR_MinVal"));
          map.put("f|TDR_MinVal", TDR_MinVal);
          hasMinVal = true;
        } catch (Exception e){
        }
        float TDR_MaxVal = 0f;
        boolean hasMaxVal = false;
        try {
          TDR_MaxVal = Float.parseFloat(res.getString("TDR_MaxVal"));
          map.put("f|TDR_MaxVal", TDR_MaxVal);
          hasMaxVal = true;
        } catch (Exception e){
        }

        float TDR_StandVal;
        try {
          TDR_StandVal = Float.parseFloat(res.getString("TDR_StandVal"));
          map.put("f|TDR_StandVal", TDR_StandVal);
        } catch (Exception e){
        }
        float TDR_Val;
        try {
          TDR_Val = Float.parseFloat(res.getString("TDR_Val"));
          map.put("f|TDR_Val", TDR_Val);
        } catch (Exception e){
        }

        if (res.getString("TDR_Unit") != null) map.put("s|TDR_Unit", res.getString("TDR_Unit"));
        if (res.getString("TDR_ISProblem") != null) map.put("s|TDR_ISProblem", res.getInt("TDR_ISProblem"));
        if (res.getString("TDR_Deal_Result") != null) map.put("s|TDR_Deal_Result", res.getString("TDR_Deal_Result"));
        if (res.getString("TDR_Deal_State") != null) map.put("s|TDR_Deal_State", res.getString("TDR_Deal_State"));
        if (res.getString("TDR_Deal_EmpName") != null) map.put("s|TDR_Deal_EmpName", res.getString("TDR_Deal_EmpName"));
        String dealTimeStr = res.getString("TDR_Deal_Time");
        DateTime dealTime = null;
        if(dealTimeStr == null){
          dealTimeNull++;
        } else {
          dealTime = DateTime.parse(res.getString("TDR_Deal_Time"), millisformatter);
          map.put("d|TDR_Deal_Time", dealTime.getMillis());
        }
        float TDR_Org_Val = 0f;
        boolean hasOrgVal = false;
        try {
          TDR_Org_Val = Float.parseFloat(res.getString("TDR_Val"));
          map.put("f|TDR_Org_Val", TDR_Org_Val);
          hasOrgVal = true;
        } catch (Exception e){
        }
        //        Offset_Val	float	巡查项向上偏离值	创建新字段，逻辑是存储(TDR_Org_Val)-(TDR_MaxVal)的值，当数值为负数时，插入NULL
        //        DownOffset_Val	float	巡查项向下偏离值	创建新字段，逻辑是存储(TDR_Org_Val)-(TDR_MinVal)的值，当数值为正数时，插入NULL
        if(hasOrgVal && hasMaxVal && TDR_Org_Val - TDR_MaxVal > 0) {
          map.put("f|Offset_Val", TDR_Org_Val - TDR_MaxVal);
//          System.out.println("Offset_Val" + (TDR_Org_Val - TDR_MaxVal));
        }
        if(hasOrgVal && hasMinVal && TDR_Org_Val - TDR_MinVal < 0) {
          map.put("f|DownOffset_Val", TDR_Org_Val - TDR_MinVal);
//          System.out.println("DownOffset_Val" + (TDR_Org_Val - TDR_MinVal));
        }
        if (res.getString("TTR_Tsk_Code") != null) map.put("s|TTR_Tsk_Code", res.getString("TTR_Tsk_Code"));
        if (res.getString("TTR_Area") != null) map.put("s|TTR_Area", res.getString("TTR_Area"));
        if (res.getString("TTR_Project") != null) map.put("s|TTR_Project", res.getString("TTR_Project"));
        if (res.getString("TTR_Ped_One") != null) map.put("s|TTR_Ped_One", res.getString("TTR_Ped_One"));
        if (res.getString("TTR_Ped_Two") != null) map.put("s|TTR_Ped_Two", res.getString("TTR_Ped_Two"));
        if (res.getString("TTR_Ped_Three") != null) map.put("s|TTR_Ped_Three", res.getString("TTR_Ped_Three"));
        if (res.getString("TTR_DeviceNo") != null) map.put("s|TTR_DeviceNo", res.getString("TTR_DeviceNo"));
        if (res.getString("TTR_Ped_Name") != null) map.put("s|TTR_Ped_Name", res.getString("TTR_Ped_Name"));
        if (res.getString("TTR_EmpName") != null) map.put("s|TTR_EmpName", res.getString("TTR_EmpName"));
        if (res.getString("TTR_DeviceNumber") != null) map.put("s|TTR_DeviceNumber", res.getString("TTR_DeviceNumber"));
        String startTimeStr = res.getString("TTR_StartTime");
        if(startTimeStr == null){
          startTimeNull++;
        } else {
          DateTime dt = DateTime.parse(res.getString("TTR_StartTime"), formatter);
          map.put("d|TTR_StartTime", dt.getMillis());
        }
        String endTimeStr = res.getString("TTR_EndTime");
        if(endTimeStr == null){
          endTimeNull++;
        } else {
          DateTime dt = DateTime.parse(res.getString("TTR_EndTime"), formatter);
          map.put("d|TTR_EndTime", dt.getMillis());
        }
        String completeTimeStr = res.getString("TTR_CompleteTime");
        DateTime completeTime = null;
        if(completeTimeStr == null){
          completeTimeNull++;
        } else {
          completeTime = DateTime.parse(res.getString("TTR_CompleteTime"), millisformatter);
          map.put("d|TTR_CompleteTime", completeTime.getMillis());
          map.put("s|TTR_CompleteTime_Interval", String.format("%02d", completeTime.getHourOfDay()));
        }
        if(dealTime != null && completeTime != null) {
          long duration = (dealTime.getMillis() - completeTime.getMillis())/ 1000 / 60;
          map.put("i|TDR_Deal_Duration", duration);
//          System.out.println(completeTime + "-" + dealTime + "=" + duration);
        }
        if (res.getString("TTR_Complete") != null) map.put("s|TTR_Complete", res.getString("TTR_Complete"));
        if (res.getString("TTR_IsProblem") != null) map.put("s|TTR_IsProblem", res.getString("TTR_IsProblem"));

        if(rand.nextInt(10000) == 0) {
          System.out.println(jsonMapper.writeValueAsString(map));
        }

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
    System.out.println("spend time:" + ((s2 - s1) % 1000) + "row count:" + cnt);

  }
}
