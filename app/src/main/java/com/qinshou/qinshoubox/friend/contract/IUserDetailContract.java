package com.qinshou.qinshoubox.friend.contract;


import com.qinshou.commonmodule.base.IBaseModel;
import com.qinshou.commonmodule.base.IBaseView;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.friend.view.fragment.UserDetailFragment;
import com.qinshou.qinshoubox.login.bean.UserBean;
import com.qinshou.immodule.listener.QSCallback;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/11 20:08
 * Description:{@link UserDetailFragment} 的契约类
 */
public interface IUserDetailContract {
    interface IModel extends IBaseModel {
        void getUserDetail(String keyword, QSCallback<UserBean> qsCallback);

        void agreeAddFriend(int fromUserId, int toUserId, String remark, Callback<Object> callback);

        void deleteFriend(int toUserId, Callback<Object> callback);

        void setRemark(int toUserId, String remark, QSCallback<Object> qsCallback);
    }

    interface IView extends IBaseView {
        void getUserDetailSuccess(UserBean userBean);

        void getUserDetailFailure(Exception e);

        void agreeAddFriendSuccess();

        void agreeAddFriendFailure(Exception e);

        void deleteFriendSuccess();

        void deleteFriendFailure(Exception e);

        void setRemarkSuccess();

        void setRemarkFailure(Exception e);
    }

    interface IPresenter {
        void getUserDetail(String keyword);

        void agreeAddFriend(int fromUserId, int toUserId, String remark);

        void deleteFriend(int toUserId);

        void setRemark(int toUserId, String remark);
    }
}