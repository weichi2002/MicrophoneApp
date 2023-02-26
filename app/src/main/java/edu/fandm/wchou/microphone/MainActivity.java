package edu.fandm.wchou.microphone;

import static android.media.MediaRecorder.AudioSource.MIC;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private String appFolderPath;
    private String timeStamp;
    private ArrayAdapter<String> historyAdapter;
    private int REQUEST_CODE_PERMISSIONS = 1;
    private MediaRecorder mc = null;
    private MediaPlayer mp = null;
    public ArrayList<String> historyList = new ArrayList<>();
    boolean isRecording = false;

    private String getPath(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File recordings = new File(path, "MediaRecorderSample");
        appFolderPath = recordings.getAbsolutePath();
        if (!recordings.exists()) {
            recordings.mkdirs();
        }
        Date date = new Date();
        timeStamp = date.toString().replace(" ", "_");
        String filePath = appFolderPath + "/myRecording@" + timeStamp +".mp4";
        Log.d("Write file", "wrote to" + filePath);
        return filePath;
    }

    private void recordAudio(){

        if(isRecording==false){
            Log.d("Record Audio", "Start Recording");
            try {
                mc = new MediaRecorder();
                mc.setAudioSource(MediaRecorder.AudioSource.MIC);
                mc.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                mc.setOutputFile(getPath());
                mc.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mc.setAudioSamplingRate(44100);
                mc.prepare();
                mc.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Log.d("Record Audio", "Stop Recording");
            historyList.add("myRecording@" + timeStamp + ".mp4");
            historyAdapter.notifyDataSetChanged();
            mc.stop();
            mc.reset();
            mc.release();
            mc = null;
        }
    }

    private void playAudio(String selected) throws FileNotFoundException {
        String recordingPath = appFolderPath + "/" + selected;
        Log.d("Play Audio", recordingPath);

        mp = new MediaPlayer();
        FileInputStream fis = null;
        try {
            File directory = new File(recordingPath);
            fis = new FileInputStream(directory);
            mp.setDataSource(fis.getFD());
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignore) {
                }
            }
        }
        mp.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission denied. Permission needed to start the app", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            historyList = savedInstanceState.getStringArrayList("myList");
        }
;
        this.lv = findViewById(R.id.record_history_lv);
        this.historyAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList);
        lv.setAdapter(historyAdapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) (lv.getItemAtPosition(position));
                try {
                    playAudio(selected);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ImageView recordBtn = findViewById(R.id.record_btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for permission first
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
                }else{
                    if(isRecording==false){
                        recordBtn.setBackground(getResources().getDrawable(R.drawable.mic_recording));
                        recordAudio();
                        isRecording = true;

                    }else{
                        recordBtn.setBackground(getResources().getDrawable(R.drawable.mic_default));
                        recordAudio();
                        isRecording = false;
                    }

                }
            }
        });

    }

    //preserve the data
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("myList", historyList);
    }


}

