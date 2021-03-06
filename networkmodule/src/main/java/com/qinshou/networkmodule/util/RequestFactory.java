package com.qinshou.networkmodule.util;

import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import java.io.File;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/7/3 19:16
 * Description:请求生成工厂
 */
public class RequestFactory {
    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/7/5 14:18
     * Description:创建一个 get 请求
     *
     * @param url          请求地址
     * @param headerMap    请求头
     * @param parameterMap 请求参数
     * @return 用于发起请求的对象
     */
    public static Request newGetRequest(String url, Map<String, String> headerMap, Map<String, Object> parameterMap) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }
        // 添加参数
        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        for (String key : parameterMap.keySet()) {
            Object value = parameterMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            httpUrlBuilder.addQueryParameter(key, value.toString());
        }
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
        return requestBuilder
                .url(httpUrlBuilder.build())
                .get()
                .build();
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/7/5 14:18
     * Description:创建一个 post 请求,参数以表单形式传递
     *
     * @param url          请求地址
     * @param parameterMap 请求参数
     * @return 用于发起请求的对象
     */
    public static Request newPostRequest(String url, Map<String, String> headerMap, Map<String, Object> parameterMap) {
        StringBuilder parameterStringBuilder = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            parameterStringBuilder.append(key).append("=").append(parameterMap.get(key)).append("&");
        }
        if (parameterStringBuilder.lastIndexOf("&") != -1) {
            parameterStringBuilder.deleteCharAt(parameterStringBuilder.lastIndexOf("&"));
        }
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
        return requestBuilder
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=utf-8"), parameterStringBuilder.toString()))
                .build();
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/7/5 14:18
     * Description:创建一个 post 请求,参数以 json 字符串形式传递
     *
     * @param url          请求地址
     * @param paramMap 请求参数
     * @return 用于发起请求的对象
     */
    public static Request newPostJsonRequest(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
        return requestBuilder
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new GsonBuilder()
                        .setLongSerializationPolicy(LongSerializationPolicy.STRING) //转换 Long 类型参数时先转为 String,否则数字太长会自动转为科学计数法
                        .create()
                        .toJson(paramMap)))
                .addHeader("Accept-Language", "zh_CN")
                .build();
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/7/5 15:24
     * Description:创建一个发送文件的 post 请求
     *
     * @param url          请求地址
     * @param paramMap 请求参数
     * @return 用于发起请求的对象
     */
    public static Request newPostFileRequest(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        // 设置类型
        multipartBodyBuilder.setType(MultipartBody.FORM);
        for (String key : paramMap.keySet()) {
            Object value = paramMap.get(key);
            if (value == null) {
                continue;
            }
            if (value instanceof File) {
                File file = (File) value;
                multipartBodyBuilder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
            } else {
                multipartBodyBuilder.addFormDataPart(key, value.toString());
            }
        }
        Log.i("daolema", "paramMap--->" + paramMap);
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
        // 创建Request
        return requestBuilder
                .url(url)
                .post(multipartBodyBuilder.build())
                .build();
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2020/1/3 09:08
     * Description:创建一个下载文件的 get 请求
     *
     * @param url 请求地址
     * @return 用于发起请求的对象
     */
    public static Request newGetDownloadRequest(String url, Map<String, String> headerMap) {
        // 添加请求头
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("Accept-Encoding", "dentity");
        for (String key : headerMap.keySet()) {
            String value = headerMap.get(key);
            if (key == null || value == null) {
                continue;
            }
            requestBuilder.addHeader(key, value);
        }
        // 创建Request
        return requestBuilder
                .url(url)
                .get()
                .build();
    }
}
