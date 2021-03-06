package com.qinshou.mediamodule;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Author: QinHao
 * Email:qinhao@jeejio.com
 * Date: 2020/4/29 14:24
 * Description:类描述
 */
public class QsIjkPlayer extends BasePlayer {
    private final IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceHolder mSurfaceHolder;

    public QsIjkPlayer(Context context) {
        super(context);
        mContext = context;
        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);

        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        mIjkMediaPlayer.setVolume(1.0f, 1.0f);
        mIjkMediaPlayer.setOnPreparedListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
//                if (mMediaPlayerListener != null) {
//                    mMediaPlayerListener.onPrepared();
//                }
                Log.i("daolema", "onPrepared");
                mIjkMediaPlayer.start();
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onStart();
                }
            }
        });
        mIjkMediaPlayer.setOnErrorListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer, int i, int i1) {
                Log.i("daolema", "onError--->" + i);
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onError(new Exception("播放失败"));
                }
                return true;
            }
        });
        mIjkMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                if (mMediaPlayerListener != null) {
                    mMediaPlayerListener.onComplete();
                }
            }
        });
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        mIjkMediaPlayer.setDisplay(surfaceHolder);
    }

//    @Override
//    public void setDataSource(Uri uri) {
//        try {
//            mIjkMediaPlayer.setDataSource(mContext, uri);
//        } catch (IOException e) {
//            return;
//        }
//    }
//
//    @Override
//    public void prepare() {
//        mIjkMediaPlayer.prepareAsync();
//    }

    @Override
    public void play(Uri uri) {
        mIjkMediaPlayer.reset();
        mIjkMediaPlayer.setDisplay(mSurfaceHolder);
        try {
            mIjkMediaPlayer.setDataSource(mContext, uri);
        } catch (IOException e) {
            Log.i("daolema", "e--->" + e.getMessage());
            if (mMediaPlayerListener != null) {
                mMediaPlayerListener.onError(e);
            }
            return;
        }
        mIjkMediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        mIjkMediaPlayer.start();
        if (mMediaPlayerListener != null) {
            mMediaPlayerListener.onStart();
        }
    }

    @Override
    public void pause() {
        mIjkMediaPlayer.pause();
        if (mMediaPlayerListener != null) {
            mMediaPlayerListener.onPause();
        }
    }

    @Override
    public void stop() {
        mIjkMediaPlayer.stop();
        if (mMediaPlayerListener != null) {
            mMediaPlayerListener.onStop();
        }
    }

    @Override
    public void release() {
        mIjkMediaPlayer.release();
    }

    @Override
    public void seekTo(long position) {
        mIjkMediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return mIjkMediaPlayer.isPlaying();
    }

    @Override
    public long getCurrentPosition() {
        return mIjkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mIjkMediaPlayer.getDuration();
    }
}
