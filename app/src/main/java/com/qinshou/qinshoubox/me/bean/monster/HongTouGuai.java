package com.qinshou.qinshoubox.me.bean.monster;

import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.me.bean.IHandleEventCallback;
import com.qinshou.qinshoubox.me.bean.Position;

import androidx.fragment.app.FragmentManager;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/4/22 23:28
 * Description:红头怪
 */
public class HongTouGuai implements Monster {
    @Override
    public int getResourceId() {
        return R.drawable.magic_tower_monster_hong_tou_guai;
    }

    @Override
    public void handleEvent(FragmentManager fragmentManager, int floor, Position position, IHandleEventCallback handleEventCallback) {

    }
}
