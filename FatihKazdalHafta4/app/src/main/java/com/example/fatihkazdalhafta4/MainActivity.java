package com.example.plakaoyunu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String[] sehirler = {"İstanbul", "Ankara", "İzmir", "Bursa", "Antalya"};
    String[] plakalar = {"34", "06", "35", "16", "07"};
    String secilenSehir = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewSehirler = findViewById(R.id.listViewSehirler);
        Button buttonPlakaOlustur = findViewById(R.id.buttonPlakaOlustur);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sehirler);
        listViewSehirler.setAdapter(adapter);

        listViewSehirler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                secilenSehir = sehirler[position];
            }
        });

        buttonPlakaOlustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!secilenSehir.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(plakalar.length);
                    String rastgelePlaka = plakalar[randomIndex];

                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("secilenSehir", secilenSehir);
                    intent.putExtra("rastgelePlaka", rastgelePlaka);
                    startActivity(intent);
                }
            }
        });
    }
}
