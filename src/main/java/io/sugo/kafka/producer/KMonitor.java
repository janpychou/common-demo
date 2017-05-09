package io.sugo.kafka.producer;

import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by root on 16-12-5.
 */
public class KMonitor implements Runnable {
  private volatile boolean stop = false;
  private long cnt = 0;
  private AtomicLong[] produceTimes;
  private AtomicInteger[] produceNums;
  private AtomicInteger[] futureNums;

  public KMonitor(
      AtomicLong[] produceTimes, AtomicInteger[] produceNums, AtomicInteger[] futureNums) {
    this.produceTimes = produceTimes;
    this.produceNums = produceNums;
    this.futureNums = futureNums;
  }

  @Override
  public void run() {
    int idx = 1;
    long count = 0;
    long lastCnt = 0;
    int produced = 0;
    int fCnt = 0;
    int lastFCnt = 0;
    while (!stop) {
      try {
        Thread.sleep(1000);
        if (cnt++ % 20 == 0) {
          System.out.println();
          for (int i = 0; i < produceTimes.length; i++) {
            produced = produceNums[i].get();
            count += produced;
            System.out.print(new DateTime(produceTimes[i].get()) + " : " + produced + "\t");
          }
          System.out.println();
          for (int i = 0; i < futureNums.length; i++) {
            fCnt += futureNums[i].get();
          }
          System.out.println(
              String.format("%d futureQueue:%d produced:%d - %d futureNums: %d - %d",
                  idx++, KRobot.futureQueue.size(), count, count - lastCnt, fCnt, fCnt - lastFCnt));
          lastCnt = count;
          count = 0;
          lastFCnt = fCnt;
          fCnt = 0;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void stop() {
    stop = true;
  }
}
