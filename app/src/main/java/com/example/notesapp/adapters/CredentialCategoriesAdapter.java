package com.example.notesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.CredentialCategory;
import com.example.notesapp.R;

import java.text.MessageFormat;
import java.util.List;

public class CredentialCategoriesAdapter extends RecyclerView.Adapter<CredentialCategoriesAdapter.Viewholder > {
    List<CredentialCategory> categories;

    public CredentialCategoriesAdapter(List<CredentialCategory> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CredentialCategoriesAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate
                                (R.layout.layout_credential_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialCategoriesAdapter.Viewholder holder, int position) {
        holder.setCategory(categories.get(position));

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateData(List<CredentialCategory> allCredentialCategory) {
        this.categories = allCredentialCategory;
        notifyDataSetChanged();

    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView name, noOFApps;
        ImageView icon;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_credentials_category_name);
            noOFApps = itemView.findViewById(R.id.tv_no_of_apps);
            icon = itemView.findViewById(R.id.credentialCategoryIcon);
        }

        private void setCategory(CredentialCategory category){
            name.setText(category.getCategoryName());
            String password  = String.valueOf(category.getNumberOfApps());
            noOFApps.setText(MessageFormat.format("{0} Passwords", password));
            icon.setImageResource(category.getCategoryIcon());
        }
    }
}
