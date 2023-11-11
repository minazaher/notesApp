package com.example.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapp.Model.CredentialCategory;

import java.util.List;

@Dao
public interface CredentialCategoryDao {

    @Query("Select * from credential_category")
    List<CredentialCategory> getAllCredentialCategories();

    @Query("Select category_name from credential_category")
    List<String> getAllCredentialCategoryNames();
    @Insert
    void insertCredentialCategory(CredentialCategory category);

    @Update
    void updateCategory(CredentialCategory category);

    @Query("select * from credential_category where category_name =:name")
    CredentialCategory getCategoryByName(String name);

}
