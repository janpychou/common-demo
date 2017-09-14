package io.sugo.metaq;


import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.client.MessageSessionFactory;
import com.taobao.metamorphosis.client.MetaClientConfig;
import com.taobao.metamorphosis.client.MetaMessageSessionFactory;
import com.taobao.metamorphosis.client.producer.MessageProducer;
import com.taobao.metamorphosis.client.producer.SendMessageCallback;
import com.taobao.metamorphosis.client.producer.SendResult;
import com.taobao.metamorphosis.utils.ZkUtils.ZKConfig;
import io.sugo.kafka.UserAction;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class Robot {


  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream("kafka.properties"));

    //    final String mytopic = props.get("mytopic").toString();
    final String mytopic = "data3";

    final int rowPerSecond = Integer.valueOf(props.getProperty("rowPerSecond", "100"));
    final String startDate = props.getProperty("startDate", "2017-09-01T").toString();
    final String endDate = props.getProperty("endDate", "2017-09-01T").toString();
    //        final KafkaProducer<Integer, String> producer = new KafkaProducer<>(props);

    final MetaClientConfig metaClientConfig = new MetaClientConfig();
    final ZKConfig zkConfig = new ZKConfig();
    //设置zookeeper地址
    zkConfig.zkConnect = "dev223.sugo.net:2181,dev225.sugo.net:2181,dev224.sugo.net:2181";
    zkConfig.zkRoot = "/metaq";
    metaClientConfig.setZkConfig(zkConfig);
    // New session factory,强烈建议使用单例
    MessageSessionFactory sessionFactory = new MetaMessageSessionFactory(metaClientConfig);
    // create producer,强烈建议使用单例
    MessageProducer producer = sessionFactory.createProducer();
    // publish topic
    producer.publish(mytopic);

    final Random rand = new Random();
    DateTime startTime = new DateTime(startDate);
    DateTime endTime = new DateTime(endDate).plusDays(1).withTimeAtStartOfDay();
    DateTime time = startTime;
    DateTime tmpTime = time;
    int cnt = 0;
    final AtomicInteger success = new AtomicInteger(0);
    final AtomicInteger failed = new AtomicInteger(0);
    long s1 = System.currentTimeMillis();
    while (time.isBefore(endTime)) {
      for (int i = 0; i < rowPerSecond; i++) {
        byte[] bytes = UserAction.getAction(tmpTime).getBytes();
        // send message
        producer.sendMessage(new Message(mytopic, bytes), new SendMessageCallback() {

          @Override public void onMessageSent(SendResult sendResult) {
            if (!sendResult.isSuccess()) {
              System.err.println("Send message failed,error message:" + sendResult.getErrorMessage());
              failed.incrementAndGet();
            } else {
              success.incrementAndGet();
            }
          }

          @Override public void onException(Throwable throwable) {
            System.err.println("Send message failed,error message:");
            throwable.printStackTrace();
          }
        });
        tmpTime = tmpTime.plusMillis(rand.nextInt(1000 / rowPerSecond));
      }
      time = time.plusSeconds(1);
      tmpTime = time;
      if (cnt++ % 10000 == 0) {
        System.out.println(time);
      }
    }
    long s2 = System.currentTimeMillis();
    producer.shutdown();
    System.out.println(String.format("count:%d, success:%d, failed:%d, spend time:%,d", cnt, success.get(), failed.get(), s2 - s1));
  }

}
