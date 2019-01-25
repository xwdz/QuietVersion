package com.xwdz.version.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkSource implements Parcelable {

    /**
     * 更新文本
     */
    private String note;

    /**
     * apk文件大小
     */
    private long fileSize;

    /**
     * apk 下载地址
     */
    private String url;

    /**
     * 远程versionCode
     */
    private int    remoteVersionCode;
    /**
     * 远程VersionName
     */
    private String remoteVersionName;

    public ApkSource(String url, String note, long fileSize, int remoteVersionCode, String remoteVersionName) {
        this.note = note;
        this.fileSize = fileSize;
        this.url = url;
        this.remoteVersionCode = remoteVersionCode;
        this.remoteVersionName = remoteVersionName;
    }


    public String getNote() {
        return note;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getUrl() {
        return url;
    }

    public int getRemoteVersionCode() {
        return remoteVersionCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.note);
        dest.writeLong(this.fileSize);
        dest.writeString(this.url);
        dest.writeInt(this.remoteVersionCode);
        dest.writeString(this.remoteVersionName);
    }

    protected ApkSource(Parcel in) {
        this.note = in.readString();
        this.fileSize = in.readLong();
        this.url = in.readString();
        this.remoteVersionCode = in.readInt();
        this.remoteVersionName = in.readString();
    }

    public static final Creator<ApkSource> CREATOR = new Creator<ApkSource>() {
        @Override
        public ApkSource createFromParcel(Parcel source) {
            return new ApkSource(source);
        }

        @Override
        public ApkSource[] newArray(int size) {
            return new ApkSource[size];
        }
    };
}
