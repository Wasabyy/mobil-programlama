package com.example.plakaoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    String[] sehirler = {"İstanbul", "Ankara", "İzmir", "Bursa", "Antalya"};
    String[] plakalar = {"34", "06", "35", "16", "07"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView textViewSonuc = findViewById(R.id.textViewSonuc);

        Intent intent = getIntent();
        String secilenSehir = intent.getStringExtra("secilenSehir");
        String rastgelePlaka = intent.getStringExtra("rastgelePlaka");

        String dogruPlaka = "";
        for (int i = 0; i < sehirler.length; i++) {
            if (sehirler[i].equals(secilenSehir)) {
                dogruPlaka = plakalar[i];
                break;
            }
        }

        if (rastgelePlaka.equals(dogruPlaka)) {
            textViewSonuc.setText("Doğru! " + secilenSehir + " plakası gerçekten " + rastgelePlaka);
        } else {
            textViewSonuc.setText("Yanlış! Seçilen şehir: " + secilenSehir + "\nGerçek plaka: " + dogruPlaka + "\nRastgele plaka: " + rastgelePlaka);
        }
    }
}
