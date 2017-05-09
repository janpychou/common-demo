package io.sugo.csv;

import com.csvreader.CsvReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartBI implements Closeable {
  //product_id,time_id,customer_id,promotion_id,store_id,store_sales,store_cost,unit_sales,warehouse_id,
  // stores_id,warehouse_country,warehouse_state_province,warehouse_city,warehouse_owner_name,product_name,
  // product_family,product_department,product_category,product_subcategory,the_year,quarter,the_month,the_day,
  // the_date,country,state_province,city,fullname

  private KafkaProducer<Integer, String> producer;
  private CsvReader data;
  private CsvReader bigData;
  private int rowCount = 0;
  private final String[] headers;
  private String topic = "smartbi1kw";
  private int sended = 0;
  public static BlockingQueue<Future<RecordMetadata>> futureQueue = new ArrayBlockingQueue<>(100000);
  private FutureRunner runner;
  private Thread consumer;

  public SmartBI() throws Exception {
    Properties props = new Properties();
    props.put("consumerNum", "8");
    props.load(new FileInputStream("kafka.properties"));
    //1118900
    data = new CsvReader("/work/win7/smartBI/100w/data_gbk.csv", ',', Charset.forName("gbk"));
    //11382000
    bigData = new CsvReader("/work/win7/smartBI/data1000w/data.csv", ',', Charset.forName("utf-8"));
    //  12500900 12448843   12448115
    producer = new KafkaProducer<>(props);
    data.readHeaders();
    headers = data.getHeaders();
    runner = new FutureRunner(futureQueue);
    consumer = new Thread(runner, "FutureRunner");
  }

  @Override public void close() throws IOException {
    data.close();
    bigData.close();
    producer.close();
    runner.shutdown();
    System.out.println("sended:" + sended);
  }

  public void work() throws Exception {

    consumer.start();
    rowCount = 0;
//    while (data.readRecord()) {
//      if (rowCount == 0) {
//        System.out.println(data.getRawRecord());
//      }
//      rowCount++;
//      dealRecord(headers, data);
//    }
//    System.out.println("rowCount:" + rowCount);
    rowCount = 0;
    bigData.setHeaders(headers);
    while (bigData.readRecord()) {
      rowCount++;
      dealRecord(headers, bigData);
    }

    System.out.println("rowCount:" + rowCount);
    System.out.println("failed:" + rowCount);
  }

  private void dealRecord(String[] headers, CsvReader data) throws IOException, ExecutionException, InterruptedException {
    String value = "";
    String header = "";
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < headers.length; i++) {
      try {
        header = headers[i];
        value = data.get(headers[i]);
        if ("the_date".equals(header)) {
          DateTime dt = new DateTime(value);
          builder.append(header).append("=").append(dt.getMillis()).append("&");
        } else {
          builder.append(header).append("=").append(value).append("&");
        }
      } catch (Exception e) {
        System.out.println(headers[i] + "--" + value);
        System.out.println(builder.toString());
        System.out.println(e.getMessage());
      }
    }
    String action = builder.substring(0, builder.length() - 1);
    //    Future<RecordMetadata> ret = producer.send(new ProducerRecord<>(topic, action), new ProducerCallback());
    Future<RecordMetadata> ret = producer.send(new ProducerRecord<>(topic, action), new ProducerCallback(failed));
    //    ret.get();
    futureQueue.put(ret);
    //    ret.get();
    if (sended % 500000 == 0) {
      System.out.println("send:" + sended + " time:" + new DateTime());
    }
    sended++;

    //12447115
    //12445413
    //12500900
  }

  private AtomicInteger failed = new AtomicInteger();

  public static CountDownLatch latch = new CountDownLatch(1);

  public static void main(String[] args) throws Exception {
    SmartBI bi = new SmartBI();
    bi.work();
    bi.close();
    latch.await();
  }


}
