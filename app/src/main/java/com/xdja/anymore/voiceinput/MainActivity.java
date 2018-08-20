package com.xdja.anymore.voiceinput;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.xdja.anymore.library.audio.AudioRecordConfig;
import com.xdja.anymore.library.audio.AudioRecorder;
import com.xdja.anymore.library.audio.OnAudioRecordListener;
import com.xdja.anymore.library.widget.WaveLineView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnRecord,btnPause,btnStop;
    private AudioRecorder mAudioRecorder;
    private WaveLineView wave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnRecord = findViewById(R.id.btn_record);
        btnRecord.setOnClickListener((v)-> mAudioRecorder.start());
        btnStop = findViewById(R.id.btn_stop);
        btnStop.setOnClickListener((v)->mAudioRecorder.stop());
        btnPause = findViewById(R.id.btn_pause);
        btnPause.setOnClickListener((v -> mAudioRecorder.pause()));
        wave = findViewById(R.id.wave);
        wave.startAnimator();
        checkPermissions();
        init();
    }


    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissions.size() > 0){
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[]{}),1);
        }
    }



    private void init() {
        mAudioRecorder = new AudioRecorder();
        mAudioRecorder.init(new AudioRecordConfig.Builder().build());
        mAudioRecorder.setFilePath(getFilesDir().getAbsolutePath()+System.currentTimeMillis()+"_test.pcm");
        mAudioRecorder.addOnAudioRecordListener(new OnAudioRecordListener() {
            @Override
            public void onStart() {
                Toast.makeText(MainActivity.this,"start",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecording() {
                Toast.makeText(MainActivity.this,"recording",Toast.LENGTH_SHORT);
            }

            @Override
            public void onPause() {
                Toast.makeText(MainActivity.this,"pause",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRestart() {
                Toast.makeText(MainActivity.this,"reStart",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStop() {
                Toast.makeText(MainActivity.this,"stop",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVolumeChange(int volume) {
                Log.d("MainActivity","volume:"+volume);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    boolean flag = true;
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED){
                            flag = false;
                        }
                    }
                    if (!flag){
                        Toast.makeText(this,"你必须授予这些权限才能运行",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }else {
                    Toast.makeText(this,"你必须授予这些权限才能运行",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
}
