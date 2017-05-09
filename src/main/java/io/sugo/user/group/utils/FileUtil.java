/*
 * Copyright (C) 2012 GZ-ISCAS Inc., All Rights Reserved.
 */
package io.sugo.user.group.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * 
 * @Description: 文件工具类  
 * @Author janpychou@qq.com
 * @CreateDate:   [Jun 26, 2015 3:25:26 PM]   
 *
 */
public class FileUtil {

    /**
     * save file，如果路径不存在，则先创建路径
     * @param inputStream   
     * @param filePath      文件保存的路径
     * @param fileName      文件名
     * @return
     * @throws IOException
     */
    public static String saveFile(InputStream inputStream, String filePath, String fileName) throws IOException {

        String jarFilePath = filePath + "/" + fileName;

        File destDir = new File(filePath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        saveFile(inputStream, jarFilePath);
        return jarFilePath;
    }

    /**
     * 
     * @param inputStream
     * @param filePath      文件保存全路径，包含路径和文件名
     * @throws IOException
     */
    public static void saveFile(InputStream inputStream, String filePath) throws IOException {

        FileOutputStream outputStream = new FileOutputStream(new File(filePath));
        FileChannel outChannel = outputStream.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ReadableByteChannel inChannel = Channels.newChannel(inputStream);

        while (true) {
            buffer.clear();
            int r = inChannel.read(buffer);
            if (r == -1) {
                break;
            }
            buffer.flip();
            outChannel.write(buffer);
        }

        inChannel.close();
        outChannel.close();
        inputStream.close();
        outputStream.close();
    }

    /**
     * 构造函数.
     */
    private FileUtil() {

    }

    public static long getFileLength(String filePath, String fileName) {
        String path = filePath + "/" + fileName;

        File file = new File(path);
        return file.length();
    }

    public static boolean exists(String jarFilePath) {
        File file = new File(jarFilePath);
        return file.exists();
    }

    /**
     * 递归删除目录中的子目录下
     * @param dir
     * @return
     */
    public static boolean deleteDirR(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirR(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static void deleteDirR(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            deleteDirR(dir);
        }
    }

    public static void writeFile(String filePath, String content) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(new File(filePath), "rw");
        raf.writeUTF(content);
        raf.close();
    }

    @SuppressWarnings("resource")
    public static String readFile(String filePath) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(new File(filePath), "r");
        String content = raf.readUTF();
        return content;
    }
    
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
