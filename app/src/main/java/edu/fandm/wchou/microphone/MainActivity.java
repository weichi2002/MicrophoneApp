package edu.fandm.wchou.microphone;

import static android.media.MediaRecorder.AudioSource.MIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSIONS = 1;

    public ArrayList<String> historyList = new ArrayList<>(Arrays.asList("Recording_Jan15_15:03 PM", "Recording_Jan15_15:02 PM"));

    MediaRecorder mc = new MediaRecorder();
    MediaPlayer mp = new MediaPlayer();
    boolean isRecording = false;
    boolean isPlaying = false;
    private void recordAudio(){
        if(isRecording==false){
            Log.d("Record Audio", "Start Recording");
            isRecording = true;
            //start recording or something
        }else{
            isRecording = false;
            Log.d("Record Audio", "Stop Recording");
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
                Toast.makeText(this, "Permission denied. Permission needed to start the app", Toast.LENGTH_LONG).show();
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

