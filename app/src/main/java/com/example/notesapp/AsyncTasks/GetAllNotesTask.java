package com.example.notesapp.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.notesapp.Activity.MainActivity;
import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Note;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetAllNotesTask extends AsyncTask<Void, Void, List<Note>> {

    private final WeakReference<Context> mContextRef;

    public interface Callback {
        void onResult(List<Note> notes);
    }

    private Callback mCallback;

    public GetAllNotesTask(Context context, Callback mCallback) {
        mContextRef = new WeakReference<>(context);
        this.mCallback  = mCallback ;
    }

    @Override
    protected List<Note> doInBackground(Void... voids) {
        Context context = mContextRef.get();
        return NotesDatabase.getInstance(context).noteDao().getActiveNotes();
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        super.onPostExecute(notes);
        mCallback.onResult(notes);

    }
}
