package io.sugo.user.group.api.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.sugo.user.group.api.dto.LogFileDto;
import io.sugo.user.group.utils.FileUtil;
import io.sugo.user.group.utils.LogUtil;

@Path("/file")
public class FileResource {

    private static String fileBasePath = "";
    private static ConcurrentMap<String, Boolean> cachedStatus = new ConcurrentHashMap<String, Boolean>();

    static {
        fileBasePath = "";
        if (fileBasePath != null && !fileBasePath.endsWith(File.separator)) {
            fileBasePath += File.separator;
        }
    }

    @GET
    @Path("/info")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response info() {
        return Response.ok("info ok").build();
    }

    @GET
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("path") String path) {
        Response response = null;
        try {
            LogUtil.formatInfo("delete path {0}", path);
            FileUtil.deleteDirR(path);
            LogUtil.info("delete path successfully");
            response = Response.ok().build();
        } catch (Exception e) {
            LogUtil.formatWarn("delete path {0} failed, error {1}", path, e.getMessage());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @POST
    @Path("/tail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response tailFile(LogFileDto fileDto) {
        Response response = null;
        try {
            Status status = tailFileContent(fileDto);
            if (Status.OK == status) {
                response = Response.ok(fileDto).build();
            } else if (Status.NOT_FOUND == status) {
                response = Response.status(Status.NOT_FOUND).entity("No such file").build();
            }
        } catch (IOException e) {
            LogUtil.error(e);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    private Status tailFileContent(LogFileDto fileDto) throws IOException {
        long pos = fileDto.getFilePosition();

        StringBuffer lines = new StringBuffer();

        String filename = fileDto.getFileName();
        File file = new File(filename);
        if (!file.exists()) {
            return Status.NOT_FOUND;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            if (pos < 0) {
                pos = raf.length() - 0;
            } else if (pos > raf.length()) {
                pos = 0;
            }
            if (pos < 0) {
                pos = 0;
            }
            raf.seek(pos);
            //        int lineLen = SystemConfig.getInt(SystemConfig.TAIL_LINE_NUM);
            int lineLen = fileDto.getLineCount();

            String line = null;
            String separator = System.getProperty("line.separator");
            for (int i = 0; i < lineLen; i++) {
                line = raf.readLine();
                if (line != null) {
                    lines.append(new String(line.getBytes("ISO-8859-1"), "utf-8") + separator);
                } else {
                    break;
                }
            }
            pos = raf.getFilePointer();

            fileDto.setContent(lines.toString());
            fileDto.setFilePosition(pos);
        } catch (IOException ie) {
            throw ie;
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
        return Status.OK;
    }

    @GET
    @Path("/{filename: .*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFile(@PathParam("filename") String filename) {
        Response response = null;
        try {
            String path = fileBasePath + filename;
            File file = new File(path);
            if (file.exists()) {
                String attachment = "attachment;filename=" + filename;
                String mt = new MimetypesFileTypeMap().getContentType(file);
                response = Response.ok(file, mt).header("Content-Disposition", attachment).build();
            } else {
                response = Response.status(Status.NOT_FOUND).entity(path + " not found").build();
            }
            LogUtil.formatInfo("get file {0}", path);
        } catch (Exception e) {
            LogUtil.formatWarn("get file {0} failed, error {1}", filename, e.getMessage());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @POST
    @Path("/write/{id: [a-zA-Z_0-9-]*}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeArgs(@PathParam("id") String containerId, String args) {
        Response response = null;
        try {
            LogUtil.formatInfo("containerId:{0}, args:{1}", containerId, args);
            FileUtil.writeFile(fileBasePath + containerId, args);
            response = Response.ok().build();
        } catch (Exception e) {
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/read/{id: [a-zA-Z_0-9-]*}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response readArgs(@PathParam("id") String containerId) {
        Response response = null;
        try {
            String content = FileUtil.readFile(fileBasePath + containerId);
            LogUtil.info(containerId + " args:" + content);
            response = Response.ok(content).build();
        } catch (Exception e) {
            LogUtil.warn(e);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/deleteArgs/{id: [a-zA-Z_0-9-]*}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteArgs(@PathParam("id") String containerId) {
        Response response = null;
        try {
            FileUtil.deleteFile(fileBasePath + containerId);
            LogUtil.info("delete args file " + containerId);
            response = Response.ok().build();
        } catch (Exception e) {
            LogUtil.warn(e);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/status/{id: [a-zA-Z_0-9-]*}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response reportStatus(@PathParam("id") String id) {
        Response response = null;
        try {
            cachedStatus.putIfAbsent(id, Boolean.TRUE);
            response = Response.ok().build();
        } catch (Exception e) {
            LogUtil.warn(e);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    @GET
    @Path("/getstatus/{id: [a-zA-Z_0-9-]*}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStatus(@PathParam("id") String id) {
        Response response = null;
        try {
            boolean status = cachedStatus.containsKey(id);
            if (status) {
                cachedStatus.remove(id);
            }
            response = Response.ok(status + "").build();
        } catch (Exception e) {
            LogUtil.warn(e);
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return response;
    }

    //http://172.25.19.17:2627/agent/file/zipfile?filepath=/tmp/worker/&filename=worker-7070.log
    //http://172.25.19.17:8080/logZipFile.do?ip=172.25.19.17&port=2627&filename=ce0b510d-4263-4ff4-8858-d0856e876648/4ebe44aa-b130-4d99-a2bf-0083f435ff93/logs/worker-7012.log&token=123456
    @GET
    @Path("/zipfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public Response getZipFile(@QueryParam("filepath") String filepath, @QueryParam("filename") String filename) {
        Response response = null;
        File zipFile = null;
        try {
            if (filepath.endsWith(File.separator)) {
                filepath += filename;
            } else {
                filepath += File.separator + filename;
            }
            String zipFilepath = filepath + ".zip";
            zipFile = new File(zipFilepath);
            String zipFilename = zipFile.getName();
            FileOutputStream os = new FileOutputStream(zipFile);
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(os));

            File file = new File(filepath);
            if (file.exists()) {
                compressedFile(zipOut, file);

                String filepath1 = filepath + ".1";
                File file1 = new File(filepath1);
                if (file1.exists()) {
                    compressedFile(zipOut, file1);
                }
                zipOut.close();

                String attachment = "attachment;filename=" + zipFilename;
                String mt = new MimetypesFileTypeMap().getContentType(zipFile);
                response = Response.ok(zipFile, mt).header("Content-Disposition", attachment).build();
            } else {
                response = Response.status(Status.NOT_FOUND).entity(filepath + " not found").build();
            }
            LogUtil.formatInfo("get file {0}", zipFilepath);
        } catch (Exception e) {
            LogUtil.formatWarn("delete path {0} failed, error {1}", filepath, e.getMessage());
            response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } finally {
            if (zipFile != null && zipFile.exists()) {
                zipFile.deleteOnExit();
            }
        }
        return response;
    }

    private void compressedFile(ZipOutputStream zipOut, File file) throws IOException {
        //文件输入流  
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        zipOut.putNextEntry(new ZipEntry(file.getName()));
        //进行写操作  
        int j = 0;
        byte[] buffer = new byte[4096];
        while ((j = bis.read(buffer)) > 0) {
            zipOut.write(buffer, 0, j);
        }
        //关闭输入流
        fis.close();
    }
}
