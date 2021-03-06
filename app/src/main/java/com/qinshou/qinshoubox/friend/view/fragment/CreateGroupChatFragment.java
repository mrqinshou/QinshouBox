package com.qinshou.qinshoubox.friend.view.fragment;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qinshou.commonmodule.rcvbaseadapter.baseholder.BaseViewHolder;
import com.qinshou.commonmodule.rcvbaseadapter.listener.IOnItemClickListener;
import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.base.QSFragment;
import com.qinshou.qinshoubox.friend.bean.GroupChatMemberForCreateBean;
import com.qinshou.qinshoubox.im.bean.UserDetailBean;
import com.qinshou.qinshoubox.friend.contract.ICreateGroupChatContract;
import com.qinshou.qinshoubox.friend.presenter.CreateGroupChatPresenter;
import com.qinshou.qinshoubox.friend.view.adapter.RcvGroupChatMemberForCreateAdapter;
import com.qinshou.qinshoubox.friend.view.adapter.RcvGroupChatMemberForCreateChooseAdapter;
import com.qinshou.qinshoubox.homepage.bean.EventBean;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.login.bean.UserBean;
import com.qinshou.qinshoubox.util.userstatusmanager.UserStatusManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/08/19 17:27
 * Description: 创建群聊界面
 */
public class CreateGroupChatFragment extends QSFragment<CreateGroupChatPresenter> implements ICreateGroupChatContract.IView {

    private RecyclerView mRcvGroupChatMemberChoose;
    /**
     * 群聊成员列表适配器
     */
    private RcvGroupChatMemberForCreateAdapter mRcvGroupChatMemberForCreateAdapter;
    /**
     * 已选择的群聊成员列表适配器
     */
    private RcvGroupChatMemberForCreateChooseAdapter mRcvGroupChatMemberForCreateChooseAdapter;
    /**
     * 完成按钮
     */
    private TextView mTvFinish;
    private EditText mEtNickname;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_create_group_chat;
    }

    @Override
    public void initView() {
        mTvFinish = findViewByID(R.id.tv_finish);
        mEtNickname = findViewByID(R.id.et_nickname);
        mRcvGroupChatMemberChoose = findViewByID(R.id.rcv_group_chat_member_choose);
        mRcvGroupChatMemberChoose.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRcvGroupChatMemberChoose.setAdapter(mRcvGroupChatMemberForCreateChooseAdapter = new RcvGroupChatMemberForCreateChooseAdapter(getContext()));

        RecyclerView rcvGroupChatMember = findViewByID(R.id.rcv_group_chat_member);
        rcvGroupChatMember.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvGroupChatMember.setAdapter(mRcvGroupChatMemberForCreateAdapter = new RcvGroupChatMemberForCreateAdapter(getContext()));
        // 去掉 item 修改时默认动画
        ((DefaultItemAnimator) rcvGroupChatMember.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void setListener() {
        findViewByID(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRcvGroupChatMemberForCreateAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mTvFinish.setEnabled(mRcvGroupChatMemberForCreateAdapter.getDataList().size() > 0);
            }
        });
        mTvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> memberIdList = new ArrayList<>();
                for (GroupChatMemberForCreateBean groupChatMemberForCreateBean : mRcvGroupChatMemberForCreateChooseAdapter.getDataList()) {
                    memberIdList.add(groupChatMemberForCreateBean.getId());
                }
                getPresenter().createGroupChat(memberIdList
                        , mEtNickname.getText().toString().trim()
                        , "");
            }
        });
        mRcvGroupChatMemberForCreateAdapter.setOnItemClickListener(new IOnItemClickListener<GroupChatMemberForCreateBean>() {
            @Override
            public void onItemClick(BaseViewHolder holder, GroupChatMemberForCreateBean itemData, int position) {
                // 修改群成员列表中的状态
                itemData.setChoose(!itemData.isChoose());
                mRcvGroupChatMemberForCreateAdapter.notifyItemChanged(position);

                if (itemData.isChoose()) {
                    // 已选择的群聊成员列表中增加一个
                    mRcvGroupChatMemberForCreateChooseAdapter.getDataList().add(itemData);
                    mRcvGroupChatMemberForCreateChooseAdapter.notifyItemInserted(mRcvGroupChatMemberForCreateChooseAdapter.getDataList().size() - 1);
                    // 已选择的群聊成员列表滚动到最后
                    mRcvGroupChatMemberChoose.scrollToPosition(mRcvGroupChatMemberForCreateChooseAdapter.getDataList().size() - 1);
                } else {
                    // 已选择的群聊成员列表中移除这一个
                    int index = mRcvGroupChatMemberForCreateChooseAdapter.getDataList().indexOf(itemData);
                    mRcvGroupChatMemberForCreateChooseAdapter.getDataList().remove(itemData);
                    mRcvGroupChatMemberForCreateChooseAdapter.notifyItemRemoved(index);
                }
            }
        });
        mRcvGroupChatMemberForCreateChooseAdapter.setOnItemClickListener(new IOnItemClickListener<GroupChatMemberForCreateBean>() {
            @Override
            public void onItemClick(BaseViewHolder holder, GroupChatMemberForCreateBean itemData, int position) {
                if (TextUtils.equals(itemData.getId(), UserStatusManager.SINGLETON.getUserBean().getId())) {
                    // 不能移除自己
                    return;
                }
                // 已选择的群聊成员列表中移除这一个
                mRcvGroupChatMemberForCreateChooseAdapter.getDataList().remove(itemData);
                mRcvGroupChatMemberForCreateChooseAdapter.notifyItemRemoved(position);

                // 修改群成员列表中的状态
                int index = mRcvGroupChatMemberForCreateAdapter.getDataList().indexOf(itemData);
                mRcvGroupChatMemberForCreateAdapter.getDataList().get(index).setChoose(false);
                mRcvGroupChatMemberForCreateAdapter.notifyItemChanged(index);
            }
        });
    }

    @Override
    public void initData() {
        getPresenter().getFriendList();
        GroupChatMemberForCreateBean groupChatMemberForCreateBean = new GroupChatMemberForCreateBean();
        UserBean userBean = UserStatusManager.SINGLETON.getUserBean();
        groupChatMemberForCreateBean.setId(userBean.getId());
        groupChatMemberForCreateBean.setHeadImgSmall(userBean.getHeadImgSmall());
        groupChatMemberForCreateBean.setNickname("我");
        mRcvGroupChatMemberForCreateChooseAdapter.getDataList().add(groupChatMemberForCreateBean);
        mRcvGroupChatMemberForCreateChooseAdapter.notifyItemInserted(mRcvGroupChatMemberForCreateChooseAdapter.getDataList().size() - 1);
    }

    @Override
    public void handleEvent(EventBean<Object> eventBean) {
    }

    @Override
    public void getFriendListSuccess(List<UserDetailBean> userDetailBeanList) {
        List<GroupChatMemberForCreateBean> groupChatMemberForCreateBeanList = new ArrayList<>();
        for (UserDetailBean userDetailBean : userDetailBeanList) {
            GroupChatMemberForCreateBean groupChatMemberForCreateBean = new GroupChatMemberForCreateBean();
            groupChatMemberForCreateBean.setId(userDetailBean.getId());
            groupChatMemberForCreateBean.setHeadImgSmall(userDetailBean.getHeadImgSmall());
            groupChatMemberForCreateBean.setRemark(userDetailBean.getRemark());
            groupChatMemberForCreateBean.setNickname(userDetailBean.getNickname());
            groupChatMemberForCreateBeanList.add(groupChatMemberForCreateBean);
        }
        mRcvGroupChatMemberForCreateAdapter.setDataList(groupChatMemberForCreateBeanList);
    }

    @Override
    public void getFriendListFailure(Exception e) {
        ShowLogUtil.logi("e--->" + e.getMessage());
    }

    @Override
    public void createGroupChatSuccess(GroupChatBean groupChatBean) {
        EventBus.getDefault().post(new EventBean<Object>(EventBean.Type.REFRESH_GROUP_CHAT_LIST, null));
        finish();
    }

    @Override
    public void createGroupChatFailure(Exception e) {
    }
}