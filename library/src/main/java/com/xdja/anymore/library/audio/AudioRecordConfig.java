package com.xdja.anymore.library.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by anymore on 2018/8/19.
 */
public class AudioRecordConfig {
    private int mAudioSource;//音频来源
    private int mSimpleRateInHz;//采样率
    private int mChannelConfig;
    private int mAudioFormat;
    private int mBufferSize;

    private AudioRecordConfig(int mAudioSource, int mSimpleRateInHz, int mChannelConfig, int mAudioFormat, int mBufferSize) {
        this.mAudioSource = mAudioSource;
        this.mSimpleRateInHz = mSimpleRateInHz;
        this.mChannelConfig = mChannelConfig;
        this.mAudioFormat = mAudioFormat;
        this.mBufferSize = mBufferSize;
    }

    public int getAudioSource() {
        return mAudioSource;
    }

    public int getSimpleRateInHz() {
        return mSimpleRateInHz;
    }

    public int getChannelConfig() {
        return mChannelConfig;
    }

    public int getAudioFormat() {
        return mAudioFormat;
    }

    public int getBufferSize() {
        return mBufferSize;
    }

    public static class Builder{
        private int mAudioSource;//音频来源->mic
        private int mSimpleRateInHz;//采样率44.1kHz
        private int mChannelConfig;
        private int mAudioFormat;
        private int mBufferSize;
        public Builder() {
            mAudioSource = MediaRecorder.AudioSource.MIC;//音频来源->mic
            mSimpleRateInHz = 44100;//采样率44.1kHz
//            mSimpleRateInHz = 22050;
            mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
            mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
//            mAudioFormat = AudioFormat.ENCODING_PCM_8BIT;
        }

        public Builder setAudioSource(int audioSource) {
            this.mAudioSource = audioSource;
            return this;
        }

        public Builder setSimpleRateInHz(int simpleRateInHz) {
            this.mSimpleRateInHz = simpleRateInHz;
            return this;
        }

        public Builder setChannelConfig(int channelConfig) {
            this.mChannelConfig = channelConfig;
            return this;
        }

        public Builder setAudioFormat(int audioFormat) {
            this.mAudioFormat = audioFormat;
            return this;
        }

        public AudioRecordConfig build(){
            mBufferSize = AudioRecord.getMinBufferSize(mSimpleRateInHz,mChannelConfig,mAudioFormat);
            return new AudioRecordConfig(mAudioSource,mSimpleRateInHz,mChannelConfig,mAudioFormat,mBufferSize);
        }

    }
}
