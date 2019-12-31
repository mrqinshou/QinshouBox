package com.qinshou.qinshoubox.conversation.view.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.qinshou.commonmodule.rcvbaseadapter.baseholder.BaseViewHolder;
import com.qinshou.commonmodule.util.MediaPlayerHelper;
import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.okhttphelper.callback.AbsDownloadCallback;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.conversation.bean.VoiceBean;
import com.qinshou.qinshoubox.im.IMClient;
import com.qinshou.qinshoubox.im.bean.MessageBean;
import com.qinshou.qinshoubox.im.enums.MessageContentType;
import com.qinshou.qinshoubox.util.userstatusmanager.UserStatusManager;

import java.io.File;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/7/12 14:32
 * Description:收到的消息,消息类型为语音
 */
public class RcvMessageAdapterFromMessageVoiceItemView extends AbsRcvMessageAdapterFromMessageItemView {
    private final RecyclerView mRecyclerView;
    private final RcvMessageAdapterToMessageVoiceItemView mRcvMessageAdapterToMessageVoiceItemView;

    public RcvMessageAdapterFromMessageVoiceItemView(Context context, RecyclerView rcvMessage, RcvMessageAdapterToMessageVoiceItemView rcvMessageAdapterToMessageVoiceItemView) {
        super(context, R.layout.item_rcv_message_from_message_voice);
        mRecyclerView = rcvMessage;
        mRcvMessageAdapterToMessageVoiceItemView = rcvMessageAdapterToMessageVoiceItemView;
    }

    @Override
    public boolean isForViewType(MessageBean item, int position) {
        // 消息来源与当前登录的用户名不同,则是收到的消息
        return !TextUtils.equals(item.getFromUserId(), UserStatusManager.SINGLETON.getUserBean().getId())
                && item.getContentType() == MessageContentType.VOICE.getValue();
    }

    @Override
    public void bindViewHolder(final BaseViewHolder baseViewHolder, final MessageBean messageBean, final int i) {
        super.bindViewHolder(baseViewHolder, messageBean, i);
        // 消息内容
        VoiceBean voiceBean = new Gson().fromJson(messageBean.getExtend(), VoiceBean.class);
        StringBuilder stringBuilder = new StringBuilder();
        int time = (int) (voiceBean.getTime() / 1000);
        for (int a = 0; a < time; a++) {
            stringBuilder.append(" ");
        }
        baseViewHolder.setTvText(R.id.tv_placeholder, stringBuilder);
        baseViewHolder.setTvText(R.id.tv_voice_time, time + "\"");
//        // 该条语音消息是否已读 0为未读  1为已读
//        if (voiceBean.getUnread() == 0) {
//            baseViewHolder.setVisibility(R.id.iv_voice_unread, View.VISIBLE);
//        } else if (voiceBean.getUnread() == 1) {
//            baseViewHolder.setVisibility(R.id.iv_voice_unread, View.GONE);
//        }

        // 因为 item 的复用,虽然数据不会错乱,但是如果修改了一个 item 的控件
        // 那么复用该 ViewHolder 的 item 的状态和背景也会一起变化,所以需要设置当前条目的语音播放动画控件
        // 如果当前条目是正在播放的,则播放动画,否则设置静态图片
        if (messageBean == mRcvMessageAdapterToMessageVoiceItemView.getCurrentPlayMessageBean()) {
            updateVoiceUI(messageBean);
        } else {
            baseViewHolder.setIvImage(R.id.iv_voice_from, R.drawable.chat_iv_voice_from_3);
        }
        // 点击播放
        baseViewHolder.setOnClickListener(R.id.ll_content, new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //                 | --- 点击的 item 位置就是正在播放的 item 的位置 --- 停止播放
                // 当时是播放状态  |
                //                 | --- 点击的 item 位置不是正在播放的 item 的位置 --- 先停止上一个,再播放当前点击的
                // 当时是未播放状态,直接播放
                if (MediaPlayerHelper.SINGLETON.isPlaying()) {
                    if (messageBean == mRcvMessageAdapterToMessageVoiceItemView.getCurrentPlayMessageBean()) {
                        stopPlay(messageBean);
                    } else {
                        stopPlay(mRcvMessageAdapterToMessageVoiceItemView.getCurrentPlayMessageBean());
                        startPlay(messageBean, baseViewHolder);
                    }
                } else {
                    startPlay(messageBean, baseViewHolder);
                }
            }
        });
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/12/19 15:38
     * Description:开始播放语音
     *
     * @param messageBean    待播放的那一条 message
     * @param baseViewHolder 条目管理器
     */
    private void startPlay(final MessageBean messageBean, BaseViewHolder baseViewHolder) {
        VoiceBean voiceBean = new Gson().fromJson(messageBean.getExtend(), VoiceBean.class);
//        if (voiceBean.getUnread() != 1) {
//            voiceBean.setUnread(1);
//            baseViewHolder.setVisibility(R.id.iv_voice_unread, View.GONE);
//            messageBean.setExtend(new Gson().toJson(voiceBean));
//            JMClient.SINGLETON.getChatManager().updateMessage(messageBean);
//        }
        final MediaPlayerHelper.IOnMediaPlayerListener onMediaPlayerListener = new MediaPlayerHelper.IOnMediaPlayerListener() {
            @Override
            public void onStart(int totalProgress) {
                // 先赋值
                mRcvMessageAdapterToMessageVoiceItemView.setCurrentPlayMessageBean(messageBean);
                // 再更新 UI
                updateVoiceUI(messageBean);
            }

            @Override
            public void onError() {
                stopPlay(messageBean);
            }

            @Override
            public void onComplete() {
                stopPlay(messageBean);
            }

            @Override
            public void onProgress(int progress, int totalProgress, int currentTimeMinute, final int currentTimeSecond, int totalTimeMinute, int totalTimeSecond) {
            }

            @Override
            public void onBufferingUpdate(int percent) {
            }
        };
        File file = new File(new Gson().fromJson(messageBean.getExtend(), VoiceBean.class).getPath());
        if (file.exists()) {
            MediaPlayerHelper.SINGLETON.playMusic(file.getAbsolutePath(), onMediaPlayerListener);
            return;
        }
        String fileName = voiceBean.getUrl().substring(voiceBean.getUrl().lastIndexOf("/" + "/".length()));
        ShowLogUtil.logi("voiceBean--->" + voiceBean);
        file = new File(getContext().getCacheDir()
                + File.separator
                + "Voice"
                + File.separator
                + fileName);
        final File finalFile = file;
        IMClient.SINGLETON.download(voiceBean.getUrl(), file, new AbsDownloadCallback() {
            @Override
            public void onStart(long length) {
                ShowLogUtil.logi("onStart: length--->" + length);
            }

            @Override
            public void onProgress(int progress) {
                ShowLogUtil.logi("onProgress: progress--->" + progress);
            }

            @Override
            public void onSuccess() {
                ShowLogUtil.logi("onSuccess");
                MediaPlayerHelper.SINGLETON.playMusic(finalFile.getAbsolutePath(), onMediaPlayerListener);
            }

            @Override
            public void onFailure(Exception e) {
                ShowLogUtil.logi("onFailure: e--->" + e.getMessage());
            }
        });
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/12/19 15:38
     * Description:停止播放语音
     *
     * @param messageBean 待停止的那一条 message
     */
    private void stopPlay(MessageBean messageBean) {
        MediaPlayerHelper.SINGLETON.stop();
        mRcvMessageAdapterToMessageVoiceItemView.setCurrentPlayMessageBean(null);
        updateVoiceUI(messageBean);
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/12/19 15:39
     * Description:更新 UI
     *
     * @param messageBean 待更新的那一条 message
     */
    private void updateVoiceUI(MessageBean messageBean) {
        // 获取目标 messageBean 在数据集合中的位置
        int position = getRcvBaseAdapter().getDataList().indexOf(messageBean);
        // 找到对应 ViewHolder
        RecyclerView.ViewHolder viewHolderForAdapterPosition = mRecyclerView.findViewHolderForAdapterPosition(position);
        if (viewHolderForAdapterPosition == null) {
            return;
        }
        if (!(viewHolderForAdapterPosition instanceof BaseViewHolder)) {
            return;
        }
        BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolderForAdapterPosition;
        final ImageView ivVoiceFrom = baseViewHolder.getImageView(R.id.iv_voice_from);
        if (ivVoiceFrom == null) {
            return;
        }
        // 如果需要更新 UI 的条目就是当前正在播放的,则播放动画,否则修改为静态图片
        if (messageBean == mRcvMessageAdapterToMessageVoiceItemView.getCurrentPlayMessageBean()) {
            AnimationDrawable animationDrawable = new AnimationDrawable();
            animationDrawable.addFrame(getContext().getDrawable(R.drawable.chat_iv_voice_from_1), 300);
            animationDrawable.addFrame(getContext().getDrawable(R.drawable.chat_iv_voice_from_2), 300);
            animationDrawable.addFrame(getContext().getDrawable(R.drawable.chat_iv_voice_from_3), 300);
            animationDrawable.setOneShot(false);
            ivVoiceFrom.setImageDrawable(animationDrawable);
            animationDrawable.start();
        } else {
            ivVoiceFrom.setImageResource(R.drawable.chat_iv_voice_from_3);
        }
    }
}
