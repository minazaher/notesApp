package com.example.notesapp.Repository;

import android.content.Context;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Category;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CategoryRepository {
    private final WeakReference<Context> mContextRef;

    public CategoryRepository(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public ArrayList<String> getCategoriesNames() {
        ArrayList<String> categories = new ArrayList<>();
        Thread getCategories = new Thread(() ->
                categories.addAll(
                        NotesDatabase
                                .getInstance(mContextRef.get())
                                .categoryDao()
                                .getCategoriesNames())
        );
        getCategories.start();

        try {
            getCategories.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }
    public int getCategoryIdByName(String categoryName){
        AtomicInteger categoryId = new AtomicInteger();
        Thread deleteThread = new Thread(() ->
                categoryId.set(NotesDatabase
                        .getInstance(mContextRef.get())
                        .categoryDao()
                        .getCategoryIdByName(categoryName)));
        deleteThread.start();
        try {
            deleteThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return categoryId.get();
    }
    public String getCategoryNameById(int selectedCategory) {
        final String[] category = new String[1];
        Thread deleteThread = new Thread(() ->
                category[0] = NotesDatabase
                        .getInstance(mContextRef.get())
                        .categoryDao()
                        .getCategoryNameById(selectedCategory));
        deleteThread.start();
        try {
            deleteThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return category[0];
    }
    public void addNewCategory(String name){
        Category category = new Category();
        category.setCategoryName(name);

        new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).categoryDao().insertCategory(category)).start();

    }
    public boolean isExist(String name){
        AtomicBoolean Exist = new AtomicBoolean(false);
        Thread thread = new Thread(() -> {
            if (NotesDatabase
                    .getInstance(mContextRef.get())
                    .categoryDao()
                    .getCategoryByName(name) != null)
                Exist.set(true);

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Exist.get();

    }
}
