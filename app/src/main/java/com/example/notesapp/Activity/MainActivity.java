package com.example.notesapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.notesapp.AsyncTasks.GetAllNotesTask;
import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Listeners.NotesListener;
import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CategoryRepository;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding mainBinding;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    NotesAdapter notesAdapter;
    public static final int REQUEST_CODE_UPDATE_NOTE = 1;
    CategoryRepository categoryRepository ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        categoryRepository = new CategoryRepository(this);

        mainBinding.imageAddNoteMain.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), CreateNoteActivity.class)));

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
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_archive) {
            navigationView.setCheckedItem(R.id.nav_archive);
            Intent intent = new Intent(MainActivity.this, CategoryNotesActivity.class);
            intent.putExtra("isArchived", true);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;    }
}
