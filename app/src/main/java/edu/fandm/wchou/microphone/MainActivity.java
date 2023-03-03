package edu.fandm.wchou.microphone;


import static android.graphics.BlendMode.COLOR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    static String appFolderPath;
    private String timeStamp;
    private int REQUEST_CODE_PERMISSIONS = 1;
    private MediaRecorder mc = null;
    static MediaPlayer mp = null;
    public ArrayList<String> historyList = new ArrayList<>();
    public AudioAdapter adapter;
    boolean isRecording = false;
    private String audioFilePath;

    private String getPath(){

        Date date = new Date();
        timeStamp = date.toString().replace(" ", "_");
        String filePath = appFolderPath + "/myRecording@" + timeStamp +".mp4";
        audioFilePath = filePath;
        Log.d("getPath", "Wrote to " + filePath);
        return filePath;
    }

    private void promptRemoveItemOnHold(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Item")
                .setIcon(R.drawable.delete_icon)
                .setMessage("Do you want to remove this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the item from the adapter
                        String audioFile = adapter.getItem(position);
                        String toDelete = MainActivity.appFolderPath + "/" + audioFile;
                        File file = new File(toDelete);
                        if(file.exists()) {
                            file.delete();
                        }
                        ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                        adapter.remove(adapter.getItem(position));
                        view.setBackgroundColor(0);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        view.setBackgroundColor(0);

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
            historyList.add(0, "myRecording@" + timeStamp + ".mp4");
            adapter.notifyDataSetChanged();
            mc.stop();
            mc.reset();
            mc.release();
            mc = null;
        }
    }

    static void playAudio(String selected) throws FileNotFoundException {
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

//    public static boolean deleteDir(File dir) {
//        if (dir != null && dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                boolean success = deleteDir(new File(dir, children[i]));
//                if (!success) {
//                    return false;
//                }
//            }
//        }
//        return dir.delete();
//    }

    private void createFolder(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File recordings = new File(path, "MediaRecorderSample");
        appFolderPath = recordings.getAbsolutePath();
        if (!recordings.exists()) {
            recordings.mkdirs();
        }
        File[] recordingFiles = recordings.listFiles();
        for (File file : recordingFiles) {
            String toLoad = file.getAbsolutePath().replace(appFolderPath, "").replace("/","");
            if (historyList.contains(toLoad)){
                continue;
            }
            historyList.add(toLoad);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
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

        createFolder();
        lv = findViewById(R.id.record_history_lv);
        adapter = new AudioAdapter(this, historyList);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                promptRemoveItemOnHold(parent, view, position, id);
                view.setBackgroundResource(R.color.red);
                return true;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("myList", historyList);
        outState.putString("audioFilePath", audioFilePath);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isRecording && audioFilePath!=null){
            Log.d("Ondestroyed", "yoyo");
            historyList.add(0, "myRecording@" + timeStamp + ".mp4");
            adapter.notifyDataSetChanged();
            mc.stop();
            mc.reset();
            mc.release();
            File audioFile = new File(audioFilePath);
            mc = null;
        }

    }

}

