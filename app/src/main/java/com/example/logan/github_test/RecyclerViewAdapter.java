package com.example.logan.github_test;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

    private ArrayList<Song> songs = new ArrayList<>();
    private Context mContext;
    private int activeHolderPosition = -1;

    private View.OnClickListener clickListener;
    

    public RecyclerViewAdapter(Context context, ArrayList<Song> songs){
        this.mContext = context;
        this.songs = songs;

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeHolderPosition = (int)v.getTag();
                v.setBackgroundResource(R.color.colorPrimary);
                class playRunner implements Runnable{
                    private View v;
                    private playRunner(View v){
                        this.v = v;
                    }

                    @Override
                    public void run() {
                        MusicPlayer.play(RecyclerViewAdapter.this.songs.get(activeHolderPosition).getSongURL());
                    }
                }


                new Thread(new playRunner(v)).start();


            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.albumImage.setImageResource(R.drawable.logo);
        holder.songTitle.setText(songs.get(position).getTitle());
        holder.creatorName.setText(songs.get(position).getAuthor());

        if(activeHolderPosition  == position){
            holder.parentLayout.setBackgroundResource(R.color.colorPrimary);
        }
        else{
            holder.parentLayout.setBackgroundResource(R.color.colorForeground);
        }

        holder.parentLayout.setTag(position);
        holder.parentLayout.setOnClickListener(clickListener);
        Picasso.get().load(songs.get(position).getImageURL()).into(holder.albumImage);
    }

    @Override
    public int getItemCount() {
        return songs.size();
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
