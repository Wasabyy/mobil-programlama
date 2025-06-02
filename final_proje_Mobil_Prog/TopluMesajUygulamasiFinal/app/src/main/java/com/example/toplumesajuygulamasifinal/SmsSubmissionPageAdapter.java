package com.example.toplumesajuygulamasifinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SmsSubmissionPageAdapter extends RecyclerView.Adapter<SmsSubmissionPageAdapter.ViewHolder> {
    private List<SmsSubmissionPageList> lists;
    private Context context;
    private ArrayList<SmsSubmissionPageList> selectedContacts;

    public SmsSubmissionPageAdapter(Context context, List<SmsSubmissionPageList> lists) {
        this.context = context;
        this.lists = lists;
        this.selectedContacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SmsSubmissionPageList list = lists.get(position);
        holder.userName.setText(list.getName());
        
        // CheckBox durumunu yönet
        holder.checkBox.setOnCheckedChangeListener(null); // Önceki listener'ı temizle
        holder.checkBox.setChecked(selectedContacts.contains(list));
        
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedContacts.add(list);
                } else {
                    selectedContacts.remove(list);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    // Seçili kişileri döndüren metod
    public ArrayList<TelephoneDirectoryList> getSelected() {
        ArrayList<TelephoneDirectoryList> result = new ArrayList<>();
        for (SmsSubmissionPageList selected : selectedContacts) {
            TelephoneDirectoryList contact = new TelephoneDirectoryList();
            contact.setUserName(selected.getName());
            contact.setUserNumber(selected.getNumber());
            result.add(contact);
        }
        return result;
    }

    // Seçimleri temizleyen metod
    public void clearSelection() {
        selectedContacts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.txt_userName);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
