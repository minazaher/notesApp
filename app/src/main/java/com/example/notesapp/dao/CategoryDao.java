package com.example.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notesapp.Model.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();


    @Query("Select category_name from categories")
    List<String> getCategoriesNames();
    @Insert
    void insertCategory(Category category);

    @Query("Select category_id from categories where category_name = :categoryName")
    int getCategoryIdByName(String categoryName);

    @Query("Select category_name from categories where category_id = :category_id")
    String getCategoryNameById(int category_id);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM categories WHERE category_name = :name")
    Category getCategoryByName(String name);
}