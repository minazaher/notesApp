package com.example.notesapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notesapp.Activity.CreateNoteActivity;
import com.example.notesapp.Activity.MainActivity;
import com.example.notesapp.Listeners.NotesListener;
import com.example.notesapp.Model.Note;
import com.example.notesapp.R;
import com.example.notesapp.Repository.NotesRepository;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.FragmentCategoryNotesBinding;

import java.util.List;

public class CategoryNotes extends Fragment {

    NotesRepository notesRepository;
    FragmentCategoryNotesBinding fragment;
    boolean archived ;

    public CategoryNotes() {
        notesRepository = new NotesRepository(getContext());
    }

    public static CategoryNotes newInstance(boolean archived) {
        CategoryNotes fragment = new CategoryNotes();
        Bundle args = new Bundle();
        args.putBoolean("isArchived", archived);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            archived = getArguments().getBoolean("isArchived");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = FragmentCategoryNotesBinding.inflate(getLayoutInflater(), container, false);

        List<Note> fragmentNotes = notesRepository.getArchivedNotes();
        System.out.println(fragmentNotes);
        NotesAdapter notesAdapter = new NotesAdapter(fragmentNotes, (note, position) -> {
            Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
            intent.putExtra("isViewOrUpdate", true);
            intent.putExtra("note", note);
            intent.putExtra("code", MainActivity.REQUEST_CODE_UPDATE_NOTE);
            startActivity(intent);
        });
        fragment.categoryNotesRecyclerView.setAdapter(notesAdapter);
        fragment.categoryNotesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return fragment.getRoot();
    }
}