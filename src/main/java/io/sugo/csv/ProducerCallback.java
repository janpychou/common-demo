package io.sugo.csv;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.atomic.AtomicInteger;

public class ProducerCallback implements Callback {
  private AtomicInteger failed;
  public ProducerCallback(AtomicInteger failed){
    this.failed = failed;
  }
  @Override
  public void onCompletion(RecordMetadata metadata, Exception e) {
    if (null != metadata) {
      // println("Sent message:" + count + "  sent to partition:" + metadata.partition() + ", offset:" + metadata.offset())
    } else {
      failed.incrementAndGet();
      e.printStackTrace();
    }
  }
}
