package io.sugo.user.group.api.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.sugo.user.group.api.dto.CreateDto;
import io.sugo.user.group.api.dto.DataConfig;
import io.sugo.user.group.api.dto.GroupData;
import io.sugo.user.group.api.dto.GroupReadConfig;
import io.sugo.user.group.api.dto.ReadDto;
import io.sugo.user.group.io.DataIO;
import io.sugo.user.group.io.DataIOFactory;

@Path("/")
public class UserGroupResource {

    @GET
    @Path("/info")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response info() {
        return Response.ok("info ok").build();
    }

    @POST
    @Path("/read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(ReadDto readDto) {
        DataConfig config = readDto.getDataConfig();
        if (config == null) {
            return Result.error("dataConfig can not be null");
        }
        DataIO dataIO = DataIOFactory.create(config);
        if (dataIO == null) {
            return Result.error("dataConfig is not available");
        }
        GroupReadConfig readConfig = readDto.getGroupReadConfig();
        if (readConfig == null) {
            readConfig = new GroupReadConfig();
        }

        byte[] bytes = dataIO.readFstByteFromRedis();
        if (bytes == null || bytes.length == 0) {
            return Result.ok(new GroupData(Collections.emptyList(), 0));
        }

        String str = new String(bytes);
        StringTokenizer tokenizer = new StringTokenizer(str);
        final int total = Integer.valueOf(tokenizer.nextToken());

        int startPos = readConfig.getStrartPos();
        if (startPos > total) {
            return Result.ok(new GroupData(Collections.emptyList(), total));
        }
        int endPos = readConfig.getEndPos();
        int idx = 0;
        List<String> list = new ArrayList<>(readConfig.getPageSize());
        while (idx < startPos && tokenizer.hasMoreElements()) {
            tokenizer.nextToken();
            idx++;
        }
        while (idx < endPos && tokenizer.hasMoreElements()) {
            list.add(tokenizer.nextToken());
            idx++;
        }

        return Result.ok(new GroupData(list, total));
    }

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(CreateDto dto) {
        List<String> ids = dto.getIds();
        int count = dto.getCount();
        if (ids == null || ids.isEmpty()) {
            return Result.error("ids can not be null");
        }
        if (count != ids.size()) {
            return Result.error("length of ids not equal to count");
        }
        DataConfig config = dto.getDataConfig();
        if (config == null) {
            return Result.error("dataConfig can not be null");
        }
        DataIO dataIO = DataIOFactory.create(config);
        if (dataIO == null) {
            return Result.error("dataConfig is not available");
        }
        StringBuilder builder = new StringBuilder();
        for (String id : ids) {
            builder.append(id).append('\t');
        }
        byte[] bytes = builder.insert(0, count + "\t").toString().getBytes();
        dataIO.writeByteToRedisList(bytes);
        return Result.ok();
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(DataConfig config) {
        if (config == null) {
            return Result.error("dataConfig can not be null");
        }
        DataIO dataIO = DataIOFactory.create(config);
        if (dataIO == null) {
            return Result.error("dataConfig is not available");
        }
        dataIO.delete();
        return Result.ok();
    }
}
