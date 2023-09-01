package com.example.notesapp.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.notesapp.Converters.mConverters;
import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.Note;
import com.example.notesapp.Model.Task;
import com.example.notesapp.Model.TaskDate;
import com.example.notesapp.dao.CategoryDao;
import com.example.notesapp.dao.CredentialDao;
import com.example.notesapp.dao.NoteDao;
import com.example.notesapp.dao.TaskDao;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Database(entities = {Category.class, Note.class, Task.class, Credential.class},version = 9, exportSchema = false )
@TypeConverters(mConverters.class)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;
    public static String DB_FILEPATH = "/data/data/com.example.notesapp/databases/database.db";

    public static RoomDatabase.Callback myCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                NoteDao noteDao = notesDatabase.noteDao();
                CategoryDao categoryDao = notesDatabase.categoryDao();
                TaskDao taskDao = notesDatabase.taskDao();
                CredentialDao credentialDao = notesDatabase.credentialDao();
            });
        }
    };

    public static synchronized NotesDatabase getInstance(Context context){
        if (notesDatabase == null){
            notesDatabase = Room.databaseBuilder(
                    context,
                    NotesDatabase.class,
                    "notes_db"
            ).fallbackToDestructiveMigration().addCallback(myCallback).build();
        }
        return notesDatabase;
    }


    public abstract NoteDao noteDao();
    public abstract CategoryDao categoryDao();
    public abstract TaskDao taskDao();
    public abstract CredentialDao credentialDao();
}
