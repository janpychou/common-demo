package io.sugo.http.resource;

import io.sugo.http.SupervisorManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/supervisor")
public class SupervisorResource {
  @GET
  @Path("/hello/{name}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  public String hello(@PathParam("name") String name) throws Exception {
    return "hello wolrd! "+name;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response specGetAll()
  {
    String str = "[\"\",\"com_HyoaKhQMl_project_rJoyANEAe\",\"com_HyoaKhQMl_project_rJIXzFOpe\"]";
    return Response.ok(str).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response specPost(final String spec)
  {
    return Response.ok(spec).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response specGet(@PathParam("id") final String id)
  {
    String str = "{\"type\":\"default_supervisor\",\"dataSchema\":{\"dataSource\":\"com_HyoaKhQMl_project_rJIXzFOpe\",\"parser\":{\"parseSpec\":{\"format\":\"json\",\"dimensionsSpec\":{\"dynamicDimension\":true,\"dimensions\":[]},\"timestampSpec\":{\"column\":\"d|sugo_timestamp\",\"format\":\"millis\"}},\"type\":\"standard\"},\"metricsSpec\":[],\"granularitySpec\":{\"type\":\"uniform\",\"segmentGranularity\":\"HOUR\",\"queryGranularity\":{\"type\":\"none\"},\"intervals\":null}},\"tuningConfig\":{\"type\":\"kafka\",\"maxRowsInMemory\":10000000,\"maxRowsPerSegment\":20000000,\"intermediatePersistPeriod\":\"PT10M\",\"basePersistDirectory\":\"/home/druid/tmp\",\"maxPendingPersists\":0,\"indexSpec\":{\"bitmap\":{\"type\":\"concise\"},\"dimensionCompression\":null,\"metricCompression\":null},\"buildV9Directly\":true,\"reportParseExceptions\":false,\"handoffConditionTimeout\":0,\"workerThreads\":null,\"chatThreads\":null,\"chatRetries\":8,\"httpTimeout\":\"PT10S\",\"shutdownTimeout\":\"PT80S\",\"maxWarmCount\":1,\"consumerThreadCount\":2},\"ioConfig\":{\"topic\":\"com_HyoaKhQMl_project_rJIXzFOpe\",\"replicas\":1,\"taskCount\":1,\"taskDuration\":\"PT600S\",\"consumerProperties\":{\"bootstrap.servers\":\"192.168.0.214:9092,192.168.0.216:9092,192.168.0.213:9092\"},\"startDelay\":\"PT5S\",\"period\":\"PT30S\",\"useEarliestOffset\":false,\"completionTimeout\":\"PT1800S\",\"lateMessageRejectionPeriod\":null},\"ipLibSpec\":{\"type\":\"none\"},\"luceneWriterConfig\":{\"maxBufferedDocs\":-1,\"ramBufferSizeMB\":16.0,\"indexRefreshIntervalSeconds\":3,\"isIndexMerge\":false,\"maxMergeAtOnce\":5,\"maxMergedSegmentMB\":256,\"maxMergesThreads\":1,\"writeThreads\":5,\"limiterMBPerSec\":0.0}}";
    return Response.ok(str).build();
  }

  @GET
  @Path("/{id}/status")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response specGetStatus(@PathParam("id") final String id)
  {
    String str = "{\"id\":\"com_HyoaKhQMl_project_rJIXzFOpe\",\"generationTime\":\"2017-04-20T10:40:23.857Z\",\"payload\":{\"dataSource\":\"com_HyoaKhQMl_project_rJIXzFOpe\",\"topic\":\"com_HyoaKhQMl_project_rJIXzFOpe\",\"partitions\":2,\"replicas\":1,\"durationSeconds\":600,\"activeTasks\":[{\"id\":\"batch_lucene_index_kafka_com_HyoaKhQMl_project_rJIXzFOpe_95cd1d2afad5380_nihfpfah\",\"startingOffsets\":{\"0\":224,\"1\":235},\"startTime\":\"2017-04-20T10:30:31.110Z\",\"remainingSeconds\":7,\"type\":\"ACTIVE\",\"currentOffsets\":{\"0\":224,\"1\":235}}],\"publishingTasks\":[]}}";
    return Response.ok(str).build();
  }

  @POST
  @Path("/{id}/shutdown")
//  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response shutdown(@PathParam("id") final String id)
  {
    Map<String, String> map = new HashMap<>();
    map.put("id", id);
    return Response.ok(map).build();
  }

  @GET
  @Path("/history")
  @Produces(MediaType.APPLICATION_JSON)
  public Response specGetAllHistory()
  {
    StringBuilder builder = SupervisorHistory.getHistory();
    return Response.ok(builder.toString()).build();
  }

  @GET
  @Path("/{id}/history")
  @Produces(MediaType.APPLICATION_JSON)
  public Response specGetHistory(@PathParam("id") final String id)
  {
    StringBuilder builder = new StringBuilder("[{\"spec\":{\"type\":\"NoopSupervisorSpec\"},\"version\":\"2017-05-06T03:33:31.948Z\"},{\"spec\":{\"type\":\"lucene_supervisor\",\"dataSchema\":{\"dataSource\":\"wuxianjiRT\",\"parser\":{\"type\":\"string\",\"parseSpec\":{\"format\":\"url\",\"timestampSpec\":{\"column\":\"EventDateTime\",\"format\":\"millis\"},\"dimensionsSpec\":{\"dimensions\":[{\"name\":\"SystemVersion\",\"type\":\"string\"},{\"name\":\"EventLabel\",\"type\":\"string\"},{\"name\":\"Network\",\"type\":\"string\"},{\"name\":\"IP\",\"type\":\"string\"},{\"name\":\"ClientDeviceID\",\"type\":\"string\"},{\"name\":\"Extras\",\"type\":\"string\"},{\"name\":\"ClientDeviceModel\",\"type\":\"string\"},{\"name\":\"EventHour\",\"type\":\"string\"},{\"name\":\"EventValue\",\"type\":\"string\"},{\"name\":\"ClientDeviceAgent\",\"type\":\"string\"},{\"name\":\"SessionID\",\"type\":\"string\"},{\"name\":\"ClientDeviceVersion\",\"type\":\"string\"},{\"name\":\"Referrer\",\"type\":\"string\"},{\"name\":\"Source\",\"type\":\"string\"},{\"name\":\"Operator\",\"type\":\"string\"},{\"name\":\"EventAction\",\"type\":\"string\"},{\"name\":\"EventDate\",\"type\":\"string\"},{\"name\":\"Nation\",\"type\":\"string\"},{\"name\":\"City\",\"type\":\"string\"},{\"name\":\"Media\",\"type\":\"string\"},{\"name\":\"EventScreen\",\"type\":\"string\"},{\"name\":\"__time\",\"type\":\"date\"},{\"name\":\"Campaign\",\"type\":\"string\"},{\"name\":\"Creative\",\"type\":\"string\"},{\"name\":\"OsScreen\",\"type\":\"string\"},{\"name\":\"Province\",\"type\":\"string\"},{\"name\":\"SystemName\",\"type\":\"string\"},{\"name\":\"ClientDeviceBrand\",\"type\":\"string\"},{\"name\":\"UserID\",\"type\":\"string\"},{\"name\":\"EventDateTime\",\"type\":\"string\"}],\"spatialDimensions\":[],\"dimensionExclusions\":[]}}},\"metricsSpec\":[],\"granularitySpec\":{\"type\":\"uniform\",\"segmentGranularity\":\"DAY\",\"queryGranularity\":{\"type\":\"none\"},\"intervals\":null}},\"tuningConfig\":{\"type\":\"kafka\",\"maxRowsInMemory\":500000,\"maxRowsPerSegment\":20000000,\"intermediatePersistPeriod\":\"PT10M\",\"basePersistDirectory\":\"/data1/druid/storage/wuxianjiRT\",\"maxPendingPersists\":0,\"indexSpec\":{\"bitmap\":{\"type\":\"concise\"},\"dimensionCompression\":null,\"metricCompression\":null},\"buildV9Directly\":true,\"reportParseExceptions\":false,\"handoffConditionTimeout\":0,\"workerThreads\":null,\"chatThreads\":null,\"chatRetries\":8,\"httpTimeout\":\"PT10S\",\"shutdownTimeout\":\"PT80S\",\"maxWarmCount\":1,\"consumerThreadCount\":2},\"ioConfig\":{\"topic\":\"wuxianjiRT\",\"replicas\":1,\"taskCount\":1,\"taskDuration\":\"PT86400S\",\"consumerProperties\":{\"bootstrap.servers\":\"192.168.0.213:9092,192.168.0.214:9092,192.168.0.216:9092\"},\"startDelay\":\"PT5S\",\"period\":\"PT30S\",\"useEarliestOffset\":true,\"completionTimeout\":\"PT1800S\",\"lateMessageRejectionPeriod\":null},\"luceneWriterConfig\":null},\"version\":\"2017-05-06T02:07:08.182Z\"},{\"spec\":{\"type\":\"NoopSupervisorSpec\"},\"version\":\"2017-05-06T01:45:53.903Z\"},{\"spec\":{\"type\":\"lucene_supervisor\",\"dataSchema\":{\"dataSource\":\"wuxianjiRT\",\"parser\":{\"type\":\"string\",\"parseSpec\":{\"format\":\"url\",\"timestampSpec\":{\"column\":\"EventDateTime\",\"format\":\"millis\"},\"dimensionsSpec\":{\"dimensions\":[{\"name\":\"SystemVersion\",\"type\":\"string\"},{\"name\":\"EventLabel\",\"type\":\"string\"},{\"name\":\"Network\",\"type\":\"string\"},{\"name\":\"IP\",\"type\":\"string\"},{\"name\":\"ClientDeviceID\",\"type\":\"string\"},{\"name\":\"Extras\",\"type\":\"string\"},{\"name\":\"ClientDeviceModel\",\"type\":\"string\"},{\"name\":\"ClientDeviceAgent\",\"type\":\"string\"},{\"name\":\"EventHour\",\"type\":\"string\"},{\"name\":\"EventValue\",\"type\":\"string\"},{\"name\":\"SessionID\",\"type\":\"string\"},{\"name\":\"ClientDeviceVersion\",\"type\":\"string\"},{\"name\":\"Operator\",\"type\":\"string\"},{\"name\":\"EventAction\",\"type\":\"string\"},{\"name\":\"Referrer\",\"type\":\"string\"},{\"name\":\"Source\",\"type\":\"string\"},{\"name\":\"Nation\",\"type\":\"string\"},{\"name\":\"EventDate\",\"type\":\"string\"},{\"name\":\"City\",\"type\":\"string\"},{\"name\":\"Media\",\"type\":\"string\"},{\"name\":\"EventScreen\",\"type\":\"string\"},{\"name\":\"__time\",\"type\":\"date\"},{\"name\":\"OsScreen\",\"type\":\"string\"},{\"name\":\"Campaign\",\"type\":\"string\"},{\"name\":\"Creative\",\"type\":\"string\"},{\"name\":\"Province\",\"type\":\"string\"},{\"name\":\"SystemName\",\"type\":\"string\"},{\"name\":\"ClientDeviceBrand\",\"type\":\"string\"},{\"name\":\"UserID\",\"type\":\"string\"},{\"name\":\"EventDateTime\",\"type\":\"string\"}],\"spatialDimensions\":[],\"dimensionExclusions\":[]}}},\"metricsSpec\":[],\"granularitySpec\":{\"type\":\"uniform\",\"segmentGranularity\":\"DAY\",\"queryGranularity\":{\"type\":\"none\"},\"intervals\":null}},\"tuningConfig\":{\"type\":\"kafka\",\"maxRowsInMemory\":500000,\"maxRowsPerSegment\":20000000,\"intermediatePersistPeriod\":\"PT10M\",\"basePersistDirectory\":\"/data1/druid/storage/wuxianjiRT\",\"maxPendingPersists\":0,\"indexSpec\":{\"bitmap\":{\"type\":\"concise\"},\"dimensionCompression\":null,\"metricCompression\":null},\"buildV9Directly\":true,\"reportParseExceptions\":false,\"handoffConditionTimeout\":0,\"workerThreads\":null,\"chatThreads\":null,\"chatRetries\":8,\"httpTimeout\":\"PT10S\",\"shutdownTimeout\":\"PT80S\",\"maxWarmCount\":1,\"consumerThreadCount\":2},\"ioConfig\":{\"topic\":\"wuxianjiRT\",\"replicas\":1,\"taskCount\":1,\"taskDuration\":\"PT86400S\",\"consumerProperties\":{\"bootstrap.servers\":\"192.168.0.213:9092,192.168.0.214:9092,192.168.0.216:9092\"},\"startDelay\":\"PT5S\",\"period\":\"PT30S\",\"useEarliestOffset\":true,\"completionTimeout\":\"PT1800S\",\"lateMessageRejectionPeriod\":null},\"luceneWriterConfig\":null},\"version\":\"2017-05-06T01:35:52.704Z\"},{\"spec\":{\"type\":\"NoopSupervisorSpec\"},\"version\":\"2017-05-05T13:46:29.985Z\"},{\"spec\":{\"type\":\"lucene_supervisor\",\"dataSchema\":{\"dataSource\":\"wuxianjiRT\",\"parser\":{\"type\":\"string\",\"parseSpec\":{\"format\":\"url\",\"timestampSpec\":{\"column\":\"EventDateTime\",\"format\":\"millis\"},\"dimensionsSpec\":{\"dimensions\":[{\"name\":\"SystemVersion\",\"type\":\"string\"},{\"name\":\"EventLabel\",\"type\":\"string\"},{\"name\":\"Network\",\"type\":\"string\"},{\"name\":\"IP\",\"type\":\"string\"},{\"name\":\"ClientDeviceID\",\"type\":\"string\"},{\"name\":\"Extras\",\"type\":\"string\"},{\"name\":\"ClientDeviceModel\",\"type\":\"string\"},{\"name\":\"ClientDeviceAgent\",\"type\":\"string\"},{\"name\":\"EventHour\",\"type\":\"string\"},{\"name\":\"EventValue\",\"type\":\"string\"},{\"name\":\"SessionID\",\"type\":\"string\"},{\"name\":\"ClientDeviceVersion\",\"type\":\"string\"},{\"name\":\"Operator\",\"type\":\"string\"},{\"name\":\"EventAction\",\"type\":\"string\"},{\"name\":\"Referrer\",\"type\":\"string\"},{\"name\":\"Source\",\"type\":\"string\"},{\"name\":\"Nation\",\"type\":\"string\"},{\"name\":\"EventDate\",\"type\":\"string\"},{\"name\":\"City\",\"type\":\"string\"},{\"name\":\"Media\",\"type\":\"string\"},{\"name\":\"EventScreen\",\"type\":\"string\"},{\"name\":\"__time\",\"type\":\"date\"},{\"name\":\"OsScreen\",\"type\":\"string\"},{\"name\":\"Campaign\",\"type\":\"string\"},{\"name\":\"Creative\",\"type\":\"string\"},{\"name\":\"Province\",\"type\":\"string\"},{\"name\":\"SystemName\",\"type\":\"string\"},{\"name\":\"ClientDeviceBrand\",\"type\":\"string\"},{\"name\":\"UserID\",\"type\":\"string\"},{\"name\":\"EventDateTime\",\"type\":\"string\"}],\"spatialDimensions\":[],\"dimensionExclusions\":[]}}},\"metricsSpec\":[],\"granularitySpec\":{\"type\":\"uniform\",\"segmentGranularity\":\"DAY\",\"queryGranularity\":{\"type\":\"none\"},\"intervals\":null}},\"tuningConfig\":{\"type\":\"kafka\",\"maxRowsInMemory\":500000,\"maxRowsPerSegment\":20000000,\"intermediatePersistPeriod\":\"PT10M\",\"basePersistDirectory\":\"/data1/druid/storage/wuxianjiRT\",\"maxPendingPersists\":0,\"indexSpec\":{\"bitmap\":{\"type\":\"concise\"},\"dimensionCompression\":null,\"metricCompression\":null},\"buildV9Directly\":true,\"reportParseExceptions\":false,\"handoffConditionTimeout\":0,\"workerThreads\":null,\"chatThreads\":null,\"chatRetries\":8,\"httpTimeout\":\"PT10S\",\"shutdownTimeout\":\"PT80S\",\"maxWarmCount\":1,\"consumerThreadCount\":2},\"ioConfig\":{\"topic\":\"wuxianjiRT\",\"replicas\":1,\"taskCount\":1,\"taskDuration\":\"PT86400S\",\"consumerProperties\":{\"bootstrap.servers\":\"192.168.0.213:9092,192.168.0.214:9092,192.168.0.216:9092\"},\"startDelay\":\"PT5S\",\"period\":\"PT30S\",\"useEarliestOffset\":true,\"completionTimeout\":\"PT1800S\",\"lateMessageRejectionPeriod\":null},\"luceneWriterConfig\":null},\"version\":\"2017-05-05T13:24:04.821Z\"},{\"spec\":{\"type\":\"NoopSupervisorSpec\"},\"version\":\"2017-05-05T13:23:31.900Z\"},{\"spec\":{\"type\":\"lucene_supervisor\",\"dataSchema\":{\"dataSource\":\"wuxianjiRT\",\"parser\":{\"type\":\"string\",\"parseSpec\":{\"format\":\"url\",\"timestampSpec\":{\"column\":\"EventDateTime\",\"format\":\"millis\"},\"dimensionsSpec\":{\"dimensions\":[{\"name\":\"SystemVersion\",\"type\":\"string\"},{\"name\":\"EventLabel\",\"type\":\"string\"},{\"name\":\"IP\",\"type\":\"string\"},{\"name\":\"Network\",\"type\":\"string\"},{\"name\":\"ClientDeviceID\",\"type\":\"string\"},{\"name\":\"EventScreen\",\"type\":\"string\"},{\"name\":\"ClientDeviceModel\",\"type\":\"string\"},{\"name\":\"OsScreen\",\"type\":\"string\"},{\"name\":\"Province\",\"type\":\"string\"},{\"name\":\"ClientDeviceBrand\",\"type\":\"string\"},{\"name\":\"SystemName\",\"type\":\"string\"},{\"name\":\"ClientDeviceAgent\",\"type\":\"string\"},{\"name\":\"ClientDeviceVersion\",\"type\":\"string\"},{\"name\":\"UserID\",\"type\":\"string\"},{\"name\":\"SessionID\",\"type\":\"string\"},{\"name\":\"Operator\",\"type\":\"string\"},{\"name\":\"EventAction\",\"type\":\"string\"},{\"name\":\"EventDateTime\",\"type\":\"string\"},{\"name\":\"Nation\",\"type\":\"string\"},{\"name\":\"City\",\"type\":\"string\"}],\"spatialDimensions\":[],\"dimensionExclusions\":[]}}},\"metricsSpec\":[],\"granularitySpec\":{\"type\":\"uniform\",\"segmentGranularity\":\"DAY\",\"queryGranularity\":{\"type\":\"none\"},\"intervals\":null}},\"tuningConfig\":{\"type\":\"kafka\",\"maxRowsInMemory\":500000,\"maxRowsPerSegment\":20000000,\"intermediatePersistPeriod\":\"PT10M\",\"basePersistDirectory\":\"/data1/druid/storage/wuxianjiRT\",\"maxPendingPersists\":0,\"indexSpec\":{\"bitmap\":{\"type\":\"concise\"},\"dimensionCompression\":null,\"metricCompression\":null},\"buildV9Directly\":true,\"reportParseExceptions\":false,\"handoffConditionTimeout\":0,\"workerThreads\":null,\"chatThreads\":null,\"chatRetries\":8,\"httpTimeout\":\"PT10S\",\"shutdownTimeout\":\"PT80S\",\"maxWarmCount\":1,\"consumerThreadCount\":2},\"ioConfig\":{\"topic\":\"wuxianjiRT\",\"replicas\":1,\"taskCount\":1,\"taskDuration\":\"PT86400S\",\"consumerProperties\":{\"bootstrap.servers\":\"192.168.0.213:9092,192.168.0.214:9092,192.168.0.216:9092\"},\"startDelay\":\"PT5S\",\"period\":\"PT30S\",\"useEarliestOffset\":true,\"completionTimeout\":\"PT1800S\",\"lateMessageRejectionPeriod\":null},\"luceneWriterConfig\":null},\"version\":\"2017-05-05T12:47:43.542Z\"}]");
    return Response.ok(builder.toString()).build();
  }

  @POST
  @Path("/topic/{id}/delete")
  @Produces(MediaType.TEXT_PLAIN)
  public Response deleteTopic(@PathParam("id") final String id)
  {
    return Response.ok(id).build();
  }

  @GET
  @Path("/stop")
  @Produces(MediaType.TEXT_PLAIN)
  public String shutdown() throws Exception {
    SupervisorManager.latch.countDown();
    return "stop server";
  }
}
