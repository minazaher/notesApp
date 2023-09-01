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

    public CredentialRepository(Context context) {
        mContextRef = new WeakReference<>(context);
    }
    public void insertCredential(Credential credential){
        Thread insertCred = new Thread(() -> NotesDatabase.getInstance(mContextRef.get()).credentialDao().insertCredential(credential));
        insertCred.start();
        try {
            insertCred.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
}
