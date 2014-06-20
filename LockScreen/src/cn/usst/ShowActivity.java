package cn.usst;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import cn.usst.service.ScreenoffService;

import com.capricorn.ArcMenu;

public class ShowActivity extends DeviceAdminReceiver {

	public static class Main extends Activity {
		private final static String TAG = "System.out";
		private Intent intent, service;
		private GridLayout layout;
		private ImageButton imageButton;
		private DevicePolicyManager dpm;
		private ComponentName componentName;
		private ArcMenu arcMenu;
		
		private static final int[] ITEM_DRAWABLES = { R.drawable.camera,
				R.drawable.music, R.drawable.place, R.drawable.sleep,
				R.drawable.message, R.drawable.contact, R.drawable.internet,
				R.drawable.gallery };

		private final static int loadCamera = 100, loadMusic = 101,
				loadMap = 102, loadSms = 104, loadContacts = 105,
				loadInternet = 106, loadGallery = 107;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_show);
			layout = (GridLayout) findViewById(R.id.container);
			imageButton = (ImageButton) findViewById(R.id.imageButton);
			imageButton.setOnTouchListener(new layoutOntouchListener());
		
			arcMenu = (ArcMenu) findViewById(R.id.arc_menu);
			initArcMenu(arcMenu, ITEM_DRAWABLES);
	
			componentName = new ComponentName(this, ShowActivity.class);
	
			dpm = (DevicePolicyManager) getSystemService("device_policy");

			service = new Intent(this, ScreenoffService.class);
			startService(service);

			if (!dpm.isAdminActive(componentName)) {
				getAdmin();
			}
			return;

		}

		/**
		 * 给界面设置拖拉方法
		 * 
		 * @author KC
		 * 
		 */
		private class layoutOntouchListener implements OnTouchListener {
			private Matrix currentMatrix = new Matrix();
			private PointF startPoint = new PointF();
			private Matrix matrix = new Matrix();
			private float dx, dy;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					startPoint.set(event.getX(), event.getY());
					currentMatrix.set(imageButton.getImageMatrix());
					Log.i(TAG, "判定为移动");
					break;
				case MotionEvent.ACTION_MOVE:
					dx = event.getX() - startPoint.x;
					dy = event.getY() - startPoint.y;
					Log.i(TAG, "x的移动距离为" + dx + "  y的移动距离为" + dy);
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
					break;
				case MotionEvent.ACTION_UP:
					Log.i(TAG, "放开");
					if (Math.abs(dy) > 180) {
						AnimationSet set = new AnimationSet(true);
						TranslateAnimation translate = new TranslateAnimation(
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, 0,
								Animation.RELATIVE_TO_SELF, getResources()
										.getDisplayMetrics().ydpi);
						translate.setDuration(1000);
						set.addAnimation(translate);
						layout.startAnimation(set);
						finish();
						startService(service);
					}
					break;
				}
				return true;
			}
		}

		private void getAdmin() {
			
			intent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
			intent.putExtra("android.app.extra.DEVICE_ADMIN", componentName);
			intent.putExtra("android.app.extra.ADD_EXPLANATION",
					"Additional text explaining why this needs to be added");
			startActivity(intent);
		}

		@Override
		protected void onDestroy() {
			service = new Intent(this, ScreenoffService.class);
			startService(service);
			super.onDestroy();
		}

		private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
			final int itemCount = itemDrawables.length;
			menu.setAlpha((float) 0.8);
			menu.setKeepScreenOn(true);
			for (int i = 0; i < itemCount; i++) {
				ImageView item = new ImageView(this);
				item.setImageResource(itemDrawables[i]);
				final int position = i;
				menu.addItem(item, new OnClickListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onClick(View v) {
						switch (position) {
						case 0:
							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
							startActivityForResult(intent, loadCamera);
							finish();
							break;
						case 1:
							intent = new Intent(Intent.ACTION_VIEW);
							intent.setType("audio/*");
							startActivityForResult(intent, loadMusic);
							finish();
							break;
						case 2:
							Uri uri = Uri.parse("geo:?,?");
							intent = new Intent(Intent.ACTION_VIEW, uri);
							startActivityForResult(intent, loadMap);
							finish();
							break;
						case 3:
							ContentResolver cr = getContentResolver();
							if (Settings.System.getString(cr,
									Settings.System.AIRPLANE_MODE_ON).equals(
									"0")) {
								Settings.System.putString(cr,
										Settings.System.AIRPLANE_MODE_ON, "1");
								intent = new Intent(
										Intent.ACTION_AIRPLANE_MODE_CHANGED);
								sendBroadcast(intent);
								Toast.makeText(Main.this, "已打开飞行模式",
										Toast.LENGTH_SHORT).show();

							} else {

								Settings.System.putString(cr,
										Settings.System.AIRPLANE_MODE_ON, "0");
								intent = new Intent(
										Intent.ACTION_AIRPLANE_MODE_CHANGED);
								sendBroadcast(intent);
								Toast.makeText(Main.this, "已关闭飞行模式",
										Toast.LENGTH_SHORT).show();
							}
							finish();
							break;
						case 4:
							intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse("sms:" + ""));
							startActivityForResult(intent, loadSms);
							finish();
							break;
						case 5:
							intent = new Intent(Intent.ACTION_DIAL);
							startActivityForResult(intent, loadContacts);
							finish();
							break;
						case 6:
							intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_APP_BROWSER);
							startActivityForResult(intent, loadInternet);
							finish();
							break;
						case 7:
							intent = new Intent(Intent.ACTION_GET_CONTENT);
							intent.setType("image/*");
							startActivityForResult(intent, loadGallery);
							finish();
							break;
						}
					}
				});

			}
		}
	}
}
