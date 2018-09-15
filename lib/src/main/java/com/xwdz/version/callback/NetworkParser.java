package com.xwdz.version.callback;

import com.xwdz.version.entry.ApkSource;

/**
 * 自定义解析器
 * @author huangxingwei(xwdz9989@gmail.com)
 * @since v0.0.1
 */
public interface NetworkParser {

    ApkSource parser(String response);
}
