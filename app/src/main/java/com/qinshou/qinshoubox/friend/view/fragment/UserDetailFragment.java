package com.qinshou.qinshoubox.friend.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qinshou.commonmodule.ContainerActivity;
import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.imagemodule.util.ImageLoadUtil;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.base.QSFragment;
import com.qinshou.qinshoubox.friend.contract.IUserDetailContract;
import com.qinshou.qinshoubox.friend.presenter.UserDetailPresenter;
import com.qinshou.qinshoubox.me.bean.UserBean;
import com.qinshou.qinshoubox.util.userstatusmanager.UserStatusManager;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/11 20:08
 * Description:用户详情界面
 */
public class UserDetailFragment extends QSFragment<UserDetailPresenter> implements IUserDetailContract.IView {

    private static final String KEYWORD = "Keyword";
    private ImageView mIvHeadImg;
    private TextView mTvRemark;
    private ImageView mIvGender;
    private TextView mTvUsername;
    private TextView mTvNickname;
    private LinearLayout mLlAdditionalMessage;
    private TextView mTvAdditionalMessage;
    private TextView mTvRemark2;
    private TextView mTvPhoneNumber;
    private TextView mTvEmail;
    private TextView mTvSignature;
    private TextView mTvSource;
    private Button mBtnAddFriend;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user_detail;
    }

    @Override
    public void initView() {
        mIvHeadImg = findViewByID(R.id.iv_head_img);
        mTvRemark = findViewByID(R.id.tv_remark);
        mIvGender = findViewByID(R.id.iv_gender);
        mTvUsername = findViewByID(R.id.tv_username);
        mTvNickname = findViewByID(R.id.tv_nickname);
        mLlAdditionalMessage = findViewByID(R.id.ll_additional_message);
        mTvAdditionalMessage = findViewByID(R.id.tv_additional_message);
        mTvRemark2 = findViewByID(R.id.tv_remark_2);
        mTvPhoneNumber = findViewByID(R.id.tv_phone_number);
        mTvEmail = findViewByID(R.id.tv_email);
        mTvSignature = findViewByID(R.id.tv_signature);
        mTvSource = findViewByID(R.id.tv_source);
        mBtnAddFriend = findViewByID(R.id.btn_add_friend);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();

        if (bundle == null) {
            return;
        }
        String keyword = bundle.getString(KEYWORD);
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        getPresenter().getUserDetail(UserStatusManager.SINGLETON.getUserBean().getId(), keyword);
    }

    @Override
    public void showFriendUI(UserBean userBean) {
        setData(userBean);
        mBtnAddFriend.setText("发送消息");
    }

    @Override
    public void showNotFriendUI(UserBean userBean) {
        setData(userBean);
        mBtnAddFriend.setText("添加到通讯录");
    }

    @Override
    public void showWaitAcceptUI(UserBean userBean) {
        setData(userBean);
        mLlAdditionalMessage.setVisibility(View.VISIBLE);
        mBtnAddFriend.setText("接受请求");
    }

    private void setData(UserBean userBean) {
        // 头像
        ImageLoadUtil.SINGLETON.loadImage(getContext(), userBean.getHeadImgSmall(), mIvHeadImg);
        // 备注
        mTvRemark.setText(TextUtils.isEmpty(userBean.getRemark()) ? userBean.getNickname() : userBean.getRemark());
        // 0 为其他,1 为女,2 为男
        if (userBean.getGender() == 1) {
            mIvGender.setImageResource(R.drawable.user_detail_iv_gender_src);
        } else {
            mIvGender.setImageResource(R.drawable.user_detail_iv_gender_src_2);
        }
        // 账号
        mTvUsername.setText(userBean.getUsername());
        // 昵称
        mTvNickname.setText(userBean.getNickname());
        // 附加消息
        mTvAdditionalMessage.setText(userBean.getAdditionalMessage());
        // 功能栏的备注
        mTvRemark2.setText(TextUtils.isEmpty(userBean.getRemark()) ? "" : userBean.getRemark());
        // 手机号
        mTvPhoneNumber.setText(userBean.getPhoneNumber());
        // 邮箱
        mTvEmail.setText(userBean.getEmail());
        // 个性签名
        mTvSignature.setText(userBean.getSignature());
        // 用户来源
        String source = "";
        if (userBean.getFriendStatus() == 3) {
            if (userBean.getSource() == 1) {
                source = "通过用户名添加";
            } else if (userBean.getSource() == 2) {
                source = "通过手机号添加";
            } else if (userBean.getSource() == 3) {
                source = "通过邮箱添加";
            } else if (userBean.getSource() == 4) {
                source = "通过扫一扫添加";
            } else if (userBean.getSource() == 5) {
                source = "通过群聊添加";
            } else if (userBean.getSource() == -1) {
                source = "对方通过用户名添加";
            } else if (userBean.getSource() == -2) {
                source = "对方通过手机号添加";
            } else if (userBean.getSource() == -3) {
                source = "对方通过邮箱添加";
            } else if (userBean.getSource() == -4) {
                source = "对方通过扫一扫添加";
            } else if (userBean.getSource() == -5) {
                source = "对方通过群聊添加";
            }
        } else {
            if (userBean.getSource() == 1) {
                source = "通过用户名搜索";
            } else if (userBean.getSource() == 2) {
                source = "通过手机号搜索";
            } else if (userBean.getSource() == 3) {
                source = "通过邮箱搜索";
            }
        }
        mTvSource.setText(source);
    }

    @Override
    public void getUserDetailFailure(Exception e) {

    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/11/12 9:20
     * Description:跳转到该界面
     *
     * @param context 上下文
     * @param keyword 用户名,可以是系统账号/手机号/邮箱
     */
    public static void start(Context context, String keyword) {
        Bundle bundle = new Bundle();
        bundle.putString(KEYWORD, keyword);
        context.startActivity(ContainerActivity.getJumpIntent(context, UserDetailFragment.class, bundle));
    }
}