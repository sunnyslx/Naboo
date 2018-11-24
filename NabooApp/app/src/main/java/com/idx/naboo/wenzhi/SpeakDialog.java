package com.idx.naboo.wenzhi;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.idx.naboo.R;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by derik on 18-1-13.
 */

public class SpeakDialog {

    private static final String TAG = SpeakDialog.class.getName();
    private final WindowManager mWindowManager;
    private View mView;
    private boolean isAddedView = false;
    private final WindowManager.LayoutParams mParams;
    private GifImageView readyView;
    private GifImageView speakingView;
    private TextView result;

    public SpeakDialog(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mView = View.inflate(context, R.layout.voice_dialog, null);
        result = mView.findViewById(R.id.recognize_result);
        readyView = mView.findViewById(R.id.ready_view);
        speakingView = mView.findViewById(R.id.speaking_view);
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.format = PixelFormat.TRANSLUCENT;

        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView();
    }

    private void addView() {
        mWindowManager.addView(mView, mParams);
        isAddedView = true;
    }

    public void showReady() {
        if (!isAddedView) {
            addView();
        }
        result.setText("");
        readyView.setVisibility(View.VISIBLE);
        speakingView.setVisibility(View.GONE);
    }

    public void showSpeaking() {
        if (!isAddedView) {
            addView();
        }
        result.setText("");
        readyView.setVisibility(View.GONE);
        speakingView.setVisibility(View.VISIBLE);
    }

    public void setText(String text) {
        result.setText(text.substring(0, text.length() - 1));
    }

    public void dismiss() {
        if (isAddedView) {
            mWindowManager.removeView(mView);
        }
        isAddedView = false;
    }
}
