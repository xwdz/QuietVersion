package com.xingwei.checkupdate.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkSource implements Parcelable {

    /**
     * 更新策略
     * 0:正常升级(用户点击确认升级再升级)
     * 1:强制更新
     */
    public int level;

    /**
     * 更新文本
     */

    public String note;

    /**
     * apk文件大小
     */

    public long fileSize;

    /**
     * apk 下载地址
     */

    public String url;


    public ApkSource() {

    }


    @Override
    public String toString() {
        return "ApkSource{" +
                "level=" + level +
                ", url='" + url + '\'' +
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
    }

    protected ApkSource(Parcel in) {
        this.level = in.readInt();
        this.note = in.readString();
        this.fileSize = in.readLong();
        this.url = in.readString();
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
