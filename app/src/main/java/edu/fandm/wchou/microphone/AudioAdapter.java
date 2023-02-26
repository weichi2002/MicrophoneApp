package edu.fandm.wchou.microphone;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AudioAdapter extends ArrayAdapter<String> implements View.OnClickListener{

    public AudioAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_custom_listview, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.custom_recordingName);
        ImageView itemButton = convertView.findViewById(R.id.custom_playButton);

        itemText.setText(item);

        itemButton.setTag(position);
        itemButton.setOnClickListener(this);

        return convertView;
    }
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        String audioFile = getItem(position);
        try {
            MainActivity.playAudio(audioFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}