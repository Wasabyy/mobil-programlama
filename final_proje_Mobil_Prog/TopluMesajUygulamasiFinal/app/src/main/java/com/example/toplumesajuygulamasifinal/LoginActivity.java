package com.example.toplumesajuygulamasifinal;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText nameEditText, numberEditText;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate başladı");

        try {
            setContentView(R.layout.activity_login);

            // SharedPreferences'ı başlat
            sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            // View'ları başlat
            initializeViews();

            // Otomatik giriş kontrolü
            if (checkAutoLogin()) {
                Log.d(TAG, "Otomatik giriş yapılıyor");
                return;
            }

        } catch (Exception e) {
            Log.e(TAG, "onCreate hatası: ", e);
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            nameEditText = findViewById(R.id.edt_login_userName);
            numberEditText = findViewById(R.id.edt_login_userNumber);
            rememberMeCheckBox = findViewById(R.id.chk_rememberMe);
            Button loginButton = findViewById(R.id.btn_login_loginButton);

            // Kayıtlı tercihleri yükle
            boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
            if (rememberMe) {
                nameEditText.setText(sharedPreferences.getString("userName", ""));
                numberEditText.setText(sharedPreferences.getString("userNumber", ""));
                rememberMeCheckBox.setChecked(true);
            }

            loginButton.setOnClickListener(v -> handleLogin());

        } catch (Exception e) {
            Log.e(TAG, "initializeViews hatası: ", e);
            throw new RuntimeException("View'lar başlatılırken hata oluştu", e);
        }
    }

    private boolean checkAutoLogin() {
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        String savedName = sharedPreferences.getString("userName", "");
        String savedNumber = sharedPreferences.getString("userNumber", "");

        if (rememberMe && !savedName.isEmpty() && !savedNumber.isEmpty()) {
            // Otomatik giriş yap
            startPhoneNumberActivity(savedName, savedNumber);
            return true;
        }
        return false;
    }

    private void handleLogin() {
        try {
            String name = nameEditText.getText().toString().trim();
            String number = numberEditText.getText().toString().trim();

            // Validasyon kontrolleri
            if (name.isEmpty()) {
                nameEditText.setError("İsim boş olamaz");
                return;
            }

            if (number.isEmpty()) {
                numberEditText.setError("Numara boş olamaz");
                return;
            }

            // Telefon numarası formatı kontrolü (05XX XXX XX XX)
            if (!number.matches("^05\\d{2}\\s?\\d{3}\\s?\\d{2}\\s?\\d{2}$")) {
                numberEditText.setError("Geçerli bir telefon numarası giriniz (05XX XXX XX XX)");
                return;
            }

            // Kullanıcı tercihlerini kaydet
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", name);
            editor.putString("userNumber", number);
            editor.putBoolean("rememberMe", rememberMeCheckBox.isChecked());
            editor.apply();

            // Ana ekrana geç
            startPhoneNumberActivity(name, number);

        } catch (Exception e) {
            Log.e(TAG, "handleLogin hatası: ", e);
            Toast.makeText(this, "Giriş yapılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startPhoneNumberActivity(String name, String number) {
        try {
            // Başarılı giriş mesajını göster
            Toast.makeText(this, "Giriş Başarılı", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, PhoneNumberActivity.class);
            intent.putExtra("user_name", name);
            intent.putExtra("user_number", number);
            startActivity(intent);
            
            // Eğer "Beni Hatırla" seçiliyse, activity'yi finish() etme
            if (!rememberMeCheckBox.isChecked()) {
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "startPhoneNumberActivity hatası: ", e);
            Toast.makeText(this, "Ana ekrana geçilirken hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume çağrıldı");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause çağrıldı");
    }
}