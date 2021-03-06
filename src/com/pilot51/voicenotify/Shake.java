/*
 * Copyright 2012 Mark Injerd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pilot51.voicenotify;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Shake implements SensorEventListener {
	private SensorManager manager;
	private Sensor sensor;
	private OnShakeListener listener;
	private int threshold;
	private float accel, accelCurrent, accelLast;

	public Shake(Context c) {
		manager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	protected void enable() {
		if (listener == null) return;
		try {
			threshold = Integer.parseInt(Common.prefs.getString("shake_threshold", null));
		} catch (NumberFormatException e) {
			// Don't enable if threshold setting is blank
			return;
		}
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		accelCurrent = SensorManager.GRAVITY_EARTH;
		accelLast = SensorManager.GRAVITY_EARTH;
	}
	
	protected void disable() {
		manager.unregisterListener(this);
	}

	public void setOnShakeListener(OnShakeListener listener) {
		this.listener = listener;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		accelCurrent = (float)Math.sqrt(x * x + y * y + z * z);
		accel = accel * 0.9f + accelCurrent - accelLast;
		if (Math.abs(accel) > threshold / 10) {
			listener.onShake();
		}
		accelLast = accelCurrent;
	}

	public interface OnShakeListener {
		public void onShake();
	}
}