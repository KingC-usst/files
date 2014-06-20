package cn.usst.service;

import cn.usst.ShowActivity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.widget.Toast;

public class RelativeBroadcastReceiver extends BroadcastReceiver {

	private KeyguardManager keyguardManager;
	private KeyguardLock keyguardLock;
	private Intent lockIntent;
	private Intent service;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		keyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		keyguardLock = keyguardManager.newKeyguardLock("lock");
		keyguardLock.disableKeyguard();

		lockIntent = new Intent(context, ShowActivity.Main.class);
		lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		context.startActivity(lockIntent);
		service = new Intent(context, ScreenoffService.class);
		context.startService(service);
	}

}
