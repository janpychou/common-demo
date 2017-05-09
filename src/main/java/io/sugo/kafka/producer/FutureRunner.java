package io.sugo.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by root on 16-12-5.
 */
public class FutureRunner implements Runnable {
  private Properties props;
  private BlockingQueue<Future<RecordMetadata>> msgQueue;
  private CountDownLatch latch;
  private AtomicInteger sendCnt;
  private CountDownLatch syncLatch;
  private AtomicInteger futureNum;

  public FutureRunner(Properties props, BlockingQueue<Future<RecordMetadata>> msgQueue, CountDownLatch latch,
      AtomicInteger sendCnt, CountDownLatch syncLatch, AtomicInteger futureNum) {
    this.props = props;
    this.msgQueue = msgQueue;
    this.latch = latch;
    this.sendCnt = sendCnt;
    this.syncLatch = syncLatch;
    this.futureNum = futureNum;
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    try {
      Future<RecordMetadata> future;
      RecordMetadata ret;
      while (!msgQueue.isEmpty() || syncLatch.getCount() > 0) {
        try {
          future = msgQueue.poll(3, TimeUnit.SECONDS);
          if(future != null) {
            ret = future.get();
            futureNum.incrementAndGet();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } finally {
      long end = System.currentTimeMillis();
      long spend = end - start;
      sendCnt.addAndGet(futureNum.get());
      System.out.println(Thread.currentThread().getName() + " futureNum:" + futureNum.get() + " spend time:" + spend);
      latch.countDown();
    }
  }

}
