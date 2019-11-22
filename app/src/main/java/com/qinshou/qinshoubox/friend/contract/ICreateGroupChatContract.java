package com.qinshou.qinshoubox.friend.contract;


import com.qinshou.commonmodule.base.IBaseModel;
import com.qinshou.commonmodule.base.IBaseView;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.friend.view.fragment.CreateGroupChatFragment;
import com.qinshou.qinshoubox.me.bean.GroupChatBean;
import com.qinshou.qinshoubox.me.bean.UserBean;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/08/19 17:27
 * Description:{@link CreateGroupChatFragment} 的契约类
 */
public interface ICreateGroupChatContract {
    interface IModel extends IBaseModel {
        void getFriendList(int fromUserId, Callback<List<UserBean>> callback);

        void createGroupChat(int ownerId, List<Integer> memberIdList, String nickname, String headImg, Callback<GroupChatBean> callback);
    }

    interface IView extends IBaseView {
        void getFriendListSuccess(List<UserBean> userBeanList);

        void getFriendListFailure(Exception e);

        void createGroupChatSuccess(GroupChatBean groupChatBean);

        void createGroupChatFailure(Exception e);
    }

    interface IPresenter {
        void getFriendList(int fromUserId);

        void createGroupChat(int ownerId, List<Integer> memberIdList, String nickname, String headImg);
    }
}