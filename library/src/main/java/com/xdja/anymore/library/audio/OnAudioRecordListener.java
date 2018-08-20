package com.xdja.anymore.library.audio;

/**
 * Created by anymore on 2018/8/19.
 */
public interface OnAudioRecordListener {
    void onStart();
    void onRecording();
    void onPause();
    void onRestart();
    void onStop();
    void onVolumeChange(int volume);
}
