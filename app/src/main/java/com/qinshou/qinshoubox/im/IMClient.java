package com.qinshou.qinshoubox.im;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.okhttphelper.callback.AbsDownloadCallback;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.conversation.bean.UploadImgResultBean;
import com.qinshou.qinshoubox.conversation.bean.UploadVoiceResultBean;
import com.qinshou.qinshoubox.im.bean.ConversationBean;
import com.qinshou.qinshoubox.im.bean.ConversationMessageRelBean;
import com.qinshou.qinshoubox.im.bean.FriendStatusBean;
import com.qinshou.qinshoubox.im.bean.GroupChatStatusBean;
import com.qinshou.qinshoubox.im.bean.MessageBean;
import com.qinshou.qinshoubox.im.bean.ServerReceiptBean;
import com.qinshou.qinshoubox.im.db.DatabaseHelper;
import com.qinshou.qinshoubox.im.enums.FriendStatus;
import com.qinshou.qinshoubox.im.enums.GroupChatStatus;
import com.qinshou.qinshoubox.im.enums.MessageStatus;
import com.qinshou.qinshoubox.im.enums.MessageType;
import com.qinshou.qinshoubox.im.enums.StatusCode;
import com.qinshou.qinshoubox.im.listener.IOnConnectListener;
import com.qinshou.qinshoubox.im.listener.IOnFriendStatusListener;
import com.qinshou.qinshoubox.im.listener.IOnGroupChatStatusListener;
import com.qinshou.qinshoubox.im.listener.IOnMessageListener;
import com.qinshou.qinshoubox.im.listener.IOnSendMessageListener;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.im.manager.ConversationManager;
import com.qinshou.qinshoubox.im.manager.FriendManager;
import com.qinshou.qinshoubox.im.manager.GroupChatManager;
import com.qinshou.qinshoubox.im.manager.MessageManager;
import com.qinshou.qinshoubox.network.OkHttpHelperForQSBoxCommonApi;
import com.qinshou.qinshoubox.network.OkHttpHelperForQSBoxOfflineApi;
import com.qinshou.qinshoubox.transformer.QSApiTransformer;

import java.io.EOFException;
import java.io.File;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Author: QinHao
 * Email:cqflqinhao@126.com
 * Date: 2019/12/05 10:43
 * Description:IM 服务
 */
public enum IMClient {
    SINGLETON;

    private static final String TAG = "IMClient";
    private final int TIME_OUT = 10 * 1000;
    //    private static final String URL = "ws://www.mrqinshou.com:10086/websocket";
    private static final String URL = "ws://172.16.60.231:10086/websocket";
    //    private static final String URL = "ws://192.168.1.109:10086/websocket";
    private Context mContext;
    private WebSocket mWebSocket;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<IOnConnectListener> mOnConnectListenerList = new ArrayList<>();
    private List<IOnMessageListener> mOnMessageListenerList = new ArrayList<>();
    private List<IOnFriendStatusListener> mOnFriendStatusListenerList = new ArrayList<>();
    private List<IOnGroupChatStatusListener> mOnGroupChatStatusListenerList = new ArrayList<>();
    private List<IOnSendMessageListener> mOnSendMessageListenerList = new ArrayList<>();
    private String mUserId;
    private GroupChatManager mGroupChatManager;
    private FriendManager mFriendManager;
    private MessageManager mMessageManager;
    private ConversationManager mConversationManager;
    //    private Map<String, MessageBean> mAckMessageMap = new HashMap<>();
//    private Map<String, Timer> mRetrySendTimerMap = new HashMap<>();

    /**
     * 发送心跳间隔
     */
    private final long HEART_BEAT_INTERVAL = 60 * 1000;
    /**
     * 发送心跳任务的线程池
     */
    private ScheduledExecutorService mHeartBeatScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Reconnect Thread");
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );
    /**
     * 心跳任务的线程
     */
    private ScheduledFuture<?> mHeartBeatScheduledFuture;
    /**
     * 心跳任务
     */
    private Runnable mHeartBeatRunnable = new Runnable() {

        @Override
        public void run() {
            if (mWebSocket == null) {
                return;
            }
            Log.i(TAG, "发送心跳");
            mWebSocket.send(new Gson().toJson(MessageBean.createHeartBeatMessage(mUserId)));
            mHeartBeatScheduledFuture = mHeartBeatScheduledExecutorService.schedule(mHeartBeatRunnable, HEART_BEAT_INTERVAL, TimeUnit.MILLISECONDS);
        }
    };

    /**
     * 重连任务的线程池
     */
    private ScheduledExecutorService mReconnectScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Reconnect Thread");
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );
    /**
     * 重连任务的线程
     */
    private ScheduledFuture<?> mReconnectScheduledFuture;
    /**
     * 最大重连次数
     */
    private final int MAX_RECONNECT_COUNT = 10000;
    /**
     * 重连次数计数器
     */
    private int mReconnectCount;
    /**
     * 重连任务
     */
    private Runnable mReconnectRunnable = new Runnable() {

        @Override
        public void run() {
            if (mReconnectCount >= MAX_RECONNECT_COUNT) {
                Log.i(TAG, "重连了 " + MAX_RECONNECT_COUNT + " 次都没有连上,不再重连了");
                return;
            }
            mReconnectCount++;
            Log.i(TAG, "即将开始第 " + mReconnectCount + " 次重连");
            connect(mUserId);
        }
    };

    IMClient() {
    }

    private void connectSuccess(WebSocket webSocket, String userId) {
        mWebSocket = webSocket;
        mUserId = userId;
        // 重置重连尝试次数
        mReconnectCount = 0;
        // 移除重连任务
        if (mReconnectScheduledFuture != null) {
            mReconnectScheduledFuture.cancel(true);
            mReconnectScheduledFuture = null;
        }
        // 开启心跳任务
        mHeartBeatScheduledFuture = mHeartBeatScheduledExecutorService.schedule(mHeartBeatRunnable, HEART_BEAT_INTERVAL, TimeUnit.MILLISECONDS);
        // 初始化数据库
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext, userId);
        // 创建好友管理者
        mFriendManager = new FriendManager(databaseHelper, userId);
        // 创建群组管理者
        mGroupChatManager = new GroupChatManager(databaseHelper, userId);
        // 创建消息管理者
        mMessageManager = new MessageManager(databaseHelper, userId);
        // 创建会话管理者
        mConversationManager = new ConversationManager(databaseHelper, userId);
        // 拉取离线消息
        OkHttpHelperForQSBoxOfflineApi.SINGLETON.getOfflineMessageList(userId)
                .transform(new QSApiTransformer<List<MessageBean>>())
                .enqueue(new Callback<List<MessageBean>>() {
                    @Override
                    public void onSuccess(List<MessageBean> data) {
                        for (MessageBean messageBean : data) {
                            handleMessage(messageBean);
                        }
                        // 通知后台删除离线消息
                        OkHttpHelperForQSBoxOfflineApi.SINGLETON.deleteOfflineMessageList(mUserId)
                                .enqueue(null);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    /**
     * Author: QinHao
     * Email:qinhao@jeejio.com
     * Date:2019/11/26 9:00
     * Description:处理消息
     *
     * @param messageBean 消息实体类
     */
    public void handleMessage(final MessageBean messageBean) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                messageBean.setReceiveTimestamp(System.currentTimeMillis());
                messageBean.setStatus(MessageStatus.RECEIVED.getValue());

                if (messageBean.getType() == MessageType.CHAT.getValue()
                        || messageBean.getType() == MessageType.GROUP_CHAT.getValue()) {
//                    boolean exist = mMessageManager.getByFromUserIdAndToUserIdAndTypeAndSendTimestamp(messageBean.getFromUserId()
//                            , messageBean.getToUserId()
//                            , messageBean.getType()
//                            , messageBean.getSendTimestamp()) != null;
                    // 去重
//                    if (exist) {
//                        return;
//                    }
                    insert2Database(messageBean, false);
                    for (IOnMessageListener onMessageListener : mOnMessageListenerList) {
                        onMessageListener.onMessage(messageBean);
                    }
//                    // 创建一个唯一索引作为 ACK Key,将其 json 串做 Base64 加密,得到的加密字符串作为 key
//                    AckKeyBean ackKeyBean = new AckKeyBean(mUserId
//                            , messageBean.getFromUserId()
//                            , messageBean.getToUserId()
//                            , messageBean.getType()
//                            , messageBean.getSendTimestamp());
//                    String key = CodeUtil.encode(new Gson().toJson(ackKeyBean));
//                    Map<String, String> map = new HashMap<>();
//                    map.put("key", key);
//                    map.put("status", "" + MessageStatus.RECEIVED.getValue());
//                    // 发送回执消息,通知客户端本条消息已成功发送
//                    MessageBean clientReceiptMessage = MessageBean.createClientReceiptMessage(mUserId);
//                    clientReceiptMessage.setExtend(new Gson().toJson(map));
//                    mWebSocket.send(new Gson().toJson(clientReceiptMessage));

                } else if (messageBean.getType() == MessageType.FRIEND_STATUS.getValue()) {
                    handleFriendStatusMessage(messageBean);
                } else if (messageBean.getType() == MessageType.GROUP_CHAT_STATUS.getValue()) {
                    handleGroupChatStatusMessage(messageBean);
                } else if (messageBean.getType() == MessageType.SERVER_RECEIPT.getValue()) {
//                    Type type = new TypeToken<Map<String, String>>() {
//                    }.getType();
//                    Map<String, String> map = new Gson().fromJson(messageBean.getExtend(), type);
//                    String key = map.get("key");
//                    int status = Integer.valueOf(map.get("status"));
//                    MessageBean ackMessageBean = mAckMessageMap.remove(key);
//                    if (ackMessageBean != null) {
//                        mMessageManager.setStatus(status, ackMessageBean.getFromUserId(), ackMessageBean.getToUserId(), ackMessageBean.getSendTimestamp());
//                        // 取消定时任务
//                        Timer timer = mRetrySendTimerMap.remove(key);
//                        if (timer != null) {
//                            timer.cancel();
//                        }
//                    }
                    ServerReceiptBean serverReceiptBean = new Gson().fromJson(messageBean.getExtend(), ServerReceiptBean.class);
                    MessageBean select = mMessageManager.selectByPid(serverReceiptBean.getPid());
                    if (serverReceiptBean.getStatus() == MessageStatus.FAILURE.getValue()) {
                        if (select != null) {
                            select.setId(serverReceiptBean.getId());
                            select.setStatus(MessageStatus.FAILURE.getValue());
                            mMessageManager.insertOrUpdate(select);
                        }
                        for (IOnSendMessageListener onSendMessageListener : mOnSendMessageListenerList) {
                            onSendMessageListener.onSendFailure(select, serverReceiptBean.getFailureCode());
                        }
                    } else {
                        if (select != null) {
                            select.setId(serverReceiptBean.getId());
                            select.setStatus(MessageStatus.SENDED.getValue());
                            mMessageManager.insertOrUpdate(select);
                        }
                        for (IOnSendMessageListener onSendMessageListener : mOnSendMessageListenerList) {
                            onSendMessageListener.onSendSuccess(select);
                        }
                    }
                }
            }
        });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/7 14:13
     * Description:处理好友状态的消息
     *
     * @param messageBean 消息实体类
     */
    private void handleFriendStatusMessage(MessageBean messageBean) {
        FriendStatusBean friendStatusBean = new Gson().fromJson(messageBean.getExtend(), FriendStatusBean.class);
        if (friendStatusBean.getStatus() == FriendStatus.ADD.getValue()) {
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.add(friendStatusBean.getFromUserId()
                        , friendStatusBean.getAdditionalMsg()
                        , friendStatusBean.isNewFriend());
            }
        } else if (friendStatusBean.getStatus() == FriendStatus.AGREE_ADD.getValue()) {
            Map<String, Object> extend = new HashMap<>();
            extend.put("status", FriendStatus.AGREE_ADD.getValue());
            // 创建已经是好友的提示信息的系统消息
            MessageBean m = MessageBean.createChatSystemMessage(friendStatusBean.getFromUserId()
                    , mUserId
                    , extend);
            handleMessage(m);
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.agreeAdd(friendStatusBean.getFromUserId());
            }
        } else if (friendStatusBean.getStatus() == FriendStatus.REFUSE_ADD.getValue()) {
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.refuseAdd(friendStatusBean.getFromUserId());
            }
        } else if (friendStatusBean.getStatus() == FriendStatus.DELETE.getValue()) {
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.delete(friendStatusBean.getFromUserId());
            }
        } else if (friendStatusBean.getStatus() == FriendStatus.ONLINE.getValue()) {
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.online(friendStatusBean.getFromUserId());
            }
        } else if (friendStatusBean.getStatus() == FriendStatus.OFFLINE.getValue()) {
            for (IOnFriendStatusListener onFriendStatusListener : mOnFriendStatusListenerList) {
                onFriendStatusListener.offline(friendStatusBean.getFromUserId());
            }
        }
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/7 14:14
     * Description:处理群相关状态的消息
     *
     * @param messageBean 消息实体类
     */
    private void handleGroupChatStatusMessage(MessageBean messageBean) {
        GroupChatStatusBean groupChatStatusBean = new Gson().fromJson(messageBean.getExtend(), GroupChatStatusBean.class);
        ShowLogUtil.logi("groupChatStatusBean--->" + groupChatStatusBean);
        Map<String, Object> extend = new HashMap<>();
        extend.put("status", groupChatStatusBean.getStatus());
        extend.put("groupChatId", groupChatStatusBean.getGroupChatId());
        extend.put("fromUser", groupChatStatusBean.getFromUser());
        extend.put("toUserList", groupChatStatusBean.getToUserList());
        // 创建群聊提示信息的系统消息
        MessageBean m = MessageBean.createChatSystemMessage(groupChatStatusBean.getFromUser().getId()
                , groupChatStatusBean.getGroupChatId()
                , extend);
        m.setType(MessageType.GROUP_CHAT.getValue());
        handleMessage(m);
        if (groupChatStatusBean.getStatus() == GroupChatStatus.ADD.getValue()) {
            for (IOnGroupChatStatusListener onGroupChatStatusListener : mOnGroupChatStatusListenerList) {
                onGroupChatStatusListener.add(groupChatStatusBean.getGroupChatId(), groupChatStatusBean.getFromUser(), groupChatStatusBean.getToUserList());
            }
        } else if (groupChatStatusBean.getStatus() == GroupChatStatus.DELETE.getValue()) {
            for (IOnGroupChatStatusListener onGroupChatStatusListener : mOnGroupChatStatusListenerList) {
                onGroupChatStatusListener.delete(groupChatStatusBean.getGroupChatId(), groupChatStatusBean.getFromUser(), groupChatStatusBean.getToUserList());
            }
        } else if (groupChatStatusBean.getStatus() == GroupChatStatus.OTHER_ADD.getValue()) {
            for (IOnGroupChatStatusListener onGroupChatStatusListener : mOnGroupChatStatusListenerList) {
                onGroupChatStatusListener.otherAdd(groupChatStatusBean.getGroupChatId(), groupChatStatusBean.getFromUser(), groupChatStatusBean.getToUserList());
            }
        } else if (groupChatStatusBean.getStatus() == GroupChatStatus.OTHER_DELETE.getValue()) {
            for (IOnGroupChatStatusListener onGroupChatStatusListener : mOnGroupChatStatusListenerList) {
                onGroupChatStatusListener.otherDelete(groupChatStatusBean.getGroupChatId(), groupChatStatusBean.getFromUser(), groupChatStatusBean.getToUserList());
            }
        } else if (groupChatStatusBean.getStatus() == GroupChatStatus.NICKNAME_CHANGED.getValue()) {
            for (IOnGroupChatStatusListener onGroupChatStatusListener : mOnGroupChatStatusListenerList) {
                onGroupChatStatusListener.nicknameChanged(groupChatStatusBean.getGroupChatId(), groupChatStatusBean.getFromUser(), groupChatStatusBean.getToUserList());
            }
        }
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/7 21:17
     * Description:断开 IM 连接
     *
     * @param statusCode 状态码
     */
    private void disconnect(StatusCode statusCode) {
        // 关闭连接
        if (mWebSocket != null) {
            mWebSocket.close(statusCode.getCode(), statusCode.getReason());
        }
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/5 10:43
     * Description:初始化 IM 服务,建议在 Application 中
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/5 16:13
     * Description:连接 IM 服务
     *
     * @param userId 用户 id
     */
    public void connect(final String userId) {
        new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
//                // 发送心跳间隔
//                .pingInterval(HEART_BEAT_INTERVAL, TimeUnit.MILLISECONDS)
                .build()
                .newWebSocket(new Request.Builder().url(URL).build(), new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        Log.i(TAG, "onOpen:");
                        for (IOnConnectListener onConnectListener : mOnConnectListenerList) {
                            onConnectListener.onConnected();
                        }
                        webSocket.send(new Gson().toJson(MessageBean.createHandshakeMessage(userId)));
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);
                        Log.i(TAG, "onMessage: text--->" + text);
                        final MessageBean messageBean = new Gson().fromJson(text, MessageBean.class);
                        if (messageBean.getType() == MessageType.HANDSHAKE_SUCCESS.getValue()) {
//                    UserBean userBean = new Gson().fromJson(messageBean.getExtend(), UserBean.class);
                            connectSuccess(webSocket, userId);
                            for (IOnConnectListener onConnectListener : mOnConnectListenerList) {
                                onConnectListener.onAuthenticated();
                            }
                            return;
                        }
                        handleMessage(messageBean);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        super.onMessage(webSocket, bytes);
                        Log.i(TAG, "onMessage: bytes--->" + bytes);
                    }

                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                        super.onClosing(webSocket, code, reason);
                        Log.i(TAG, "onClosing: code--->" + code + ",reason--->" + reason);
                        if (code == StatusCode.SERVER_FORCE_CLOSE.getCode()) {
                            // 服务器强制断开连接
                            disconnect(StatusCode.SERVER_FORCE_CLOSE);
                        }
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        super.onClosed(webSocket, code, reason);
                        Log.i(TAG, "onClosed: code--->" + code + ",reason--->" + reason);
                        for (IOnConnectListener onConnectListener : mOnConnectListenerList) {
                            onConnectListener.onDisconnected();
                        }
                        release();
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, final Throwable t, Response response) {
                        super.onFailure(webSocket, t, response);
                        Log.i(TAG, "onFailure: t.class--->" + t.getClass() + ",t.message--->" + t.getMessage());
                        // 移除心跳任务
                        if (mHeartBeatScheduledFuture != null) {
                            mHeartBeatScheduledFuture.cancel(true);
                            mHeartBeatScheduledFuture = null;
                        }
                        if ((t instanceof SocketException) || (t instanceof EOFException)) {
                            // 自动重连
                            mReconnectScheduledFuture = mReconnectScheduledExecutorService.schedule(mReconnectRunnable, TIME_OUT, TimeUnit.MILLISECONDS);
                        } else if (t instanceof SocketTimeoutException) {
                            if (mReconnectCount != 0) {
                                // 如果是重连中的超时,则继续重连,不用通知用户
                                mReconnectScheduledFuture = mReconnectScheduledExecutorService.schedule(mReconnectRunnable, TIME_OUT, TimeUnit.MILLISECONDS);
                                return;
                            }
                            for (IOnConnectListener onConnectListener : mOnConnectListenerList) {
                                onConnectListener.onConnectFailure(new Exception(t));
                            }
                        }
                    }
                });
    }


    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/5 16:13
     * Description:断开 IM 连接
     */
    public void disconnect() {
        disconnect(StatusCode.NORMAL_CLOSE);
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/7 21:38
     * Description:释放资源
     */
    private void release() {
        mOnConnectListenerList.clear();
        mOnFriendStatusListenerList.clear();
        mOnGroupChatStatusListenerList.clear();
        mOnMessageListenerList.clear();
        mOnSendMessageListenerList.clear();
//        for (Timer timer : mRetrySendTimerMap.values()) {
//            timer.cancel();
//        }
//        mAckMessageMap.clear();
//        mRetrySendTimerMap.clear();
        mUserId = null;
        // 移除重连任务
        if (mReconnectScheduledFuture != null) {
            mReconnectScheduledFuture.cancel(true);
            mReconnectScheduledFuture = null;
        }
        // 移除心跳任务
        if (mHeartBeatScheduledFuture != null) {
            mHeartBeatScheduledFuture.cancel(true);
            mHeartBeatScheduledFuture = null;
        }
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/8 15:55
     * Description:保存消息和会话到数据库
     *
     * @param messageBean 消息对象
     * @param send        true 表示是发送的消息,false 表示是接收的消息
     */
    private void insert2Database(MessageBean messageBean, boolean send) {
        mMessageManager.insertOrUpdate(messageBean);
        // 插入或更新会话
        String toUserId;
        long lastMsgTimestamp;
        if (send || messageBean.getType() == MessageType.GROUP_CHAT.getValue()) {
            // 发送的消息, conversation 的目标 id 为接收方 id
            // 群聊的发送方永远是自己,接收方永远是群 id,所以群聊类型的消息,conversation 的目标 id 为永远为群 id
            toUserId = messageBean.getToUserId();
            lastMsgTimestamp = messageBean.getSendTimestamp();
        } else {
            // 接收的消息, conversation 的目标 id 为发送方 id
            toUserId = messageBean.getFromUserId();
            lastMsgTimestamp = messageBean.getReceiveTimestamp();
        }
        ConversationBean conversationBean = mConversationManager.getByTypeAndToUserId(messageBean.getType(), toUserId);
        if (conversationBean == null) {
            conversationBean = new ConversationBean();
        }
        // 发送的消息, conversation 的目标 id 为接收方 id
        // 群聊的发送方永远是自己,接收方永远是群 id,所以群聊类型的消息,conversation 的目标 id 为永远为群 id
        conversationBean.setToUserId(toUserId);
        conversationBean.setType(messageBean.getType());
        conversationBean.setLastMsgContentType(messageBean.getContentType());
        conversationBean.setLastMsgContent(messageBean.getContent());
        conversationBean.setLastMsgTimestamp(lastMsgTimestamp);
        conversationBean.setLastMsgPid(messageBean.getPid());
        if (!send) {
            if (conversationBean.getUnreadCount() == -1) {
                // 如果被标记过未读,则设置未读数为 1
                conversationBean.setUnreadCount(1);
            } else {
                // 否则在原有的未读数上 +1
                conversationBean.setUnreadCount(conversationBean.getUnreadCount() + 1);
            }
        }
        mConversationManager.insertOrUpdate(conversationBean);
        // 插入会话与消息关系
        mConversationManager.insertConversationMessageRel(new ConversationMessageRelBean(conversationBean.getId(), messageBean.getPid()));
    }

    public void sendMessage(final MessageBean messageBean) {
        if (mWebSocket == null) {
            return;
        }
        messageBean.setFromUserId(mUserId);
        if (messageBean.getType() == MessageType.CHAT.getValue()
                || messageBean.getType() == MessageType.GROUP_CHAT.getValue()) {
            insert2Database(messageBean, true);
//            // 创建一个唯一索引作为 ACK Key,将其 json 串做 Base64 加密,得到的加密字符串作为 key
//            AckKeyBean ackKeyBean = new AckKeyBean(mUserId
//                    , messageBean.getFromUserId()
//                    , messageBean.getToUserId()
//                    , messageBean.getType()
//                    , messageBean.getSendTimestamp());
//            String key = CodeUtil.encode(new Gson().toJson(ackKeyBean));
//            mAckMessageMap.put(key, messageBean);
//            // 设置一个定时任务,一定时间后如果还没有收到服务端回执,则重新发送
//            Timer timer = new Timer();
//            timer.schedule(new RetrySendTimerTask(key), TIME_OUT);
//            mRetrySendTimerMap.put(key, timer);
        }
        mWebSocket.send(new Gson().toJson(messageBean));
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (IOnSendMessageListener onSendMessageListener : mOnSendMessageListenerList) {
                    onSendMessageListener.onSending(messageBean);
                }
            }
        });
    }

//    private class RetrySendTimerTask extends TimerTask {
//        private String mKey;
//
//        public RetrySendTimerTask(String key) {
//            mKey = key;
//        }
//
//        @Override
//        public void run() {
//            MessageBean ackMessageBean = mAckMessageMap.get(mKey);
//            if (ackMessageBean != null) {
//                mWebSocket.send(new Gson().toJson(ackMessageBean));
//                mRetrySendTimerMap.get(mKey).schedule(new RetrySendTimerTask(mKey), TIME_OUT);
//            }
//        }
//    }

    public void uploadVoice(long time, File voice, final QSCallback<UploadVoiceResultBean> qsCallback) {
        OkHttpHelperForQSBoxCommonApi.SINGLETON.uploadVoice(mUserId, time, voice)
                .transform(new QSApiTransformer<UploadVoiceResultBean>())
                .enqueue(new Callback<UploadVoiceResultBean>() {
                    @Override
                    public void onSuccess(UploadVoiceResultBean data) {
                        qsCallback.onSuccess(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        qsCallback.onFailure(e);
                    }
                });
    }

    public void uploadImg(File img, final QSCallback<UploadImgResultBean> qsCallback) {
        OkHttpHelperForQSBoxCommonApi.SINGLETON.uploadImg(mUserId, img)
                .transform(new QSApiTransformer<UploadImgResultBean>())
                .enqueue(new Callback<UploadImgResultBean>() {
                    @Override
                    public void onSuccess(UploadImgResultBean data) {
                        qsCallback.onSuccess(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        qsCallback.onFailure(e);
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/3 14:13
     * Description:下载文件,重载
     */
    public void download(String url, final File file, final QSCallback<File> qsCallback) {
        download(url, file, null, qsCallback);
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/3 14:13
     * Description:下载文件
     *
     * @param url              下载地址
     * @param file             下载文件目标地址
     * @param downloadCallback 下载进度回调
     * @param qsCallback       下载结果回调
     */
    public void download(String url, final File file, AbsDownloadCallback downloadCallback, final QSCallback<File> qsCallback) {
        OkHttpHelperForQSBoxCommonApi.SINGLETON.download(url, file, downloadCallback).enqueue(new Callback<File>() {
            @Override
            public void onSuccess(File data) {
                qsCallback.onSuccess(data);
            }

            @Override
            public void onFailure(Exception e) {
                qsCallback.onFailure(e);
            }
        });
    }

    public GroupChatManager getGroupChatManager() {
        return mGroupChatManager;
    }

    public FriendManager getFriendManager() {
        return mFriendManager;
    }

    public MessageManager getMessageManager() {
        return mMessageManager;
    }

    public ConversationManager getConversationManager() {
        return mConversationManager;
    }

    public void addOnConnectListener(IOnConnectListener onConnectListener) {
        mOnConnectListenerList.add(onConnectListener);
    }

    public void removeOnConnectListener(IOnConnectListener onConnectListener) {
        mOnMessageListenerList.remove(onConnectListener);
    }

    public void addOnMessageListener(IOnMessageListener onMessageListener) {
        mOnMessageListenerList.add(onMessageListener);
    }

    public void removeOnMessageListener(IOnMessageListener onMessageListener) {
        mOnMessageListenerList.remove(onMessageListener);
    }

    public void addOnFriendStatusListener(IOnFriendStatusListener onFriendStatusListener) {
        mOnFriendStatusListenerList.add(onFriendStatusListener);
    }

    public void removeOnFriendStatusListener(IOnFriendStatusListener onFriendStatusListener) {
        mOnFriendStatusListenerList.remove(onFriendStatusListener);
    }

    public void addOnGroupChatStatusListener(IOnGroupChatStatusListener onGroupChatStatusListener) {
        mOnGroupChatStatusListenerList.add(onGroupChatStatusListener);
    }

    public void removeOnGroupChatStatusListener(IOnGroupChatStatusListener onGroupChatStatusListener) {
        mOnGroupChatStatusListenerList.remove(onGroupChatStatusListener);
    }

    public void addOnSendMessageListener(IOnSendMessageListener onSendMessageListener) {
        mOnSendMessageListenerList.add(onSendMessageListener);
    }

    public void removeOnSendMessageListener(IOnSendMessageListener onSendMessageListener) {
        mOnSendMessageListenerList.remove(onSendMessageListener);
    }

}
