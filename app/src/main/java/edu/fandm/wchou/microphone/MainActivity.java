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

    private int STORAGE_PERMISSION_CODE = 1;

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

    private void requestPermisison(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("These permissions are needed in order to record audio and create and play audio on your device")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults){
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
;
        ListView lv = findViewById(R.id.record_history_lv);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList));

        boolean hasReadExternal = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        boolean hasWriteExternal = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
        boolean hasRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED;

        ImageView recordBtn = findViewById(R.id.record_btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for permission first
                if(hasRecordAudio == false || hasWriteExternal == false || hasReadExternal == false) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,
                            }, 1);
                }else{
                    Toast
                    recordAudio();
                }
            }
        });
    }

}