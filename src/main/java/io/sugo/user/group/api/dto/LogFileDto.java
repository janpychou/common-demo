package io.sugo.user.group.api.dto;

import java.io.Serializable;

public class LogFileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    
    private long filePosition;
    
    private String content;
    
    private int lineCount;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFilePosition() {
        return filePosition;
    }

    public void setFilePosition(long filePosition) {
        this.filePosition = filePosition;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
}
