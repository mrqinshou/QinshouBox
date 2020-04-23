package com.qinshou.qinshoubox.me.bean.floor;


import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.me.bean.CaseBean;
import com.qinshou.qinshoubox.me.bean.Position;
import com.qinshou.qinshoubox.me.enums.Building;
import com.qinshou.qinshoubox.me.enums.Monster;
import com.qinshou.qinshoubox.me.enums.Npc;
import com.qinshou.qinshoubox.me.enums.Prop;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:第 7 层
 * Created by 禽兽先生
 * Created on 2017/4/26
 */

public class Floor7 extends AbsFloor {

    @Override
    public int getFloor() {
        return 7;
    }

    @Override
    public List<List<CaseBean>> setData() {
        List<List<CaseBean>> floor7 = new ArrayList<>();
//        List<CaseBean> row0 = new ArrayList<>();
//        row0.add(new GoUpstairs());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Road());
//        row0.add(new Wall());
//        row0.add(new Wall());
//        row0.add(new Wall());
//        floor7.add(row0);
//
//        List<CaseBean> row1 = new ArrayList<>();
//        row1.add(new Wall());
//        row1.add(new Wall());
//        row1.add(new Road());
//        row1.add(new CaseBean(IMonster.HONG_BIAN_FU, R.drawable.magic_tower_monster_hong_bian_fu));
//        row1.add(new Wall());
//        row1.add(new BlueGate());
//        row1.add(new Wall());
//        row1.add(new CaseBean(IMonster.KU_LOU_DUI_ZHANG, R.drawable.magic_tower_monster_ku_lou_dui_zhang));
//        row1.add(new Road());
//        row1.add(new Wall());
//        row1.add(new Wall());
//        floor7.add(row1);
//
//        List<CaseBean> row2 = new ArrayList<>();
//        row2.add(new Wall());
//        row2.add(new Road());
//        row2.add(new CaseBean(IMonster.HONG_BIAN_FU, R.drawable.magic_tower_monster_hong_bian_fu));
//        row2.add(new BlueGem());
//        row2.add(new Wall());
//        row2.add(new CaseBean(IMonster.BAI_YI_WU_SHI, R.drawable.magic_tower_monster_bai_yi_wu_shi));
//        row2.add(new Wall());
//        row2.add(new RedGem());
//        row2.add(new CaseBean(IMonster.KU_LOU_DUI_ZHANG, R.drawable.magic_tower_monster_ku_lou_dui_zhang));
//        row2.add(new Road());
//        row2.add(new Wall());
//        floor7.add(row2);
//
//        List<CaseBean> row3 = new ArrayList<>();
//        row3.add(new Road());
//        row3.add(new Road());
//        row3.add(new Wall());
//        row3.add(new Wall());
//        row3.add(new Wall());
//        row3.add(new CaseBean(Npc.GATE_IRON_CLOSE, R.drawable.magic_tower_npc_gate_iron_1));
//        row3.add(new Wall());
//        row3.add(new Wall());
//        row3.add(new Wall());
//        row3.add(new Road());
//        row3.add(new Road());
//        floor7.add(row3);
//
//        List<CaseBean> row4 = new ArrayList<>();
//        row4.add(new Road());
//        row4.add(new Road());
//        row4.add(new BlueGate());
//        row4.add(new CaseBean(IMonster.BAI_YI_WU_SHI, R.drawable.magic_tower_monster_bai_yi_wu_shi));
//        row4.add(new CaseBean(Npc.GATE_IRON_OPEN, R.drawable.magic_tower_npc_gate_iron_1));
//        row4.add(new CaseBean(IProp.XING_YUN_SHI_ZI_JIA, R.drawable.magic_tower_prop_xing_yun_shi_zi_jia));
//        row4.add(new CaseBean(Npc.GATE_IRON_CLOSE, R.drawable.magic_tower_npc_gate_iron_1));
//        row4.add(new CaseBean(IMonster.BAI_YI_WU_SHI, R.drawable.magic_tower_monster_bai_yi_wu_shi));
//        row4.add(new BlueGate());
//        row4.add(new Road());
//        row4.add(new Road());
//        floor7.add(row4);
//
//        List<CaseBean> row5 = new ArrayList<>();
//        row5.add(new Road());
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new CaseBean(Npc.GATE_IRON_CLOSE, R.drawable.magic_tower_npc_gate_iron_1));
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new Wall());
//        row5.add(new Road());
//        floor7.add(row5);
//
//        List<CaseBean> row6 = new ArrayList<>();
//        row6.add(new Road());
//        row6.add(new Wall());
//        row6.add(new SmallBloodBottle());
//        row6.add(new RedGem());
//        row6.add(new Wall());
//        row6.add(new CaseBean(IMonster.BAI_YI_WU_SHI, R.drawable.magic_tower_monster_bai_yi_wu_shi));
//        row6.add(new Wall());
//        row6.add(new BlueGem());
//        row6.add(new SmallBloodBottle());
//        row6.add(new Wall());
//        row6.add(new Road());
//        floor7.add(row6);
//
//        List<CaseBean> row7 = new ArrayList<>();
//        row7.add(new Road());
//        row7.add(new Wall());
//        row7.add(new YellowKey());
//        row7.add(new SmallBloodBottle());
//        row7.add(new Wall());
//        row7.add(new BlueGate());
//        row7.add(new Wall());
//        row7.add(new SmallBloodBottle());
//        row7.add(new YellowKey());
//        row7.add(new Wall());
//        row7.add(new Road());
//        floor7.add(row7);
//
//        List<CaseBean> row8 = new ArrayList<>();
//        row8.add(new Road());
//        row8.add(new Wall());
//        row8.add(new Wall());
//        row8.add(new BlueKey());
//        row8.add(new BlueKey());
//        row8.add(new BigBloodBottle());
//        row8.add(new BlueKey());
//        row8.add(new BlueKey());
//        row8.add(new Wall());
//        row8.add(new Wall());
//        row8.add(new Road());
//        floor7.add(row8);
//
//        List<CaseBean> row9 = new ArrayList<>();
//        row9.add(new Road());
//        row9.add(new Road());
//        row9.add(new Wall());
//        row9.add(new Wall());
//        row9.add(new Wall());
//        row9.add(new CaseBean(Npc.GATE_RED, R.drawable.magic_tower_npc_gate_red_1));
//        row9.add(new Wall());
//        row9.add(new Wall());
//        row9.add(new Wall());
//        row9.add(new Road());
//        row9.add(new Road());
//        floor7.add(row9);
//
//        List<CaseBean> row10 = new ArrayList<>();
//        row10.add(new Wall());
//        row10.add(new Road());
//        row10.add(new Road());
//        row10.add(new YellowGate());
//        row10.add(new GoDownstairs());
//        row10.add(new Road());
//        row10.add(new Road());
//        row10.add(new YellowGate());
//        row10.add(new Road());
//        row10.add(new Road());
//        row10.add(new Wall());
//        floor7.add(row10);

        return floor7;
    }

    @Override
    public void fromUpstairsToThisFloor() {
        resetWarriorPosition(new Position(0, 1));
    }

    @Override
    public void fromDownstairsToThisFloor() {
        resetWarriorPosition(new Position(10, 5));
    }
}
