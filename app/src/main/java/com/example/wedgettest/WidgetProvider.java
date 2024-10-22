package com.example.wedgettest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "WidgetProvider";
    private static HashSet<Integer> hashSet = new HashSet<>();
    private String imageDirectotyPath = Environment.getExternalStorageDirectory().toString() + "/Drawable/Animation/app/fff";
//    private List<String> imagePaths = getAllImagesInDirectory(imageDirectotyPath);
    private Bitmap bitmap;
    private static final int[] ResId = {
            R.drawable.a000, R.drawable.a001, R.drawable.a002, R.drawable.a003, R.drawable.a004, R.drawable.a005,
            R.drawable.a006, R.drawable.a007, R.drawable.a008, R.drawable.a009, R.drawable.a010, R.drawable.a011,
            R.drawable.a012, R.drawable.a013, R.drawable.a014, R.drawable.a015, R.drawable.a016, R.drawable.a017,
            R.drawable.a018, R.drawable.a019, R.drawable.a020, R.drawable.a021, R.drawable.a022, R.drawable.a023,
            R.drawable.a024, R.drawable.a025, R.drawable.a026, R.drawable.a027, R.drawable.a028, R.drawable.a029,
            R.drawable.a030, R.drawable.a031, R.drawable.a032, R.drawable.a033, R.drawable.a034, R.drawable.a035,
            R.drawable.a036, R.drawable.a037, R.drawable.a038, R.drawable.a039, R.drawable.a040, R.drawable.a041,
            R.drawable.a042, R.drawable.a043, R.drawable.a044, R.drawable.a045, R.drawable.a046
    };
    private static final int[] intro = {R.drawable.war, R.drawable.war2};
    private static int iCur = -1;

    private final String IMAGE = "image";

    private Context mContext;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "onUpdate: Creating Widget OK");
        mContext = context;
        for (int id : appWidgetIds) {
            hashSet.add(id);
        }
        updateAllWidget(context, appWidgetManager, hashSet);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "onDeleted: ===============");
        for (int id : appWidgetIds) {
            hashSet.remove(id);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "onEnabled: ==================");
        // 启动 JobIntentService
        Intent intent = new Intent(context, WidgetService.class);
        intent.setAction(Constant.ACTION_UPDATE_ALL);
        WidgetService.enqueueWork(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "onDisabled: ================");
        // 这里可以添加清理资源的代码
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String getAction = intent.getAction();
        mContext = context;
        if (Constant.ACTION_UPDATE_ALL.equals(getAction)) {
            updateAllWidget(context, AppWidgetManager.getInstance(context), hashSet);
        }

        if ("ACTION_CHANGE_BACKGROUND".equals(intent.getAction())) {
            Log.d(TAG, "onReceive: 按钮已点击");
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            setInt(IMAGE, getInt(IMAGE) + 1); // 在成功更新后再增加
            Log.d(TAG, "onReceive: ===============================");
            if (getInt(IMAGE) >= 2) {
                setInt(IMAGE, 0);
            }
            Log.d(TAG, "onReceive: " + getInt(IMAGE));
            if (getInt("image") < intro.length) { // 修正条件
                remoteViews.setInt(R.id.layout, "setBackgroundResource", intro[getInt(IMAGE)]);
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                for (int id : hashSet) {
                    manager.updateAppWidget(id, remoteViews); // 更新所有微件
                }
            }
        }
    }

    private void updateAllWidget(Context context, AppWidgetManager manager, HashSet<Integer> set) {
        Log.d(TAG, "updateAllWidget: ===================");
        int appId;
        Iterator<Integer> iterator = set.iterator();
        iCur = iCur + 1 >= ResId.length ? 0 : iCur + 1;
        while (iterator.hasNext()) {
            appId = iterator.next();
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            Log.d(TAG, "updateAllWidget: " + remoteViews);
            remoteViews.setImageViewResource(R.id.imageview_1, ResId[iCur]);
            // 设置切换背景图的点击事件
            remoteViews.setOnClickPendingIntent(R.id.button_1, getChangeBackgroundPendingIntent(context, R.id.button_1));
            manager.updateAppWidget(appId, remoteViews);
        }


//            bitmap = loadImage(imagePaths.get(iCur));
//            while (iterator.hasNext()) {
//                appId = iterator.next();
//                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_list);
//                Log.d(TAG, "updateAllWidget: " + remoteViews);
//                remoteViews.setImageViewBitmap(R.id.imageview_1, bitmap);
//                // 设置切换背景图的点击事件
//                remoteViews.setOnClickPendingIntent(R.id.button_1, getChangeBackgroundPendingIntent(context, R.id.button_1));
//                manager.updateAppWidget(appId, remoteViews);
//        }
//            bitmap.recycle();
    }

    private PendingIntent getChangeBackgroundPendingIntent(Context context, int buttonid) {
        Log.d(TAG, "getChangeBackgroundPendingIntent: ==========================");
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction("ACTION_CHANGE_BACKGROUND");
        intent.putExtra("BUTTON_ID", buttonid);
        return PendingIntent.getBroadcast(context, buttonid, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private int getInt(String name) {
        Log.d(TAG, "getInt: " + mContext);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        int data = sharedPreferences.getInt(name, 0);
        return data;
    }

    private void setInt(String name, int value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.apply();
    }

    public static boolean hasWidgets() {
        return !hashSet.isEmpty();
    }








    public Bitmap loadImage(String imagePath){
        return BitmapFactory.decodeFile(imagePath);
    }

    public static List<String> getAllImagesInDirectory(String directoryPath) {
        List<String> imagePaths = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // 如果是图片文件，则将其路径添加到列表中
                        imagePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return imagePaths;
    }
}