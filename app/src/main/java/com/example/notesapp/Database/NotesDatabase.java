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
import com.example.notesapp.Model.Note;
import com.example.notesapp.Model.Task;
import com.example.notesapp.Model.TaskDate;
import com.example.notesapp.dao.CategoryDao;
import com.example.notesapp.dao.NoteDao;
import com.example.notesapp.dao.TaskDao;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Database(entities = {Category.class, Note.class, Task.class},version = 8, exportSchema = false )
@TypeConverters(mConverters.class)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;
    // get a reference to a Context object

    public static String DB_FILEPATH = "/data/data/com.example.notesapp/databases/database.db";


    public static RoomDatabase.Callback myCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new Thread(() -> {
                NoteDao noteDao = notesDatabase.noteDao();
                CategoryDao categoryDao = notesDatabase.categoryDao();
                TaskDao taskDao = notesDatabase.taskDao();
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


    public boolean exportDatabase(String dbPath) throws IOException {
        close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtils.copyFile(newDb, oldDb);
            getOpenHelper().getWritableDatabase().close();
            return true;
        }
        return false;
    }

    public abstract NoteDao noteDao();
    public abstract CategoryDao categoryDao();
    public abstract TaskDao taskDao();

}
