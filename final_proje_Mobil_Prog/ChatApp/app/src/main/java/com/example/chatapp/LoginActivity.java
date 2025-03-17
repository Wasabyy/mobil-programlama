package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText email,password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (emailText.isEmpty() || passwordText.isEmpty()){
                Toast.makeText(LoginActivity.this, "Lutfen bos alanları doldurunuz", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.signInWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(authResult -> {
                    Toast.makeText(LoginActivity.this,"Giris basarili", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Giris basarisiz!!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}