package com.example.notesapp.Repository;

import android.content.Context;
import android.widget.Toast;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Credential;
import com.example.notesapp.Model.Note;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CredentialRepository {
    private final WeakReference<Context> mContextRef;
    private final CredentialCategoryRepository credentialCategoryRepository;

    public CredentialRepository(Context context) {
        mContextRef = new WeakReference<>(context);
        credentialCategoryRepository = new CredentialCategoryRepository(mContextRef.get());
    }
    public void insertCredential(Credential credential) throws InterruptedException {
        Thread insertCred = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).credentialDao().insertCredential(credential));
        insertCred.start();

        insertCred.join();
        credentialCategoryRepository.increaseCategoryNumberOfApps(credential.getCategoryName());
    }

    public ArrayList<Credential> getAllCredentials(){
        ArrayList<Credential> allCredentials = new ArrayList<>();
        Thread getCreds = new Thread(() -> allCredentials.addAll(NotesDatabase.getInstance(mContextRef.get()).credentialDao().getAllCredentials()));
        getCreds.start();
        try {
            getCreds.join();
        } catch (InterruptedException e) {
            Toast.makeText(mContextRef.get(), "Error! Try Again Later", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
        return allCredentials;
    }

    public void removeCredential(Credential credential) throws InterruptedException {
        Thread insertCred = new Thread(() ->
                NotesDatabase.getInstance(mContextRef.get()).credentialDao().deleteCredential(credential));
        insertCred.start();
        try {
            insertCred.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        credentialCategoryRepository.decreaseCategoryNumberOfApps(credential.getCategoryName());

    }
}
