package io.sugo.kafka.producer;


import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gary on 16-9-30.
 */
public class KRobot {

  public static AtomicInteger totalCount;
  public static volatile boolean produceFinished = false;
  public static BlockingQueue<Future<RecordMetadata>> futureQueue = new ArrayBlockingQueue<>(100000);

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put("myTopic", "ten");

    props.load(new FileInputStream("kafka.properties"));
    System.out.println(props);

    int producerNum = Integer.valueOf(props.getProperty("producerNum", "9"));
    int futureNum = Integer.valueOf(props.getProperty("futureNum", "10"));
    int dimNum = Integer.valueOf(props.getProperty("dimNum", "10"));
    int totalCount = Integer.valueOf(props.getProperty("totalCount", "100000000"));

    AtomicInteger produceCnt = new AtomicInteger();
    AtomicInteger sendCnt = new AtomicInteger();

    CountDownLatch latch = new CountDownLatch(producerNum + futureNum);
    CountDownLatch syncLatch = new CountDownLatch(producerNum);

    AtomicLong[] produceTimes = new AtomicLong[producerNum];
    AtomicInteger[] produceNums = new AtomicInteger[producerNum];
    for (int i = 0; i < producerNum; i++) {
      produceTimes[i] = new AtomicLong();
      produceNums[i] = new AtomicInteger();
    }

    AtomicInteger[] futureNums = new AtomicInteger[futureNum];
    for (int i = 0; i < futureNum; i++) {
      futureNums[i] = new AtomicInteger();
    }

    long s1 = System.currentTimeMillis();

    for (int i = 0; i < futureNum; i++) {
      Thread consumer = new Thread(new FutureRunner(props, futureQueue, latch, sendCnt, syncLatch, futureNums[i]), "FutureRunner-" + i);
      consumer.start();
    }

    List<Thread> producers = new ArrayList<>(producerNum);
    int sPos = 1;
    int ePos = totalCount / producerNum;
    for (int i = 0; i < producerNum; i++) {
      Thread producer = new Thread(new KProducer(props, futureQueue, latch, syncLatch, produceCnt, sPos, ePos, produceTimes[i], produceNums[i], dimNum), "Producer-" + i);
      sPos = ePos + 1;
      ePos += totalCount / producerNum;
      if (i == producerNum - 2) {
        ePos = totalCount;
      }
      producers.add(producer);
      producer.start();
    }

    KMonitor monitor = new KMonitor(produceTimes, produceNums, futureNums);
    new Thread(monitor).start();

    latch.await();
    monitor.stop();

    long s2 = System.currentTimeMillis();
    long spend = s2 - s1;
    System.out.println(String.format("spendTime:[%d] produced:[%d] send:[%d]",
        spend, produceCnt.get(), sendCnt.get()));
  }
}
