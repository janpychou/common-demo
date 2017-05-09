package io.sugo.user.group.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import io.sugo.user.group.SystemConfig;
import io.sugo.user.group.utils.LogUtil;
import io.sugo.user.group.utils.RunController;
import io.sugo.user.group.utils.StringUtil;

public class ApiServer implements Runnable {

    //    private HttpServer httpServer;

    private URI getBaseURI() {
        String ip = SystemConfig.getString(SystemConfig.LISTEN_IP, "0.0.0.0");
        int port = SystemConfig.getInt(SystemConfig.LISTEN_PORT);
        String rootPath = SystemConfig.getString(SystemConfig.PROJECT_NAME);

        String urlStr = StringUtil.formatByNumber("http://{0}:{1}/{2}/", ip, port, rootPath);

        URI baseUri = UriBuilder.fromUri(urlStr).build();
        return baseUri;
    }

    @Override
    public void run() {
        LogUtil.info("Starting Api Server using grizzly...");

        ResourceConfig resourceConfig = new PackagesResourceConfig("io.sugo.user.group.api.resource");
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        URI baseUri = getBaseURI();

        try {
            GrizzlyServerFactory.createHttpServer(baseUri, resourceConfig);
            LogUtil.info("Api Server started, url is " + baseUri);
            RunController.blockApiServer();
        } catch (Exception e) {
            LogUtil.error(e);
            LogUtil.info("Start Api Server failed, System exit");
            System.exit(0);
        }
    }

}
