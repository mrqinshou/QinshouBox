package com.qinshou.qinshoubox.network;

import com.qinshou.okhttphelper.annotation.Api;
import com.qinshou.okhttphelper.annotation.Get;
import com.qinshou.okhttphelper.annotation.Query;
import com.qinshou.okhttphelper.call.Call;
import com.qinshou.qinshoubox.constant.IUrlConstant;
import com.qinshou.qinshoubox.homepage.bean.QinshouResultBean;
import com.qinshou.qinshoubox.homepage.bean.WallpaperBean;

import java.util.List;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2019/10/16 16:49
 * Description:类描述
 */
@Api(IUrlConstant.DEFAULT_HOST_ONLINE+IUrlConstant.WALLPAPER_API)
public interface QSBoxWallpaperApi {

    @Get("/getList")
    Call<QinshouResultBean<List<WallpaperBean>>> getWallpaperList(@Query(name = "page") int page, @Query(name = "pageSize") int pageSize);
}