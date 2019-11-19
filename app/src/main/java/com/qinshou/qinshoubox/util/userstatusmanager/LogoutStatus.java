package com.qinshou.qinshoubox.util.userstatusmanager;

import android.content.Context;
import android.content.Intent;

import com.qinshou.commonmodule.ContainerActivity;
import com.qinshou.commonmodule.util.SharedPreferencesHelper;
import com.qinshou.immodule.chat.ChatManager;
import com.qinshou.qinshoubox.MainActivity;
import com.qinshou.qinshoubox.constant.IConstant;
import com.qinshou.qinshoubox.me.bean.UserBean;
import com.qinshou.qinshoubox.me.ui.fragment.LoginOrRegisterFragment;

import org.greenrobot.eventbus.EventBus;


/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/9/19 10:11
 * Description:登出状态
 */
public class LogoutStatus implements IUserStatus {
    @Override
    public UserBean getUserBean() {
        return null;
    }

    @Override
    public void login(Context context, UserBean userBean) {
        // 存储最后一次登录成功的用户名
        SharedPreferencesHelper.SINGLETON.putString(IConstant.SP_KEY_LAST_LOGIN_USERNANE, userBean.getUsername());
        context.startActivity(new Intent(context, MainActivity.class));
        // 发送事件,更新 UI
        EventBus.getDefault().post(userBean);
        // 连接聊天服务
        ChatManager.SINGLETON.connect(userBean.getUsername());
        UserStatusManager.SINGLETON.setUserStatus(new LoginStatus(userBean));
    }

    @Override
    public void logout(Context context) {
    }

    @Override
    public void jump2DataSetting(Context context) {
        context.startActivity(ContainerActivity.getJumpIntent(context, LoginOrRegisterFragment.class));
    }
}
