package com.jeejio.networkmodule.call;


import android.os.Handler;
import android.os.Looper;

import com.jeejio.networkmodule.callback.Callback;

import okhttp3.Call;

/**
 * Author: MrQinshou
 * Email:cqflqinhao@126.com
 * Date: 2020/3/3 17:37
 * Description:请求的抽象类
 */
public abstract class AbsCall<T> {
    private Call mCall;
    Handler mHandler = new Handler(Looper.getMainLooper());

    public AbsCall(Call call) {
        mCall = call;
    }

    /**
     * Author: MrQinshou
     * Email:cqflqinhao@126.com
     * Date:2020-03-03 20:39
     * Description:发起异步请求
     */
    public abstract void enqueue(Callback<T> callback);

    /**
     * Author: MrQinshou
     * Email:cqflqinhao@126.com
     * Date:2020-03-03 20:39
     * Description:发起同步请求
     */
    public abstract T execute() throws Exception;

    /**
     * Author: MrQinshou
     * Email:cqflqinhao@126.com
     * Date:2020-03-03 20:39
     * Description:转换请求，可以对结果做一些中间的统一处理
     *
     * @param responseTransformer 转换者
     */
    public abstract <O> TransformCallImpl<T, O> transform(ResponseTransformer<T, O> responseTransformer);

    public Call getCall() {
        return mCall;
    }

    /**
     * Author: MrQinshou
     * Email:cqflqinhao@126.com
     * Date:2020-03-03 20:40
     * Description:取消请求
     */
    public void cancel() {
        if (mCall == null) {
            return;
        }
        mCall.cancel();
    }
}
