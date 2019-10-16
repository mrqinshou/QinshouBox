package com.qinshou.qinshoubox.homepage.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/10/16 16:55
 * Description:QinshouBox Api 返回的数据的统一格式
 */
public class QinshouBoxResultBean<T> {
    @SerializedName("success")
    private int success;
    @SerializedName("failureInfo")
    private String failureInfo;
    @SerializedName("data")
    private T data;

    public QinshouBoxResultBean() {
    }

    @Override
    public String toString() {
        return "QinshouBoxResultBean{" +
                "success=" + success +
                ", failureInfo='" + failureInfo + '\'' +
                ", data=" + data +
                '}';
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getFailureInfo() {
        return failureInfo;
    }

    public void setFailureInfo(String failureInfo) {
        this.failureInfo = failureInfo;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
