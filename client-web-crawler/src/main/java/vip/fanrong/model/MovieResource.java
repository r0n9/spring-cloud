package vip.fanrong.model;

import java.util.Date;
import java.util.List;

public class MovieResource {

    public static class MovieResourceFile {
        int id;
        String source;
        String resourceId;
        String name;
        String altName;

        String fileName;
        String fileSize;
        String downloadType;
        String downloadLink;
        Date insertTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAltName() {
            return altName;
        }

        public void setAltName(String altName) {
            this.altName = altName;
        }

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

        public String getDownloadType() {
            return downloadType;
        }

        public void setDownloadType(String downloadType) {
            this.downloadType = downloadType;
        }

        public String getDownloadLink() {
            return downloadLink;
        }

        public void setDownloadLink(String downloadLink) {
            this.downloadLink = downloadLink;
        }

        public Date getInsertTime() {
            return insertTime;
        }

        public void setInsertTime(Date insertTime) {
            this.insertTime = insertTime;
        }
    }

    private int id;

    private String name;
    private String nameChn;

    private String imgUrl;

    private String source; // e.g. zmz

    private String resourceId;

    private List<ResourceFile> resources; // JSON format for ResourceFile list;

    private Date updateTime;

    private Date insertTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameChn() {
        return nameChn;
    }

    public void setNameChn(String nameChn) {
        this.nameChn = nameChn;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<ResourceFile> getResources() {
        return resources;
    }

    public void setResources(List<ResourceFile> resources) {
        this.resources = resources;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "MovieResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nameChn='" + nameChn + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", source='" + source + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", resources='" + resources + '\'' +
                ", updateTime=" + updateTime +
                ", insertTime=" + insertTime +
                '}';
    }
}
