package io.sugo.user.group.api.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.NotFoundException;

import io.sugo.user.group.utils.LogUtil;

@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Exception> {
    /**
     * 异常处理
     * 
     * @param exception
     * @return 异常处理后的Response对象
     */
    public Response toResponse(Exception exception) {
        Status statusCode = Status.BAD_REQUEST;
        if (exception instanceof NotFoundException) {
            statusCode = Status.NOT_FOUND;
        }
        LogUtil.warn(exception.getClass().getName() + ": " + exception.getMessage());
        return Response.ok(exception.getMessage(), MediaType.TEXT_PLAIN).status(statusCode).build();
    }
}
