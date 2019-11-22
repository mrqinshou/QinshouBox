package com.qinshou.qinshoubox.me.bean;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinshou.databasehelper.annotation.Column;
import com.qinshou.databasehelper.annotation.Id;

/**
 * Description:群实体类
 * Author: QinHao
 * Date: 2019/11/21 22:13
 */
@DatabaseTable(tableName = "group_chat")
public class GroupChatBean {
    /**
     * Id
     */
    @DatabaseField(columnName = "id",id = true)
    private int id;
    /**
     * 群主 Id
     */
    @DatabaseField(columnName = "ownerId")
    private int ownerId;
    /**
     * 昵称
     */
    @DatabaseField(columnName = "nickname")
    private String nickname;
    /**
     * 默认群昵称,由前 5 个群成员的昵称拼接起来的字符串
     */
    @DatabaseField(columnName = "nicknameDefault")
    private String nicknameDefault;
    /**
     * 头像
     */
    @DatabaseField(columnName = "headImg")
    private String headImg;
    /**
     * 头像,缩略图
     */
    @DatabaseField(columnName = "headImgSmall")
    private String headImgSmall;

    public GroupChatBean() {
    }

    @Override
    public String toString() {
        return "GroupChatBean{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", nickname='" + nickname + '\'' +
                ", nicknameDefault='" + nicknameDefault + '\'' +
                ", headImg='" + headImg + '\'' +
                ", headImgSmall='" + headImgSmall + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNicknameDefault() {
        return nicknameDefault;
    }

    public void setNicknameDefault(String nicknameDefault) {
        this.nicknameDefault = nicknameDefault;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getHeadImgSmall() {
        return headImgSmall;
    }

    public void setHeadImgSmall(String headImgSmall) {
        this.headImgSmall = headImgSmall;
    }
}
