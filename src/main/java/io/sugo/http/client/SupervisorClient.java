package io.sugo.http.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import io.sugo.http.SupervisorManager;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class SupervisorClient {

  private Client client;

  public static void main(String[] args) {
    SupervisorClient sc = new SupervisorClient();
    sc.init();
    sc.getInfo();
    sc.postShutdown();
  }

  private void postShutdown() {
    WebResource resource = client.resource(String.format("http://localhost:%d/druid/indexer/v1/supervisor/%s/shutdown", SupervisorManager.port, "janpy"));
    Map<String, Object> data = new HashMap<>();
    data.put("id", "zhoujianping");
    ClientResponse result = resource.accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON)
        .post(ClientResponse.class, data);
    System.out.println(result.getStatusInfo());
    String str = result.getEntity(String.class);
    System.out.println(str);
  }

  private void getInfo() {
    WebResource resource = client.resource(String.format("http://localhost:%d/druid/indexer/v1/supervisor/hello/%s", SupervisorManager.port, "janpy"));
    String result = resource.accept(MediaType.TEXT_PLAIN)
        .type(MediaType.TEXT_PLAIN)
        .get(String.class);
    System.out.println(result);
  }

  private void init() {
    ClientConfig cfg = new DefaultClientConfig();
    cfg.getClasses().add(JacksonJsonProvider.class);
    client = Client.create(cfg);
  }
}
