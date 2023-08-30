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
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
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
import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CategoryRepository;
import com.example.notesapp.Repository.NotesRepository;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.raphaelebner.roomdatabasebackup.core.RoomBackup;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding mainBinding;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    final String SECRET_PASSWORD = "verySecretEncryptionKey";

    NotesAdapter notesAdapter;
    private AlertDialog dialogConfirmNote;
    boolean fabClicked;
    public static final int REQUEST_CODE_UPDATE_NOTE = 1;
    private Animation rotateOpenAnim ;
    private Animation rotateCloseAnim ;
    private Animation toBottomAnim  ;
    private Animation fromBottomAnim ;
    SubMenu labelsSubMenu;
    NotesRepository notesRepository;
    ActivityResultLauncher<Intent> speechResultLauncher;
    CategoryRepository categoryRepository ;

    RoomBackup roomBackup ;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        roomBackup= new RoomBackup(MainActivity.this);
        notesRepository = new NotesRepository(this);
        speechResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        String recognizedSpeech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                        System.out.println("The Recognized speech is : "
                                +recognizedSpeech);
                        showConfirmNoteDialog(recognizedSpeech);

                    }
                });
        System.out.println("package name is :" +getApplicationContext().getPackageName());
        fromBottomAnim =  AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottomAnim  = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        rotateCloseAnim =  AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        rotateOpenAnim =  AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
mainBinding.imageBackup.setOnClickListener(view -> {
    roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_FILE);
    roomBackup.backupLocationCustomFile(new File(getFilesDir()+"/databasebackup/geilesBackup.sqlite3\""));
    roomBackup.database(NotesDatabase.getInstance(getApplicationContext()));
    roomBackup.enableLogDebug(true);
    roomBackup.backupIsEncrypted(true);
    System.out.println(RoomBackup.BACKUP_FILE_LOCATION_EXTERNAL);
    roomBackup.customEncryptPassword(SECRET_PASSWORD);
    roomBackup.onCompleteListener((success, message, exitCode) -> {
        System.out.println("oncomplete: " + success + ", message: " + message + ", exitCode: " + exitCode);
        if (success)
            roomBackup.restartApp(new Intent(getApplicationContext(), MainActivity.class));
        else
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
    });
    roomBackup.backup();
});
    mainBinding.imageAddChangeTheme.setOnClickListener(view -> {
        roomBackup.backupLocation(RoomBackup.BACKUP_FILE_LOCATION_CUSTOM_FILE);
        roomBackup.backupLocationCustomFile(new File(getFilesDir()+"/databasebackup/geilesBackup.sqlite3\""));
        roomBackup.database(NotesDatabase.getInstance(getApplicationContext()));
        roomBackup.enableLogDebug(true);
        roomBackup.backupIsEncrypted(true);
        roomBackup.customEncryptPassword(SECRET_PASSWORD);
        roomBackup.onCompleteListener((success, message, exitCode) -> {
            System.out.println("oncomplete: " + success + ", message: " + message + ", exitCode: " + exitCode);
            if (success)
                roomBackup.restartApp(new Intent(getApplicationContext(), MainActivity.class));
        });
        roomBackup.restore();
    });
        categoryRepository = new CategoryRepository(this);
        mainBinding.fabAddNote.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));
        initializeFloatingActionButtons();
        initializeDrawerLayout();

    }

    public void backupDatabase(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        getNotes();
        initializeCategories();

    }

    private void initializeFloatingActionButtons(){
        mainBinding.fabAddNote.setOnClickListener(view -> fabAddNoteClicked());

        mainBinding.fabWriteNote.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));
        mainBinding.fabASRNote.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-EG");
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5);
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,10);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking...");
            speechResultLauncher.launch(intent);
        });

        mainBinding.imageAddTask.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CreateTaskActivity.class)));
        mainBinding.fabAttachVoiceNote.setOnClickListener(view -> Toast.makeText(MainActivity.this, "VN", Toast.LENGTH_SHORT).show());
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
        labelsSubMenu = labelsItem.getSubMenu();
        labelsSubMenu.clear();


        for (String categoryName : categoryRepository.getCategoriesNames() ) {
            labelsSubMenu.add(categoryName);
            labelsSubMenu.setIcon(R.drawable.baseline_category_24);
            System.out.println("Labels sub menu : " + labelsSubMenu.getItem(0).getItemId());
            labelsSubMenu.getItem().setIcon(R.drawable.baseline_category_24);
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
                Note note = new Note(noteText);
                notesRepository.insertNote(note);
                Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
                dialogConfirmNote.dismiss();
            });
            view.findViewById(R.id.textNoConfirm).setOnClickListener(view12 -> dialogConfirmNote.dismiss());
        }
        dialogConfirmNote.show();
        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        List<String> categories = categoryRepository.getCategoriesNames();
        String categoryName = Objects.requireNonNull(item.getTitle()).toString();
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
        else if (categories.contains(item.getTitle().toString())){
            System.out.println("this is category : " + categoryName );
            Intent intent = new Intent(MainActivity.this, CategoryNotesActivity.class);
            intent.putExtra("category", categoryName);
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
