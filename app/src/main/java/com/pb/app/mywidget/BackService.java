
package com.pb.app.mywidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.RemoteViews;

public class BackService extends Service{

    private static Context context;
    /**电池状态*/
    public static String BATTERY_STATUS;
    /**电池健康度*/
    public static String BATTERY_HEALTH;
    /**电池剩余电电量*/
    public static String BATTERY_LEVEL = "0";
    /**电池最大值*/
    public static String BATTERY_SCALE = "100";
    /**连接的电源插座*/
    public static String BATTERY_PLUGGED;
    /**电池电压*/
    public static String BATTERY_VOLTAGE;
    /**电池温度*/
    public static String BATTERY_TEMPERATURE;
    /**电池类型*/
    public static String BATTERY_TECHNOLOGY;
    /**是否开始改变图标*/
    public boolean isChange = false;
    /**是否改变图标标识*/
    public boolean isChange_flag = true;
    public int changeNum;
    /**图标资源*/
    final int srcIDS[] = {R.drawable.f20,R.drawable.f30,R.drawable.f45,R.drawable.f60,
			R.drawable.f75,R.drawable.f90,R.drawable.f100};
    RemoteViews views;
//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 10:
//				StopCharging();
//				break;
//			default:
//				Log.d("xiaomage","srcIDS("+msg.what+")");
////				views.setImageViewResource(R.id.content, srcIDS[msg.what]);
//				break;
//			}
//		}
//	};
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        Log.d("***BackService***", "onCreate");
    }

    @Override
    public void onDestroy() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
//        Log.d("***BackService***", "onStart");
//        int level = intent.getIntExtra("level", 0);
//        BATTERY_LEVEL = "" + level;
//        Log.d("***BackService***", "level="+level);
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
//        views.setTextViewText(R.id.content, BATTERY_LEVEL);
        GetAndUpdate(context);
//        if(isChange)
//        	views.setImageViewResource(R.id.content, srcIDS[changeNum]);
        
    }
    /**
     * 获得数据，更新widget
     */
    public void GetAndUpdate(Context context) {
        ComponentName thisWidget = new ComponentName(context, Batterywidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews updateViews = buildUpdate(context);
        manager.updateAppWidget(thisWidget, updateViews);
    }

    /**
     * 更新widget
     */
    public RemoteViews buildUpdate(Context context) {
        // 得到widget的view
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
//        flowerViews = new RemoteViews(context.getPackageName(), R.layout.main);
        Intent in = new Intent(context, SettingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, in, 0);
        // 当点击 widget中的Item时 会启动的 Activity
        views.setOnClickPendingIntent(R.id.content, pendingIntent);
        // 更新电池信息
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
//        Log.d("xiaomage","buildUpdate ("+changeNum+")");
        if(!isChange){
        	// 更新widget上信息
        	int batteryLevel = Integer.parseInt(BATTERY_LEVEL);
	        if(batteryLevel>=90){
	        	views.setImageViewResource(R.id.content, R.drawable.f100);
	        }else if(batteryLevel>=75&&batteryLevel<90){
	            views.setImageViewResource(R.id.content, R.drawable.f90);
	        }else if(batteryLevel>=60&&batteryLevel<75){
	            views.setImageViewResource(R.id.content, R.drawable.f75);
	        }else if(batteryLevel>=45&&batteryLevel<60){
	            views.setImageViewResource(R.id.content, R.drawable.f60);
	        }else if(batteryLevel>=30&&batteryLevel<45){
	            views.setImageViewResource(R.id.content, R.drawable.f45);
	        }else if(batteryLevel>=20&&batteryLevel<30){
	            views.setImageViewResource(R.id.content, R.drawable.f30);
	        }else if(batteryLevel<20){
	            views.setImageViewResource(R.id.content, R.drawable.f20);
	        }
        }else{
        	views.setImageViewResource(R.id.content, srcIDS[changeNum]);
        }
        views.setTextViewText(R.id.text, BATTERY_LEVEL + "%");
		if("100".equals(BATTERY_LEVEL)){
        	views.setTextViewText(R.id.text, "满电");
        }
        return views;
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
               
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
//                boolean present = intent.getBooleanExtra("present", false);
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
//                int icon_small = intent.getIntExtra("icon-small", 0);
                int plugged = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                String technology = intent.getStringExtra("technology");

                String statusString = "";
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
//                        statusString = "unknown";
                        statusString = "未知";
                        isChange = false;
                        isChange_flag = true;
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
//                        statusString = "charging";
                        statusString = "充电中";
                        isChange = true;
                        StartCharging();
                        isChange_flag = false;
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
//                        statusString = "discharging";
                        statusString = "放电中";
                        isChange = false;
                        isChange_flag = true;
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
//                        statusString = "not charging";
                        statusString = "未充电";
                        isChange = false;
                        isChange_flag = true;
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
//                        statusString = "full";
                        statusString = "满电";
                        isChange = false;
                        isChange_flag = true;
                        break;
                }
                String healthString = "";
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
//                        healthString = "unknown";
                        healthString = "未知";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
//                        healthString = "good";
                        healthString = "良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthString = "overheat";
                        healthString = "过热";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
//                        healthString = "dead";
                        healthString = "失灵";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
//                        healthString = "voltage";
                        healthString = "电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
//                        healthString = "unspecified failure";
                        healthString = "检测失败";
                        break;
                }

                String acString = "";
                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
//                        acString = "plugged ac";
                        acString = "交流电";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
//                        acString = "plugged usb";
                        acString = "USB";
                        break;
                    default:
                    	acString = "未插电源";
    					break;
                }
//                Log.d("==backService==", "voltage="+voltage+"   temperature="+temperature);
                
                StringBuffer sb = new StringBuffer(String.valueOf(voltage));
                String c = "未检测";
                if(sb.length()>1){
	                String a = sb.substring(0, 1);
	                String b = sb.substring(1, sb.length());
	                c = a+"."+b+"伏";
                }else{
                	c = sb.toString()+"伏";
                }
                
                String z = "未检测";
                StringBuffer sb1 = new StringBuffer(String.valueOf(temperature));
                if(sb1.length()>2){
                	String x = sb1.substring(0, 2);
	                String y = sb1.substring(2, sb1.length());
	                z = x+"."+y+"℃";
                }else{
                	z = sb1.toString()+"℃";
                }
                
                SettingActivity.batteryInfo[0] = BATTERY_STATUS = statusString;
                SettingActivity.batteryInfo[1] = BATTERY_HEALTH = healthString;
                SettingActivity.batteryInfo[2] = BATTERY_LEVEL = String.valueOf(level);
                SettingActivity.batteryInfo[3] = BATTERY_SCALE = String.valueOf(scale);
                SettingActivity.batteryInfo[4] = BATTERY_PLUGGED = acString;
                SettingActivity.batteryInfo[5] = BATTERY_VOLTAGE = c;
                SettingActivity.batteryInfo[6] = BATTERY_TEMPERATURE = z;
                SettingActivity.batteryInfo[7] = BATTERY_TECHNOLOGY = technology;
//                Log.v("level", BATTERY_LEVEL);
            }
        }
    };
    public void StartCharging(){
    	if(isChange_flag){
//			Log.d("xiaomage","in StartCharging()");
			new Thread(new Runnable() {
				public void run() {
					while (isChange) {
//						Log.d("xiaomage","in while(true)");
						for(int i=0;i<srcIDS.length;i++){
							try {
								Thread.currentThread().sleep(800l);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
//							handler.sendEmptyMessage(i);
							changeNum = i;
//							Log.d("xiaomage","sendEmptyMessage("+i+")");
						}
					}
					
				}
			}).start();
    	}
    }
//    public void StopCharging(){
//    	isChange = false;
//    }
    public class MyBinder extends Binder {
        BackService getService() {
            return BackService.this;
        }
    }
}
