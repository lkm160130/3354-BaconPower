package com.example.logan.github_test;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Song> songs;
    private int activeHolderPosition = -1;

    private View.OnClickListener clickListener;
    private OnBottomReachedListener onBottomReachedListener;


    RecyclerViewAdapter(final MainActivity context, ArrayList<Song> songs){
        this.songs = songs;

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeHolderPosition = (int)v.getTag();
                v.setBackgroundResource(R.color.colorPrimary);
                notifyDataSetChanged();
                context.songLoadingCircleView.setVisibility(View.VISIBLE);
                context.durationText.setVisibility(View.GONE);
                MusicPlayer.getInstance().play(RecyclerViewAdapter.this.songs.get(activeHolderPosition),context);
            }
        };

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.albumImage.setImageResource(R.drawable.logo);
        holder.songTitle.setText(songs.get(position).getTitle());
        holder.creatorName.setText("@" + songs.get(position).getAuthor().getName());

        if(activeHolderPosition  == position){
            holder.parentLayout.setBackgroundResource(R.color.colorPrimary);
        }
        else{
            holder.parentLayout.setBackgroundResource(R.color.colorForeground);
        }

        holder.parentLayout.setTag(position);
        holder.parentLayout.setOnClickListener(clickListener);
        Picasso.get().load(songs.get(position).getImageURL()).resize(400,400).into(holder.albumImage);

        if (position == songs.size() - 1){
            onBottomReachedListener.onBottomReached(position);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).hashCode();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView albumImage;
        TextView creatorName;
        TextView songTitle;
        RelativeLayout parentLayout;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.albumCover);
            creatorName = itemView.findViewById(R.id.songCreator);
            songTitle = itemView.findViewById(R.id.songTitle);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    void resetActiveHolder(){
        activeHolderPosition = -1;
    }

    int getActiveHolderPosition(){
        return activeHolderPosition;
    }

    void setActiveHolderPosition(int activeHolderPosition){
        this.activeHolderPosition = activeHolderPosition;
        notifyDataSetChanged();
    }

    void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }


}
