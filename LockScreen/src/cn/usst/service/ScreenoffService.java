package cn.usst.service;

import cn.usst.SensorManagerHelper;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenoffService extends Service {

	private SensorManagerHelper sensorManagerHelper;
	private BroadcastReceiver br;
	private IntentFilter filter;

	@Override
	public void onStart(Intent intent, int startId) {
		sensorManagerHelper.start();
		filter = new IntentFilter();
		filter.addAction("android.intent.action.SCREEN_ON");
		br = new RelativeBroadcastReceiver();
		registerReceiver(br, filter);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		sensorManagerHelper = new SensorManagerHelper(getApplicationContext());
		sensorManagerHelper.start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		if (sensorManagerHelper == null) {
			sensorManagerHelper = new SensorManagerHelper(
					getApplicationContext());
		}
		sensorManagerHelper.start();
		registerReceiver(br, filter);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
