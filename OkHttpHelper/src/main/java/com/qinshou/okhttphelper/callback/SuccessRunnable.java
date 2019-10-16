package com.qinshou.okhttphelper.callback;

public class SuccessRunnable<T> implements Runnable {
        private Callback<T> mRequestCallback;
        private T mData;

        public SuccessRunnable(Callback<T> requestCallback, T data) {
            mRequestCallback = requestCallback;
            mData = data;
        }

        @Override
        public void run() {
            mRequestCallback.onSuccess(mData);
        }
    }