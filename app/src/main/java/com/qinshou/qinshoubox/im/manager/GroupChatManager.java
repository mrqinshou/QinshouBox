package com.qinshou.qinshoubox.im.manager;

import com.qinshou.commonmodule.util.ShowLogUtil;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.conversation.bean.GroupChatDetailBean;
import com.qinshou.qinshoubox.friend.bean.UserDetailBean;
import com.qinshou.qinshoubox.im.IMClient;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.im.cache.GroupChatDatabaseCache;
import com.qinshou.qinshoubox.im.cache.GroupChatDoubleCache;
import com.qinshou.qinshoubox.im.cache.MemoryCache;
import com.qinshou.qinshoubox.im.db.DatabaseHelper;
import com.qinshou.qinshoubox.im.listener.IOnGroupChatStatusListener;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.qinshoubox.listener.FailureRunnable;
import com.qinshou.qinshoubox.listener.SuccessRunnable;
import com.qinshou.qinshoubox.network.OkHttpHelperForQSBoxGroupChatApi;
import com.qinshou.qinshoubox.transformer.QSApiTransformer;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/26 14:07
 * Description:群管理者
 */
public class GroupChatManager extends AbsManager<String, GroupChatBean> {

    public GroupChatManager(DatabaseHelper databaseHelper, String userId) {
        super(userId, new GroupChatDoubleCache(new MemoryCache<String, GroupChatBean>(), new GroupChatDatabaseCache(databaseHelper)));
        IMClient.SINGLETON.addOnGroupChatStatusListener(new IOnGroupChatStatusListener() {
            @Override
            public void add(String groupChatId, UserDetailBean fromUser, List<UserDetailBean> toUserList) {
                getDetail(groupChatId, null);
            }

            @Override
            public void delete(String groupChatId, UserDetailBean fromUser, List<UserDetailBean> toUserList) {

            }

            @Override
            public void otherAdd(String groupChatId, UserDetailBean fromUser, List<UserDetailBean> toUserList) {
                getDetail(groupChatId, null);
            }

            @Override
            public void otherDelete(String groupChatId, UserDetailBean fromUser, List<UserDetailBean> toUserList) {
                getDetail(groupChatId, null);
            }

            @Override
            public void nicknameChanged(String groupChatId, UserDetailBean fromUser, List<UserDetailBean> toUserList) {
                getDetail(groupChatId, null);
            }
        });
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
    public void getGroupChatList(final Callback<List<GroupChatBean>> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getMyGroupChatList(getUserId())
                .transform(new QSApiTransformer<List<GroupChatBean>>())
                .enqueue(new Callback<List<GroupChatBean>>() {
                    @Override
                    public void onSuccess(final List<GroupChatBean> data) {
                        getExecutorService().submit(new Runnable() {
                            @Override
                            public void run() {
                                for (GroupChatBean groupChatBean : data) {
                                    getCache().put(groupChatBean.getId(), groupChatBean);
                                }
                                getHandler().post(new SuccessRunnable<>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void create(List<String> toUserIdList, String nickname, String headImg, Callback<GroupChatBean> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.create(getUserId(), toUserIdList, nickname, headImg)
                .transform(new QSApiTransformer<GroupChatBean>())
                .enqueue(callback);
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getDetail(groupChatId, getUserId())
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
                        getCache().put(groupChatBean.getId(), groupChatBean);

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
    public void getMemberList(String groupChatId, final Callback<List<UserDetailBean>> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getMemberList(groupChatId, getUserId())
                .transform(new QSApiTransformer<List<UserDetailBean>>())
                .enqueue(callback);
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/2 20:12
     * Description:添加群成员
     *
     * @param groupChatId 待添加的群成员的 Id 集合
     */
    public void addMember(String groupChatId, List<String> toUserIdList, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.addMember(groupChatId, getUserId(), toUserIdList)
                .transform(new QSApiTransformer<Object>())
                .enqueue(callback);
    }

    /**
     * Author: QinHao
     * Email:cqflqinhao@126.com
     * Date:2019/12/2 20:12
     * Description:删除群成员
     *
     * @param groupChatId 待删除的群成员的 Id 集合
     */
    public void deleteMember(String groupChatId, List<String> toUserIdList, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.deleteMember(groupChatId, getUserId(), toUserIdList)
                .transform(new QSApiTransformer<Object>())
                .enqueue(callback);
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
    public void setNickname(final String groupChatId, final String nickname, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setNickname(groupChatId, getUserId(), nickname)
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

                                getHandler().post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void setNicknameInGroupChat(final String groupChatId, final String nicknameInGroupChat, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, getUserId(), nicknameInGroupChat, null, null, null)
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

                                getHandler().post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void setTop(final String groupChatId, final int top, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, getUserId(), null, top, null, null)
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

                                getHandler().post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void setDoNotDisturb(final String groupChatId, final int doNotDisturb, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, getUserId(), null, null, doNotDisturb, null)
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

                                getHandler().post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void setShowGroupChatMemberNickname(final String groupChatId, final int showGroupChatMemberNickname, final Callback<Object> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, getUserId(), null, null, null, showGroupChatMemberNickname)
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

                                getHandler().post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        getHandler().post(new FailureRunnable<>(callback, e));
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
    public void exit(String groupChatId, Callback<Object> callBack) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.exit(groupChatId, getUserId())
                .transform(new QSApiTransformer<Object>())
                .enqueue(callBack);
    }
}
