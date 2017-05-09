package io.sugo.csv;

import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 16-12-5.
 */
public class FutureRunner implements Runnable {
  private BlockingQueue<Future<RecordMetadata>> msgQueue;
  private volatile boolean running = true;

  public FutureRunner(BlockingQueue<Future<RecordMetadata>> msgQueue) {
    this.msgQueue = msgQueue;
  }

  public void shutdown() {
    running = false;
  }

  @Override
  public void run() {
    try {
      Future<RecordMetadata> future;
      RecordMetadata ret;
      int count = 0;
      while (true) {
        try {
          future = msgQueue.poll(3, TimeUnit.SECONDS);
          if (future != null) {
            ret = future.get();
            count++;
          }
          if (!running) {
            System.out.println("get futrue count:" + count);
            SmartBI.latch.countDown();
            break;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.getStackTrace();
    }
  }

}
