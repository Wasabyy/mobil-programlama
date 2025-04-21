package com.example.sensordeneme;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class PressureActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        textView.setTextSize(20);
        setContentView(textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }

        if (pressureSensor == null) {
            textView.setText("Bu cihazda basınç sensörü yok.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pressureSensor != null)
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float pressure = event.values[0];
        textView.setText("Basınç: " + pressure + " hPa");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
