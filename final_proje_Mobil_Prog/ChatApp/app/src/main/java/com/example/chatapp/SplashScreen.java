package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    Button login,register;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        construct();

        eventHandler();

        //Intent intent2 = new Intent(SplashScreen.this, LogoutActivity.class);
        //startActivity(intent2);

        /*if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }
    void construct(){
        login = findViewById(R.id.loginRedirect);
        register = findViewById(R.id.registerRedirect);
        mAuth = FirebaseAuth.getInstance();
    }

    void eventHandler(){
        login.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
        });
        register.setOnClickListener(v -> {
            Intent intent = new Intent( SplashScreen.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}