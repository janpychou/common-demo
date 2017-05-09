package io.sugo.user.group.api.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class Result {

    public static Response ok() {
        return Response.ok("execute successfully").build();
    }

    public static Response error(String msg) {
        return Response.status(Status.BAD_REQUEST).entity(msg).build();
    }

    public static Response ok(Object data) {
        return Response.ok(data).build();
    }
}
