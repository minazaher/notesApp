package com.example.notesapp.Activity;

import static androidx.recyclerview.widget.RecyclerView.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.CredentialCategory;
import com.example.notesapp.Model.Task;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CredentialRepository;
import com.example.notesapp.adapters.CredentialAdapter;
import com.example.notesapp.adapters.CredentialCategoriesAdapter;
import com.example.notesapp.adapters.TasksAdapter;
import com.example.notesapp.databinding.ActivityPasswordManagerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PasswordManagerActivity extends AppCompatActivity {
    ActivityPasswordManagerBinding binding;
    AlertDialog addCredentialDialog, iconPickerDialog ;
    CredentialRepository credentialRepository;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    CredentialAdapter credentialAdapter;
    List<Credential> credentialList;
    @SuppressLint("SwitchIntDef")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        credentialRepository = new CredentialRepository(this);

//        initializeFingerprintAuthentication();
        initializeCredentialsRecyclerView();

        binding.etSearchPasswords.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                credentialAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty ( newText ) ) {
                    credentialAdapter.getFilter().filter("");
                } else {
                    credentialAdapter.getFilter().filter(newText.toString());
                }
                return true;
            }
        });


        CredentialCategory category = new CredentialCategory("Apps");
        CredentialCategory category1 = new CredentialCategory("Wallets");
        CredentialCategory category2 = new CredentialCategory("Socials");
        CredentialCategory category3 = new CredentialCategory("others");

        category.setNumberOfApps(18);
        category1.setNumberOfApps(6);
        category2.setNumberOfApps(3);
        category3.setNumberOfApps(1);

        ArrayList<CredentialCategory> credentialCategories = new ArrayList<>();
        credentialCategories.add(category);
        credentialCategories.add(category1);
        credentialCategories.add(category2);
        credentialCategories.add(category3);

        CredentialCategoriesAdapter adapter = new CredentialCategoriesAdapter(credentialCategories);
        binding.credentialCategoriesRecyclerView.setAdapter(adapter);
        binding.credentialCategoriesRecyclerView.setLayoutManager(new GridLayoutManager(this,2,VERTICAL,false));
    }

    void initializeItemHelperForCredentials(RecyclerView recyclerView, CredentialAdapter adapter){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int swipeDir) {
                        int position = viewHolder.getAdapterPosition();
                        Credential credential = adapter.getCredentialList().get(position);
                        credentialRepository.removeCredential(credential);
                        adapter.updateData((ArrayList<Credential>) credentialRepository.getAllCredentials());
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initializeCredentialsRecyclerView(){
        credentialList = credentialRepository.getAllCredentials();
        credentialAdapter = new CredentialAdapter(credentialList);
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

        initializeItemHelperForCredentials(binding.credentialsRecyclerView, credentialAdapter);
    }
    private void initializeFingerprintAuthentication(){

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
                if(appName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()){
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    Credential credential = new Credential(email, password,appName,R.drawable.facebook_icon);
                    credentialRepository.insertCredential(credential);
                    credentialAdapter.updateData(credentialRepository.getAllCredentials());
                    addCredentialDialog.dismiss();
                }
            });
            view.findViewById(R.id.img_pick_app_icon).setOnClickListener(view13 -> showPickIconDialog());
            view.findViewById(R.id.textCancelCredential).setOnClickListener(view12 -> addCredentialDialog.dismiss());
            addCredentialDialog.show();

        }
    }

    void showPickIconDialog(){
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_icon_picker,
                (ViewGroup) findViewById(R.id.layout_addCredentialIconContainer));
        builder.setView(view);
        iconPickerDialog = builder.create();
        iconPickerDialog.show();
    }
}
