package com.qinshou.qinshoubox.conversation.presenter;


import com.qinshou.commonmodule.base.AbsPresenter;
import com.qinshou.qinshoubox.conversation.contract.IConversationContract;
import com.qinshou.qinshoubox.conversation.model.ConversationModel;
import com.qinshou.qinshoubox.conversation.view.fragment.ConversationFragment;
import com.qinshou.qinshoubox.im.bean.ConversationDetailBean;
import com.qinshou.qinshoubox.im.listener.QSCallback;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/08/07 15:08
 * Description: {@link ConversationFragment} 的 P 层
 */
public class ConversationPresenter extends AbsPresenter<IConversationContract.IView, IConversationContract.IModel> implements IConversationContract.IPresenter {
    @Override
    public IConversationContract.IModel initModel() {
        return new ConversationModel();
    }

    @Override
    public void getConversationList() {
        getModel().getConversationList(new QSCallback<List<ConversationDetailBean>>() {
            @Override
            public void onSuccess(List<ConversationDetailBean> data) {
                if (!isViewAttached()) {
                    return;
                }
                getView().getConversationListSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isViewAttached()) {
                    return;
                }
                getView().getConversationListFailure(e);
            }
        });
    }
}