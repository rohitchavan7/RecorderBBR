package com.bbr.recorder.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bbr.recorder.R;
import com.bbr.recorder.model.Recordings;
import com.bbr.recorder.utils.AudioListAdapter;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class AudiosFragment extends Fragment {

    private AudioListAdapter mAdapter;
    private ListView listView;


    private List<Recordings> recordingArrayList = new ArrayList<>();

    private SimpleExoPlayer player;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audios, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        player = new SimpleExoPlayer.Builder(requireContext()).build();

        listView = view.findViewById(R.id.list_audios);

        recordingArrayList = new ArrayList<Recordings>();


        fetchRecordings();



    }


    private void fetchRecordings() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/RecorderBBR";
        //Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        //Log.d("Files", "Size: "+ files.length);
        if( files!=null ){

            for (int i = 0; i < files.length; i++) {

                //Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/RecorderBBR" + fileName;

                Recordings recording = new Recordings(recordingUri,fileName,false);
                recordingArrayList.add(recording);
            }


            setListView();

        }

    }

    private void setListView() {
        Collections.reverse(recordingArrayList);
        mAdapter = new AudioListAdapter(Objects.requireNonNull(getActivity()), R.layout.layout_list_audio_item, recordingArrayList );
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(requireContext(), "clicked" + recordingArrayList.get(position).getUri(), Toast.LENGTH_SHORT).show();

                MediaItem path = MediaItem.fromUri(recordingArrayList.get(position).getUri());

                player.addMediaItem(path);
                player.prepare();
                player.play();

            }
        });

    }

}