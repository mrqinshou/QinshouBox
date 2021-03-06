package com.qinshou.qinshoubox.music.view.fragment;

import android.Manifest;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qinshou.commonmodule.rcvdecoration.DividerDecoration;
import com.qinshou.commonmodule.util.DisplayUtil;
import com.qinshou.commonmodule.util.permissionutil.IOnRequestPermissionResultCallBack;
import com.qinshou.commonmodule.util.permissionutil.PermissionUtil;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.base.QSFragment;
import com.qinshou.qinshoubox.homepage.bean.EventBean;
import com.qinshou.qinshoubox.music.bean.MusicBean;
import com.qinshou.qinshoubox.music.contract.IMusicListContract;
import com.qinshou.qinshoubox.music.presenter.MusicListPresenter;
import com.qinshou.qinshoubox.music.view.adapter.RcvMusicAdapter;

import java.util.List;

/**
 * Description:音乐列表界面
 * Author: QinHao
 * Date: 2019/3/26 19:43
 */
public class MusicListFragment extends QSFragment<MusicListPresenter> implements IMusicListContract.IMusicListView {
    private RcvMusicAdapter mRcvMusicAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_music_list;
    }

    @Override
    public void initView() {
        RecyclerView rcvMusic = findViewByID(R.id.rcv_music);
        //设置 RecyclerView 布局管理器
        rcvMusic.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置 RecyclerView 适配器
        mRcvMusicAdapter = new RcvMusicAdapter(getContext());
        rcvMusic.setAdapter(mRcvMusicAdapter);
        //设置 RecyclerView ItemDecoration
        DividerDecoration dividerDecoration = new DividerDecoration.Builder()
                .setWidth(getResources().getDimensionPixelSize(R.dimen.px1))
                .setColor(0xFF999999)
                .setMarginTop(getResources().getDimensionPixelOffset(R.dimen.px50))
                .build();
        rcvMusic.addItemDecoration(dividerDecoration);
    }

    @Override
    public void setListener() {
    }

    @Override
    public void initData() {
        PermissionUtil.requestPermission(getChildFragmentManager(), new IOnRequestPermissionResultCallBack() {
            @Override
            public void onSuccess() {
                getPresenter().getMusicList(getContext());
            }

            @Override
            public void onFailure(List<String> deniedPermissionList) {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void handleEvent(EventBean<Object> eventBean) {
    }

    @Override
    public void setMusicList(List<MusicBean> musicList) {
        mRcvMusicAdapter.setDataList(musicList);
    }

    @Override
    public void queryMusicListFailure(String failureInfo) {

    }
}
