package com.example.notesapp.Activity;

import static androidx.recyclerview.widget.RecyclerView.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesapp.Model.Credential;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CredentialRepository;
import com.example.notesapp.adapters.CredentialAdapter;
import com.example.notesapp.databinding.ActivityPasswordManagerBinding;

import java.util.concurrent.Executor;

public class PasswordManagerActivity extends AppCompatActivity {
    ActivityPasswordManagerBinding binding;
    AlertDialog addCredentialDialog ;
    CredentialRepository credentialRepository;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;


    @SuppressLint("SwitchIntDef")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        credentialRepository = new CredentialRepository(this);

        // Initializing Fingerprint Auth
        BiometricManager biometricManager = BiometricManager.from(this);

        switch ((biometricManager.canAuthenticate())) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Your Device Doesn't Have Fingerprint Sensor", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Fingerprint Sensor is Not Working Properly", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No Fingerprint Assigned!", Toast.LENGTH_SHORT).show();
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(PasswordManagerActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                binding.layoutPasswordManager.setVisibility(VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(PasswordManagerActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Notey Password Manager")
                .setDescription("Authenticate to view your passwords please").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);

        CredentialAdapter credentialAdapter = new CredentialAdapter(credentialRepository.getAllCredentials());
        binding.credentialsRecyclerView.setAdapter(credentialAdapter);
        binding.credentialsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));

        binding.fabAddCredential.setOnClickListener(view -> showAddCredentialDialog());

        binding.credentialsRecyclerView.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && binding.fabAddCredential.isShown())
                    binding.fabAddCredential.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE)
                    binding.fabAddCredential.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    private void showAddCredentialDialog(){
        if(addCredentialDialog == null){
            AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_credential,
                    (ViewGroup) findViewById(R.id.layout_addCredentialContainer));
            builder.setView(view);
            addCredentialDialog = builder.create();
            if(addCredentialDialog.getWindow() != null){
                addCredentialDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            EditText et_appName = view.findViewById(R.id.et_enterAppName);
            EditText et_email = view.findViewById(R.id.et_enterEmail);
            EditText et_password = view.findViewById(R.id.et_enterPassword);

            et_appName.requestFocus();

            view.findViewById(R.id.textAddCredential).setOnClickListener(view1 -> {
                String appName = et_appName.getText().toString();
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                Credential credential = new Credential(email, password,appName,R.drawable.baseline_category_24);
                    credentialRepository.insertCredential(credential);
                    addCredentialDialog.dismiss();
            });

            view.findViewById(R.id.textCancelCredential).setOnClickListener(view12 -> addCredentialDialog.dismiss());
        }
        addCredentialDialog.show();
    }

}