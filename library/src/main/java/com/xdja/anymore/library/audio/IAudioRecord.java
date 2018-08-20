package com.xdja.anymore.library.audio;

/**
 * Created by anymore on 2018/8/19.
 */
public interface IAudioRecord {
    void init(AudioRecordConfig config);
    void start();
    void pause();
    void stop();
    void destory();
    void addOnAudioRecordListener(OnAudioRecordListener listener);
}
