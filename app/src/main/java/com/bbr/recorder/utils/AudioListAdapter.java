package com.bbr.recorder.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bbr.recorder.MainActivity;
import com.bbr.recorder.R;
import com.bbr.recorder.model.Recordings;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AudioListAdapter extends ArrayAdapter<Recordings> {

    private LayoutInflater mInflater;
    private List<Recordings> recordingsList = null;
    private int layoutResource;
    private Context mContext;


    public AudioListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Recordings> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.recordingsList = objects;
    }

    private static class ViewHolder{
        TextView name;
        ImageView mPlayPause;
        ConstraintLayout constraintLayout;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.audio_name);
            holder.mPlayPause = (ImageView) convertView.findViewById(R.id.play_pause);
            holder.constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.view_holder);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getFileName());





        return convertView;
    }




}
