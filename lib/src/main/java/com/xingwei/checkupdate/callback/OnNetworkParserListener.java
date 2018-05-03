package com.xingwei.checkupdate.callback;

import com.xingwei.checkupdate.entry.ApkResultSource;

public interface OnNetworkParserListener {

    ApkResultSource parser(String response);
}
