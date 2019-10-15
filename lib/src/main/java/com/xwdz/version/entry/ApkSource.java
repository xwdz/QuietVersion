package com.xwdz.version.entry;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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
    /**
     * 应用md5
     */
    private String md5;

    public ApkSource() {
    }

    public ApkSource(String url, String note, long fileSize, int remoteVersionCode, String remoteVersionName, String md5) {
        this.note = note;
        this.fileSize = fileSize;
        this.url = url;
        this.remoteVersionCode = remoteVersionCode;
        this.remoteVersionName = remoteVersionName;
        this.md5 = md5;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRemoteVersionCode() {
        return remoteVersionCode;
    }

    public void setRemoteVersionCode(int remoteVersionCode) {
        this.remoteVersionCode = remoteVersionCode;
    }

    public String getRemoteVersionName() {
        return remoteVersionName;
    }

    public void setRemoteVersionName(String remoteVersionName) {
        this.remoteVersionName = remoteVersionName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
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
        dest.writeString(this.md5);
    }

    protected ApkSource(Parcel in) {
        this.note = in.readString();
        this.fileSize = in.readLong();
        this.url = in.readString();
        this.remoteVersionCode = in.readInt();
        this.remoteVersionName = in.readString();
        this.md5 = in.readString();
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

    @Override
    public String toString() {
        return "{" +
                "remoteVersionCode=" + remoteVersionCode +
                ", remoteVersionName='" + remoteVersionName + '\'' +
                '}';
    }


///////////

    public static ApkSource simpleParser(String json) {
        try {
            JSONObject jsonObject        = new JSONObject(json);
            String     note              = jsonObject.getString("note");
            String     fileSize          = jsonObject.getString("fileSize");
            String     url               = jsonObject.getString("url");
            String     remoteVersionCode = jsonObject.getString("remoteVersionCode");
            String     remoteVersionName = jsonObject.getString("remoteVersionName");
            String     md5               = jsonObject.getString("md5");
            return new ApkSource(url, note,
                    Long.parseLong(fileSize),
                    Integer.parseInt(remoteVersionCode),
                    remoteVersionName,
                    md5);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ApkSource();
    }
}
