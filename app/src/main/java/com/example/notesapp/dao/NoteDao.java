package com.example.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.notesapp.Model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("Select * from notes order by id DESC")
    List<Note> getALlNotes();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Delete(entity = Note.class)
    void deleteNote(Note note);

    @Query("SELECT * FROM notes WHERE category_id = :category")
    List<Note> getNotesByCategory(int category);

}
