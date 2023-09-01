package com.example.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesapp.Model.Credential;

import java.util.List;

@Dao
public interface CredentialDao {

    @Insert
    void insertCredential(Credential credential);

    @Delete
    void deleteCredential(Credential credential);

    @Query("select * from credentials")
    List<Credential> getAllCredentials();
}
