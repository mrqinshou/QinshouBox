package com.qinshou.qinshoubox.im.db.dao;

/**
 * Author: QinHao
 * Email:cqflqinhao@126.com
 * Date: 2019/12/04 22:08
 * Description:friend 表的 Dao
 */
public interface IFriendDao {
    // 创建表语句
    // CREATE TABLE IF NOT EXISTS friend(id TEXT PRIMARY KEY
    // ,nickname TEXT,headImg TEXT,headImgSmall TEXT
    // ,signature TEXT,remark TEXT,top INTEGER,doNotDisturb INTEGER,blackList INTEGER
    // );

    // 插入语句
    // INSERT INTO friend
    // (id,nickname,headImg,headImgSmall,signature,remark
    // ,top,doNotDisturb,blackList)
    // VALUES
    // (#{id},#{nickname},#{headImg},#{headImgSmall},#{signature},#{remark}
    // ,#{top},#{doNotDisturb},#{blackList});

    // 修改语句
    // UPDATE friend SET
    // nickname=#{nickname},headImg=#{headImg},headImgSmall=#{headImgSmall}
    // ,signature#{signature},remark=#{remark},top=#{top}
    // ,doNotDisturb#{doNotDisturb},blackList=#{blackList}
    // WHERE id=#{id};

    // 查询语句
    // SELECT
    // f.id,f.nickname,f.headImg,f.headImgSmall,f.signature,f.remark,f.top
    // ,f.doNotDisturb,f.blackList
    // FROM friend AS f
    // WHERE f.id=#{id};

    // existsById
    // SELECT COUNT(id) FROM friend WHERE id=#{id};
}
