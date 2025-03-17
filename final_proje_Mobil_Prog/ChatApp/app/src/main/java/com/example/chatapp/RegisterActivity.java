package com.example.chatapp;

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

public class RegisterActivity extends AppCompatActivity {

    Button registerButton;
    EditText email,password;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            if (emailText.isEmpty() || passwordText.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Lutfen bos alanları doldurunuz", Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnSuccessListener(authResult -> {
                    Toast.makeText(RegisterActivity.this,"Kayit basarili", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Kayit basarisiz!!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}