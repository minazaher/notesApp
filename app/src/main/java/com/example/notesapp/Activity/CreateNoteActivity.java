package com.example.notesapp.Activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.databinding.ActivityCreateNoteBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private static final String NOTE_TITLE_EMPTY = "Note title cannot be empty!";
    private static final String NOTE_TEXT_EMPTY = "Note text cannot be empty!";
    private static final String NOTE_SUBTITLE_EMPTY = "Note subtitle cannot be empty!";
    private static final String NOTE_VALID = "";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    ActivityResultLauncher<String> selectPhoto;
    private ActivityResultLauncher<String> selectImageLauncher;
    private Bitmap imgToStore;
    String Filepath;
    private String selectedNoteColor;
    private String selectedImagePath;
    String noteTitle, noteText, noteSubtitle;
    ActivityCreateNoteBinding createNoteBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNoteBinding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(createNoteBinding.getRoot());
        selectedNoteColor = "#333333";

        selectPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                createNoteBinding.imageNote.setImageURI(result);
               selectedImagePath = getPathFromUri(result);
            }
        });

        createNoteBinding.imageBack.setOnClickListener(view -> onBackPressed());

        initializeMisc();

        createNoteBinding.textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM, yyyy HH:mm a " , Locale.getDefault())
                        .format(new Date())
        );

        createNoteBinding.imageSave.setOnClickListener(view -> {
            try {
                saveNote();
            } catch (InterruptedException e) {
                Toast.makeText(CreateNoteActivity.this, "Error occurred! Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getNoteStatus(){
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
        if (!getNoteStatus().isEmpty()){
            Toast.makeText(this, getNoteStatus(), Toast.LENGTH_SHORT).show();
        }

        else{
            final Note note = new Note();
            note.setTitle(noteTitle);
            note.setSubtitle(noteSubtitle);
            note.setNoteText(noteText);
            note.setColor(selectedNoteColor);
            note.setImagePath(selectedImagePath);
            note.setDateTime(createNoteBinding.textDateTime.getText().toString());
            Thread myThread = new Thread(() -> NotesDatabase.getInstance(getApplicationContext()).noteDao().insertNote(note));
            myThread.start();
            myThread.join();
            onBackPressed();
            Toast.makeText(this, "Note Saved!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeMisc(){
      setColorPicker();
      createNoteBinding.misc.layoutAddImage.setOnClickListener(view -> {
          if(!isPermissionGranted())
              grantPermission();
          else
              selectImage();
      });

    }

    private void selectImage(){
        selectPhoto.launch("image/*");

    }

    private boolean isPermissionGranted(){
       return ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void grantPermission(){
        ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length  > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                selectImage();
            else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setColorPicker(){
        String[] colors = {"#333333", "#FDBE3B", "#3a52Fc", "#000000", "#FF48F2"};
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
        }
    }


    private void setSubtitleIndicatorColor(){
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



}