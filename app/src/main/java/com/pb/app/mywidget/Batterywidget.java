package com.pb.app.mywidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;

/**
 * 电量widget
 * @author mayong
 *
 */
public class Batterywidget extends AppWidgetProvider {
    
    /**刷新时间变量*/
    public static long REF_TIME = 1;
    /**刷新时间类型（分钟/秒钟）*/
    public static long REF_TYPE = DateUtils.SECOND_IN_MILLIS;
    private static boolean isFirst = true;
    
    private SharedPreferences BatteryInfo;
    private static Thread Ref_Thread;
	@Override
	public void onEnabled(final Context context) {
		super.onEnabled(context);
		Log.d("xmg","onEnabled");
		BatteryInfo = context.getSharedPreferences("BatteryInfo",Context.MODE_PRIVATE);
		long rad_type = BatteryInfo.getLong("rad_type", 0);
		long ref_time = BatteryInfo.getLong("ref_time", 0);
		if(rad_type!=0){
			REF_TYPE = rad_type;
		}
		if(ref_time!=0){
			REF_TIME = ref_time;
		}
		isFirst = true;
		Ref_Thread = new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cancelRuntime(context);
				startMyService(context);
			}
		});
	}
	/**更新 部件*/
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		Log.d("xmg","onUpdate" + "   isFirst="+isFirst);
		startMyService(context);
	}
	/**
	 * 启用闹钟管理器，设定“定时刷新”时间
	 * @param context
	 */
	public static void startMyService(final Context context){
		long interval;
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
        Intent intent = new Intent(context, BackService.class);  
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);  
        if(Ref_Thread == null){
        	Ref_Thread = new Thread(new Runnable() {
    			
    			public void run() {
    				try {
    					Thread.sleep(3000);
    				} catch (InterruptedException e) {
    					e.printStackTrace();
    				}
    				cancelRuntime(context);
    				startMyService(context);
    			}
    		});
        }
        if(isFirst){
        	interval = 1000;
        	isFirst = false;
        	Ref_Thread.start();
        }else{
        	interval = REF_TYPE * REF_TIME;//定时刷新时间
        }
        
        long firstWake = System.currentTimeMillis() + interval;  
        am.setRepeating(AlarmManager.RTC,firstWake, interval, pendingIntent);
    }

	/**
	 * 关闭“定时刷新”
	 * @param context
	 */
	public static void cancelRuntime(Context context){
	    
	    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
        Intent intent = new Intent(context, BackService.class);  
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);  
        am.cancel(pendingIntent);
        
    }
}