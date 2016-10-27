package com.jimei.k3wise_mobile.Component;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.jimei.k3wise_mobile.LoginActivity;
import com.jimei.k3wise_mobile.R;

/**
 * Created by lee on 2016/9/9.
 */
public class AudioPlayer {
    private static SoundPool soundPool;
    private static LoginActivity activity;
    private static int AttentionSoundID;
    private static int SuccessSoundID;
    private static int WarningSoundID;

    /**
     * Verify device's API before to load soundpool
     * @return
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void buildSoundPool(LoginActivity activity1) {
        activity = activity1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(5);//传入音频数量
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//设置音频流的合适的属性
            builder.setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes
            soundPool = builder.build();
        } else {
            soundPool = buildBeforeAPI21();
        }
        AttentionSoundID = soundPool.load(activity, R.raw.attention, 1);
        SuccessSoundID = soundPool.load(activity, R.raw.success, 1);
        WarningSoundID = soundPool.load(activity, R.raw.warning, 1);
    }

    private static SoundPool buildBeforeAPI21() {
        return new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
    }

    public static void playAttentionSound(){
        soundPool.play(AttentionSoundID,1, 1, 0, 0, 1);
    }

    public static void playSuccessSound(){
        soundPool.play(SuccessSoundID,1, 1, 0, 0, 1);
    }

    public static void playWarningSound(){
        soundPool.play(WarningSoundID,1, 1, 0, 0, 1);
    }

    public static void Release(){
        soundPool.release();
    }
}
