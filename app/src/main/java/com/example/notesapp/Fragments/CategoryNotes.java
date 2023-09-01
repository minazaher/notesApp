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
import com.example.notesapp.Repository.CategoryRepository;
import com.example.notesapp.Repository.NotesRepository;
import com.example.notesapp.adapters.NotesAdapter;
import com.example.notesapp.databinding.FragmentCategoryNotesBinding;

import java.util.ArrayList;
import java.util.List;

public class CategoryNotes extends Fragment {

    NotesRepository notesRepository;
    CategoryRepository categoryRepository ;
    FragmentCategoryNotesBinding fragment;
    boolean archived, favorite ;
    String categoryName;

    public CategoryNotes() {
        notesRepository = new NotesRepository(getContext());
        categoryRepository = new CategoryRepository(getContext());
    }

    public static CategoryNotes newInstance(boolean favorite, boolean archived, String categoryName) {
        CategoryNotes fragment = new CategoryNotes();
        Bundle args = new Bundle();
        args.putBoolean("isArchived", archived);
        args.putBoolean("isFavorite", favorite);
        args.putString("categoryName", categoryName);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            archived = getArguments().getBoolean("isArchived");
            favorite = getArguments().getBoolean("isFavorite");
            categoryName = getArguments().getString("categoryName");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = FragmentCategoryNotesBinding.inflate(getLayoutInflater(), container, false);

        List<Note> fragmentNotes = getData();
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


    ArrayList<Note> getData(){
        if(archived){
            return notesRepository.getArchivedNotes();
        }
        if (favorite){
            return notesRepository.getFavoriteNotes();
        }
        else {
            int id = categoryRepository.getCategoryIdByName(categoryName);
            return notesRepository.getNotesByCategory(id);
        }
    }
}