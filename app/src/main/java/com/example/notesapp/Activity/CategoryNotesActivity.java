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
        boolean isFavorite = getIntent().getBooleanExtra("isFavorite", false);
        if(isArchived){
            binding.tvCategoryName.setText("Archived Notes");
            Fragment categoryFragment  = CategoryNotes.newInstance(false,isArchived,"none");
            getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
        } else if (isFavorite) {
            binding.tvCategoryName.setText("Favorite Notes");
            Fragment categoryFragment  = CategoryNotes.newInstance(isFavorite,false,"none");
            getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
        } else
        {
            String categoryName = getIntent().getStringExtra("category");
            binding.tvCategoryName.setText(categoryName + " Notes");
            Fragment categoryFragment  = CategoryNotes.newInstance(true, false,categoryName);
            getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
        }

    }
}