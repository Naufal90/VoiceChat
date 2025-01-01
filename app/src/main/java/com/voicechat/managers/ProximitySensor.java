package com.voicechat.managers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ProximitySensor {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private ProximityListener listener;

    public ProximitySensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void startListening(ProximityListener listener) {
        this.listener = listener;
        sensorManager.registerListener(sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopListening() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                listener.onProximityChanged(event.values[0]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    public interface ProximityListener {
        void onProximityChanged(float distance);
    }
}
