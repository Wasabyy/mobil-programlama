package com.example.toplumesajuygulamasifinal;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhoneNumberActivity extends AppCompatActivity {
    private static final String TAG = "PhoneNumberActivity";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    
    private ArrayList<TelephoneDirectoryList> contactsList;
    private PhoneNumberAdapter adapter;
    private TextView userNameText;
    private RecyclerView recyclerView;
    private RadioButton rdBtnRelative, rdBtnFriend, rdBtnWork;
    
    // Gruplandırılmış kişileri saklamak için HashMap
    private Map<String, ArrayList<TelephoneDirectoryList>> groupedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate başladı");
        
        try {
            setContentView(R.layout.activity_phone_number);
            
            // Gruplandırılmış kişiler için HashMap'i başlat
            groupedContacts = new HashMap<>();
            groupedContacts.put("relative", new ArrayList<>());
            groupedContacts.put("friend", new ArrayList<>());
            groupedContacts.put("work", new ArrayList<>());

            // View'ları başlat
            initializeViews();

            // Intent'ten gelen verileri al
            String userName = getIntent().getStringExtra("user_name");
            if (userName == null) {
                throw new IllegalStateException("user_name intent extra'sı bulunamadı");
            }
            
            // Kullanıcı adını göster
            userNameText.setText("Hoş geldin, " + userName);
            Log.d(TAG, "Kullanıcı adı ayarlandı: " + userName);

            // RecyclerView'ı ayarla
            setupRecyclerView();

            // İzinleri kontrol et
            checkContactPermission();

        } catch (Exception e) {
            Log.e(TAG, "onCreate sırasında hata: ", e);
            Toast.makeText(this, "Uygulama başlatılırken hata oluştu", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            userNameText = findViewById(R.id.txt_userName);
            if (userNameText == null) throw new IllegalStateException("userNameText bulunamadı");

            recyclerView = findViewById(R.id.recyclerView);
            if (recyclerView == null) throw new IllegalStateException("recyclerView bulunamadı");

            Button smsScreen = findViewById(R.id.btn_smsScreen);
            if (smsScreen == null) throw new IllegalStateException("smsScreen bulunamadı");

            // Radio butonları ve RadioGroup'u başlat
            RadioGroup radioGroup = findViewById(R.id.radioGroup);
            rdBtnRelative = findViewById(R.id.rdBtn_relative);
            rdBtnFriend = findViewById(R.id.rdBtn_friend);
            rdBtnWork = findViewById(R.id.rdBtn_work);

            // RadioGroup için listener ayarla
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                rdBtnRelative.setChecked(checkedId == R.id.rdBtn_relative);
                rdBtnFriend.setChecked(checkedId == R.id.rdBtn_friend);
                rdBtnWork.setChecked(checkedId == R.id.rdBtn_work);
            });

            Button addContact = findViewById(R.id.btn_addContact);
            if (addContact == null) throw new IllegalStateException("addContact bulunamadı");

            Button logoutButton = findViewById(R.id.btn_logout);
            if (logoutButton == null) throw new IllegalStateException("logoutButton bulunamadı");

            // Rehberi yükle
            loadContacts();

            // Çıkış yapma butonu
            logoutButton.setOnClickListener(v -> {
                try {
                    // SharedPreferences'dan login durumunu temizle
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // LoginActivity'ye geri dön
                    Intent intent = new Intent(PhoneNumberActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Çıkış yapma hatası: ", e);
                    Toast.makeText(this, "Çıkış yapılırken hata oluştu", Toast.LENGTH_SHORT).show();
                }
            });

            // Kişi ekleme butonu
            addContact.setOnClickListener(v -> {
                try {
                    ArrayList<TelephoneDirectoryList> selectedContacts = adapter.getSelected();
                    if (selectedContacts != null && !selectedContacts.isEmpty()) {
                        String groupName = getCurrentGroup();
                        if (groupName == null) {
                            Toast.makeText(this, "Lütfen bir grup seçin", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Seçilen kişileri ilgili gruba ekle
                        groupedContacts.get(groupName).addAll(selectedContacts);

                        // Seçilen kişi sayısını al
                        int selectedCount = selectedContacts.size();
                        
                        // Başarı mesajını göster
                        Toast.makeText(this, selectedCount + " kişi " + groupName + " grubuna eklendi", Toast.LENGTH_SHORT).show();
                        
                        // Seçimleri temizle
                        adapter.clearSelection();
                    } else {
                        Toast.makeText(this, "Lütfen en az bir kişi seçin", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Kişi ekleme hatası: ", e);
                    Toast.makeText(this, "Kişi eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // SMS ekranına geçiş butonu
            smsScreen.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "SMS ekranına geçiş başlatılıyor");
                    
                    // Tüm gruplandırılmış kişileri bir ArrayList'e topla
                    ArrayList<TelephoneDirectoryList> allGroupedContacts = new ArrayList<>();
                    
                    // Her grup için kişileri ekle ve grup bilgisini ayarla
                    for (Map.Entry<String, ArrayList<TelephoneDirectoryList>> entry : groupedContacts.entrySet()) {
                        String groupName = entry.getKey();
                        ArrayList<TelephoneDirectoryList> contacts = entry.getValue();
                        
                        for (TelephoneDirectoryList contact : contacts) {
                            TelephoneDirectoryList newContact = new TelephoneDirectoryList();
                            newContact.setUserName(contact.getUserName());
                            newContact.setUserNumber(contact.getUserNumber());
                            newContact.setGroup(groupName);
                            allGroupedContacts.add(newContact);
                        }
                    }
                    
                    if (allGroupedContacts.isEmpty()) {
                        Toast.makeText(this, "Lütfen önce gruplara kişi ekleyin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Intent oluştur
                    Intent intent = new Intent(PhoneNumberActivity.this, SmsSubmissionPageActivity.class);
                    Log.d(TAG, "Intent oluşturuldu: " + intent.toString());
                    
                    // Gruplandırılmış kişileri gönder
                    intent.putExtra("selected_contacts", allGroupedContacts);
                    Log.d(TAG, "Gönderilen kişi sayısı: " + allGroupedContacts.size());
                    
                    // Activity'yi başlat
                    Toast.makeText(this, "SMS ekranına geçiliyor...", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 1);
                    Log.d(TAG, "startActivityForResult çağrıldı");
                } catch (Exception e) {
                    Log.e(TAG, "SMS ekranına geçiş hatası: ", e);
                    Toast.makeText(this, "SMS ekranına geçiş sırasında hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "initializeViews hatası: ", e);
            throw e;
        }
    }

    private String getCurrentGroup() {
        if (rdBtnRelative.isChecked()) return "relative";
        if (rdBtnFriend.isChecked()) return "friend";
        if (rdBtnWork.isChecked()) return "work";
        return null;
    }

    private void setupRecyclerView() {
        try {
            contactsList = new ArrayList<>();
            adapter = new PhoneNumberAdapter(this, contactsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView ayarlandı");
        } catch (Exception e) {
            Log.e(TAG, "setupRecyclerView hatası: ", e);
            throw e;
        }
    }

    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa iste
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
            Log.d(TAG, "Rehber izni istendi");
        } else {
            // İzin varsa rehberi yükle
            Log.d(TAG, "Rehber izni zaten var, kişiler yükleniyor");
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi
                Log.d(TAG, "Rehber izni verildi, kişiler yükleniyor");
                loadContacts();
            } else {
                // İzin reddedildi
                Log.w(TAG, "Rehber izni reddedildi");
                Toast.makeText(this, 
                    "Rehbere erişim izni olmadan kişilerinizi görüntüleyemezsiniz", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadContacts() {
        try {
            Cursor cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            if (cursor != null && cursor.getCount() > 0) {
                Log.d(TAG, "Bulunan kişi sayısı: " + cursor.getCount());
                contactsList.clear(); // Mevcut listeyi temizle
                
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    TelephoneDirectoryList contact = new TelephoneDirectoryList();
                    contact.setUserName(name);
                    contact.setUserNumber(number);
                    contactsList.add(contact);
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Kişiler yüklendi ve adapter güncellendi");
            } else {
                Log.w(TAG, "Rehberde kişi bulunamadı");
                Toast.makeText(this, "Rehberde kayıtlı kişi bulunamadı", Toast.LENGTH_SHORT).show();
            }
            if (cursor != null) cursor.close();
        } catch (SecurityException e) {
            Log.e(TAG, "Rehber erişim izni hatası: ", e);
            Toast.makeText(this, "Rehbere erişim izni gerekiyor", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Rehber yükleme hatası: ", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("clear_groups", false)) {
                // Tüm grupları temizle
                for (ArrayList<TelephoneDirectoryList> groupContacts : groupedContacts.values()) {
                    groupContacts.clear();
                }
                // Radio butonları sıfırla
                rdBtnRelative.setChecked(false);
                rdBtnFriend.setChecked(false);
                rdBtnWork.setChecked(false);
                // Seçimleri temizle
                if (adapter != null) {
                    adapter.clearSelection();
                }
                Toast.makeText(this, "Tüm gruplar temizlendi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}