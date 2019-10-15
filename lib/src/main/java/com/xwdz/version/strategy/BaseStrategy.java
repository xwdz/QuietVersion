package com.xwdz.version.strategy;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface BaseStrategy {

    // 所有策略优先级
    // 数值越高优先级越大

    int PRIORITY_0  = 0;
    int PRIORITY_2  = 2;
    int PRIORITY_4  = 4;
    int PRIORITY_6  = 6;
    int PRIORITY_8  = 8;
    int PRIORITY_10 = 10;

    int priority();

    /**
     * 策略唯一名称
     */
    String getName();

}
