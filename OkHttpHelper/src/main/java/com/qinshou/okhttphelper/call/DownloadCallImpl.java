package com.qinshou.okhttphelper.call;

import com.google.gson.Gson;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.okhttphelper.callback.FailureRunnable;
import com.qinshou.okhttphelper.callback.SuccessRunnable;
import com.qinshou.okhttphelper.util.CallbackUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/1/3 9:35
 * Description:下载请求实现类
 */
public class DownloadCallImpl extends AbsCall<File> {
    private File mFile;

    public DownloadCallImpl(OkHttpClient okHttpClient, Request request, File file) {
        super(okHttpClient.newCall(request));
        mFile = file;
    }

    public void enqueue(final Callback<File> callback) {
        getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                CallbackUtil.failureCallback(callback, e);
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) {

                if (response.body() == null) {
                    CallbackUtil.failureCallback(callback, new Exception(("Response's body is null")));
                    return;
                }
                if (mFile == null) {
                    CallbackUtil.failureCallback(callback, new NullPointerException("The file to save is null"));
                }
                InputStream inputStream = response.body().byteStream();
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(mFile, "rwd");
                    randomAccessFile.seek(randomAccessFile.length());
                    byte[] bytes = new byte[1024 * 1024];
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        randomAccessFile.write(bytes, 0, len);
                    }
                    CallbackUtil.successCallback(callback, mFile);
                } catch (IOException e) {
                    CallbackUtil.failureCallback(callback, e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public <O> TransformCallImpl<File, O> transform(ResponseTransformer<File, O> responseTransformer) {
        return new TransformCallImpl<>(getCall(), mFile.getClass(), responseTransformer);
    }
}