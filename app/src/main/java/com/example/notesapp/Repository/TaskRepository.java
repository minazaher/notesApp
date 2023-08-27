package com.example.notesapp.Repository;

import android.content.Context;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final WeakReference<Context> mContextRef;

    public TaskRepository(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    public List<Task> getFinishedTasks(){
        List<Task> finishedTasks = new ArrayList<>();
        Thread getFinished = new Thread(new Runnable() {
            @Override
            public void run() {
                finishedTasks.addAll(NotesDatabase.getInstance(mContextRef.get()).taskDao().getFinishedTasks());
            }
        });
        getFinished.start();
        try {
            getFinished.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return finishedTasks;
    }

    public void removeTask(Task task){
        Thread removeTask = new Thread(new Runnable() {
            @Override
            public void run() {
                NotesDatabase.getInstance(mContextRef.get()).taskDao().deleteTask(task);
            }
        });
        removeTask.start();
        try {
            removeTask.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateTask(Task task){
        Thread updateTask = new Thread(() ->
              NotesDatabase.getInstance(mContextRef.get()).taskDao().updateTask(task));
        updateTask.start();
        try {
            updateTask.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Task> getUnfinishedTasks() {
        List<Task> unfinishedTasks = new ArrayList<>();
        Thread getUnfinished = new Thread(() ->
                unfinishedTasks.addAll(NotesDatabase.getInstance(mContextRef.get()).taskDao().getUnfinishedTasks()));
        getUnfinished.start();
        try {
            getUnfinished.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return unfinishedTasks;
    }
}
