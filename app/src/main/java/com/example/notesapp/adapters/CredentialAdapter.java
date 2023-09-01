package com.example.notesapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.Model.Credential;
import com.example.notesapp.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.ViewHolder> {
    List<Credential> credentialList;

    public CredentialAdapter(List<Credential> credentialList) {
        this.credentialList = credentialList;
    }

    @NonNull
    @Override
    public CredentialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate
                                (R.layout.credential, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialAdapter.ViewHolder holder, int position) {
            holder.setCredential(credentialList.get(position));
            AtomicBoolean isVisible = new AtomicBoolean(false);
            holder.showPassword.setOnClickListener(view -> {
                isVisible.set(holder.setPasswordVisibility(isVisible.get()));
            });
    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView password, email, appName;
        ImageView appIcon,showPassword;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            password = itemView.findViewById(R.id.tv_password);
            email = itemView.findViewById(R.id.tv_email);
            appName = itemView.findViewById(R.id.tv_appName);
            appIcon = itemView.findViewById(R.id.image_appIcon);
            showPassword = itemView.findViewById(R.id.image_showPassword);
        }
        void setCredential(Credential credential){
            password.setText(credential.getPassword());
            email.setText(credential.getEmail());
            appName.setText(credential.getAppName());
            appIcon.setImageResource(credential.getIcon());
        }

        boolean setPasswordVisibility(boolean isVisible){
            if(!isVisible){
                password.setVisibility(View.VISIBLE);
                showPassword.setRotation(90);
            }
            else{
                password.setVisibility(View.GONE);
                showPassword.setRotation(270);
            }
            return !isVisible;
        }

    }
}
