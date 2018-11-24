package com.idx.naboo.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Context 全局上下文
 * newPath wz文件路径
 */

public class FileCopyUtils {
    private static String TAG = "acdefg";

    public static void copyFilesToSD(Context context) {
        Log.i(TAG, "准备进行文件创建");
        try {
            //判断sd卡是否存在
            boolean sdCardExist = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                //获取根目录路径
                String newPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                //判断wz是否存在
                File wzDir = new File(newPath + "/wz");
                if (!wzDir.exists()) {
                    wzDir.mkdir();
                    //读取assets下数据
                    InputStream model = context.getAssets().open("model.dat");
                    String path1 = wzDir.getAbsolutePath() + "/model.dat";
                    //写文件进去
                    writeToSDRoot(model, path1);
                    //读取assets下数据
                    InputStream test = context.getAssets().open("test.pcm");
                    String path2 = wzDir.getAbsolutePath() + "/test.pcm";
                    //写文件进去
                    writeToSDRoot(test, path2);
                } else {
                    Log.i(TAG, "copyFilesToSD: 已经存在此文件夹了");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "捕捉到了错误");
        }
    }

    private static void writeToSDRoot(InputStream is, String newPath) {
        try {
            FileOutputStream fos = new FileOutputStream(newPath);
            byte[] bytes = new byte[1024];
            int len = 0;
            //循环从输入流读取 buffer字节
            while ((len = is.read(bytes)) != -1) {
                //将读取的输入流写入到输出流
                fos.write(bytes, 0, len);
            }
            fos.flush();//刷新缓冲区
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
