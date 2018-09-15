package com.xwdz.quietversion.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkSource implements Parcelable {

    /**
     * 更新策略
     * 0:正常升级(用户点击确认升级再升级)
     * 1:强制更新
     */
    private final int level;

    /**
     * 更新文本
     */

    private final String note;

    /**
     * apk文件大小
     */

    private final long fileSize;

    /**
     * apk 下载地址
     */

    private final String url;

    /**
     * 远程versionCode
     */

    private final int remoteVersionCode;


    public ApkSource( String url,String note,long fileSize,int level,int remoteVersionCode) {
        this.level = level;
        this.note = note;
        this.fileSize = fileSize;
        this.url = url;
        this.remoteVersionCode = remoteVersionCode;
    }

    public int getLevel() {
        return level;
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
    public String toString() {
        return "ApkSource{" +
                "level=" + level +
                ", url='" + url + '\'' +
                ", remoteVersionCode=" + remoteVersionCode +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.level);
        dest.writeString(this.note);
        dest.writeLong(this.fileSize);
        dest.writeString(this.url);
        dest.writeInt(this.remoteVersionCode);
    }

    protected ApkSource(Parcel in) {
        this.level = in.readInt();
        this.note = in.readString();
        this.fileSize = in.readLong();
        this.url = in.readString();
        this.remoteVersionCode = in.readInt();
    }

    public static final Parcelable.Creator<ApkSource> CREATOR = new Parcelable.Creator<ApkSource>() {
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
