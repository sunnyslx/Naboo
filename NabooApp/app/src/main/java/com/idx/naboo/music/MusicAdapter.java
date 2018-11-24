package com.idx.naboo.music;

import android.graphics.Color;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import com.idx.naboo.R;
import com.idx.naboo.music.data.Song;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 18-4-12.
 */

public class MusicAdapter extends BaseAdapter {

    private int mCurrentItem=0;

    private boolean isClick=false;

    private List<Song> mSong=new ArrayList<>();
    public MusicAdapter (List<Song> mSong){
        this.mSong=mSong;
    }
    @Override
    public int getCount() {
        return mSong.size();
    }

    @Override
    public Song getItem(int i) {
        return mSong.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view==null){
            viewHolder=new ViewHolder();
            view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.music_list,null);
            viewHolder.musicIdex=view.findViewById(R.id.music_list_id);
            viewHolder.musicName=view.findViewById(R.id.music_list_title);
            viewHolder.musicSinger=view.findViewById(R.id.music_list_singer);
            viewHolder.imageView=view.findViewById(R.id.music_list_image);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)view.getTag();
        }
        if (i<9){
            viewHolder.musicIdex.setText("0"+Integer.toString(i+1));
        }else {
            viewHolder.musicIdex.setText(Integer.toString(i+1));
        }
        if (mSong!=null && mSong.size()>0) {
            try {
                viewHolder.musicName.setText(mSong.get(i).getName());
                if (mSong.get(i).getSinger()!=null && mSong.get(i).getSinger().length>0) {
                    viewHolder.musicSinger.setText(mSong.get(i).getSinger()[0]);
                }
                if (mCurrentItem == i && isClick) {
                    viewHolder.musicName.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.musicSinger.setTextColor(Color.parseColor("#ffffff"));
                    viewHolder.musicIdex.setVisibility(View.GONE);
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                    viewHolder.imageView.setImageResource(R.mipmap.music_sound_icon);
                    viewHolder.musicName.setSelected(true);
                    viewHolder.musicName.setFocusable(true);
                    viewHolder.musicName.setSingleLine(true);
                    viewHolder.musicName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    viewHolder.musicName.setMarqueeRepeatLimit(-1);
                } else {
                    viewHolder.musicName.setTextColor(Color.parseColor("#4dffffff"));
                    viewHolder.musicSinger.setTextColor(Color.parseColor("#4dffffff"));
                    viewHolder.musicIdex.setTextColor(Color.parseColor("#4dffffff"));
                    viewHolder.musicName.setEllipsize(TextUtils.TruncateAt.END);
                    viewHolder.musicName.setFocusable(false);
                    viewHolder.musicName.setSingleLine(true);
                    viewHolder.imageView.setVisibility(View.GONE);
                    viewHolder.musicIdex.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return view;
    }
    public class ViewHolder{
        //音乐列表
        private TextView musicIdex;
        //音乐名称
        private TextView musicName;
        //歌手
        private TextView musicSinger;
        private ImageView imageView;
    }

    public void setCurrentItem(int currentItem){
        this.mCurrentItem=currentItem;
    }
    public void setClick(boolean click){
        this.isClick=click;
    }
}
