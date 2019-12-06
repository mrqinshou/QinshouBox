package com.qinshou.qinshoubox.conversation.model;


import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.conversation.contract.IChatSettingContract;
import com.qinshou.qinshoubox.conversation.view.fragment.ChatSettingFragment;
import com.qinshou.qinshoubox.im.IMClient;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.im.bean.FriendBean;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/09/03 15:05
 * Description:{@link ChatSettingFragment} 的 M 层
 */
public class ChatSettingModel implements IChatSettingContract.IModel {

    @Override
    public void getFriend(String id, Callback<FriendBean> callback) {
        FriendBean friendBean = IMClient.SINGLETON.getFriendManager().getById(id);
        callback.onSuccess(friendBean);
    }

    @Override
    public void setTop(String toUserId, int top, Callback<Object> callback) {
        IMClient.SINGLETON.getFriendManager().setTop(toUserId, top, callback);
    }

    @Override
    public void setDoNotDisturb(String toUserId, int doNotDisturb, Callback<Object> callback) {
        IMClient.SINGLETON.getFriendManager().setDoNotDisturb(toUserId, doNotDisturb, callback);
    }

    @Override
    public void setBlackList(String toUserId, int blackList, Callback<Object> callback) {
        IMClient.SINGLETON.getFriendManager().setBlackList(toUserId, blackList, callback);
    }
}