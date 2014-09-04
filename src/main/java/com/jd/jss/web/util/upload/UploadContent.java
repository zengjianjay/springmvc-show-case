package com.jd.jss.web.util.upload;

import java.io.InputStream;
import java.util.Map;

/**
 * @author 刘兆敏
 * @since 2014-09-03
 */
public class UploadContent {

    // 除文件域之外的其他字段
    // 可以是 null
    private Map<String, String[]> kvs;

    // 原始文件名
    private String filename;

    // 原始文件类型 image/png
    private String contentType;

    // 上传文件的大小
    private int size;

    // 上传文件的 InputStream
    private InputStream input;

    public Map<String, String[]> getKvs() {
        return kvs;
    }

    public void setKvs(Map<String, String[]> kvs) {
        this.kvs = kvs;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
