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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notesapp.Listeners.OnIconPickedListener;
import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.CredentialCategory;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CredentialCategoryRepository;
import com.example.notesapp.Repository.CredentialRepository;
import com.example.notesapp.adapters.CredentialAdapter;
import com.example.notesapp.adapters.CredentialCategoriesAdapter;
import com.example.notesapp.databinding.ActivityPasswordManagerBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class PasswordManagerActivity extends AppCompatActivity {
    ActivityPasswordManagerBinding binding;
    CredentialRepository credentialRepository;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    CredentialAdapter credentialAdapter;
    List<Credential> credentialList;
    CredentialCategoriesAdapter credentialCategoriesAdapter;
    CredentialCategoryRepository credentialCategoryRepository;
    int appIcon;
    @SuppressLint("SwitchIntDef")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        credentialRepository = new CredentialRepository(this);
        credentialCategoryRepository = new CredentialCategoryRepository(this);
        appIcon = R.drawable.baseline_category_24;

        binding.layoutPasswordManager.setOnRefreshListener(() -> {
            refreshData();
            binding.layoutPasswordManager.setRefreshing(false);
        });

        initializeFingerprintAuthentication();
        initializeCredentialsRecyclerView();
        initializeCategoriesRecyclerView();

        binding.etSearchPasswords.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.layoutCredentialsCategories.setVisibility(GONE);
                credentialAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                binding.layoutCredentialsCategories.setVisibility(GONE);
                if (TextUtils.isEmpty ( newText ) ) {
                    credentialAdapter.getFilter().filter("");
                } else {
                    credentialAdapter.getFilter().filter(newText.toString());
                }
                return true;
            }
        });

    }

    private void refreshData() {
        credentialAdapter.updateData(credentialRepository.getAllCredentials());
        credentialCategoriesAdapter.updateData(credentialCategoryRepository.getAllCredentialCategory());

    }

    @Override
    public void onBackPressed() {
        if(binding.layoutCredentialsCategories.getVisibility() == GONE){
            binding.layoutCredentialsCategories.setVisibility(VISIBLE);
        }
        else
            super.onBackPressed();
    }

    void initializeCategoriesRecyclerView(){
        ArrayList<CredentialCategory> credentialCategories = new ArrayList<>(credentialCategoryRepository.getAllCredentialCategory());

        if(credentialCategories.isEmpty()){
            credentialCategoryRepository.insertDefaultCategories();
        }
        credentialCategoriesAdapter = new CredentialCategoriesAdapter(credentialCategories);
        binding.credentialCategoriesRecyclerView.setAdapter(credentialCategoriesAdapter);
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
                        try {
                            credentialRepository.removeCredential(credential);
                        } catch (InterruptedException e) {
                            Toast.makeText(PasswordManagerActivity.this, "Cannot remove it!", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                        refreshData();
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
        binding.fabAddCredential.setOnClickListener(view -> startActivity(new Intent(PasswordManagerActivity.this, CreateCredentialActivity.class)));
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


}

