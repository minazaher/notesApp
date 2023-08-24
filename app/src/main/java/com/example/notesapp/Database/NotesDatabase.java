package com.example.notesapp.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Note;
import com.example.notesapp.dao.CategoryDao;
import com.example.notesapp.dao.NoteDao;

@Database(entities = {Category.class, Note.class},version = 4, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;

    public static RoomDatabase.Callback myCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                NoteDao noteDao = notesDatabase.noteDao();
                CategoryDao categoryDao = notesDatabase.categoryDao();
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

}
