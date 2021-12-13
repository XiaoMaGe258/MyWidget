package com.pb.app.mywidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SetTimeActivity extends Activity{
	
	private long rad_type = DateUtils.SECOND_IN_MILLIS; 
	RadioGroup rg;
	EditText et;
	Button bt_ok,bt_alarmAir;
	Context context;
	private SharedPreferences BatteryInfo;
	private Editor BatteryInfoEditor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.settime);
		rg = (RadioGroup) findViewById(R.id.st_radioGroup);
		et = (EditText) findViewById(R.id.st_edittext);
		bt_ok = (Button) findViewById(R.id.st_btn_ok);
		bt_alarmAir = (Button) findViewById(R.id.st_btn_alarmAir);
		
		
		
		
		// setOnCheckedChangeListener() - 响应单选框组内的选中项发生变化时的事件
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {	
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.st_rad_min:
					rad_type = DateUtils.MINUTE_IN_MILLIS;
					break;
				case R.id.st_rad_sec:
					rad_type = DateUtils.SECOND_IN_MILLIS;
					break;
				}
				
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				String etText = et.getText().toString();
				
				BatteryInfo = getSharedPreferences("BatteryInfo",Context.MODE_WORLD_READABLE);
				BatteryInfoEditor = BatteryInfo.edit();
				
				if(!"".equals(etText)){
					Batterywidget.REF_TYPE = rad_type;
					int editText = Integer.parseInt(etText);
					Batterywidget.REF_TIME = editText;
					
					BatteryInfoEditor.putLong("rad_type", rad_type);
					BatteryInfoEditor.putLong("ref_time", editText);
					BatteryInfoEditor.commit();
					
					Batterywidget.cancelRuntime(context);
					Batterywidget.startMyService(context);
					
					Toast.makeText(context, "设置成功！", Toast.LENGTH_SHORT).show();
					
					setResult(5);
					finish();
				}else{
					Toast.makeText(context, "请输入刷新时间", Toast.LENGTH_SHORT).show();
				}
			}
		});
		bt_alarmAir.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(context, AlarmToAirPortModel.class));
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
			}else{
				startActivity(new Intent(this, AlarmToAirPortModel.class));
			}
		}
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem mi;
		mi = menu.add(0, 1, 0, "���ڳ���");
		mi.setIcon(android.R.drawable.ic_menu_compass);
		mi = menu.add(0, 2, 0, "�˳�����");
		mi.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1:
			new AlertDialog.Builder(context).setTitle("����").setMessage(R.string.about).setIcon(
							R.drawable.f100).setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {}
							}).show();
			break;
		case 2:
			setResult(10);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
