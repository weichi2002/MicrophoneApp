package edu.fandm.wchou.microphone;

import static android.media.MediaRecorder.AudioSource.MIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1;

    private MediaRecorder mc = null;

    public int recordingNumber = 0;

    public ArrayList<String> historyList = new ArrayList<>(Arrays.asList("Recording_Jan15_15:03 PM", "Recording_Jan15_15:02 PM"));
    boolean isRecording = false;
    boolean isPlaying = false;
    private void recordAudio(){

        mc = new MediaRecorder();
        mc.reset();
        mc.setAudioSource(MediaRecorder.AudioSource.MIC);
        mc.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        String state = Environment.getExternalStorageDirectory().getAbsolutePath();
        if(!state.equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(MainActivity.this, "External Storage is not available", Toast.LENGTH_SHORT).show();
            return;
        }else{
            File recordings = new File(state, "MediaRecorderSample");
            if (!recordings.exists()) {
                recordings.mkdirs();
            }
            //use time stamp as unique id
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalTime currentTime = LocalTime.now();
                mc.setOutputFile(recordings.getAbsolutePath() + "/my_recording_" + currentTime.toString()+".mp4");
            }else{
                mc.setOutputFile(recordings.getAbsolutePath() + "/my_recording_" + String.valueOf(recordingNumber) + ".mp4");
                recordingNumber+=1;
            }
        }

        if(isRecording==false){
            Log.d("Record Audio", "Start Recording");
            isRecording = true;
            try {
                mc.prepare();
                mc.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("Record Audio", "Stop Recording");
            isRecording = false;
            mc.stop();
            mc.reset();
            mc.release();
            mc = null;
            //stop recording... and write the file to the external storage
        }
    }

    private void playAudio(){
        if(isPlaying==false){
            isPlaying = true;

        }else{
            isPlaying = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, access external storage and record audio
            } else {
                // Permission denied, display a message to the user
                Toast.makeText(this, "Permission denied. Permission needed to start the app", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
;
        ListView lv = findViewById(R.id.record_history_lv);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList));

        ImageView recordBtn = findViewById(R.id.record_btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for permission first
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // Request permission to access external storage and record audio
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
                }else{
                    recordAudio();
                }
            }
        });

    }


}

