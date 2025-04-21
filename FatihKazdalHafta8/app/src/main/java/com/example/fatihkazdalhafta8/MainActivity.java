package com.example.fatihkazdalhafta8;
import android.widget.TextView;


import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView textViewInfo = findViewById(R.id.textViewInfo);

// Accelerometer
        findViewById(R.id.button).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Accelerometer Sensor (İvmeölçer)\n" +
                    "Cihazın üç eksendeki (X, Y, Z) hareketlerini ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Adım sayar uygulamaları\n" +
                    "- Ekranı döndürme (portrait-landscape)\n" +
                    "- Cihazın sallanmasıyla işlem başlatma");
        });

// Compass
        findViewById(R.id.button2).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Compass Sensor (Pusula) / Magnetometer\n" +
                    "Cihazın Dünya'nın manyetik alanına göre yönünü belirler.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Navigasyon uygulamaları\n" +
                    "- Artırılmış gerçeklik (AR)\n" +
                    "- Yön tayini");
        });

// Gyroscope
        findViewById(R.id.button3).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Gyroscope Sensor (Jiroskop)\n" +
                    "Cihazın açısal dönüş hareketlerini ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Hassas hareket kontrolü (örneğin oyunlarda)\n" +
                    "- Sanal gerçeklik (VR)\n" +
                    "- Kamera sabitleme");
        });

// Humidity
        findViewById(R.id.button4).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Humidity Sensor (Nem Sensörü)\n" +
                    "Ortamın bağıl nem oranını ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Hava durumu uygulamaları\n" +
                    "- Ortam kontrol sistemleri");
        });

// Pressure
        findViewById(R.id.button7).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Pressure Sensor (Basınç Sensörü)\n" +
                    "Ortam basıncını (hava basıncı) ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Yükseklik ölçümü (altimetre)\n" +
                    "- Hava durumu uygulamaları");
        });

// Proximity
        findViewById(R.id.button8).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Proximity Sensor (Yakınlık Sensörü)\n" +
                    "Cihaza bir nesnenin ne kadar yakın olduğunu algılar.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Arama sırasında ekranı kapatma\n" +
                    "- Cepteyken ekran kilidi\n" +
                    "- Hareketle kontrol");
        });

// Thermometer
        findViewById(R.id.button9).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Thermometer Sensor (Sıcaklık Sensörü)\n" +
                    "Ortam sıcaklığını ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Hava durumu uygulamaları\n" +
                    "- Cihaz sıcaklık kontrolü");
        });

// Light
        findViewById(R.id.button5).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Light Sensor (Işık Sensörü)\n" +
                    "Ortam ışığını (lux cinsinden) ölçer.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Otomatik ekran parlaklığı ayarı\n" +
                    "- Akıllı gece/gündüz modları");
        });

// Magnometer
        findViewById(R.id.button6).setOnClickListener(v -> {
            textViewInfo.setText("🔹 Magnometer Sensor (Manyetometre)\n" +
                    "Manyetik alanı algılar.\n\n" +
                    "Kullanım Alanları:\n" +
                    "- Yön tayini\n" +
                    "- Metal dedektör uygulamaları");
        });

    }
}