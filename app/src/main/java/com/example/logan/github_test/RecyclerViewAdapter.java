package com.example.logan.github_test;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> songImageURL = new ArrayList<>();
    private ArrayList<String> creatorNames = new ArrayList<>();
    private ArrayList<String> songTitles = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> creatorNames, ArrayList<String> songTitles, ArrayList<String> songImageURL){
        this.mContext = context;
        this.creatorNames = creatorNames;
        this.songTitles = songTitles;
        this.songImageURL = songImageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.albumImage.setImageResource(R.drawable.logo);
        holder.songTitle.setText(songTitles.get(position));
        holder.creatorName.setText(creatorNames.get(position));
    }

    @Override
    public int getItemCount() {
        return creatorNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView albumImage;
        TextView creatorName;
        TextView songTitle;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.albumCover);
            creatorName = itemView.findViewById(R.id.songCreator);
            songTitle = itemView.findViewById(R.id.songTitle);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }
}
