package com.example.notesapp.Repository;


import android.content.Context;
import android.widget.Toast;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Note;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NotesRepository {
    private final WeakReference<Context> mContextRef;

    public NotesRepository(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public void insertNote(Note note){
        Thread thread = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).noteDao().insertNote(note));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<Note> getArchivedNotes(){
        ArrayList<Note> archivedNotes = new ArrayList<>();
        Thread getArchived = new Thread(() -> archivedNotes.addAll(NotesDatabase.getInstance(mContextRef.get()).noteDao().getArchivedNotes()));
        getArchived.start();
        try {
            getArchived.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextRef.get(), "Error! Try Again Later", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return archivedNotes;
    }

    public ArrayList<Note> getNotesByCategory(int id){
        ArrayList<Note> categoryNotes = new ArrayList<>();
        Thread getArchived = new Thread(() ->
                categoryNotes.addAll(NotesDatabase.getInstance(mContextRef.get()).noteDao().getNotesByCategory(id)));
        getArchived.start();
        try {
            getArchived.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextRef.get(), "Error! Try Again Later", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return categoryNotes;
    }

    public ArrayList<Note> getFavoriteNotes() {
        ArrayList<Note> favoriteNotes = new ArrayList<>();
        Thread getArchived = new Thread(() ->
                favoriteNotes.addAll(NotesDatabase.getInstance(mContextRef.get()).noteDao().getFavoriteNotes()));
        getArchived.start();
        try {
            getArchived.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextRef.get(), "Error! Try Again Later", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return favoriteNotes;
    }
}
