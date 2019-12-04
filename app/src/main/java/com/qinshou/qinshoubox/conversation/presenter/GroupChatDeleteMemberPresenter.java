package com.qinshou.qinshoubox.conversation.presenter;

import com.qinshou.commonmodule.base.AbsPresenter;
import com.qinshou.qinshoubox.conversation.contract.IGroupChatDeleteMemberContract;
import com.qinshou.qinshoubox.conversation.model.GroupChatDeleteMemberModel;
import com.qinshou.qinshoubox.conversation.view.fragment.GroupChatDeleteMemberFragment;
import com.qinshou.qinshoubox.login.bean.UserBean;
import com.qinshou.immodule.listener.QSCallback;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/12/02 17:41
 * Description:{@link GroupChatDeleteMemberFragment} 的 P 层
 */
public class GroupChatDeleteMemberPresenter extends AbsPresenter<IGroupChatDeleteMemberContract.IView, IGroupChatDeleteMemberContract.IModel> implements IGroupChatDeleteMemberContract.IPresenter {
    @Override
    public IGroupChatDeleteMemberContract.IModel initModel() {
        return new GroupChatDeleteMemberModel();
    }

    @Override
    public void getMemberList(int groupChatId) {
        getModel().getMemberList(groupChatId, new QSCallback<List<UserBean>>() {
            @Override
            public void onSuccess(List<UserBean> data) {
                if (!isViewAttached()) {
                    return;
                }
                getView().getMemberListSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                if (!isViewAttached()) {
                    return;
                }
                getView().getMemberListFailure(e);
            }
        });
    }

    @Override
    public void deleteMember(int groupChatId, List<Integer> deleteMemberIdList) {
        getModel().deleteMember(groupChatId, deleteMemberIdList, new QSCallback<Object>() {
            @Override
            public void onSuccess(Object data) {
                if (!isViewAttached()) {
                    return;
                }
                getView().deleteMemberSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                if (!isViewAttached()) {
                    return;
                }
                getView().deleteMemberFailure(e);
            }
        });
    }
}