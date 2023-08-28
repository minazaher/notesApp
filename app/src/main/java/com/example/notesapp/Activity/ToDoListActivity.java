package com.example.notesapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.notesapp.Model.Task;
import com.example.notesapp.R;
import com.example.notesapp.Repository.TaskRepository;
import com.example.notesapp.adapters.TasksAdapter;
import com.example.notesapp.databinding.ActivityToDoListBinding;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {
    ActivityToDoListBinding binding ;
    TaskRepository taskRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        taskRepository= new TaskRepository(this);
        initializeToDoRecyclerView();
        initializeCompletedTasksRecyclerView();
    }

    void initializeToDoRecyclerView(){
        RecyclerView todoRecyclerView = binding.toDoTasksRecyclerView;
        List<Task> tasksList = taskRepository.getUnfinishedTasks();

        TasksAdapter tasksAdapter = new TasksAdapter(tasksList, taskRepository, task -> {
            initializeToDoRecyclerView();
            initializeCompletedTasksRecyclerView();
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        todoRecyclerView.setLayoutManager(layoutManager);
        todoRecyclerView.setAdapter(tasksAdapter);
        initializeItemHelperForTodo(todoRecyclerView, tasksAdapter);
    }
    void initializeItemHelperForTodo(RecyclerView recyclerView,TasksAdapter adapter){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int swipeDir) {
                        int position = viewHolder.getAdapterPosition();
                        Task task = adapter.getTasks().get(position);
                        taskRepository.removeTask(task);
                        adapter.setTasks(taskRepository.getUnfinishedTasks());
                        restartActivity();
                        adapter.notifyDataSetChanged();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    void initializeCompletedTasksRecyclerView(){
        RecyclerView unfinishedTasks = binding.completedTasksRecyclerView;
        if (!taskRepository.getFinishedTasks().isEmpty()){
            binding.tvDoneTasks.setVisibility(View.VISIBLE);
            unfinishedTasks.setVisibility(View.VISIBLE);
            List<Task> tasksList = taskRepository.getFinishedTasks();
            TasksAdapter tasksAdapter = new TasksAdapter(tasksList, taskRepository, task -> {
                initializeToDoRecyclerView();
                initializeCompletedTasksRecyclerView();
            });
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL,false);

            unfinishedTasks.setLayoutManager(layoutManager);
            unfinishedTasks.setAdapter(tasksAdapter);
        }
        else {
            binding.tvDoneTasks.setVisibility(View.GONE);
            unfinishedTasks.setVisibility(View.GONE);
        }

    }
    void initializeItemHelperForCompleted(RecyclerView recyclerView,TasksAdapter adapter){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int swipeDir) {
                        int position = viewHolder.getAdapterPosition();
                        Task task = adapter.getTasks().get(position);
                        taskRepository.removeTask(task);
                        adapter.setTasks(taskRepository.getFinishedTasks());
                        adapter.notifyDataSetChanged();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    public void restartActivity() {
       recreate();
    }

}