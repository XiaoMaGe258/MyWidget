package com.pb.app.mywidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SettingActivity extends Activity{
	
	Context context;
	Button btn_stop;
	Button btn_setTime;
	public static String batteryInfo[] = new String[8];
	
	TelephonyManager        Tel;
	MyPhoneStateListener    MyListener;
	
	TextView status;
	TextView health;
	TextView level;
	TextView plugged;
	TextView scale;
	TextView technology;
	TextView temperature;
	TextView voltage;
	
	TextView signalstrengths;
	TextView wifiSignalstrengths;
	
	boolean isRefreshAct = false;
	
	private Handler ref_handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case 1:
			refreshAct();
			break;
		default:
			break;
		}
	}
};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.setting);
        Log.d("========","settingAcitivity------onCreate");

		Batterywidget.startMyService(context);
        
        btn_setTime = (Button) findViewById(R.id.btn_set_time);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        
        status = (TextView) findViewById(R.id.btn_battery_status);
        health = (TextView) findViewById(R.id.btn_battery_health);
        level = (TextView) findViewById(R.id.btn_battery_level);//
        plugged = (TextView) findViewById(R.id.btn_battery_plugged);
        scale = (TextView) findViewById(R.id.btn_battery_scale);//
        technology = (TextView) findViewById(R.id.btn_battery_technology);
        temperature = (TextView) findViewById(R.id.btn_battery_temperature);
        voltage = (TextView) findViewById(R.id.btn_battery_voltage);//
        signalstrengths = (TextView) findViewById(R.id.btn_tel_signalstrengths);
        wifiSignalstrengths = (TextView) findViewById(R.id.btn_wifi_signalstrengths);
        
        status.setText(batteryInfo[0]);
        health.setText(batteryInfo[1]);
        level.setText(batteryInfo[2]);//
        scale.setText(batteryInfo[3]);//
        plugged.setText(batteryInfo[4]);
        voltage.setText(batteryInfo[5]);//
        temperature.setText(batteryInfo[6]);
        technology.setText(batteryInfo[7]);
        
        final String START = getResources().getString(R.string.start);
        final String STOP = getResources().getString(R.string.stop);
        btn_stop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(STOP.equals(btn_stop.getText().toString())){
                	Log.d("========","settingAcitivity------stopService");
                	Batterywidget.cancelRuntime(context);
                	btn_stop.setText(R.string.start);
                }else if(START.equals(btn_stop.getText().toString())){
                	Log.d("========","settingAcitivity------startService");
                	Batterywidget.startMyService(context);
                	btn_stop.setText(R.string.stop);
                }
            }
        });
        btn_setTime.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, SetTimeActivity.class);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
            }
        });
        
        MyListener = new MyPhoneStateListener();
        Tel = ( TelephonyManager )getSystemService(Context.TELEPHONY_SERVICE);
        Tel.listen(MyListener ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        
        refreshAct();

        isRefreshAct = true;
        refreshActThread.start();
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
			if(x - x_up > 200){
				Intent intent = new Intent(context, SetTimeActivity.class);
                startActivityForResult(intent, 4);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 5){
			btn_stop.setText(R.string.stop);
		}else if(resultCode == 10){
			finish();
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem mi;
		mi = menu.add(0, 1, 0, "关于程序");
		mi.setIcon(android.R.drawable.ic_menu_compass);
		mi = menu.add(0, 2, 0, "退出程序");
		mi.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			new AlertDialog.Builder(context).setTitle("关于").setMessage(R.string.about).setIcon(
							R.drawable.f100).setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {}
							}).show();
			break;
		case 2:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onPause() {
		Tel.listen(MyListener, PhoneStateListener.LISTEN_NONE);
		super.onPause();
	}


	@Override
	protected void onResume() {
		Tel.listen(MyListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		isRefreshAct = false;
		super.onDestroy();
	}

	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each tiome there is an update */
      @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
    	  int signal = signalStrength.getGsmSignalStrength();
         signalstrengths.setText(String.valueOf((signal<0?0:signal)+" (0~31)"));
         super.onSignalStrengthsChanged(signalStrength);
      }

    };
    //刷新界面数据
    private void refreshAct(){
    	status.setText(batteryInfo[0]);
        health.setText(batteryInfo[1]);
        level.setText(batteryInfo[2]);//
        scale.setText(batteryInfo[3]);//
        plugged.setText(batteryInfo[4]);
        voltage.setText(batteryInfo[5]);//
        temperature.setText(batteryInfo[6]);
        technology.setText(batteryInfo[7]);
        
        WifiManager mWifiManager=(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo mWifiInfo=mWifiManager.getConnectionInfo();
        double wifi = mWifiInfo.getRssi();//获取wifi信号强度
        double wifisignal = (wifi + 110) * 1.67;
        wifisignal = wifisignal > 100 ? 100 : wifisignal ;
        wifiSignalstrengths.setText(String.valueOf(((int)wifisignal<0?0:(int)wifisignal) +" (0~100)"));
        
    }
    //刷新界面数据线程
    Thread refreshActThread = new Thread(new Runnable() {
		public void run() {
			while (isRefreshAct) {
				try {
					Thread.currentThread().sleep(3000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ref_handler.sendEmptyMessage(1);
			}
		}
	});
}
