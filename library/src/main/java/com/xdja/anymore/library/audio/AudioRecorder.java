package com.xdja.anymore.library.audio;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xdja.anymore.library.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by anymore on 2018/8/18.
 */
public class AudioRecorder implements IAudioRecord{
    private static final String TAG = "AudioRecorder";
    private static final int LISTEN_START = 1;
    private static final int LISTEN_RECORDING = 2;
    private static final int LISTEN_RESTART = 3;
    private static final int LISTEN_PAUSE = 4;
    private static final int LISTEN_STOP = 5;
    private AudioRecord mAudioRecord;
    private AudioRecordConfig mConfig;
    private String mFilePath;//保存文件路径
    private ExecutorService mExecutor;
    private volatile State mState;
    private AudioFormatHelper mFormatHelper;
    private List<File> mPcmFiles;
    private String mCacheFileDict;
    private List<OnAudioRecordListener> mListeners;
    private Handler mHandler;//对应当前AudioRecorder实例化时候所在线程，而非主线程
    private boolean deletePcmFile = true;//合成wav文件后是否删除pcm源文件，默认删除
    private ExecutorService mFileWriteService;//文件写入线程
    public AudioRecorder(){
        mExecutor = Executors.newSingleThreadExecutor();
        mFileWriteService = Executors.newSingleThreadExecutor();
        mPcmFiles = new ArrayList<>();
        mListeners = new ArrayList<>();
        mHandler = new Handler(Looper.myLooper());
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
        this.mCacheFileDict = filePath.substring(0,filePath.lastIndexOf('/'));
    }

    public void setDeletePcmFile(boolean deletePcmFile) {
        this.deletePcmFile = deletePcmFile;
    }

    @Override
    public void init(AudioRecordConfig config) {
        this.mConfig = config;
        mAudioRecord = new AudioRecord(mConfig.getAudioSource(),mConfig.getSimpleRateInHz(),mConfig.getChannelConfig(),mConfig.getAudioFormat(),mConfig.getBufferSize());
        mState = State.IDLE;
        mFormatHelper = new AudioFormatHelper(mConfig);
        Log.d(TAG,"init");
    }

    @Override
    public void start(){
//        mPcmFiles.clear();//每次开始前，清空录音片段队列
        if (mState != State.RECORDING){
            mExecutor.execute(this::startAudioRecord);
            Log.d(TAG,"start");
            if (mState == State.PAUSE){
                listen(LISTEN_RESTART);
            }else {
                listen(LISTEN_START);
            }
        }else {
            Log.e(TAG,"当前正在录制音频");
        }

    }

    private void startAudioRecord(){
        if (mState == State.IDLE || mState == State.PAUSE){//空闲状态才能开始录音
//            File file = new File(mFilePath);//录音保存文件
            File file = new File(mCacheFileDict,"audio_recorder_"+mPcmFiles.size()+".pcm");
            FileOutputStream fos = null;
            try {
                if (file.exists()){
                    file.delete();
                }
                file.createNewFile();
                fos = new FileOutputStream(file);
                mAudioRecord.startRecording();
                mState = State.RECORDING;
                mHandler.post(() -> listen(LISTEN_RECORDING));
                byte[] buff = new byte[mConfig.getBufferSize()];
                while (mState == State.RECORDING){
                    int len = mAudioRecord.read(buff,0,buff.length);
                    int volum = 0;
                    for (int i = 0; i < len;i++){
                        volum +=buff[i]*buff[i];
                    }
                    final double dB = 10*Math.log10(volum/(double)len);
                    mHandler.post(() -> onVolumeChanged((int) dB));
                    fos.write(buff,0,len);
                    fos.flush();
                }
                mAudioRecord.stop();
//                if (mState == State.STOP){
//                    mergePcmFiles();
//                    mAudioRecord.release();
//                }
//                mState = State.IDLE;
//                String wav = mFilePath.replace("pcm","wav");
//                mFormatHelper.pcmToWav(mFilePath,wav);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mPcmFiles.add(file);
            }
            if (mState == State.STOP){
                mergePcmFiles();
                mAudioRecord.release();
            }
        }else {
            Log.d(TAG,mState.name());
        }

    }

    @Override
    public void pause() {
        if (mState == State.RECORDING){
            mState = State.PAUSE;
            Log.d(TAG,"pause");
            listen(LISTEN_PAUSE);
        }else {
            Log.e(TAG,"当前没有在录音，暂停毛线？");
        }
    }

    @Override
    public void stop(){
        if (mState == State.RECORDING){
            mState = State.STOP;
            Log.d(TAG,"stop");
        }else if (mState == State.PAUSE){
            mState = State.STOP;
            mExecutor.execute(this::mergePcmFiles);
            Log.d(TAG,"stop");
        }else {
            Log.e(TAG,"当前没有在录音，停止毛线？");
            return;
        }
    }

    @Override
    public void destory() {
        mAudioRecord.release();
        mAudioRecord = null;
        mState = State.DESTORY;
    }

    private void mergePcmFiles(){
        File file = FileUtil.mergePcmFiles(mPcmFiles, mFilePath);
        if (file != null){
            String wav = mFilePath.replace("pcm","wav");
            mFormatHelper.pcmToWav(file.getAbsolutePath(),wav);
            for (File f:mPcmFiles){//删除缓存pcm片段
                if (f != null){
                    f.delete();
                }
            }
            mPcmFiles.clear();
            if (deletePcmFile){
                file.delete();
            }
            mHandler.post(() -> listen(LISTEN_STOP));
        }

    }

    @Override
    public void addOnAudioRecordListener(OnAudioRecordListener listener) {
        mListeners.add(listener);
    }

    public State getState() {
        return mState;
    }

    private void listen(int listenCommand){
        if (mListeners != null && mListeners.size() > 0){
            for (OnAudioRecordListener listener : mListeners) {
                switch (listenCommand){
                    case LISTEN_START:
                        listener.onStart();
                        break;
                    case LISTEN_RECORDING:
                        listener.onRecording();
                        break;
                    case LISTEN_PAUSE:
                        listener.onPause();
                        break;
                    case LISTEN_RESTART:
                        listener.onRestart();
                        break;
                    case LISTEN_STOP:
                        listener.onStop();
                        break;
                }
            }
        }
    }

    private void onVolumeChanged(int volume){
        if (mListeners != null && mListeners.size() > 0){
            for (OnAudioRecordListener listener : mListeners) {
                listener.onVolumeChange(volume);
            }
        }
    }

    public enum State{
        IDLE,//空闲
        RECORDING,//录音中
        PAUSE,//暂停(还可以继续，用start恢复录音)
        STOP,//停止，即结束一次录音
        ERROR,//出错
        DESTORY//销毁，释放资源的状态
    }

    private static class PcmFileWriteThread implements Runnable{
        private String mPcmFileName;
        private OnPcmFileWriteListener mListener;
        private List<byte[]> mDataColletList = Collections.synchronizedList(new ArrayList<>());//一级缓存，对外收集字节流
        private List<byte[]> mDateWriteList = Collections.synchronizedList(new ArrayList<>());//二级缓存，对内写字节流到文件
        private File mFile;
        private boolean flag;
        public PcmFileWriteThread(String pcmFileName, OnPcmFileWriteListener listener) {
            this.mPcmFileName = pcmFileName;
            this.mListener = listener;
            init();
        }

        private void init() {
            flag = true;
            mFile = new File(mPcmFileName);
            try {
                if (mFile.exists()){
                    mFile.delete();
                }
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(mFile);
                while (flag){
                    if (mDateWriteList.size()== 0 && mDataColletList.size() > 0){
                        mDateWriteList.add(mDataColletList.remove(0));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public interface OnPcmFileWriteListener{
            void onWriteFinished();
        }
    }
}
