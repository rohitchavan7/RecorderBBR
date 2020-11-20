package com.bbr.recorder.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bbr.recorder.R;


import java.io.File;
import java.io.IOException;



public class RecorderFragment extends Fragment {

    private static final String LOG_TAG = "RecorderFragment";

    private MediaRecorder mRecorder;
    private String fileName ;

    private boolean isRecording = false;
    private boolean isTimerRunning = false;
    long mTimeWhenStopped = 0;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private EditText mEnterName;
    private CardView mStartRecord, mPauseRecord;
    private Chronometer mTimer;
    private TextView mTxtSave, mTxtPause;
    private LottieAnimationView mWave, mPlayPause;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recorder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        verifyStoragePermissions(requireActivity());
        setupUI(view.findViewById(R.id.layout_record));

        mEnterName = view.findViewById(R.id.editText_name);
        mStartRecord = view.findViewById(R.id.cardView_record);
        mPauseRecord = view.findViewById(R.id.pause);
        mTimer = view.findViewById(R.id.timer);
        mTxtSave = view.findViewById(R.id.txt_save);
        mTxtPause = view.findViewById(R.id.txt_pause);
        mWave = view.findViewById(R.id.animationView);
        mPlayPause = view.findViewById(R.id.play);

        prepareForRecording();

        //start record
        mStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording){
                    stopRecording();
                    isRecording = false;
                }else {
                    startRecording();
                    mPlayPause.playAnimation();
                }



            }
        });


        //pause record
        mPauseRecord.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (isTimerRunning){
                    stopTimer();
                    mWave.pauseAnimation();
                    pauseRecording();
                }else {
                    mWave.playAnimation();
                    resumeTimer();
                    resumeRecording();

                }

            }
        });


        //restrict user to enter space in name
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        mEnterName.setFilters(new InputFilter[] { filter });
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // permission to prompt
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //on start screen
    private void prepareForRecording() {
        mPauseRecord.setVisibility(View.GONE);
        mTxtPause.setVisibility(View.GONE);
        mTxtSave.setVisibility(View.GONE);
        mWave.setProgress(0);
        mPlayPause.setProgress(0);

    }


    private void startRecording() {
        fileName = null;
        mEnterName.setEnabled(false);

        //default dir creating
        File file = new File(Environment.getExternalStorageDirectory(), "RecorderBBR");
        if (!file.exists()) {
            file.mkdirs();
        }

        if (mEnterName.getText().toString().isEmpty()){
            fileName = String.valueOf(System.currentTimeMillis());

        }else {
            fileName = String.valueOf(mEnterName.getText().toString() + "_" + System.currentTimeMillis());
        }


        File f = new File(Environment.getExternalStoragePublicDirectory("RecorderBBR") + File.separator + fileName +".mp3");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder = new MediaRecorder();
        mRecorder.reset();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mRecorder.setOutputFile(f);
        }
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mTimer.setBase(SystemClock.elapsedRealtime());
            mTimer.start();
            isTimerRunning = true;
            isRecording = true;
            mPauseRecord.setVisibility(View.VISIBLE);
            mTxtSave.setVisibility(View.VISIBLE);
            mTxtPause.setVisibility(View.VISIBLE);
            mWave.playAnimation();
            Toast.makeText(requireContext(), "Recording.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed" + e);

            Toast.makeText(requireContext(), "failed try again.", Toast.LENGTH_SHORT).show();

        }

        mRecorder.start();




    }

    private void stopRecording() {
        try{
            mRecorder.stop();
            mRecorder.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        mWave.pauseAnimation();
        mEnterName.setEnabled(true);
        Toast.makeText(requireContext(), "Recording saved successfully.", Toast.LENGTH_SHORT).show();
        prepareForRecording();
        resetTimer();
        mEnterName.setText("");



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void pauseRecording() {
        try{
            mRecorder.pause();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void resumeRecording() {
        try{
            mRecorder.resume();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View focusedView = activity.getCurrentFocus();

        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard(requireActivity());
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void resumeTimer() {
        mTimer.setBase(SystemClock.elapsedRealtime() - mTimeWhenStopped);
        mTimer.start();
        isTimerRunning = true;

    }

    private void stopTimer() {
        mTimer.stop();
        mTimeWhenStopped = SystemClock.elapsedRealtime() - mTimer.getBase();
        isTimerRunning = false;
    }

    private void resetTimer() {
        mTimer.setBase(SystemClock.elapsedRealtime());
        mTimer.stop();
        isTimerRunning = false;
    }




}