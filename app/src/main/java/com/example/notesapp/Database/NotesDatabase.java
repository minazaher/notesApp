package com.example.notesapp.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.notesapp.Converters.mConverters;
import com.example.notesapp.Model.Category;
import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.CredentialCategory;
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

@Database(entities = {Category.class, Note.class, Task.class, Credential.class, CredentialCategory.class},
        version = 10, exportSchema = false )
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
            ).addMigrations(MIGRATION_9_10).addCallback(myCallback).build();
        }
        return notesDatabase;
    }
    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE credential_category (no_of_apps INTEGER NOT NULL, " +
                            "category_name TEXT NOT NULL PRIMARY KEY DEFAULT 'other')");

            database.execSQL(
                    "CREATE TABLE credential_new (id INTEGER PRIMARY KEY NOT NULL, " +
                            "email TEXT, category_name TEXT DEFAULT 'other', password TEXT, appName TEXT, icon INTEGER NOT NULL," +
                            "FOREIGN KEY(category_name) REFERENCES credential_category(category_name) ON DELETE SET DEFAULT)");

            database.execSQL("CREATE INDEX index_credential_new_category_name ON credential_new(category_name)");
            database.execSQL(
                    "INSERT INTO credential_new (id, email, password, appName, icon) " +
                            "SELECT id, email, password, appName, icon FROM credentials");

            database.execSQL("DROP TABLE credentials");

            database.execSQL("ALTER TABLE credential_new RENAME TO credentials");
        }
    };


    public abstract NoteDao noteDao();
    public abstract CategoryDao categoryDao();
    public abstract TaskDao taskDao();
    public abstract CredentialDao credentialDao();
}
