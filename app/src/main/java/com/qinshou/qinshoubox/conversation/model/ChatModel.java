package com.qinshou.qinshoubox.conversation.model;


import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.qinshoubox.im.bean.ConversationBean;
import com.qinshou.qinshoubox.im.bean.MessageBean;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.im.manager.ChatManager;
import com.qinshou.qinshoubox.conversation.contract.IChatContract;
import com.qinshou.qinshoubox.conversation.view.activity.ChatActivity;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/06/20 10:26
 * Description:{@link ChatActivity} 的 M 层
 */
public class ChatModel implements IChatContract.IModel {
    @Override
    public void getMessageList(int type, int toUserId, int page, int pageSize, QSCallback<List<MessageBean>> qsCallback) {
        ShowLogUtil.logi("toUserId--->" + toUserId);
        ConversationBean conversationBean = ChatManager.SINGLETON.getConversationManager().getByTypeAndToUserId(type, toUserId);
        ShowLogUtil.logi("conversationBean--->" + conversationBean);
        if (conversationBean == null) {
            return;
        }
        List<MessageBean> messageBeanList = ChatManager.SINGLETON.getMessageManager().getList(conversationBean.getId(), page, pageSize);
        qsCallback.onSuccess(messageBeanList);
    }
}