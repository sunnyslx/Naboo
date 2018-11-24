package com.idx.naboo.user.personal_center;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {
    private Intent intent;
    private static final String TAG = "Main2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main2);

        intent = getIntent();
        Log.d(TAG,intent.getStringExtra("name"));
        Log.d(TAG,intent.getStringExtra("phone"));
        Log.d(TAG,intent.getStringExtra("select"));
        Log.d(TAG,intent.getStringExtra("detailed"));

    }
}
