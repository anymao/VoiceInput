package com.xdja.anymore.library.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.xdja.anymore.library.audio.AudioRecordConfig;
import com.xdja.anymore.library.audio.AudioRecorder;
import com.xdja.anymore.library.audio.OnAudioRecordListener;

/**
 * Created by anymore on 2018/8/18.
 */
public class VoiceInputButton extends AppCompatButton implements OnAudioRecordListener {
    private static final String TAG = "VoiceInputButton";
    private Context mContext;
    private AudioRecorder mAudioRecorder;


    public VoiceInputButton(Context context) {
        this(context,null);
    }

    public VoiceInputButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VoiceInputButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        setText("按下录音");
    }

    private void init() {
        mAudioRecorder = new AudioRecorder();
        mAudioRecorder.init(new AudioRecordConfig.Builder().build());
        mAudioRecorder.setFilePath(mContext.getFilesDir()+"/VoiceInputButton.pcm");
        mAudioRecorder.addOnAudioRecordListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://0
                mAudioRecorder.start();
                return true;
            case MotionEvent.ACTION_UP://1
                mAudioRecorder.stop();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onStart() {
        setText("开始录音");
    }

    @Override
    public void onRecording() {
        setText("正在录音");
    }

    @Override
    public void onPause() {
        setText("暂停");
    }

    @Override
    public void onRestart() {
        setText("重新开始");
    }

    @Override
    public void onStop() {
        setText("按下录音");
    }

    @Override
    public void onVolumeChange(int volume) {

    }
}
