package edu.fandm.wchou.microphone;

import static android.media.MediaRecorder.AudioSource.MIC;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public ArrayList<String> historyList = new ArrayList<>(Arrays.asList("Recording_Jan15_15:03 PM", "Recording_Jan15_15:02 PM"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView lv = findViewById(R.id.record_history_lv);
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, historyList));

        //Initialize Media Recorder
        MediaRecorder mc = new MediaRecorder();
        MediaPlayer mp;
        ImageView recordBtn;
        ImageView playBtn;
        boolean isRecording = false;
        boolean isPlaying = false;

        int seconds = 0;
        String path=null;
        int dummySecond = 0;
        int playableSeconds=0;
    }
}