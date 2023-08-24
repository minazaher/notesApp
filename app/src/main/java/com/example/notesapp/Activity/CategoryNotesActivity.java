package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.notesapp.Fragments.CategoryNotes;
import com.example.notesapp.R;

public class CategoryNotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catgeory_notes);

        boolean isArchived = getIntent().getBooleanExtra("isArchived", false);
        Fragment categoryFragment  = CategoryNotes.newInstance(isArchived);
        getSupportFragmentManager().beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();
    }
}