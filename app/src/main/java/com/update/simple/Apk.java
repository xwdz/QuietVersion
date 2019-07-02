package com.update.simple;


public class Apk {

    public final String verb;
    public final String ver;
    public final String md5;
    public final String url;
    public final String app_pkg;
    public final long   size;
    public final String des;

    public Apk(String verb, String ver, String md5, String url, String app_pkg, long size, String des) {
        this.verb = verb;
        this.ver = ver;
        this.md5 = md5;
        this.url = url;
        this.app_pkg = app_pkg;
        this.size = size;
        this.des = des;
    }

    @Override
    public String toString() {
        return "Apk{" +
                "verb='" + verb + '\'' +
                ", ver='" + ver + '\'' +
                ", md5='" + md5 + '\'' +
                ", url='" + url + '\'' +
                ", app_pkg='" + app_pkg + '\'' +
                ", size=" + size +
                ", des='" + des + '\'' +
                '}';
    }
}
