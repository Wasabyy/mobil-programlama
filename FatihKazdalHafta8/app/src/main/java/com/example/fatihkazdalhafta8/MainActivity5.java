package com.example.sensordeneme;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class HumidityActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        textView.setTextSize(20);
        setContentView(textView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        if (humiditySensor == null) {
            textView.setText("Bu cihazda nem sensörü yok.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (humiditySensor != null)
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float humidity = event.values[0];
        textView.setText("Nem Oranı: " + humidity + " %");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
