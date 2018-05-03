package com.xingwei.checkupdate.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkResultSource implements Parcelable {

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

    /**
     * apk md5
     */

    public String md5;


    /**
     * todo
     * apk 名称(非必选,默认路径)
     */
    public String apkName;

    /**
     * todo
     * apk 路径(非必选,默认路径)
     */
    public String apkPath;

    public ApkResultSource(){

    }


    @Override
    public String toString() {
        return "ApkResultSource{" +
                "url='" + url + '\'' +
                ", apkName='" + apkName + '\'' +
                ", apkPath='" + apkPath + '\'' +
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
        dest.writeString(this.md5);
    }

    protected ApkResultSource(Parcel in) {
        this.level = in.readInt();
        this.note = in.readString();
        this.fileSize = in.readLong();
        this.url = in.readString();
        this.md5 = in.readString();
    }

    public static final Parcelable.Creator<ApkResultSource> CREATOR = new Parcelable.Creator<ApkResultSource>() {
        @Override
        public ApkResultSource createFromParcel(Parcel source) {
            return new ApkResultSource(source);
        }

        @Override
        public ApkResultSource[] newArray(int size) {
            return new ApkResultSource[size];
        }
    };
}
