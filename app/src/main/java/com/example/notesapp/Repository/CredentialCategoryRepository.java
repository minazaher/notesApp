package com.example.notesapp.Repository;

import android.content.Context;
import android.widget.Toast;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.CredentialCategory;
import com.example.notesapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CredentialCategoryRepository {
    private final WeakReference<Context> mContextRef;

    public CredentialCategoryRepository(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public void insertCredentialCategory(CredentialCategory category){
        Thread insert = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().insertCredentialCategory(category));
        insert.start();
        try {
            insert.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertDefaultCategories(){
        CredentialCategory AppsCategory = new CredentialCategory("Apps", R.drawable.ic_apps);
        CredentialCategory WalletsCategory = new CredentialCategory("Wallets", R.drawable.ic_wallet);
        CredentialCategory SocialsCategory = new CredentialCategory("Socials", R.drawable.baseline_groups_24);
        CredentialCategory OthersCategory = new CredentialCategory("others", R.drawable.ic_more);

        insertCredentialCategory(AppsCategory);
        insertCredentialCategory(WalletsCategory);
        insertCredentialCategory(SocialsCategory);
        insertCredentialCategory(OthersCategory);
    }

    public List<CredentialCategory> getAllCredentialCategory (){
        List<CredentialCategory> categories = new ArrayList<>();
        Thread getAll = new Thread(() -> categories.addAll(NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().getAllCredentialCategories()));
        getAll.start();
        try {
            getAll.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public List<String> getCredentialCategoryNames(){
        List<String> names = new ArrayList<>();
        Thread getNames = new Thread(() -> names.addAll(NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().getAllCredentialCategoryNames()));
        getNames.start();
        try {
            getNames.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return names;
    }

    public CredentialCategory getCategoryByName(String name){
        final CredentialCategory[] category = new CredentialCategory[1];
        Thread getCategory = new Thread(() -> category[0] = NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().getCategoryByName(name));
        getCategory.start();
        try {
            getCategory.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextRef.get(), "Category Not Found", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return category[0];
    }
    public void increaseCategoryNumberOfApps(String name) throws InterruptedException {
        CredentialCategory category = getCategoryByName(name);
        category.setNumberOfApps(category.getNumberOfApps()+1);
        Thread thread = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().updateCategory(category));
        thread.start();
        thread.join();
    }

    public void decreaseCategoryNumberOfApps(String name) throws InterruptedException {
        CredentialCategory category = getCategoryByName(name);
        category.setNumberOfApps(category.getNumberOfApps()-1);
        Thread thread = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).credentialCategoryDao().updateCategory(category));
        thread.start();
        thread.join();
    }

}
