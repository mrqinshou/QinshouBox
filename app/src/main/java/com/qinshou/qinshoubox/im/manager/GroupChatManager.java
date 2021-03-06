package com.qinshou.qinshoubox.im.manager;

import android.text.TextUtils;

import com.qinshou.networkmodule.OkHttpHelper;
import com.qinshou.networkmodule.callback.Callback;
import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.qinshoubox.im.bean.GroupChatDetailBean;
import com.qinshou.qinshoubox.im.IMClient;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.im.bean.GroupChatStatusBean;
import com.qinshou.qinshoubox.im.bean.MessageBean;
import com.qinshou.qinshoubox.im.bean.UserDetailBean;
import com.qinshou.qinshoubox.im.cache.GroupChatDatabaseCache;
import com.qinshou.qinshoubox.im.cache.GroupChatDoubleCache;
import com.qinshou.qinshoubox.im.cache.MemoryCache;
import com.qinshou.qinshoubox.im.enums.GroupChatStatus;
import com.qinshou.qinshoubox.im.enums.MessageType;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.listener.FailureRunnable;
import com.qinshou.qinshoubox.listener.SuccessRunnable;
import com.qinshou.qinshoubox.network.QSBoxGroupChatApi;
import com.qinshou.qinshoubox.transformer.QSApiTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/26 14:07
 * Description:群管理者
 */
public class GroupChatManager extends AbsManager<String, GroupChatBean> {
    private boolean mLoaded = false;

    public GroupChatManager() {
        super(new GroupChatDoubleCache(new MemoryCache<>(), new GroupChatDatabaseCache()));
    }

    public GroupChatBean getById(String id) {
        GroupChatBean groupChatBean = getCache().get(id);
        if (groupChatBean == null) {
            ShowLogUtil.logi("从网络拿");
        }
        return groupChatBean;
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/6 17:57
     * Description:获取我的群列表
     */
    public void getList(final QSCallback<List<GroupChatBean>> qsCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mLoaded) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (getCache().getValues() == null) {
                    getHandler().post(new SuccessRunnable<>(qsCallback, new ArrayList<>()));
                } else {
                    getHandler().post(new SuccessRunnable<>(qsCallback, new ArrayList<>(getCache().getValues())));
                }
            }
        }).start();
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/6 17:57
     * Description:同步获取我的群列表
     */
    public void getList() {
        getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    String userId = IMClient.SINGLETON.getUserDetailBean().getId();
                    List<GroupChatBean> groupChatBeanList = OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).getMyGroupChatList(userId)
                            .transform(new QSApiTransformer<List<GroupChatBean>>())
                            .execute();
                    for (GroupChatBean groupChatBean : groupChatBeanList) {
                        getCache().put(groupChatBean.getId(), groupChatBean);
                    }
                } catch (Exception e) {
                    getList();
                    return;
                }
                mLoaded = true;
            }
        });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/6 17:56
     * Description:创建群
     *
     * @param toUserIdList 需要加入到群的用户列表的 id
     * @param nickname     群昵称
     * @param headImg      群头像
     */
    public void create(List<String> toUserIdList, String nickname, String headImg, QSCallback<GroupChatBean> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).create(userId, toUserIdList, nickname, headImg)
                .transform(new QSApiTransformer<GroupChatBean>())
                .enqueue(new Callback<GroupChatBean>() {
                    @Override
                    public void onSuccess(GroupChatBean data) {
                        // 更新缓存
                        getCache().put(data.getId(), data);

                        // 创建群聊提示信息的系统消息
                        GroupChatStatusBean groupChatStatusBean = new GroupChatStatusBean();
                        groupChatStatusBean.setStatus(GroupChatStatus.OTHER_ADD.getValue());
                        groupChatStatusBean.setFromUser(IMClient.SINGLETON.getUserDetailBean());
                        List<UserDetailBean> userDetailBeanList = new ArrayList<>();
                        for (String toUserId : toUserIdList) {
                            if (TextUtils.equals(toUserId, userId)) {
                                // 不包括自己
                                continue;
                            }
                            userDetailBeanList.add(IMClient.SINGLETON.getFriendManager().getById(toUserId));
                        }
                        groupChatStatusBean.setToUserList(userDetailBeanList);

                        MessageBean m = MessageBean.createGroupChatStatusMessage(userId
                                , data.getId()
                                , groupChatStatusBean);
                        m.setType(MessageType.GROUP_CHAT.getValue());
                        IMClient.SINGLETON.handleMessage(m);

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
     * Date:2020/1/6 17:57
     * Description:获取群详情
     *
     * @param groupChatId 群 id
     */
    public void getDetail(String groupChatId, final QSCallback<GroupChatDetailBean> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).getDetail(groupChatId, userId)
                .transform(new QSApiTransformer<GroupChatDetailBean>())
                .enqueue(new Callback<GroupChatDetailBean>() {
                    @Override
                    public void onSuccess(GroupChatDetailBean data) {
                        GroupChatBean groupChatBean = new GroupChatBean();
                        groupChatBean.setId(data.getId());
                        groupChatBean.setOwnerId(data.getOwnerId());
                        groupChatBean.setNickname(data.getNickname());
                        groupChatBean.setHeadImg(data.getHeadImg());
                        groupChatBean.setHeadImgSmall(data.getHeadImgSmall());
                        groupChatBean.setNicknameDefault(data.getNicknameDefault());
                        groupChatBean.setNicknameInGroupChat(data.getNicknameInGroupChat());
                        groupChatBean.setTop(data.getTop());
                        groupChatBean.setDoNotDisturb(data.getDoNotDisturb());
                        groupChatBean.setShowGroupChatMemberNickname(data.getShowGroupChatMemberNickname());
                        // 更新群缓存
                        getCache().put(groupChatBean.getId(), groupChatBean);
                        // 更新群成员缓存
                        for (UserDetailBean userDetailBean : data.getMemberList()) {
                            IMClient.SINGLETON.getGroupChatMemberManager().getCache().put(data.getId() + "_" + userDetailBean.getId()
                                    , userDetailBean);
                        }

                        if (qsCallback != null) {
                            qsCallback.onSuccess(data);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (qsCallback != null) {
                            qsCallback.onFailure(e);
                        }
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2020/1/6 17:58
     * Description:获取群成员列表
     *
     * @param groupChatId 群 id
     */
    public void getMemberList(final String groupChatId, final QSCallback<List<UserDetailBean>> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).getMemberList(groupChatId, userId)
                .transform(new QSApiTransformer<List<UserDetailBean>>())
                .enqueue(new Callback<List<UserDetailBean>>() {
                    @Override
                    public void onSuccess(final List<UserDetailBean> data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                for (UserDetailBean userDetailBean : data) {
                                    IMClient.SINGLETON.getGroupChatMemberManager().getCache().put(groupChatId + "_" + userDetailBean.getId()
                                            , userDetailBean);
                                }
                                getHandler().post(new SuccessRunnable<>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (qsCallback != null) {
                            qsCallback.onFailure(e);
                        }
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/2 20:12
     * Description:添加群成员
     *
     * @param groupChatId 待添加的群成员的 Id 集合
     */
    public void addMember(String groupChatId, List<String> toUserIdList, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).addMember(groupChatId, userId, toUserIdList)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(Object data) {
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
     * Date:2019/12/2 20:12
     * Description:删除群成员
     *
     * @param groupChatId 待删除的群成员的 Id 集合
     */
    public void deleteMember(String groupChatId, List<String> toUserIdList, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).deleteMember(groupChatId, userId, toUserIdList)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(Object data) {
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
     * Date:2019/12/9 14:12
     * Description:设置群昵称
     *
     * @param groupChatId 群 id
     * @param nickname    群昵称
     */
    public void setNickname(final String groupChatId, final String nickname, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).setNickname(groupChatId, userId, nickname)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                // 更新缓存
                                GroupChatBean groupChatBean = getCache().get(groupChatId);
                                groupChatBean.setNickname(nickname);
                                getCache().put(groupChatBean.getId(), groupChatBean);

                                getHandler().post(new SuccessRunnable<>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(qsCallback, e));
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/25 10:25
     * Description:修改在本群中的昵称
     *
     * @param groupChatId         群 id
     * @param nicknameInGroupChat 在本群中的昵称
     */
    public void setNicknameInGroupChat(final String groupChatId, final String nicknameInGroupChat, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).setInfo(groupChatId, userId, nicknameInGroupChat, null, null, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                // 更新缓存
                                GroupChatBean groupChatBean = getCache().get(groupChatId);
                                groupChatBean.setNicknameInGroupChat(nicknameInGroupChat);
                                getCache().put(groupChatBean.getId(), groupChatBean);

                                getHandler().post(new SuccessRunnable<Object>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(qsCallback, e));
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/25 10:25
     * Description:修改在本群中的昵称
     *
     * @param groupChatId 群 id
     * @param top         0 表示不置顶,1 表示置顶
     */
    public void setTop(final String groupChatId, final int top, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).setInfo(groupChatId, userId, null, top, null, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                // 更新缓存
                                GroupChatBean groupChatBean = getCache().get(groupChatId);
                                groupChatBean.setTop(top);
                                getCache().put(groupChatBean.getId(), groupChatBean);

                                getHandler().post(new SuccessRunnable<Object>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(qsCallback, e));
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/25 10:25
     * Description:设置是否免打扰
     *
     * @param groupChatId  群 id
     * @param doNotDisturb 0 表示非免打扰,1 表示免打扰
     */
    public void setDoNotDisturb(final String groupChatId, final int doNotDisturb, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).setInfo(groupChatId, userId, null, null, doNotDisturb, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                // 更新缓存
                                GroupChatBean groupChatBean = getCache().get(groupChatId);
                                groupChatBean.setDoNotDisturb(doNotDisturb);
                                getCache().put(groupChatBean.getId(), groupChatBean);

                                getHandler().post(new SuccessRunnable<>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(qsCallback, e));
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/25 10:25
     * Description:设置是否显示群成员昵称
     *
     * @param groupChatId                 群 id
     * @param showGroupChatMemberNickname 0 表示不显示,1 表示显示
     */
    public void setShowGroupChatMemberNickname(final String groupChatId, final int showGroupChatMemberNickname, final QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).setInfo(groupChatId, userId, null, null, null, showGroupChatMemberNickname)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                // 更新缓存
                                GroupChatBean groupChatBean = getCache().get(groupChatId);
                                groupChatBean.setShowGroupChatMemberNickname(showGroupChatMemberNickname);
                                getCache().put(groupChatBean.getId(), groupChatBean);

                                getHandler().post(new SuccessRunnable<>(qsCallback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(qsCallback, e));
                    }
                });
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/9 15:55
     * Description:退出群聊
     *
     * @param groupChatId 群 id
     */
    public void exit(String groupChatId, QSCallback<Object> qsCallback) {
        String userId = IMClient.SINGLETON.getUserDetailBean().getId();
        OkHttpHelper.SINGLETON.getCaller(QSBoxGroupChatApi.class).exit(groupChatId, userId)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(Object data) {
                        getCache().remove(groupChatId);
                        qsCallback.onSuccess(data);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        qsCallback.onFailure(e);
                    }
                });
    }
}
