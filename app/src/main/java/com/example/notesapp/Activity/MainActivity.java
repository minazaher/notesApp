package com.example.notesapp.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesapp.AsyncTasks.GetAllNotesTask;
import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CategoryRepository;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding mainBinding;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    NotesAdapter notesAdapter;
    private AlertDialog dialogConfirmNote;

    boolean fabClicked;
    public static final int REQUEST_CODE_UPDATE_NOTE = 1;
    private Animation rotateOpenAnim ;
    private Animation rotateCloseAnim ;

    private Animation toBottomAnim  ;
    private Animation fromBottomAnim ;
    ActivityResultLauncher<Intent> speechResultLauncher;
    CategoryRepository categoryRepository ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        showConfirmNoteDialog("Hey Google!");

        speechResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String recognizedSpeech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                        System.out.println("The Recognized speech is : "
                                +recognizedSpeech);

                    }
                });


        fromBottomAnim =  AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottomAnim  = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        rotateCloseAnim =  AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpenAnim =  AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);


        categoryRepository = new CategoryRepository(this);
        mainBinding.fabAddNote.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));
        initializeFloatingActionButtons();
        initializeDrawerLayout();
        String category = "Health";
        Category category1 = new Category();
        category1.setCategoryName(category);


//        new Thread(() -> NotesDatabase.getInstance(getApplicationContext()).categoryDao().insertCategory(category1)).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotes();
        initializeCategories();

    }

    private void initializeFloatingActionButtons(){
        mainBinding.fabAddNote.setOnClickListener(view -> {
            fabAddNoteClicked();
        });

        mainBinding.fabWriteNote.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));
        mainBinding.fabASRNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking...");
                speechResultLauncher.launch(intent);
            }
        });
        mainBinding.imageAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateTaskActivity.class));
            }
        });
        mainBinding.fabAttachVoiceNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "VN", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void fabAddNoteClicked() {
        setVisibility(fabClicked);
        setAnimation(fabClicked);
        setClickability(fabClicked);
        fabClicked = !fabClicked;
    }

    private void setClickability(boolean clicked) {
        if(!clicked){
            mainBinding.fabWriteNote.setClickable(true);
            mainBinding.fabASRNote.setClickable(true);
            mainBinding.fabAttachVoiceNote.setClickable(true);
        }
        else
        {
            mainBinding.fabWriteNote.setClickable(false);
            mainBinding.fabASRNote.setClickable(false);
            mainBinding.fabAttachVoiceNote.setClickable(false);
        }
    }

    private void setAnimation(boolean clicked) {
        if(!clicked){
            mainBinding.fabAddNote.setAnimation(rotateOpenAnim);
            mainBinding.fabAttachVoiceNote.setAnimation(fromBottomAnim);
            mainBinding.fabWriteNote.setAnimation(fromBottomAnim);
            mainBinding.fabASRNote.setAnimation(fromBottomAnim);
        }
        else
        {
            mainBinding.fabAddNote.setAnimation(rotateCloseAnim);
            mainBinding.fabAttachVoiceNote.setAnimation(toBottomAnim);
            mainBinding.fabWriteNote.setAnimation(toBottomAnim);
            mainBinding.fabASRNote.setAnimation(toBottomAnim);
        }
    }

    private void setVisibility(boolean clicked) {
        if(!clicked){
            mainBinding.fabAttachVoiceNote.setVisibility(View.VISIBLE);
            mainBinding.fabWriteNote.setVisibility(View.VISIBLE);
            mainBinding.fabASRNote.setVisibility(View.VISIBLE);
        }
        else
        {
            mainBinding.fabAttachVoiceNote.setVisibility(View.INVISIBLE);
            mainBinding.fabWriteNote.setVisibility(View.INVISIBLE);
            mainBinding.fabASRNote.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeCategories(){
        Menu menu = navigationView.getMenu();

        MenuItem labelsItem = menu.findItem(R.id.categories_submenu);
        SubMenu labelsSubmenu = labelsItem.getSubMenu();
        labelsSubmenu.clear();


        for (String categoryName : categoryRepository.getCategoriesNames() ) {
            labelsSubmenu.add(categoryName);
            labelsSubmenu.setIcon(R.drawable.baseline_category_24);
            labelsSubmenu.getItem().setIcon(R.drawable.baseline_category_24);
        }
    }
    private void initializeDrawerLayout(){
            drawerLayout = mainBinding.drawerLayout;
            navigationView = mainBinding.navView;
            navigationView.bringToFront();
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (fabClicked)
            fabAddNoteClicked();
        else if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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

    private void showConfirmNoteDialog(String noteText) {
        if (dialogConfirmNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_confirm_asr,
                    (ViewGroup) findViewById(R.id.layout_confirmNoteASR));
            builder.setView(view);
            dialogConfirmNote = builder.create();
            if (dialogConfirmNote.getWindow() != null) {
                dialogConfirmNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            EditText et_confirmNote = view.findViewById(R.id.et_confirmNote);
            et_confirmNote.setText(noteText);
            view.findViewById(R.id.textConfirm).setOnClickListener(view1 -> {
                Note note = new Note();
                note.setNoteText(noteText);
                Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
                dialogConfirmNote.dismiss();
            });

            view.findViewById(R.id.textNoConfirm).setOnClickListener(view12 -> dialogConfirmNote.dismiss());
        }
        dialogConfirmNote.show();
        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_archive) {
            navigationView.setCheckedItem(R.id.nav_archive);
            Intent intent = new Intent(MainActivity.this, CategoryNotesActivity.class);
            intent.putExtra("isArchived", true);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.nav_todo)
        {
            Intent intent = new Intent(MainActivity.this, ToDoListActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void checkSpeechRecognition() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mainBinding.fabASRNote.setVisibility(View.GONE);
        }
        else mainBinding.fabASRNote.setVisibility(View.VISIBLE);
    }
}
