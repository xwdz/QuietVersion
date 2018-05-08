package com.update.testabc;

public class Response {

    public final String code;
    public final String message;
    public final String reason;
    public final String syn;
    public final String sign;
    public final String update;

    public Response(String code, String message, String reason, String syn, String sign, String update) {
        this.code = code;
        this.message = message;
        this.reason = reason;
        this.syn = syn;
        this.sign = sign;
        this.update = update;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", reason='" + reason + '\'' +
                ", syn='" + syn + '\'' +
                ", sign='" + sign + '\'' +
                ", update='" + update + '\'' +
                '}';
    }
}
