package com.pb.app.mywidget.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.pb.app.mywidget.R;
import com.pb.app.mywidget.TimeWidget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mayong on 2021/12/7
 */
public class TimeService extends Service {

    private Timer mTimer;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("xmg", "TimeService onCreate");
    }

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("xmg", "TimeService onStartCommand 服务启动");
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateView();
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateView(){
//        Log.i("xmg", "updateView");
        String time = sdf.format(new Date());
        String date = sdf1.format(new Date());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.time_widget);
        rv.setTextViewText(R.id.tv_widget_time, time);
        rv.setTextViewText(R.id.tv_widget_date, date);
        AppWidgetManager manager =   AppWidgetManager.getInstance(getApplicationContext());
        ComponentName cn = new ComponentName(getApplicationContext(), TimeWidget.class);
        manager.updateAppWidget(cn, rv);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("xmg", "TimeService Destroy");
        try {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        } catch (Exception e) {
            //do nothing
        }
    }

}
