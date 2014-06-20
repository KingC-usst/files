package cn.usst;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class SensorManagerHelper implements SensorEventListener {

	private static final int SPEED_SHRESHOLD = 500;
	private static final int UPDATE_INTERVAL_TIME = 100;
	private SensorManager sensorManager;
	private Sensor sensor;
	private Context context;
	private float lastX, lastY, lastZ;
	private long lastUpdateTime;
	public SensorManagerHelper(Context context) {
		super();
		this.context = context;
	}

	public void start() {
		// TODO Auto-generated method stub
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (sensor != null) {
				sensorManager.registerListener(this, sensor,
						sensorManager.SENSOR_DELAY_NORMAL);

			}
		}
	}

	public void stop() {
		if (sensor != null) {
			sensorManager.unregisterListener(this);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			long currentTime = System.currentTimeMillis();
			if (lastUpdateTime != 0) {
				long diffTime = currentTime - lastUpdateTime;
	
				if (diffTime > UPDATE_INTERVAL_TIME) {
				
					float diff_X = x - lastX;
					float diff_Y = y - lastY;
					float diff_Z = z - lastZ;
					float diff = FloatMath.sqrt(diff_X * diff_X + diff_Y
							* diff_Y + diff_Z * diff_Z)
							/ diffTime * 10000;
				
					if (diff > SPEED_SHRESHOLD) {
					
						DevicePolicyManager manager = (DevicePolicyManager) context
								.getSystemService("device_policy");
					
						manager.lockNow();
					} else {
					
					}
				}
			}
		
			lastUpdateTime = currentTime;
		
			lastX = x;
			lastY = y;
			lastZ = z;

		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
