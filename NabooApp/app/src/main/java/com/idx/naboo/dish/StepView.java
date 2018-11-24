package com.idx.naboo.dish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idx.naboo.R;


/**
 * Created by sunny on 18-5-9.
 */

public class StepView extends LinearLayout {
    //步骤内容
    private TextView content;
    //步骤图片
    private ImageView imageView;
    private Context mContext;
    //步骤
    private TextView index;
    public StepView(Context context){
        super(context);
        mContext=context;
        initView(context);
    }
    public StepView(Context context, @Nullable AttributeSet attributeSet){
        super(context,attributeSet);
        initView(context);
    }
    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.dish_step_content,this,true);
        content=findViewById(R.id.step_content);
        imageView=findViewById(R.id.step_image);
        index=findViewById(R.id.step_index);
    }

    public void setText(String content1,String picUrl,String index1){
        content.setText(content1);
        if (!picUrl.equals("")) {
            Glide.with(mContext).load(picUrl).into(imageView);
        }else {
            imageView.setImageResource(R.drawable.dish);
        }
        index.setText(index1);
    }

}
