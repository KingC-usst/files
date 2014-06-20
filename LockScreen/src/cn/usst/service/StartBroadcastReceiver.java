package cn.usst.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartBroadcastReceiver extends BroadcastReceiver {
	private Intent service;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		service = new Intent(context, ScreenoffService.class);
		context.startService(service);
	}

}
