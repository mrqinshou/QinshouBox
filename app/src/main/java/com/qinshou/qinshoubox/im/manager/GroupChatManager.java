package com.qinshou.qinshoubox.im.manager;

import android.os.Handler;
import android.os.Looper;

import com.qinshou.qinshoubox.conversation.bean.GroupChatDetailBean;
import com.qinshou.qinshoubox.friend.bean.UserDetailBean;
import com.qinshou.qinshoubox.im.listener.QSCallback;
import com.qinshou.okhttphelper.callback.Callback;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.im.db.DatabaseHelper;
import com.qinshou.qinshoubox.im.db.dao.IGroupChatDao;
import com.qinshou.qinshoubox.listener.FailureRunnable;
import com.qinshou.qinshoubox.listener.SuccessRunnable;
import com.qinshou.qinshoubox.network.OkHttpHelperForQSBoxGroupChatApi;
import com.qinshou.qinshoubox.transformer.QSApiTransformer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/26 14:07
 * Description:群聊管理者
 */
public class GroupChatManager {
    private IGroupChatDao mGroupChatDao;
    private String mUserId;
    /**
     * 线程池,线程数量不定,适合执行大量耗时较少的任务
     */
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    /**
     * 将回调切换到主线程的 Handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public GroupChatManager(DatabaseHelper databaseHelper, String userId) {
        mGroupChatDao = databaseHelper.getDao(IGroupChatDao.class);
        mUserId = userId;
    }

    public void create(List<String> toUserIdList, String nickname, String headImg, Callback<GroupChatBean> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.create(mUserId, toUserIdList, nickname, headImg)
                .transform(new QSApiTransformer<GroupChatBean>())
                .enqueue(callback);
    }

    public void getGroupChatList(final Callback<List<GroupChatBean>> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getMyGroupChatList(mUserId)
                .transform(new QSApiTransformer<List<GroupChatBean>>())
                .enqueue(new Callback<List<GroupChatBean>>() {
                    @Override
                    public void onSuccess(final List<GroupChatBean> data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                for (GroupChatBean groupChatBean : data) {
                                    if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                        mGroupChatDao.update(groupChatBean);
                                    } else {
                                        mGroupChatDao.insert(groupChatBean);
                                    }
                                }
                                mHandler.post(new SuccessRunnable<List<GroupChatBean>>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
    }

    public void getDetail(String groupChatId, final Callback<GroupChatDetailBean> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getDetail(groupChatId, mUserId)
                .transform(new QSApiTransformer<GroupChatDetailBean>())
                .enqueue(callback);
    }

    public void getMemberList(String groupChatId, final Callback<List<UserDetailBean>> callback) {
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.getMemberList(groupChatId, mUserId)
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.addMember(groupChatId, mUserId, toUserIdList)
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.deleteMember(groupChatId, mUserId, toUserIdList)
                .transform(new QSApiTransformer<Object>())
                .enqueue(callback);
    }

    public GroupChatBean getById(String groupChatId) {
        return mGroupChatDao.selectById(groupChatId);
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setNickname(groupChatId, mUserId, nickname)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                GroupChatBean groupChatBean = mGroupChatDao.selectById(groupChatId);
                                groupChatBean.setNickname(nickname);
                                if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                    mGroupChatDao.update(groupChatBean);
                                } else {
                                    mGroupChatDao.insert(groupChatBean);
                                }
                                mHandler.post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mHandler.post(new FailureRunnable<>(callback, e));
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, mUserId, nicknameInGroupChat, null, null, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                GroupChatBean groupChatBean = mGroupChatDao.selectById(groupChatId);
                                groupChatBean.setNicknameInGroupChat(nicknameInGroupChat);
                                if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                    mGroupChatDao.update(groupChatBean);
                                } else {
                                    mGroupChatDao.insert(groupChatBean);
                                }
                                mHandler.post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mHandler.post(new FailureRunnable<>(callback, e));
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, mUserId, null, top, null, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                GroupChatBean groupChatBean = mGroupChatDao.selectById(groupChatId);
                                groupChatBean.setTop(top);
                                if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                    mGroupChatDao.update(groupChatBean);
                                } else {
                                    mGroupChatDao.insert(groupChatBean);
                                }
                                mHandler.post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mHandler.post(new FailureRunnable<>(callback, e));
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, mUserId, null, null, doNotDisturb, null)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                GroupChatBean groupChatBean = mGroupChatDao.selectById(groupChatId);
                                groupChatBean.setDoNotDisturb(doNotDisturb);
                                if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                    mGroupChatDao.update(groupChatBean);
                                } else {
                                    mGroupChatDao.insert(groupChatBean);
                                }
                                mHandler.post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mHandler.post(new FailureRunnable<>(callback, e));
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.setInfo(groupChatId, mUserId, null, null, null, showGroupChatMemberNickname)
                .transform(new QSApiTransformer<Object>())
                .enqueue(new Callback<Object>() {
                    @Override
                    public void onSuccess(final Object data) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                GroupChatBean groupChatBean = mGroupChatDao.selectById(groupChatId);
                                groupChatBean.setShowGroupChatMemberNickname(showGroupChatMemberNickname);
                                if (mGroupChatDao.existsById(groupChatBean.getId())) {
                                    mGroupChatDao.update(groupChatBean);
                                } else {
                                    mGroupChatDao.insert(groupChatBean);
                                }
                                mHandler.post(new SuccessRunnable<Object>(callback, data));
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        mHandler.post(new FailureRunnable<>(callback, e));
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
        OkHttpHelperForQSBoxGroupChatApi.SINGLETON.exit(groupChatId, mUserId)
                .transform(new QSApiTransformer<Object>())
                .enqueue(callBack);
    }
}
