package com.pb.app.mywidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("xmg", "######AlarmReceiver#####");
		Log.d("xmg", "action="+intent.getAction());
		startAirPlanAction(context, intent.getAction());
	}
	//转换飞行模式
	public void startAirPlanAction(Context context, String action){

		if(AlarmToAirPortModel.start_action.equals(action)){
			Toast.makeText(context, "飞行模式启动中...", Toast.LENGTH_LONG).show();
			boolean isEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
			if(!isEnabled){
				Toast.makeText(context, "飞行启动", Toast.LENGTH_LONG).show();
//				gettime();
				Settings.System.putInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 1);  
				Intent start_intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
				start_intent.putExtra("state", !isEnabled);  
				context.sendBroadcast(start_intent);
			}
		}else if(AlarmToAirPortModel.stop_action.equals(action)){
			Toast.makeText(context, "飞行模式关闭中...", Toast.LENGTH_LONG).show();
			boolean isEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
			if(isEnabled){
				Toast.makeText(context, "飞行关闭", Toast.LENGTH_LONG).show();
//				gettime();
				Settings.System.putInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0);  
				Intent start_intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
				start_intent.putExtra("state", !isEnabled);  
				context.sendBroadcast(start_intent);
			}
		}
	}
	public void gettime(){
		Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(
//        		calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
//        		calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
//        		calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        Log.d("xmg", calendar.get(Calendar.HOUR_OF_DAY)
        		+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
	}
}
