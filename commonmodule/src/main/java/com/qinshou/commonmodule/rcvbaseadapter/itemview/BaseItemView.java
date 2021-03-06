package com.qinshou.commonmodule.rcvbaseadapter.itemview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinshou.commonmodule.rcvbaseadapter.RcvBaseAdapter;
import com.qinshou.commonmodule.rcvbaseadapter.baseholder.BaseViewHolder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Description:RecyclerView 单数据对多类型布局，多数据对多类型布局的适配器的 BaseItemView
 * Created by 禽兽先生
 * Created on 2018/4/9
 */

public abstract class BaseItemView<T> {
    private Context mContext;
    private int layoutId;
    private RcvBaseAdapter<T> mRcvBaseAdapter;

    public BaseItemView(Context context, int layoutId) {
        this.mContext = context;
        this.layoutId = layoutId;
    }

    public Context getContext() {
        return mContext;
    }

    public BaseViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return new BaseViewHolder(mContext, itemView);
    }

    /**
     * Description:     子类可以覆蓋此方法决定引用该子布局的时机
     * Date:2018/8/6
     *
     * @param item     该position对应的数据
     * @param position position
     * @return 是否属于子布局
     */
    public boolean isForViewType(T item, int position) {
        if (item == null) {
            return false;
        }
        Type type = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class clazz = (Class) parameterizedType.getActualTypeArguments()[0];
        return item.getClass() == clazz;
    }

    /**
     * Description:绑定 UI 和数据
     * Date:2018/8/6
     */
    public abstract void bindViewHolder(BaseViewHolder holder, T itemData, int position);

    public RcvBaseAdapter<T> getRcvBaseAdapter() {
        return mRcvBaseAdapter;
    }

    public void setRcvBaseAdapter(RcvBaseAdapter<T> rcvBaseAdapter) {
        mRcvBaseAdapter = rcvBaseAdapter;
    }
}