package com.qinshou.qinshoubox.conversation.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.TextView;

import com.qinshou.commonmodule.ContainerActivity;
import com.qinshou.commonmodule.rcvbaseadapter.baseholder.BaseViewHolder;
import com.qinshou.commonmodule.rcvbaseadapter.listener.IOnItemClickListener;
import com.qinshou.commonmodule.widget.TitleBar;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.base.QSFragment;
import com.qinshou.qinshoubox.conversation.bean.GroupChatDetailBean;
import com.qinshou.qinshoubox.conversation.enums.GroupChatMemberFunction;
import com.qinshou.qinshoubox.conversation.contract.IGroupChatSettingContract;
import com.qinshou.qinshoubox.conversation.presenter.GroupChatSettingPresenter;
import com.qinshou.qinshoubox.conversation.view.adapter.RcvGroupChatMemberAdapter;
import com.qinshou.qinshoubox.friend.bean.UserDetailBean;
import com.qinshou.qinshoubox.friend.view.fragment.UserDetailFragment;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.login.bean.UserBean;
import com.qinshou.qinshoubox.util.userstatusmanager.UserStatusManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Author：WangGuifa
 * Email：wangguifa@jeejio.com
 * Date：2019/8/6 19:05
 * Description：群聊设置页面
 */
public class GroupChatSettingFragment extends QSFragment<GroupChatSettingPresenter> implements IGroupChatSettingContract.IView {
    public static final String GROUP_CHAT_ID = "GroupChatId";
    private TitleBar mTitleBar;
    private RcvGroupChatMemberAdapter mRcvGroupChatMemberAdapter;
    /**
     * 群昵称
     */
    private TextView mTvNickname;
    private GroupChatDetailBean mGroupChatDetailBean;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_group_chat_setting;
    }

    @Override
    public void initView() {
        mTitleBar = findViewByID(R.id.title_bar);
        RecyclerView rcvGroupChatMember = findViewByID(R.id.rcv_group_chat_member);
        rcvGroupChatMember.setLayoutManager(new GridLayoutManager(getContext(), 5));
        rcvGroupChatMember.setAdapter(mRcvGroupChatMemberAdapter = new RcvGroupChatMemberAdapter(getContext()));
        mTvNickname = findViewByID(R.id.tv_nickname);
    }

    @Override
    public void setListener() {
        mRcvGroupChatMemberAdapter.setOnItemClickListener(new IOnItemClickListener() {
            @Override
            public void onItemClick(BaseViewHolder holder, Object itemData, int position) {
                if (itemData instanceof UserDetailBean) {
                    UserDetailFragment.start(getContext(), ((UserDetailBean) itemData).getId());
                } else if (itemData instanceof GroupChatMemberFunction) {
                    if (itemData == GroupChatMemberFunction.ADD_MEMBER) {
                        GroupChatAddMemberFragment.start(getContext(), mGroupChatDetailBean.getId());
                    } else if (itemData == GroupChatMemberFunction.DELETE_MEMBER) {
                        GroupChatDeleteMemberFragment.start(getContext(), mGroupChatDetailBean.getId());
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        String groupChatId = bundle.getString(GROUP_CHAT_ID);
        if (TextUtils.isEmpty(groupChatId)) {
            return;
        }
        getPresenter().getGroupChatDetail(groupChatId);
    }

    @Override
    public void getGroupChatDetailSuccess(GroupChatDetailBean groupChatDetailBean) {
        mGroupChatDetailBean = groupChatDetailBean;
        mTitleBar.setTitleText(getString(R.string.group_chat_setting_title, "" + groupChatDetailBean.getMemberList().size()));
        List list = new ArrayList(groupChatDetailBean.getMemberList());
        list.add(GroupChatMemberFunction.ADD_MEMBER);
        if (TextUtils.equals(groupChatDetailBean.getOwnerId(), UserStatusManager.SINGLETON.getUserBean().getId())) {
            list.add(GroupChatMemberFunction.DELETE_MEMBER);
        }
        mRcvGroupChatMemberAdapter.setDataList(list);
        mTvNickname.setText(TextUtils.isEmpty(groupChatDetailBean.getNickname())
                ? getString(R.string.group_chat_setting_tv_nickname_text)
                : groupChatDetailBean.getNickname());
    }

    @Override
    public void getGroupChatDetailFailure(Exception e) {

    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/9/3 15:09
     * Description:跳转到该界面
     *
     * @param context     上下文
     * @param groupChatId 群 Id
     */
    public static void start(Context context, String groupChatId) {
        Bundle bundle = new Bundle();
        bundle.putString(GROUP_CHAT_ID, groupChatId);
        context.startActivity(ContainerActivity.getJumpIntent(context
                , GroupChatSettingFragment.class
                , bundle));
    }
}
