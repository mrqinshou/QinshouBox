package com.qinshou.qinshoubox.me.bean.npc;


import androidx.fragment.app.FragmentManager;

import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.me.bean.IHandleEventCallback;
import com.qinshou.qinshoubox.me.bean.Position;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/4/23 13:44
 * Description:第 2 层的商人
 */
public class BusinessManFloor2 implements INpc {
    @Override
    public int getResourceId() {
        return R.drawable.magic_tower_npc_shang_ren;
    }

    @Override
    public void handleEvent(FragmentManager fragmentManager, int floor, Position position, IHandleEventCallback handleEventCallback) {

    }
}