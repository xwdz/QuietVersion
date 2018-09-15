package com.update.simple;

public class Result<T> {
    public final int code;
    public final T data;

    public Result(int code, T data) {
        this.code = code;
        this.data = data;
    }
}
