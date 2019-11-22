package com.qinshou.immodule.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/22 19:50
 * Description:会话和消息关联类
 */
@DatabaseTable(tableName = "conversation_message_rel")
public class ConversationMessageRelBean {
    @DatabaseField(columnName = "conversationId")
    private int conversationId;
    @DatabaseField(columnName = "messageId")
    private int messageId;

    public ConversationMessageRelBean(int conversationId, int messageId) {
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
