package vip.fanrong.model;

public enum DownloadType {
    ftp("FTP"), thunder("迅雷"), ed2k("电驴"), magnet("磁力"), baidu("网盘"), ctfile("诚通网盘"), miwifi("小米路由器远程离线下载");

    String nameChn; // TODO list can be better.


    DownloadType(String nameChn) {
        this.nameChn = nameChn;
    }

    public String getNameChn() {
        return this.nameChn;
    }

    public static DownloadType valueOfNameChn(String nameChn) {
        for (DownloadType type : DownloadType.values()) {
            if (type.getNameChn().equalsIgnoreCase(nameChn)) {
                return type;
            }
        }
        return null;
    }

}
