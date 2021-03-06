package com.qinshou.qinshoubox.network;

import com.qinshou.networkmodule.annotation.Api;
import com.qinshou.networkmodule.annotation.Json;
import com.qinshou.networkmodule.annotation.Param;
import com.qinshou.networkmodule.annotation.Post;
import com.qinshou.networkmodule.call.AbsCall;
import com.qinshou.qinshoubox.constant.IUrlConstant;
import com.qinshou.qinshoubox.homepage.bean.QinshouResultBean;
import com.qinshou.qinshoubox.im.bean.GroupChatBean;
import com.qinshou.qinshoubox.im.bean.GroupChatDetailBean;
import com.qinshou.qinshoubox.im.bean.UserDetailBean;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/11/22 13:35
 * Description:QSBox 群聊模块的接口
 */
@Api(value = IUrlConstant.DEFAULT_HOST + "/groupChat")
public interface QSBoxGroupChatApi {

    @Json
    @Post("/create")
    AbsCall<QinshouResultBean<GroupChatBean>> create(@Param("fromUserId") String fromUserId
            , @Param("toUserIdList") List<String> toUserIdList
            , @Param("nickname") String nickname
            , @Param("headImg") String headImg);

    @Json
    @Post("/getMyGroupChatList")
    AbsCall<QinshouResultBean<List<GroupChatBean>>> getMyGroupChatList(@Param("fromUserId") String fromUserId);

    @Json
    @Post("/getDetail")
    AbsCall<QinshouResultBean<GroupChatDetailBean>> getDetail(@Param("groupChatId") String groupChatId
            , @Param("fromUserId") String fromUserId);

    @Json
    @Post("/getMemberList")
    AbsCall<QinshouResultBean<List<UserDetailBean>>> getMemberList(@Param("groupChatId") String groupChatId
            , @Param("fromUserId") String fromUserId);

    @Json
    @Post("/addMember")
    AbsCall<QinshouResultBean<Object>> addMember(@Param("groupChatId") String groupChatId
            , @Param("fromUserId") String fromUserId
            , @Param("toUserIdList") List<String> toUserIdList);

    @Json
    @Post("/deleteMember")
    AbsCall<QinshouResultBean<Object>> deleteMember(@Param("groupChatId") String groupChatId
            , @Param("fromUserId") String fromUserId
            , @Param("toUserIdList") List<String> toUserIdList);

    @Json
    @Post("/setNickname")
    AbsCall<QinshouResultBean<Object>> setNickname(@Param("groupChatId") String groupChatId
            , @Param("userId") String userId
            , @Param("nickname") String nickname);

    @Json
    @Post("/setInfo")
    AbsCall<QinshouResultBean<Object>> setInfo(@Param("groupChatId") String groupChatId
            , @Param("userId") String userId
            , @Param("nicknameInGroupChat") String nicknameInGroupChat
            , @Param("top") Integer top
            , @Param("doNotDisturb") Integer doNotDisturb
            , @Param("showGroupChatMemberNickname") Integer showGroupChatMemberNickname);

    @Json
    @Post("/exit")
    AbsCall<QinshouResultBean<Object>> exit(@Param("groupChatId") String groupChatId
            , @Param("userId") String userId);
}
