package vip.fanrong.model;

import java.util.Map;

public class ResourceFile {

    String fileName;
    String fileSize;
    Map<DownloadType, String> resources;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Map<DownloadType, String> getResources() {
        return resources;
    }

    public void setResources(Map<DownloadType, String> resources) {
        this.resources = resources;
    }
}
