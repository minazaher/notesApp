package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.example.notesapp.Fragments.CategoryNotes;
import com.example.notesapp.R;
import com.example.notesapp.databinding.ActivityCatgeoryNotesBinding;

public class CategoryNotesActivity extends AppCompatActivity {
    ActivityCatgeoryNotesBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatgeoryNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isArchived = getIntent().getBooleanExtra("isArchived", false);
        if(isArchived){
            binding.tvCategoryName.setText("Archived Notes");
            Fragment categoryFragment  = CategoryNotes.newInstance(isArchived,"none");
            getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
        }
        else
        {
            String categoryName = getIntent().getStringExtra("category");
            binding.tvCategoryName.setText(categoryName + " Notes");
            Fragment categoryFragment  = CategoryNotes.newInstance(false,categoryName);
            getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
        }

    }
}