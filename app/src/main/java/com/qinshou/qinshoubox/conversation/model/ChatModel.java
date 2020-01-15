package com.qinshou.qinshoubox.conversation.model;


import com.qinshou.qinshoubox.conversation.bean.UploadImgResultBean;
import com.qinshou.qinshoubox.conversation.bean.UploadResultBean;
import com.qinshou.qinshoubox.conversation.bean.UploadVoiceResultBean;
import com.qinshou.qinshoubox.im.IMClient;
import com.qinshou.qinshoubox.im.bean.MessageBean;
import com.qinshou.qinshoubox.im.enums.MessageType;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.conversation.contract.IChatContract;
import com.qinshou.qinshoubox.conversation.view.activity.ChatActivity;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/06/20 10:26
 * Description:{@link ChatActivity} 的 M 层
 */
public class ChatModel implements IChatContract.IModel {
    @Override
    public void getMessageList(String toUserId, int page, int pageSize, QSCallback<List<MessageBean>> qsCallback) {
        List<MessageBean> messageBeanList = IMClient.SINGLETON.getMessageManager().getList(MessageType.CHAT.getValue(), toUserId, page, pageSize);
        // 翻转顺序
        Collections.reverse(messageBeanList);
        qsCallback.onSuccess(messageBeanList);
    }

    @Override
    public void uploadVoice(long time, File voice, QSCallback<UploadVoiceResultBean> qsCallback) {
        IMClient.SINGLETON.uploadVoice(time, voice,qsCallback);
    }

    @Override
    public void uploadImg(File img, QSCallback<UploadImgResultBean> qsCallback) {
        IMClient.SINGLETON.uploadImg(img, qsCallback);
    }
}