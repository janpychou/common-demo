package io.sugo.text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;

import java.io.Closeable;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class ExceptionDataCreator implements Closeable {
  private static final ObjectMapper jsonMapper = new ObjectMapper();
  private static final String mytopic = "exception2";

  private final KafkaProducer<Integer, String> producer;
  private final Random random = new Random();
  private final DateTime now = new DateTime();
  private MessageDigest md;

  public ExceptionDataCreator() {
    Properties props = new Properties();
    props.put("bootstrap.servers", "192.168.0.220:9092,192.168.0.221:9092,192.168.0.222:9092");
    props.put("client.id", "DemoProducer1");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<>(props);
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    ExceptionDataCreator creator = new ExceptionDataCreator();
    try {
      int sended = 0;
      Random rand = new Random();
      long s1 = System.currentTimeMillis();
      for (int i = 0; i < 100000; i++) {
        //      for (int i = 0; i < 10; i++) {
        String msg = creator.generateData(rand.nextInt(1000));
        if(i % 1000 == 0) {
          System.out.println(msg);
        }
        creator.send(msg);
        sended++;
      }
      long s2 = System.currentTimeMillis();
      System.out.println(String.format("sended:%d, spendTime:%d (s), time:%s", sended, (s2 - s1) / 1000, new DateTime()));
    } catch (Exception e) {
    } finally {
      creator.close();
    }
  }

  public void send(String msg) {
    producer.send(new ProducerRecord<>(mytopic, msg));
  }

  @Override public void close() throws IOException {
    producer.close();
  }

  private String getMd5Sum(String pos) {
    StringBuffer sb = new StringBuffer();
    md.reset();
    md.update(pos.getBytes());
    byte[] digest = md.digest();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }

  private String generateData(int num) throws JsonProcessingException {
    Map<String, Object> map = new HashMap<>();
    map.put("d|create_time", now.getMillis());
    map.put("i|num", num);
    map.put("s|userId", getMd5Sum(num + ""));
    map.put("i|age", random.nextInt(50) + 10);
    map.put("f|score", String.format("%.2f", random.nextFloat() * 100));
    map.put("l|duration", random.nextInt(100000));
    map.put("exception", EXCEPTION_STRING);

    return jsonMapper.writeValueAsString(map);
  }

  private static final String EXCEPTION_STRING = "2017-06-13 07:20:53.128 WARN [qtp1413020227-52][HttpChannel.java:395] - /druid/v2?pretty\n" +
      "java.io.IOException: java.util.concurrent.TimeoutException: Idle timeout expired: 300000/300000 ms\n" +
      "\tat org.eclipse.jetty.util.SharedBlockingCallback$Blocker.block(SharedBlockingCallback.java:234) ~[jetty-util-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.HttpOutput.write(HttpOutput.java:133) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.HttpOutput.write(HttpOutput.java:347) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.gzip.DeflatedOutputStream.deflate(DeflatedOutputStream.java:74) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.gzip.DeflatedOutputStream.write(DeflatedOutputStream.java:64) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.gzip.GzipOutputStream.write(GzipOutputStream.java:46) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.gzip.AbstractCompressedStream.write(AbstractCompressedStream.java:226) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat com.sun.jersey.spi.container.servlet.WebComponent$Writer.write(WebComponent.java:300) ~[jersey-servlet-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.spi.container.ContainerResponse$CommittingOutputStream.write(ContainerResponse.java:135) ~[jersey-server-1.19.jar:1.19]\n" +
      "\tat com.google.common.io.CountingOutputStream.write(CountingOutputStream.java:53) ~[guava-16.0.1.jar:?]\n" +
      "\tat com.fasterxml.jackson.core.json.UTF8JsonGenerator._flushBuffer(UTF8JsonGenerator.java:1855) ~[jackson-core-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.core.json.UTF8JsonGenerator.writeNumber(UTF8JsonGenerator.java:825) ~[jackson-core-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.std.NumberSerializers$LongSerializer.serialize(NumberSerializers.java:179) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.std.NumberSerializers$LongSerializer.serialize(NumberSerializers.java:170) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:100) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:21) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase.serialize(AsArraySerializerBase.java:183) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:100) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer.serializeContents(IndexedListSerializer.java:21) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase.serialize(AsArraySerializerBase.java:183) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:505) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:639) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:152) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:128) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ObjectMapper.writeValue(ObjectMapper.java:1902) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.core.base.GeneratorBase.writeObject(GeneratorBase.java:280) ~[jackson-core-2.4.6.jar:2.4.6]\n" +
      "\tat io.druid.jackson.DruidDefaultSerializersModule$5.serialize(DruidDefaultSerializersModule.java:139) ~[druid-processing-1.0.0.jar:1.0.0]\n" +
      "\tat io.druid.jackson.DruidDefaultSerializersModule$5.serialize(DruidDefaultSerializersModule.java:130) ~[druid-processing-1.0.0.jar:1.0.0]\n" +
      "\tat com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:128) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ObjectWriter._configAndWriteValue(ObjectWriter.java:800) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat com.fasterxml.jackson.databind.ObjectWriter.writeValue(ObjectWriter.java:642) ~[jackson-databind-2.4.6.jar:2.4.6]\n" +
      "\tat io.druid.server.QueryResource$2.write(QueryResource.java:254) ~[druid-server-1.0.0.jar:1.0.0]\n" +
      "\tat com.sun.jersey.core.impl.provider.entity.StreamingOutputProvider.writeTo(StreamingOutputProvider.java:71) ~[jersey-core-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.core.impl.provider.entity.StreamingOutputProvider.writeTo(StreamingOutputProvider.java:57) ~[jersey-core-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.spi.container.ContainerResponse.write(ContainerResponse.java:302) ~[jersey-server-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.server.impl.application.WebApplicationImpl._handleRequest(WebApplicationImpl.java:1510) ~[jersey-server-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1419) ~[jersey-server-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.server.impl.application.WebApplicationImpl.handleRequest(WebApplicationImpl.java:1409) ~[jersey-server-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.spi.container.servlet.WebComponent.service(WebComponent.java:409) ~[jersey-servlet-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:558) ~[jersey-servlet-1.19.jar:1.19]\n" +
      "\tat com.sun.jersey.spi.container.servlet.ServletContainer.service(ServletContainer.java:733) ~[jersey-servlet-1.19.jar:1.19]\n" +
      "\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:790) ~[javax.servlet-api-3.1.0.jar:3.1.0]\n" +
      "\tat com.google.inject.servlet.ServletDefinition.doServiceImpl(ServletDefinition.java:286) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat com.google.inject.servlet.ServletDefinition.doService(ServletDefinition.java:276) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat com.google.inject.servlet.ServletDefinition.service(ServletDefinition.java:181) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat com.google.inject.servlet.ManagedServletPipeline.service(ManagedServletPipeline.java:91) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat com.google.inject.servlet.ManagedFilterPipeline.dispatch(ManagedFilterPipeline.java:120) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat com.google.inject.servlet.GuiceFilter.doFilter(GuiceFilter.java:135) ~[guice-servlet-4.1.0.jar:?]\n" +
      "\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1652) ~[jetty-servlet-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.UserAgentFilter.doFilter(UserAgentFilter.java:83) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlets.GzipFilter.doFilter(GzipFilter.java:364) ~[jetty-servlets-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1652) ~[jetty-servlet-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:585) ~[jetty-servlet-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:221) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1125) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:515) ~[jetty-servlet-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:185) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1059) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.handler.HandlerList.handle(HandlerList.java:52) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:97) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.Server.handle(Server.java:497) ~[jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:310) [jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:248) [jetty-server-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:540) [jetty-io-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:620) [jetty-util-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:540) [jetty-util-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat java.lang.Thread.run(Thread.java:745) [?:1.8.0_91]\n" +
      "Caused by: java.util.concurrent.TimeoutException: Idle timeout expired: 300000/300000 ms\n" +
      "\tat org.eclipse.jetty.io.IdleTimeout.checkIdleTimeout(IdleTimeout.java:156) ~[jetty-io-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat org.eclipse.jetty.io.IdleTimeout$1.run(IdleTimeout.java:50) ~[jetty-io-9.2.5.v20141112.jar:9.2.5.v20141112]\n" +
      "\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[?:1.8.0_91]\n" +
      "\tat java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[?:1.8.0_91]\n" +
      "\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180) ~[?:1.8.0_91]\n" +
      "\tat java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293) ~[?:1.8.0_91]\n" +
      "\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142) ~[?:1.8.0_91]\n" +
      "\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617) ~[?:1.8.0_91]\n" +
      "\t... 1 more\n";

}
