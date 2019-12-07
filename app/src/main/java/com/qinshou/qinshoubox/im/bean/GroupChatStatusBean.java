package com.qinshou.qinshoubox.im.bean;

/**
 * Author: QinHao
 * Email:cqflqinhao@126.com
 * Date: 2019/12/7 14:15
 * Description:群状态事件监听的映射类
 */
public class GroupChatStatusBean {
    /**
     * 群相关操作类型
     */
    private int status;
    /**
     * 目标群聊 id
     */
    private String groupChatId;
    /**
     * 发起该操作的人
     */
    private String fromUserId;
    /**
     * 被操作的人
     */
    private String toUserId;

    public GroupChatStatusBean() {
    }

    @Override
    public String toString() {
        return "GroupChatStatusBean{" +
                "status=" + status +
                ", groupChatId='" + groupChatId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
}
