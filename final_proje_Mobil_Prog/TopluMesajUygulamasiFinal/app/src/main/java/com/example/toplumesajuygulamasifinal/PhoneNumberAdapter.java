package com.example.toplumesajuygulamasifinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.PostHolder> {
    private static final String TAG = "PhoneNumberAdapter";
    Context context;
    ArrayList<TelephoneDirectoryList> telephoneDirectoryList;
    private ArrayList<TelephoneDirectoryList> selectedContacts;

    public PhoneNumberAdapter(Context context, ArrayList<TelephoneDirectoryList> telephoneDirectoryList){
        this.context = context;
        this.telephoneDirectoryList = telephoneDirectoryList;
        this.selectedContacts = new ArrayList<>();
    }

    public void setTelephoneDirectoryList(ArrayList<TelephoneDirectoryList> telephoneDirectoryList){
        this.telephoneDirectoryList = new ArrayList<>();
        this.telephoneDirectoryList = telephoneDirectoryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.phone_number_row, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position){
        holder.bind(telephoneDirectoryList.get(position));
    }

    @Override
    public int getItemCount(){
        return telephoneDirectoryList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        TextView nameText, numberText;
        ImageView imageView;
        CheckBox checkBox;

        public PostHolder(@NonNull View itemView){
            super(itemView);
            nameText = itemView.findViewById(R.id.txt_recyclerRow_userNameTextView);
            numberText = itemView.findViewById(R.id.txt_recyclerRow_userNumberTextView);
            imageView = itemView.findViewById(R.id.img_recyclerRow_userImageView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        void bind(final TelephoneDirectoryList telephoneDirectoryList){
            nameText.setText(telephoneDirectoryList.getUserName());
            numberText.setText(telephoneDirectoryList.getUserNumber());

            if(telephoneDirectoryList.getUserFoto() != null) {
                try{
                    Bitmap bitmap = BitmapFactory.decodeFile(telephoneDirectoryList.getUserFoto());
                    imageView.setImageBitmap(bitmap);
                }
                catch (Exception e){
                    Log.e(TAG, "bind: " + e.getMessage());
                }
            }

            // CheckBox durumunu yönet
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(telephoneDirectoryList.isSelected());
            
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    telephoneDirectoryList.setSelected(isChecked);
                    if (isChecked) {
                        if (!selectedContacts.contains(telephoneDirectoryList)) {
                            selectedContacts.add(telephoneDirectoryList);
                        }
                    } else {
                        selectedContacts.remove(telephoneDirectoryList);
                    }
                }
            });
        }
    }

    public ArrayList<TelephoneDirectoryList> getSelected() {
        selectedContacts.clear();
        for (TelephoneDirectoryList contact : telephoneDirectoryList) {
            if (contact.isSelected()) {
                selectedContacts.add(contact);
            }
        }
        return selectedContacts;
    }

    public void clearSelection() {
        for (TelephoneDirectoryList contact : telephoneDirectoryList) {
            contact.setSelected(false);
        }
        selectedContacts.clear();
        notifyDataSetChanged();
    }
}