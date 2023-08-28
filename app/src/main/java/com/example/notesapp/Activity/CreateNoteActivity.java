package com.example.notesapp.Activity;

import static com.example.notesapp.R.style.ColorPickerDialog_Dark;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.Repository.CategoryRepository;
import com.example.notesapp.databinding.ActivityCreateNoteBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;
import me.jfenn.colorpickerdialog.interfaces.OnColorPickedListener;

public class CreateNoteActivity extends AppCompatActivity{
    private static final String NOTE_TITLE_EMPTY = "Note title cannot be empty!";
    private static final String NOTE_TEXT_EMPTY = "Note text cannot be empty!";
    private static final String NOTE_SUBTITLE_EMPTY = "Note subtitle cannot be empty!";
    private static final String NOTE_VALID = "";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    ActivityCreateNoteBinding createNoteBinding;

    public ActivityResultLauncher<String> selectPhoto;

    String noteTitle, noteText, noteSubtitle;
    private String selectedNoteColor;
    Boolean isArchived = false ;
    Boolean isFavourite= false ;

    private String selectedImagePath;
    private String URL ="";
    private int selectedCategory;
    private CategoryRepository categoryRepository;
    private AlertDialog dialogAddUrl;
    private Note alreadyAvailableNote;
    private AlertDialog dialogAddCategory;
    public static String selectedLocation  ;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        createNoteBinding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(createNoteBinding.getRoot());
        categoryRepository = new CategoryRepository(this);
        selectedNoteColor = "#deeae6";


        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            createNoteBinding.misc.layoutAddToArchive.setVisibility(View.VISIBLE);
            createNoteBinding.misc.layoutDeleteNote.setVisibility(View.VISIBLE);
            setViewOrUpdateNote();
        }
        initializeColorPicker();

        selectPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            createNoteBinding.imageDeleteImage.setVisibility(View.VISIBLE);
            createNoteBinding.imageNote.setImageURI(result);
            selectedImagePath = getPathFromUri(result);
        });

        createNoteBinding.imageBack.setOnClickListener(view -> onBackPressed());
        createNoteBinding.misc.layoutAddToArchive.setOnClickListener(view -> {isArchived = !alreadyAvailableNote.getArchived();});
        initializeMisc();

        createNoteBinding.textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM, yyyy HH:mm a ", Locale.getDefault())
                        .format(new Date())
        );

        createNoteBinding.imageSave.setOnClickListener(view -> {
            try {
                saveNote();
            } catch (InterruptedException e) {
                Toast.makeText(CreateNoteActivity.this, "Error occurred! Try again later", Toast.LENGTH_SHORT).show();
            }
        });
        createNoteBinding.imageDeleteWebUrl.setOnClickListener(view -> {
            createNoteBinding.textWebUrl.setText(null);
            createNoteBinding.layoutWebUrl.setVisibility(View.GONE);
            alreadyAvailableNote.setNoteText(null);
        });
        createNoteBinding.imageDeleteImage.setOnClickListener(view -> {
            createNoteBinding.imageNote.setImageBitmap(null);
            createNoteBinding.imageNote.setVisibility(View.GONE);
            createNoteBinding.imageDeleteImage.setVisibility(View.GONE);
            selectedImagePath = "";
        });

        createNoteBinding.misc.layoutDeleteNote.setOnClickListener(view -> {
            try {
                deleteNote(alreadyAvailableNote);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(CreateNoteActivity.this, "Failed! Try Again Later Please", Toast.LENGTH_SHORT).show();
            }
        });
        initializeSpinner();

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("location") != null){
            selectedLocation = getIntent().getStringExtra("location");
        }

        createNoteBinding.misc.layoutAddLocation.setOnClickListener(view -> {
            if (isLocationEnabled())
                startActivity(new Intent(CreateNoteActivity.this, GetLocationActivity.class));
            else
                showTurnLocationOnDialog();
        });

    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void showTurnLocationOnDialog(){
        if (!isLocationEnabled()) {
            new AlertDialog.Builder(this)
                    .setMessage("Your location settings are turned off. Please turn them on to use this feature.")
                    .setPositiveButton("Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

    }

    private void setViewOrUpdateNote(){
        selectedNoteColor = alreadyAvailableNote.getColor();
        createNoteBinding.etNoteTitle.setText(alreadyAvailableNote.getTitle());
        createNoteBinding.etNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        createNoteBinding.etNote.setText(alreadyAvailableNote.getNoteText());
        createNoteBinding.textDateTime.setText(alreadyAvailableNote.getDateTime());
        createNoteBinding.categoryAutoComplete.setText(categoryRepository.getCategoryNameById(alreadyAvailableNote.getCategoryId()));

        if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()){
            createNoteBinding.imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            createNoteBinding.imageNote.setVisibility(View.VISIBLE);
            createNoteBinding.imageDeleteImage.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }

        if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            createNoteBinding.layoutWebUrl.setVisibility(View.VISIBLE);
            createNoteBinding.textWebUrl.setText(alreadyAvailableNote.getWebLink());
        }
        if (alreadyAvailableNote.getArchived().equals(true))
            createNoteBinding.misc.layoutAddToArchive.setVisibility(View.GONE);

    }

    private String getNoteStatus() {
        noteTitle = createNoteBinding.etNoteTitle.getText().toString();
        noteText = createNoteBinding.etNote.getText().toString();
        noteSubtitle = createNoteBinding.etNoteSubtitle.getText().toString();

        if (noteTitle.isEmpty())
            return NOTE_TITLE_EMPTY;
        if (noteText.isEmpty())
            return NOTE_TEXT_EMPTY;
        if (noteSubtitle.isEmpty())
            return NOTE_SUBTITLE_EMPTY;

        return NOTE_VALID;
    }

    private void saveNote() throws InterruptedException {
        if (!getNoteStatus().isEmpty()) {
            Toast.makeText(this, getNoteStatus(), Toast.LENGTH_SHORT).show();
        } else {
            final Note note = new Note();
            note.setTitle(noteTitle);
            note.setSubtitle(noteSubtitle);
            note.setNoteText(noteText);
            note.setColor(selectedNoteColor);
            note.setImagePath(selectedImagePath);
            note.setDateTime(createNoteBinding.textDateTime.getText().toString());
            note.setArchived(isArchived);
            note.setFavourite(isFavourite);
            note.setCategoryId(categoryRepository.getCategoryIdByName(createNoteBinding.categoryAutoComplete.getText().toString()));
            if (!URL.isEmpty())
            {
                note.setWebLink(URL);
                System.out.println("The URL Saved is : " + URL);
            }
            if (selectedLocation != null){
                note.setLocation(selectedLocation);
            }
            if(alreadyAvailableNote != null)
                note.setId(alreadyAvailableNote.getId());

            Thread myThread = new Thread(() -> NotesDatabase.getInstance(getApplicationContext()).noteDao().insertNote(note));
            myThread.start();
            myThread.join();
            onBackPressed();
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeMisc() {
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(createNoteBinding.misc.getRoot());
        setColorPicker();
        createNoteBinding.misc.layoutAddImage.setOnClickListener(view -> {
            if (!isPermissionGranted())
                grantPermission();
            else
                selectImage();
        });

    createNoteBinding.misc.layoutAddUrl.setOnClickListener(view -> {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        showAddUrlDialog();});

    }

    private void initializeColorPicker(){
        createNoteBinding.misc.tvPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerDialog()
                        .withColor(Color.RED)
                        .withAlphaEnabled(true)
                        .withTitle("Pick Your Note Color")
                        .withCornerRadius(5.0F)
                        .withTheme(ColorPickerDialog_Dark)
                        .withListener((dialog, color) -> {
                            selectedNoteColor = String.format("#%06X", (0xFFFFFF & color));
                            setSubtitleIndicatorColor();
                            Toast.makeText(CreateNoteActivity.this, "Color Picked!", Toast.LENGTH_SHORT).show();
                        })
                        .show(getSupportFragmentManager(), "colorPicker");
            }
        });
    }
    public void selectImage() {
        selectPhoto.launch("image/*");
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void grantPermission() {
        ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
            else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setColorPicker() {
        String[] colors = {"#deeae6", "#FF1D8E", "#3a52Fc", "#F3DD5C", "#1AA7EC"};
        View[] views = {createNoteBinding.misc.viewColor1, createNoteBinding.misc.viewColor2, createNoteBinding.misc.viewColor3, createNoteBinding.misc.viewColor4, createNoteBinding.misc.viewColor5};
        ImageView[] images = {createNoteBinding.misc.imageColor1, createNoteBinding.misc.imageColor2, createNoteBinding.misc.imageColor3, createNoteBinding.misc.imageColor4, createNoteBinding.misc.imageColor5};

        for (int i = 0; i < views.length; i++) {
            int finalI = i;
            views[i].setOnClickListener(view -> {
                selectedNoteColor = colors[finalI];
                images[finalI].setImageResource(R.drawable.ic_done);
                for (int j = 0; j < images.length; j++) {
                    if (j != finalI) {
                        images[j].setImageResource(0);
                    }
                }
                setSubtitleIndicatorColor();
            });

            if(alreadyAvailableNote != null){
                switch (alreadyAvailableNote.getColor()){
                    case "#FDBE3B":
                        createNoteBinding.misc.viewColor2.performClick();
                        break;
                    case "#3a52Fc":
                        createNoteBinding.misc.viewColor3.performClick();
                        break;
                    case "#000000":
                        createNoteBinding.misc.viewColor4.performClick();
                        break;
                    case "#FF48F2":
                        createNoteBinding.misc.viewColor5.performClick();
                        break;
                }
            }
        }
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) createNoteBinding.viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    private String getPathFromUri(Uri uri) {
        String filepath;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            String id = documentId.split(":")[1];
            Uri mediaStoreUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Long.parseLong(id));
            Cursor cursor = getContentResolver().query(mediaStoreUri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex("_data");
                filepath = cursor.getString(index);
                System.out.println("The FilePath is :" + filepath);
                cursor.close();
            } else {
                throw new IllegalStateException("Cursor is null");
            }
        } else {
            // Handle other types of Uri
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                filepath = uri.getPath();
            } else {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex("_data");
                if (index == -1) {
                    // Handle error: column with name "_data" does not exist in Cursor
                    throw new IllegalArgumentException("Invalid Uri: column with name \"_data\" does not exist in Cursor");
                } else {
                    filepath = cursor.getString(index);
                    System.out.println("The FilePath is :" + filepath);
                    cursor.close();
                }
            }
        }
        return filepath;
    }

    private void showAddUrlDialog(){
        if(dialogAddUrl == null){
            AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layout_addUrlContainer));
            builder.setView(view);
            dialogAddUrl = builder.create();
            if(dialogAddUrl.getWindow() != null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            EditText et_url = view.findViewById(R.id.et_url);
            et_url.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(view1 -> {
                String url = et_url.getText().toString();
                if (url.trim().isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                }
                else if(!Patterns.WEB_URL.matcher(url).matches()){
                    Toast.makeText(CreateNoteActivity.this, "Enter A Valid URL", Toast.LENGTH_SHORT).show();
                }
                else{
                    createNoteBinding.textWebUrl.setText(url);
                    createNoteBinding.layoutWebUrl.setVisibility(View.VISIBLE);
                    URL = url;
                    dialogAddUrl.dismiss();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(view12 -> dialogAddUrl.dismiss());
        }
        dialogAddUrl.show();
    }

    private void showAddCategoryDialog(){
        if(dialogAddCategory == null){
            AlertDialog.Builder  builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_category,
                    (ViewGroup) findViewById(R.id.layout_addCategoryContainer));
            builder.setView(view);
            dialogAddCategory = builder.create();
            if(dialogAddCategory.getWindow() != null){
                dialogAddCategory.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            EditText et_addCategory = view.findViewById(R.id.et_addCategory);
            et_addCategory.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(view1 -> {
                String Category = et_addCategory.getText().toString();
                if (Category.trim().isEmpty()){
                    Toast.makeText(CreateNoteActivity.this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                }
                else if (categoryRepository.isExist(Category))
                    Toast.makeText(this, "Category Already Exists!", Toast.LENGTH_SHORT).show();
                else{
                    createNoteBinding.categoryAutoComplete.setText(Category);
                    categoryRepository.addNewCategory(Category);
                    dialogAddCategory.dismiss();
                }
            });

            view.findViewById(R.id.textCancelCategory).setOnClickListener(view12 -> dialogAddCategory.dismiss());
        }
        dialogAddCategory.show();
    }

    private void initializeSpinner(){
        ArrayList<String> cats = new ArrayList<>();
        cats.add("Add New Category");
        cats.addAll(1, categoryRepository.getCategoriesNames());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_category_item, cats);
        createNoteBinding.categoryAutoComplete.setAdapter(arrayAdapter);

        createNoteBinding.categoryAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                System.out.println(selectedItem);
                if (selectedItem.equals("Add New Category")) {
                    showAddCategoryDialog();
                } else {
                    selectedCategory = categoryRepository.getCategoryIdByName(selectedItem);
                }
            }
        });
    }
    private void deleteNote(Note note) throws InterruptedException {
        Thread deleteThread = new Thread(() ->
                NotesDatabase
                        .getInstance(getApplicationContext())
                        .noteDao()
                        .deleteNote(note));
        deleteThread.start();
        deleteThread.join();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

}