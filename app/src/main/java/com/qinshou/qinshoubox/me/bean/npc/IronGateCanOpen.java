package com.qinshou.qinshoubox.me.bean.npc;


import androidx.fragment.app.FragmentManager;

import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.me.bean.IHandleEventCallback;
import com.qinshou.qinshoubox.me.bean.Position;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/4/23 13:44
 * Description:可以打开的铁门
 */
public class IronGateCanOpen implements INpc {
    @Override
    public int getResourceId() {
        return R.drawable.magic_tower_npc_gate_iron_1;
    }

    @Override
    public void handleEvent(FragmentManager fragmentManager, int floor, Position position, IHandleEventCallback handleEventCallback) {

    }
}