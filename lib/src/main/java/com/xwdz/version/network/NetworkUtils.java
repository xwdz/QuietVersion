package com.xwdz.version.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.xwdz.version.utils.LOG;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public class NetworkUtils {


    /**
     * 网络类型定义
     */
    public static final int NET_NONE = -1;
    public static final int NET_UNKNOWN = 0;
    public static final int NET_WIFI = 1;
    public static final int NET_2G = 2;
    public static final int NET_3G = 3;
    public static final int NET_4G = 4;


    /**
     * 判断网络连接是否可用
     * @param context 上下文环境
     * @return true 可用；false 不可用
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = connMgr.getActiveNetworkInfo();
            return ((null != info) && info.isAvailable());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }

    /**
     * 判断WIFI是否连接
     * @param context 上下文
     * @return true 连接；false 未连接
     */
    public static boolean isWIFIAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return ((null != info) && info.isAvailable()
                    && (info.getType() == ConnectivityManager.TYPE_WIFI));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }

    /**
     * 判断数据网络是否连接
     * @param context 上下文
     * @return true 连接；false 未连接
     */
    public static boolean isMobileAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            return ((null != info) && info.isAvailable()
                    && (info.getType() == ConnectivityManager.TYPE_MOBILE));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }

    /**
     * 获取网络类型
     * @param context 上下文
     * @return 当前网络类型
     */
    public static int getNetworkType(Context context) {
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(null != connMgr){
                @SuppressLint("MissingPermission") NetworkInfo info = connMgr.getActiveNetworkInfo();

                if (null == info){
                    return NET_NONE;
                } else {
                    int type = info.getType();

                    if (type == ConnectivityManager.TYPE_WIFI){
                        return NET_WIFI;
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        return typeOf(info.getSubtype());
                    } else {
                        return NET_UNKNOWN;
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return NET_UNKNOWN;
    }

    /**
     * 将系统网络类型转为自定义
     * @param networkType 系统网络类型
     * @return 自定义网络类型
     */
    private static int typeOf(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NET_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NET_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NET_4G;
            default:
                return NET_UNKNOWN;
        }
    }
}
