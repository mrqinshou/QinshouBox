package com.qinshou.qinshoubox.me.bean.building;

import androidx.fragment.app.FragmentManager;

import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.me.bean.IHandleEventCallback;
import com.qinshou.qinshoubox.me.enums.Building;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/4/22 18:03
 * Description:类描述
 */
public class StarrySkyBean extends BuildingBean {

    public StarrySkyBean() {
        super(Building.STARRY_SKY, R.drawable.magic_tower_building_starry_sky);
    }

    @Override
    public void handleEvent(FragmentManager fragmentManager, IHandleEventCallback handleEventCallback) {
        handleEventCallback.onFailure(new Exception("你要上天吗?"));
    }
}
