package vip.fanrong.model;

public enum DownloadType {
    ftp("FTP"), thunder("迅雷"), ed2k("电驴"), magnet("磁力"), baidu("百度云盘"), ctfile("诚通网盘"), miwifi("小米路由");

    String nameChn;


    DownloadType(String nameChn) {
        this.nameChn = nameChn;
    }

    String getNameChn() {
        return this.nameChn;
    }
}
