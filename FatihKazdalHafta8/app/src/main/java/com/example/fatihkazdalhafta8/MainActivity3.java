package com.example.sensordeneme;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class CompassActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magnetometer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        textView.setTextSize(20);
        setContentView(textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magnetometer != null)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        textView.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
