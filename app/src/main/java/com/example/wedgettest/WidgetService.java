package com.example.wedgettest;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class WidgetService extends JobIntentService {

    private static final int JOB_ID = 1000;
    private String TAG = "WidgetService";
    private Handler handler = new Handler();
    private boolean isRunning = false; // 用于标识是否在运行

    // 使用 enqueueWork 方法来启动服务
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WidgetService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (Constant.ACTION_UPDATE_ALL.equals(intent.getAction())) {
            if (!isRunning) {
                isRunning = true;
                startUpdating(); // 启动更新任务
            }
        }
    }

    private void startUpdating() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 发送广播以更新 Widget
                Intent updateIntent = new Intent(Constant.ACTION_UPDATE_ALL);
                updateIntent.setPackage("com.example.wedgettest");
                sendBroadcast(updateIntent);
                handler.postDelayed(this, 30);

                // 检查是否还有微件存在
                if (!WidgetProvider.hasWidgets()) {
                    Log.d(TAG, "run: ==== 空");
                    stopUpdating(); // 如果没有微件，停止更新
                }
            }
        }, 30);
    }

    private void stopUpdating() {
        Log.d(TAG, "stopUpdating: == 清空");
        handler.removeCallbacksAndMessages(null); // 清除所有回调
        isRunning = false; // 更新运行状态
    }
}