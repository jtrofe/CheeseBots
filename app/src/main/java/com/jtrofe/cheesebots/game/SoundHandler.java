package com.jtrofe.cheesebots.game;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import com.jtrofe.cheesebots.R;

/**
 * Created by MAIN on 8/14/16
 */
public class SoundHandler{

    public static int EFFECT_POP_SMALL = 0;
    public static int EFFECT_POP_MEDIUM = 1;
    public static int EFFECT_POP_LARGE = 2;
    public static int EFFECT_POP_GIANT = 3;
    public static int EFFECT_MUNCH = 4;
    public static int EFFECT_DENT = 5;

    private Context mContext;
    private SoundPool mPool;
    private int[] mSoundIds;

    public SoundHandler(Activity activity){
        mContext = activity;

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mPool = new SoundPool.Builder()
                .setMaxStreams(20)
                .setAudioAttributes(attrs)
                .build();

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        LoadSounds();
    }

    public void Destroy(){
        mPool.release();
    }

    public void LoadSounds(){
        mSoundIds = new int[10];

        mSoundIds[EFFECT_POP_SMALL] = mPool.load(mContext, R.raw.pop_small, 1);
        mSoundIds[EFFECT_POP_MEDIUM] = mPool.load(mContext, R.raw.pop_medium, 1);
        mSoundIds[EFFECT_POP_LARGE] = mPool.load(mContext, R.raw.pop_large, 1);
        mSoundIds[EFFECT_POP_GIANT] = mPool.load(mContext, R.raw.pop_giant, 1);
        mSoundIds[EFFECT_MUNCH] = mPool.load(mContext, R.raw.munch, 1);
        mSoundIds[EFFECT_DENT] = mPool.load(mContext, R.raw.dent, 1);
    }


    public void Play(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPool.play(mSoundIds[id], 1, 1, 1, 0, 1);
            }
        }).start();
    }

    public void Play(final int id, final float rate, final float volume){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPool.play(mSoundIds[id], volume, volume, 1, 0, rate);
            }
        }).start();
    }
}
