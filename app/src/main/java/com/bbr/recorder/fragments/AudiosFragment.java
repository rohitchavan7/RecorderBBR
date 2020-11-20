package com.bbr.recorder.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bbr.recorder.R;
import com.bbr.recorder.model.Recordings;
import com.bbr.recorder.utils.SongAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class AudiosFragment extends Fragment {



    private ArrayList<Recordings> recorded_audios = new ArrayList<Recordings>();;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private MediaPlayer mediaPlayer;
    private Handler myHandler = new Handler();

    private SeekBar audioProgress;
    private TextView audioName;
    private LottieAnimationView playPause;
    private ImageView mPlayNext, mPlayBack;
    private Random random = new Random();
    private int defaultNumber = 0;
    private boolean isMediaPlayerRunning = false;

    private boolean play = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audios, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView =  view.findViewById(R.id.list_audios);
        audioName = view.findViewById(R.id.audio_name);
        audioProgress = view.findViewById(R.id.seek_bar_audio);
        playPause = view.findViewById(R.id.play_pause);
        mPlayBack = view.findViewById(R.id.btn_back);
        mPlayNext = view.findViewById(R.id.btn_next);
        songAdapter = new SongAdapter(requireContext(), recorded_audios);
        recyclerView.setAdapter(songAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);


        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final ConstraintLayout b, View view, final Recordings obj, int position) {
                if (mediaPlayer !=null ){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();

                }

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(obj.getUri());
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                    isMediaPlayerRunning = true;
                                    play = true;
                                    playPause.setProgress(1);
                                    audioProgress.setProgress(0);
                                    audioProgress.setMax(mediaPlayer.getDuration());
                                    audioName.setText(obj.getFileName());


                                }
                            });



                        }catch (Exception e){

                        }
                    }

                };
                myHandler.postDelayed(runnable,100);

            }
        });

        checkUserPermission();


        if (!recorded_audios.isEmpty()){

            if (defaultNumber == 0){
                defaultNumber = random.nextInt(recorded_audios.size());
                audioName.setText(recorded_audios.get(defaultNumber).getFileName());

            }

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isMediaPlayerRunning){
                        if (play){
                            mediaPlayer.pause();
                            playPause.setProgress(0);
                            play = false;
                        }else {
                            mediaPlayer.start();
                            playPause.setProgress(1);
                            play = true;

                        }

                    }else {
                        if (mediaPlayer !=null ){
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();

                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                    mediaPlayer.setDataSource(recorded_audios.get(defaultNumber).getUri());
                                    mediaPlayer.prepareAsync();
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                            playPause.setProgress(1);
                                            isMediaPlayerRunning = true;
                                            play = true;
                                            audioProgress.setProgress(0);
                                            audioProgress.setMax(mediaPlayer.getDuration());
                                            audioName.setText(recorded_audios.get(defaultNumber).getFileName());


                                        }
                                    });



                                }catch (Exception e){

                                }
                            }

                        };
                        myHandler.postDelayed(runnable,100);

                    }
                }
            });

            mPlayNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext(defaultNumber);
                }
            });

            mPlayBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playBack(defaultNumber);
                }
            });
        }


        Thread t = new runThread();
        t.start();

    }

    private void playBack(int num){
        if (num == 1){
            defaultNumber = 2;
        }else {
            defaultNumber = num-1;
        }

        if (mediaPlayer !=null ){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(recorded_audios.get(defaultNumber).getUri());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            playPause.setProgress(1);
                            isMediaPlayerRunning = true;
                            play = true;
                            audioProgress.setProgress(0);
                            audioProgress.setMax(mediaPlayer.getDuration());
                            audioName.setText(recorded_audios.get(defaultNumber).getFileName());


                        }
                    });



                }catch (Exception e){

                }
            }

        };
        myHandler.postDelayed(runnable,100);

    }

    private void playNext(int num){
        if (num == recorded_audios.size()){
            defaultNumber = 1;
        }else {
            defaultNumber = num+1;
        }


        if (mediaPlayer !=null ){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(recorded_audios.get(defaultNumber).getUri());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            playPause.setProgress(1);
                            isMediaPlayerRunning = true;
                            play = true;
                            audioProgress.setProgress(0);
                            audioProgress.setMax(mediaPlayer.getDuration());
                            audioName.setText(recorded_audios.get(defaultNumber).getFileName());


                        }
                    });



                }catch (Exception e){

                }
            }

        };
        myHandler.postDelayed(runnable,200);

    }

    public class runThread extends Thread {

        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mediaPlayer != null) {

                    audioProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isMediaPlayerRunning){
                                audioProgress.setProgress(mediaPlayer.getCurrentPosition());

                            }
                        }
                    });

                }
            }
        }

    }


    private void checkUserPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
                return;
            }
        }
        fetchRecordings();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchRecordings();
                }else{
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    checkUserPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

    }


    private void fetchRecordings() {
        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/RecorderBBR";

        File directory = new File(path);
        File[] files = directory.listFiles();

        if( files!=null ){

            for (int i = 0; i < files.length; i++) {


                String name = files[i].getName();
                String url = files[i].getPath();

                Recordings s = new Recordings(url,name,false);
                recorded_audios.add(s);

            }



        }

    }



}