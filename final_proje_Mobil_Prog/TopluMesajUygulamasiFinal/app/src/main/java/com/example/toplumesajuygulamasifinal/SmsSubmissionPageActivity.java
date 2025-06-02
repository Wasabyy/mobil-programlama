package com.example.toplumesajuygulamasifinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.Intent;

public class SmsSubmissionPageActivity extends AppCompatActivity {
    private static final String TAG = "SmsSubmissionPage";

    EditText messageEditText;
    RadioButton radioFriendButton, radioWorkButton, radioRelativeButton;
    Button submissionButton, refresButton, clearRecordsButton;
    RecyclerView recyclerView;
    private SmsSubmissionPageAdapter adapter;
    List<SmsSubmissionPageList> lists;
    String userNumber = null;
    ArrayList<String> numbersArrayList;
    final int SEND_SMS_PERMISSION_REQUEST_CODE=1;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate başladı");
        
        try {
            setContentView(R.layout.activity_sms_submission_page);
            Log.d(TAG, "Layout yüklendi");

            initializeViews();
            Log.d(TAG, "Views başlatıldı");
            
            setupRecyclerView();
            Log.d(TAG, "RecyclerView ayarlandı");
            
            checkSmsPermission();
            Log.d(TAG, "SMS izinleri kontrol edildi");

            // Veritabanını başlat
            initializeDatabase();

            // Intent'ten gelen seçili kişileri al
            ArrayList<TelephoneDirectoryList> selectedContacts = 
                (ArrayList<TelephoneDirectoryList>) getIntent().getSerializableExtra("selected_contacts");
            Log.d(TAG, "Intent'ten gelen kişi sayısı: " + (selectedContacts != null ? selectedContacts.size() : 0));
            
            if (selectedContacts != null && !selectedContacts.isEmpty()) {
                // Seçili kişileri veritabanına ekle
                saveContactsToDatabase(selectedContacts);
            } else {
                Log.w(TAG, "Intent'ten gelen seçili kişi yok");
            }

            // Başlangıçta tüm kişileri yükle
            loadAllContacts();
            Log.d(TAG, "Tüm kişiler yüklendi");

        } catch (Exception e) {
            Log.e(TAG, "onCreate hatası: ", e);
            Toast.makeText(this, "SMS ekranı başlatılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeDatabase() {
        try {
            database = this.openOrCreateDatabase("Groups", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS groups(id INTEGER PRIMARY KEY,name VARCHAR, number VARCHAR,grup VARCHAR)");
            Log.d(TAG, "Veritabanı başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "Veritabanı başlatma hatası: ", e);
            Toast.makeText(this, "Veritabanı başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveContactsToDatabase(ArrayList<TelephoneDirectoryList> contacts) {
        if (database == null) {
            initializeDatabase();
        }

        try {
            database.beginTransaction();
            for (TelephoneDirectoryList contact : contacts) {
                String sqlString = "INSERT INTO groups (name,number,grup) VALUES (?,?,?)";
                SQLiteStatement statement = database.compileStatement(sqlString);
                statement.bindString(1, contact.getUserName());
                statement.bindString(2, contact.getUserNumber());
                statement.bindString(3, contact.getGroup()); // Kişinin gerçek grup bilgisini kullan
                statement.execute();
                Log.d(TAG, "Kişi eklendi: " + contact.getUserName() + " - Grup: " + contact.getGroup());
            }
            database.setTransactionSuccessful();
            Log.d(TAG, "Tüm kişiler veritabanına eklendi");
        } catch (Exception e) {
            Log.e(TAG, "Veritabanına kişi ekleme hatası: ", e);
            Toast.makeText(this, "Kişiler eklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        } finally {
            if (database != null) {
                database.endTransaction();
            }
        }
    }

    private boolean clearDatabase() {
        if (database == null) {
            initializeDatabase();
        }

        try {
            // Önce transaction başlat
            database.beginTransaction();
            
            try {
                // Tüm kayıtları sil
                database.execSQL("DELETE FROM groups");
                
                // Tabloyu yeniden oluştur
                database.execSQL("DROP TABLE IF EXISTS groups");
                database.execSQL("CREATE TABLE IF NOT EXISTS groups(id INTEGER PRIMARY KEY,name VARCHAR, number VARCHAR,grup VARCHAR)");
                
                // Transaction'ı başarılı olarak işaretle
                database.setTransactionSuccessful();
                Log.d(TAG, "Veritabanı başarıyla temizlendi");
                return true;
            } finally {
                // Transaction'ı sonlandır
                database.endTransaction();
            }
        } catch (Exception e) {
            Log.e(TAG, "Veritabanı temizleme hatası: ", e);
            Toast.makeText(this, "Kayıtlar temizlenirken hata oluştu", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void initializeViews() {
        try {
            messageEditText = findViewById(R.id.edt_smsSubmissionPage_smsEditText);
            radioFriendButton = findViewById(R.id.rdBtn_smsSubmissionPage_friend);
            radioWorkButton = findViewById(R.id.rdBtn_smsSubmissionPage_work);
            radioRelativeButton = findViewById(R.id.rdBtn_smsSubmissionPage_relative);
            submissionButton = findViewById(R.id.btn_smsSubmissionPage_submissionButton);
            recyclerView = findViewById(R.id.lst_smsSubmissionPage_RecyclerView);
            refresButton = findViewById(R.id.btn_refreshButton);
            clearRecordsButton = findViewById(R.id.btn_clearRecords);

            numbersArrayList = new ArrayList<>();
            lists = new ArrayList<>();

            setupClickListeners();

            if (clearRecordsButton != null) {
                clearRecordsButton.setOnClickListener(v -> {
                    try {
                        // Kullanıcıya onay sor
                        new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Kayıtları Temizle")
                            .setMessage("Tüm kayıtlar silinecek. Emin misiniz?")
                            .setPositiveButton("Evet", (dialog, which) -> {
                                // Veritabanını temizle
                                if (clearDatabase()) {
                                    // Listeleri temizle
                                    lists.clear();
                                    numbersArrayList.clear();
                                    adapter.notifyDataSetChanged();
                                    
                                    // Radio butonları sıfırla
                                    radioFriendButton.setChecked(false);
                                    radioWorkButton.setChecked(false);
                                    radioRelativeButton.setChecked(false);
                                    
                                    // PhoneNumberActivity'ye geri dön ve grupları temizlemesini söyle
                                    Intent intent = new Intent();
                                    intent.putExtra("clear_groups", true);
                                    setResult(RESULT_OK, intent);
                                    
                                    // Kullanıcıya bilgi ver
                                    Toast.makeText(this, "Tüm kayıtlar temizlendi", Toast.LENGTH_SHORT).show();
                                    
                                    // Ana ekrana geri dön
                                    finish();
                                }
                            })
                            .setNegativeButton("Hayır", null)
                            .show();
                    } catch (Exception e) {
                        Log.e(TAG, "Kayıtları temizleme hatası: ", e);
                        Toast.makeText(this, "Kayıtlar temizlenirken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "initializeViews hatası: ", e);
            throw new RuntimeException("View'lar başlatılırken hata oluştu", e);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new SmsSubmissionPageAdapter(this, lists);
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        refresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                lists.clear();
                numbersArrayList.clear();

                if(radioFriendButton.isChecked()) {
                    readingData("SELECT * FROM groups WHERE grup = 'friend'");
                } else if (radioWorkButton.isChecked()){
                    readingData("SELECT * FROM groups WHERE grup = 'work'");
                } else if(radioRelativeButton.isChecked()){
                    readingData("SELECT * FROM groups WHERE grup = 'relative'");
                } else {
                    readingData("SELECT * FROM groups");
                }
            }
        });

        submissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String message = messageEditText.getText().toString().trim();
                if(message.isEmpty()) {
                    messageEditText.setError("Lütfen mesaj yazın");
                    return;
                }
                smsSubmission(message);
            }
        });
    }

    private void checkSmsPermission() {
        submissionButton.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)){
            submissionButton.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    private void loadAllContacts() {
        readingData("SELECT * FROM groups");
    }

    private boolean checkPermission(String sendSms){
        int check = ContextCompat.checkSelfPermission(this,sendSms);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private void smsSubmission(String message){
        // Önce grup seçimi kontrolü yap
        if (!radioFriendButton.isChecked() && !radioWorkButton.isChecked() && !radioRelativeButton.isChecked()) {
            Toast.makeText(this, "Lütfen bir grup seçiniz", Toast.LENGTH_LONG).show();
            return;
        }

        // Seçili grubu belirle
        String selectedGroup = null;
        if (radioFriendButton.isChecked()) {
            selectedGroup = "friend";
        } else if (radioWorkButton.isChecked()) {
            selectedGroup = "work";
        } else if (radioRelativeButton.isChecked()) {
            selectedGroup = "relative";
        }

        // Seçili gruptaki kişileri getir
        loadSelectedGroupContacts(selectedGroup);

        if(numbersArrayList == null || numbersArrayList.isEmpty()){
            Toast.makeText(this, "Seçili grupta kişi bulunamadı", Toast.LENGTH_LONG).show();
            return;
        }

        if(!checkPermission(Manifest.permission.SEND_SMS)){
            Toast.makeText(this, "SMS gönderme izni verilmedi", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            int successCount = 0;

            // Gönderici numarasını al
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String senderName = sharedPreferences.getString("userName", "");
            String senderNumber = sharedPreferences.getString("userNumber", "");

            // Mesaja gönderici bilgisini ekle
            String fullMessage = "Gönderen: " + senderName + " (" + senderNumber + ")\n\n" + message;

            for(String number : numbersArrayList){
                try {
                    smsManager.sendTextMessage(number, null, fullMessage, null, null);
                    successCount++;
                } catch (Exception e) {
                    Log.e(TAG, "SMS gönderme hatası: " + number, e);
                }
            }

            if(successCount > 0) {
                Toast.makeText(this, successCount + " kişiye mesaj gönderildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Mesaj gönderilemedi", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "SMS gönderme hatası", e);
            Toast.makeText(this, "Mesaj gönderirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadSelectedGroupContacts(String groupType) {
        lists.clear();
        numbersArrayList.clear();
        readingData("SELECT * FROM groups WHERE grup = '" + groupType + "'");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void readingData(String sqlQuery){
        Cursor cursor = null;

        try {
            if (database == null) {
                initializeDatabase();
            }
            
            cursor = database.rawQuery(sqlQuery,null);
            Log.d(TAG, "Veritabanından " + cursor.getCount() + " kayıt bulundu");

            int nameIndex = cursor.getColumnIndex("name");
            int numberIndex = cursor.getColumnIndex("number");
            int groupIndex = cursor.getColumnIndex("grup");

            lists.clear();
            numbersArrayList.clear();

            while (cursor.moveToNext()){
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);
                String group = cursor.getString(groupIndex);

                SmsSubmissionPageList list = new SmsSubmissionPageList();
                list.setName(name);
                list.setNumber(number);
                list.setGroup(group);
                lists.add(list);

                numbersArrayList.add(number);
                Log.d(TAG, "Kişi eklendi: " + name + " - " + number + " - Grup: " + group);
            }

            adapter.notifyDataSetChanged();

            if(lists.isEmpty()) {
                Toast.makeText(this, "Seçili grupta kişi bulunamadı", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            Log.e(TAG, "Veri okuma hatası", e);
            Toast.makeText(this, "Veri okuma hatası: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if(cursor != null) cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}