package io.sugo.http;

import com.google.common.base.Joiner;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;

import javax.ws.rs.HttpMethod;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SupervisorManager {
  public static final String GZIP_METHODS = Joiner.on(",").join(HttpMethod.GET, HttpMethod.POST);

  public static final int port = 6660;

  private static final AtomicInteger activeConnections = new AtomicInteger();
  public static final CountDownLatch latch = new CountDownLatch(1);

  public static void main(String[] args) throws Exception {
    Server server = null;
    try {
      server = makeJettyServer();
      initialize(server);
      server.start();
      System.out.println("start...in " + port);
      latch.await();
    } finally {
      if (server != null) {
        server.stop();
      }
      System.out.println("server stopped!");
    }
  }

  static Server makeJettyServer() {
    final Server server = new Server();

    // Without this bean set, the default ScheduledExecutorScheduler runs as non-daemon, causing lifecycle hooks to fail
    // to fire on main exit. Related bug: https://github.com/druid-io/druid/pull/1627
    server.addBean(new ScheduledExecutorScheduler("JettyScheduler", true), true);

    ServerConnector connector = new ServerConnector(server);
    connector.setPort(port);
    connector.setIdleTimeout(600000);
    // workaround suggested in -
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=435322#c66 for jetty half open connection issues during failovers
    connector.setAcceptorPriorityDelta(-1);

    List<ConnectionFactory> monitoredConnFactories = new ArrayList<>();
    for (ConnectionFactory cf : connector.getConnectionFactories()) {
      monitoredConnFactories.add(new JettyMonitoringConnectionFactory(cf, activeConnections));
    }
    connector.setConnectionFactories(monitoredConnFactories);

    server.setConnectors(new Connector[]{connector});

    return server;
  }

  static void initialize(Server server) {
    HandlerList handlerList = new HandlerList();

    final ServletContextHandler htmlHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    htmlHandler.setContextPath("/");
    htmlHandler.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
    htmlHandler.setInitParameter("org.eclipse.jetty.servlet.Default.redirectWelcome", "true");
    htmlHandler.setWelcomeFiles(new String[]{"console.html"});

    ServletHolder holderPwd = new ServletHolder("default", DefaultServlet.class);

    htmlHandler.addServlet(holderPwd, "/*");
    htmlHandler.setBaseResource(
        new ResourceCollection(
            new String[]{
                SupervisorManager.class.getClassLoader().getResource("static").toExternalForm(),
                SupervisorManager.class.getClassLoader().getResource("indexer_static").toExternalForm()
            }
        )
    );
    ServletHolder servletHolder = new ServletHolder(ServletContainer.class);
    servletHolder.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
    servletHolder.setInitParameter("com.sun.jersey.config.property.packages", "io.sugo.http.resource");
    htmlHandler.addServlet(servletHolder, "/druid/indexer/v1/*");
    handlerList.addHandler(htmlHandler);

    server.setHandler(handlerList);
  }

}
