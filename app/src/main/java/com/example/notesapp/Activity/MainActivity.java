package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notesapp.AsyncTasks.GetAllNotesTask;
import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Listeners.NotesListener;
import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Note;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;
    NotesAdapter notesAdapter;
    public static final int REQUEST_CODE_UPDATE_NOTE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        mainBinding.imageAddNoteMain.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));


//        String category = "Personal";
//        Category category1 = new Category();
//        category1.setCategoryName(category);
//
//        new Thread(() -> NotesDatabase.getInstance(getApplicationContext()).categoryDao().insertCategory(category1)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotes();

    }


    private void getNotes() {
        GetAllNotesTask getAllNotesTask = new GetAllNotesTask(this,
                notes -> {
                    mainBinding.notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    notesAdapter = new NotesAdapter(notes, (note, position) -> {
                        Intent intent = new Intent(MainActivity.this,CreateNoteActivity.class);
                        intent.putExtra("isViewOrUpdate", true);
                        intent.putExtra("note", note);
                        intent.putExtra("code", REQUEST_CODE_UPDATE_NOTE);
                        startActivity(intent);
                    });
                    mainBinding.notesRecyclerView.setAdapter(notesAdapter);
                });
        getAllNotesTask.execute();

//        mainBinding.etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                notesAdapter.searchNotes(s);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                notesAdapter.searchNotes(s);
//                return true;
//            }
//        });
    }
}
