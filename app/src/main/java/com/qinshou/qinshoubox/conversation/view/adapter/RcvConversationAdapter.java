package com.qinshou.qinshoubox.conversation.view.adapter;

import android.content.Context;
import android.view.View;

import com.qinshou.commonmodule.rcvbaseadapter.RcvSingleBaseAdapter;
import com.qinshou.commonmodule.rcvbaseadapter.baseholder.BaseViewHolder;
import com.qinshou.commonmodule.rcvbaseadapter.listener.IOnItemClickListener;
import com.qinshou.imagemodule.util.ImageLoadUtil;
import com.qinshou.immodule.bean.ConversationBean;
import com.qinshou.immodule.enums.MessageType;
import com.qinshou.immodule.manager.ConversationManager;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.conversation.view.activity.ChatActivity;
import com.qinshou.qinshoubox.conversation.view.activity.GroupChatActivity;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/8/7 15:32
 * Description:会话列表适配器
 */
public class RcvConversationAdapter extends RcvSingleBaseAdapter<ConversationBean> {
    private String[] mWeekArray;

    public RcvConversationAdapter(Context context) {
        super(context, R.layout.item_rcv_conversation);
        mWeekArray = context.getResources().getStringArray(R.array.conversation_tv_last_msg_time_text);
        setOnItemClickListener(new IOnItemClickListener<ConversationBean>() {
            @Override
            public void onItemClick(BaseViewHolder holder, ConversationBean itemData, int position) {
                if (itemData.getType() == MessageType.CHAT.getValue()) {
                    ChatActivity.start(getContext(), itemData.getToUserId());
                } else if (itemData.getType() == MessageType.GROUP_CHAT.getValue()) {
                    GroupChatActivity.start(getContext(), itemData.getToUserId());
                }
                // 重置未读数
                ConversationManager.SINGLETON.resetUnreadCount(itemData.getId());
                itemData.setUnreadCount(0);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public void bindViewHolder(final BaseViewHolder baseViewHolder, final ConversationBean conversationBean, final int i) {
        ImageLoadUtil.SINGLETON.loadImage(getContext(), conversationBean.getHeadImgSmall(), baseViewHolder.getImageView(R.id.iv_head_img));
        baseViewHolder.setTvText(R.id.tv_title, conversationBean.getTitle());
        baseViewHolder.setTvText(R.id.tv_last_msg_content, conversationBean.getLastMsgContent());

        // 设置未读消息数量 大于99条：'···'; 没有未读消息&&设置未读：1; 有未读消息：未读消息（大于999 显示999）
        if (conversationBean.getUnreadCount() > 0) {
            baseViewHolder.setVisibility(R.id.tv_unread_count, View.VISIBLE);
            if (conversationBean.getUnreadCount() > 99) {
                baseViewHolder.setTvText(R.id.tv_unread_count, "···");
            } else {
                baseViewHolder.setTvText(R.id.tv_unread_count, "" + conversationBean.getUnreadCount());
            }
        } else {
            baseViewHolder.setVisibility(R.id.tv_unread_count, View.GONE);
        }
    }
}