package com.idx.naboo.video.data;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.idx.naboo.R;
import com.idx.naboo.video.VideoContentInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 18-4-16.
 */

public class VideoContentAdapter extends RecyclerView.Adapter<VideoContentAdapter.VideoViewHolder> {
    private static  final  String TAG=VideoContentAdapter.class.getSimpleName();
    private List<Movie> movies=new ArrayList<>();
    private VideoContentInterface videoContentInterface;
    private Context mContext;
    public VideoContentAdapter(Context context,List<Movie>  movies ){
        this.mContext=context;
        this.movies=movies;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.video_content,null);
        VideoViewHolder videoViewHolder=new VideoViewHolder(view,videoContentInterface);
        videoViewHolder.movie_id=view.findViewById(R.id.movie_index);
        videoViewHolder.movie_image=view.findViewById(R.id.movie_image);
        videoViewHolder.movie_name=view.findViewById(R.id.movie_name);
        videoViewHolder.movie_grade=view.findViewById(R.id.movie_grade);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
            holder.movie_id.setText(Integer.toString(position+1));
//            holder.movie_image.setImageUrl(movies.get(position).getIconaddress());
            Glide.with(mContext).load(movies.get(position).getIconaddress()).into(holder.movie_image);
            holder.movie_name.setText(movies.get(position).getTitle());
            holder.movie_name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            holder.movie_grade.setText(movies.get(position).getGrade()+"分");
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //电影海报
        private ImageView movie_image;
        //电影名称
        private TextView movie_name;
        //电影评分
        private TextView movie_grade;
        //电影列表位置
        private TextView movie_id;
        //电影集数
        private TextView movie_update;
        public VideoViewHolder(View view,VideoContentInterface contentInterface){
            super(view);
            videoContentInterface=contentInterface;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (videoContentInterface!=null) {
                videoContentInterface.onVideoItem(view, getPosition());
            }else {
                Log.i(TAG, "onClick: videoContentInterface为空");
            }
        }
    }

    public void setVideoContentInterface(VideoContentInterface contentInterface){
        this.videoContentInterface=contentInterface;
    }
}
