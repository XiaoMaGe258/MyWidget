package com.pb.app.mywidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmToAirPortModel extends Activity{
	
	Context context;
	TimePicker tp;
	Button btn_start_time;
	Button btn_stop_time;
	Button btn_ok;
	Button btn_cancelAlarm;
	
	final static String start_action = "startAirModel";
	final static String stop_action = "stopAirModel";
	final int start_requestCode = 0;
	final int stop_requestCode = 1;
	Intent Intent; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.alarmtoairport);
		initView();
		ButtonEvent();
	}
	
	public void initView(){
		MySharedPreferences.init_SP_Instance(context, "set_info");
		String startTimeStr = MySharedPreferences.get_String("start_time_str", "--:--");
		String stopTimeStr = MySharedPreferences.get_String("stop_time_str", "--:--");
		//时间选择器
        tp = (TimePicker) findViewById(R.id.timePicker);
        tp.setIs24HourView(true);
        btn_start_time = (Button) findViewById(R.id.btn_start_time);
        btn_start_time.setText(startTimeStr);
        btn_stop_time = (Button) findViewById(R.id.btn_stop_time);
        btn_stop_time.setText(stopTimeStr);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancelAlarm = (Button) findViewById(R.id.btn_cancelAlarm);
	}
	
	public void ButtonEvent(){
		//开始时间
		btn_start_time.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Button btn = (Button)v;
				String tp_hour = tp.getCurrentHour().toString();
				String tp_min = tp.getCurrentMinute().toString();
				if(tp_hour.length() == 1){
					tp_hour = "0"+tp_hour;
				}
				if(tp_min.length() == 1){
					tp_min = "0"+tp_min;
				}
				btn.setText(tp_hour+":"+tp_min);
				MySharedPreferences.init_SP_Instance(context, "set_info");
				MySharedPreferences.put_String("start_time_str", tp_hour+":"+tp_min);
				MySharedPreferences.put_Long("start_time_long", getTime());
			}
		});
		//停止时间
		btn_stop_time.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Button btn = (Button)v;
				String tp_hour = tp.getCurrentHour().toString();
				String tp_min = tp.getCurrentMinute().toString();
				if(tp_hour.length() == 1){
					tp_hour = "0"+tp_hour;
				}
				if(tp_min.length() == 1){
					tp_min = "0"+tp_min;
				}
				btn.setText(tp_hour+":"+tp_min);
				MySharedPreferences.init_SP_Instance(context, "set_info");
				MySharedPreferences.put_String("stop_time_str", tp_hour+":"+tp_min);
				MySharedPreferences.put_Long("stop_time_long", getTime());
			}
		});
		//确认
		btn_ok.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				MySharedPreferences.init_SP_Instance(context, "set_info");
				long startTime = MySharedPreferences.get_Long("start_time_long", 0);
				long stopTime = MySharedPreferences.get_Long("stop_time_long", 0);
				if(startTime == 0){
					Toast.makeText(context, "请设定开启飞行模式的时间！", Toast.LENGTH_SHORT).show();
				}else if(stopTime == 0){
					Toast.makeText(context, "请设定关闭飞行模式的时间！", Toast.LENGTH_SHORT).show();
				}else{					
					Toast.makeText(context, "设定完成！", Toast.LENGTH_SHORT).show();
					initAlarm(start_action, start_requestCode, startTime);
					initAlarm(stop_action, stop_requestCode, stopTime);
					finish();
				}
			}
		});
		//取消省电模式
		btn_cancelAlarm.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				cancelAlarm(start_action, start_requestCode);
				cancelAlarm(stop_action, stop_requestCode);
				btn_start_time.setText("--:--");
				btn_stop_time.setText("--:--");
				MySharedPreferences.init_SP_Instance(context, "set_info");
				MySharedPreferences.put_String("start_time_str", "--:--");
				MySharedPreferences.put_String("stop_time_str", "--:--");
				MySharedPreferences.put_Long("start_time_long", 0);
				MySharedPreferences.put_Long("stop_time_long", 0);
			}
		});
	}
	static float x = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x_up = 0;
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			x = event.getX();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			x_up = event.getX();
			if(x_up - x > 200){
				finish();
			}
		}
		return super.onTouchEvent(event);
	}
	
	//闹铃初始化
	public void initAlarm(String action, int requestCode, long alarm_time){
		Intent alarm_intent = new Intent(this, AlarmReceiver.class);  
		alarm_intent.setAction(action);   
        PendingIntent alarm_PendingIntent = PendingIntent.getBroadcast(this, requestCode, alarm_intent, 0);   
        AlarmManager alarm_am = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm_am.setRepeating(AlarmManager.RTC_WAKEUP, alarm_time, 24*60*60*1000, alarm_PendingIntent);
	}
//	public void initAlarm1(){
//        Intent stop_intent = new Intent(this, AlarmReceiver.class);   
//        stop_intent.setAction("stopAirModel");
//        PendingIntent  stopPendingIntent = PendingIntent.getBroadcast(this, 1, stop_intent, 0);   
//        long stop_alarm_time = System.currentTimeMillis();
//        AlarmManager stop_am = (AlarmManager)getSystemService(ALARM_SERVICE);
//        stop_am.setRepeating(AlarmManager.RTC_WAKEUP, stop_alarm_time, 24*60*60*1000, stopPendingIntent);	
//	}
	
//	public void initTime(int Calendar_Time, int value){
//		//时间设定
//        Calendar calendar=Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar_Time, value);
//        
//        calendar.set(
//        		calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
//        		calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
//        		calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
//	}
	
	public long getTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int tp_hour = tp.getCurrentHour();
		int tp_min = tp.getCurrentMinute();
		int cur_hour = calendar.get(Calendar.HOUR_OF_DAY);
		int cur_min = calendar.get(Calendar.MINUTE);
		int hour = 0;
		int min = 0;

		hour = tp_hour - cur_hour;
		min = tp_min - cur_min;
		if(tp_hour < cur_hour)//day +1
			hour = hour + 24;
		
		Log.d("xmg", "hour="+hour+"   min="+min);
		calendar.add(Calendar.HOUR_OF_DAY, hour);
		calendar.add(Calendar.MINUTE, min);
		calendar.set(
        		calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
        		calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 
        		calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		
		Log.d("xmg", "arrive time="+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
		Log.d("xmg", "hour="+calendar.getTimeInMillis());
		return calendar.getTimeInMillis();
	}
	/**
	 * 销毁闹钟
	 * @param action
	 * @param requestCode
	 */
	public void cancelAlarm(String action, int requestCode){
		Intent alarm_intent = new Intent(this, AlarmReceiver.class);
		alarm_intent.setAction(action);   
    	PendingIntent sender = PendingIntent.getBroadcast(this, requestCode, alarm_intent, 0);   
    	AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);    
    	alarm.cancel(sender); 
    }
}
