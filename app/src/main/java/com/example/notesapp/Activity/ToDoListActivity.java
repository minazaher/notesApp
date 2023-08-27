package com.example.notesapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.notesapp.Model.Task;
import com.example.notesapp.R;
import com.example.notesapp.Repository.TaskRepository;
import com.example.notesapp.adapters.TasksAdapter;
import com.example.notesapp.databinding.ActivityToDoListBinding;

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

        TasksAdapter tasksAdapter = new TasksAdapter(tasksList, taskRepository, new TasksAdapter.onItemUpdated() {
            @Override
            public void onTaskUpdated(Task task) {
                initializeToDoRecyclerView();
                initializeCompletedTasksRecyclerView();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        todoRecyclerView.setLayoutManager(layoutManager);
        todoRecyclerView.setAdapter(tasksAdapter);
        initializeItemHelper(todoRecyclerView, tasksAdapter);

    }
    void initializeItemHelper(RecyclerView recyclerView,TasksAdapter adapter){
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
                        adapter.getTasks().remove(position);
                        taskRepository.removeTask(task);
                        adapter.notifyItemRemoved(position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    void initializeCompletedTasksRecyclerView(){
        RecyclerView unfinishedTasks = binding.completedTasksRecyclerView;
        List<Task> tasksList = taskRepository.getFinishedTasks();
        TasksAdapter tasksAdapter = new TasksAdapter(tasksList, taskRepository, task -> {
            initializeToDoRecyclerView();
            initializeCompletedTasksRecyclerView();
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        unfinishedTasks.setLayoutManager(layoutManager);
        unfinishedTasks.setAdapter(tasksAdapter);
        initializeItemHelper(unfinishedTasks, tasksAdapter);

    }
    public void restartActivity(Activity act) {
        Intent intent = new Intent();
        intent.setClass(act, act.getClass());
        act.startActivity(intent);
        act.finish();
    }
}