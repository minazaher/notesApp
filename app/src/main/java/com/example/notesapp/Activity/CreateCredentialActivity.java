package com.example.notesapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.notesapp.Listeners.OnIconPickedListener;
import com.example.notesapp.Model.Credential;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CredentialCategoryRepository;
import com.example.notesapp.Repository.CredentialRepository;
import com.example.notesapp.databinding.ActivityCreateCredentialBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateCredentialActivity extends AppCompatActivity {
    ActivityCreateCredentialBinding binding;
    String appName, password, email, category;
    OnIconPickedListener mOnIconPicked;
    CredentialRepository credentialRepository ;
    CredentialCategoryRepository credentialCategoryRepository;
    AlertDialog iconPickerDialog;
    int appIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCredentialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appIcon = R.drawable.baseline_category_24;
        credentialRepository = new CredentialRepository(this);
        credentialCategoryRepository = new CredentialCategoryRepository(this);

        initializeSpinner(binding.credentialCategoryAutoComplete);
        binding.imageBackCred.setOnClickListener(view -> onBackPressed());
        binding.imageSaveCred.setOnClickListener(view -> {
            try {
                saveCredential();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Error! Please, Try Again Later", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        });
        binding.imgPickAppIcon.setOnClickListener(view -> {
            showPickIconDialog();
            mOnIconPicked = drawableResource -> {
                appIcon = drawableResource;
                binding.imgPickAppIcon.setImageResource(drawableResource);
            };
        });

    }

    private void saveCredential() throws InterruptedException {
        Credential credential;
        if(DataIsValid()){
            getData();
            credential = new Credential(email,category, password, appName, appIcon);
            credentialRepository.insertCredential(credential);
            Toast.makeText(this, "Credential Saved Successfully!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        else
            Toast.makeText(this, "Please fill out all the fields!", Toast.LENGTH_SHORT).show();
    }
    private boolean DataIsValid() {
        return !binding.etEnterAppName.getText().toString().isEmpty()
        || !binding.etEnterPassword.getText().toString().isEmpty()
                ||!binding.etEnterEmail.getText().toString().isEmpty()
        || !binding.credentialCategoryAutoComplete.getText().toString().isEmpty();
    }

    private void getData(){
        appName = binding.etEnterAppName.getText().toString();
        password = binding.etEnterPassword.getText().toString();
        email = binding.etEnterEmail.getText().toString();
    }


    void showPickIconDialog() {
        AtomicInteger icon = new AtomicInteger();
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_icon_picker,
                (ViewGroup) findViewById(R.id.layout_addCredentialIconContainer));

        Map<View, Integer> viewIconMap = new HashMap<>();
        viewIconMap.put(view.findViewById(R.id.pick_facebook), R.drawable.icons8_facebook);
        viewIconMap.put(view.findViewById(R.id.pick_X), R.drawable.icons8_twitter);
        viewIconMap.put(view.findViewById(R.id.pick_instagram), R.drawable.instagram_ic);
        viewIconMap.put(view.findViewById(R.id.pick_default), R.drawable.baseline_category_24);
        setOnlySelectedIcon(viewIconMap, icon);

        view.findViewById(R.id.textAddIcon).setOnClickListener(view12 -> {
            appIcon = icon.get();
            iconPickerDialog.dismiss();
            if (mOnIconPicked != null) {
                mOnIconPicked.iconClicked(appIcon);
            }
        });
        view.findViewById(R.id.textCancelIcon).setOnClickListener(view13 -> iconPickerDialog.dismiss());


        builder.setView(view);
        iconPickerDialog = builder.create();
        iconPickerDialog.show();
    }

    private void initializeSpinner(AutoCompleteTextView selectCategory){
        ArrayList<String> cats = new ArrayList<>();
        cats.addAll(credentialCategoryRepository.getCredentialCategoryNames());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_category_item, cats);
        selectCategory.setAdapter(arrayAdapter);
        selectCategory.setOnItemClickListener(this::onItemClick);
    }

    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }
    void setAsSelected(View view){
        view.setBackgroundColor(getColor(R.color.colorIcons));
    }

    void setOnlySelectedIcon(Map<View, Integer> viewIconMap, AtomicInteger icon){
        for (Map.Entry<View, Integer> entry : viewIconMap.entrySet()) {
            View iconView = entry.getKey();
            int iconResource = entry.getValue();
            iconView.setOnClickListener(v -> {
                icon.set(iconResource);
                setAsSelected(v);
                for (View otherView : viewIconMap.keySet()) {
                    if (otherView != v) {
                        setAsNotSelected(otherView);
                    }
                }
            });
        }

    }
    void setAsNotSelected(View view){
        view.setBackgroundColor(getColor(R.color.colorSearchBackground));
    }

}