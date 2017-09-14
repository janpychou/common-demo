package io.sugo.http.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.smile.SmileMediaTypes;
import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Path("/lookups")
public class LookupResource {
  @GET
  @Path("/hello/{name}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.TEXT_PLAIN)
  public String hello(@PathParam("name") String name) throws Exception {
    return "hello wolrd! " + name;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response getAll() {
    String str = "[\"__default\"]";
    return Response.ok(str).build();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response specPost(final String spec) {
    return Response.ok(spec).build();
  }

  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@PathParam("id") final String id) {
    String str = "[\"usergroup_HJn22mOwZ\",\"usergroup_BkYTBtjQZ\",\"usergroup_SyLOdVA4Z\",\"usergroup_HJWTqCr8W\",\"usergroup_ryy1q03zZ\",\"usergroup_SyZ7cGzQZ\",\"usergroup_r1gtzm8Ib\",\"usergroup_HJlkKLoj7W\",\"usergroup_ry3LFRHUb\",\"usergroup_HJdim4OPb\",\"usergroup_SkuAKqlEZ\",\"usergroup_Sy73bnfgZ\",\"usergroup_rJcT0oGxZ\",\"usergroup_rkvaSy4GZ\",\"usergroup_SkgEm8EOwW\",\"usergroup_SkloBBTIIb\",\"usergroup_HJgFRLAhzZ\",\"usergroup_S1Ruqms4W\",\"usergroup_S1T903By0\",\"usergroup_r14VY4OwZ\",\"usergroup_ryzrwtNEZ\",\"usergroup_Bk2oY4dPZ\",\"usergroup_B1Wpsz02fW\",\"usergroup_B1GA0hNEW\",\"usergroup_Bks4mmREb\",\"usergroup_BkBXcTWDb\",\"usergroup_SkomEjsQb\",\"usergroup_HJlEX2EOwZ\",\"usergroup_r1wZ56g7b\",\"usergroup_ByzbacbKXZ\",\"usergroup_BJ65w7uPW\",\"usergroup_B1XHinzPb\",\"usergroup_SkZ4w4ssXW\",\"usergroup_rJzbQ0BUb\",\"usergroup_ByvLM2v4W\",\"usergroup_SytzPbTMb\",\"usergroup_rJzj5TvQW\",\"usergroup_ByZB2jomb\",\"usergroup_ryL73mw8b\",\"usergroup_SyAdlrxPZ\",\"usergroup_H12i1qjmb\",\"usergroup_rkw9grxwZ\",\"usergroup_SkbWGH0xNb\",\"usergroup_SkldKnEdPW\",\"usergroup_HkPdJmM7W\",\"usergroup_SJNux2MgZ\",\"usergroup_rJLwDcRWW\",\"usergroup_ryEN59eEW\",\"usergroup_HJZMjpYo7b\",\"usergroup_r1eCOaLPUW\",\"usergroup_r1eqONdPZ\",\"usergroup_SyQ9OC3fb\",\"usergroup_H1Y_sVw0l\",\"usergroup_S1usPaoXb\",\"usergroup_rJecLtpoXb\",\"usergroup_BkxRAllDZ\",\"usergroup_S1TZQVdvZ\",\"usergroup_HkJvicCWW\",\"usergroup_ryeT3jisXW\",\"usergroup_rklDKQuvb\",\"usergroup_SyVOrbrQb\",\"usergroup_BJ_16RaBZ\",\"usergroup_ryvwFbdIZ\",\"usergroup_HyuzCC3MZ\",\"usergroup_S1IKEOX4W\",\"usergroup_SkBHRi4XZ\",\"usergroup_rkawByEMW\",\"usergroup_HkjADTjQW\",\"usergroup_S1bwKcCBLb\",\"usergroup_rJxJVEXW7W\",\"usergroup_rkOfvr_wZ\",\"usergroup_H1I1eK1Qb\",\"usergroup_SypJiabvW\",\"usergroup_rkaRK6Zwb\",\"usergroup_BJlgr56WvW\",\"usergroup_SkE8CWWXW\",\"usergroup_rktAUQDRx\",\"usergroup_BJE6hiuXW\",\"usergroup_Syew8V2dmW\",\"usergroup_HyKgIbq7b\",\"usergroup_HyDVSqAbZ\",\"usergroup_S1gaFKsi7W\",\"usergroup_Sy2IVXo4b\"]";
    return Response.ok(str).build();
  }

  @DELETE
  @Produces({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
  @Path("/{tier}/{lookup}")
  public Response deleteLookup(
      @PathParam("tier") String tier,
      @PathParam("lookup") String lookup,
      @Context HttpServletRequest req
  ) {
    return Response.status(Response.Status.ACCEPTED).build();
  }

  @POST
  @Produces({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
  @Path("/{tier}/{lookup}")
  public Response createOrUpdateLookup(
      @PathParam("tier") String tier,
      @PathParam("lookup") String lookup,
      InputStream in,
      @Context HttpServletRequest req
  ) {
    try {
      if (Strings.isNullOrEmpty(tier)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("`tier` required").build();
      }

      if (Strings.isNullOrEmpty(lookup)) {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity("`lookup` required")
            .build();
      }
      final boolean isSmile = SmileMediaTypes.APPLICATION_JACKSON_SMILE.equals(req.getContentType());
      final ObjectMapper mapper = new ObjectMapper();
      final Map<String, Object> lookupSpec;
      try {
        lookupSpec = mapper.readValue(in, new TypeReference<Map<String, Object>>() {
        });
      } catch (IOException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(e).build();
      }
      return Response.status(Response.Status.ACCEPTED).build();
    } catch (Exception e) {
      return Response.serverError().entity(e).build();
    }
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON, SmileMediaTypes.APPLICATION_JACKSON_SMILE})
  @Path("/{tier}/{lookup}")
  public Response getSpecificLookup(
      @PathParam("tier") String tier,
      @PathParam("lookup") String lookup
  ){
    String str = "{\"type\":\"cachingLookup\",\"version\":\"SJW5YFNCN-\",\"dataFetcher\":{\"type\":\"redis\",\"clusterMode\":false,\"hostAndPorts\":\"dev220.sugo.net:6379\",\"groupId\":\"usergroup_B1GA0hNEW\"}}";
    return Response.ok(str).build();
  }
}
