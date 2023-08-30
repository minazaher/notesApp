package com.example.notesapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.Model.Task;
import com.example.notesapp.R;
import com.example.notesapp.Repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    List<Task> tasks;
    TaskRepository taskRepository;
    onItemUpdated onItemUpdated;
    public interface onItemUpdated{
        void onTaskUpdated(Task task);
    }
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public TasksAdapter(List<Task> tasks, TaskRepository taskRepository,onItemUpdated onItemUpdated) {
        this.tasks = tasks;
        this.taskRepository = taskRepository;
        this.onItemUpdated = onItemUpdated;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate
                                (R.layout.task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.setTask(tasks.get(position));
        holder.setTaskCompleted.setOnClickListener(view -> {
            if(!tasks.get(position).isCompleted()){
                tasks.get(position).setCompleted(true);
                tasks.get(position).setCreationDate(new SimpleDateFormat("EEEE, dd MMMM, yyyy HH:mm a ", Locale.getDefault())
                        .format(new Date()));
                taskRepository.updateTask(tasks.get(position));
                this.onItemUpdated.onTaskUpdated(tasks.get(position));
                this.notifyDataSetChanged();
            }
            holder.itemView.setOnLongClickListener(view1 -> {
                System.out.println("Long Clicked!");
                return true;
            });

        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle,month,dayName,dayNumber,status,dueTime;
        ImageView setTaskCompleted;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_taskTitle);
            subtitle = itemView.findViewById(R.id.tv_taskSubtitle);
            month = itemView.findViewById(R.id.tv_taskMonth);
            dayName = itemView.findViewById(R.id.tv_taskDayName);
            dayNumber = itemView.findViewById(R.id.tv_taskDayNumber);
            status = itemView.findViewById(R.id.tv_taskStatus);
            dueTime = itemView.findViewById(R.id.tv_taskDueHour);
            setTaskCompleted = itemView.findViewById(R.id.image_setTaskCompleted);

        }

        public void setTask(Task task){
            title.setText(task.getTaskName());
            subtitle.setText(task.getTaskSubtitle());
            month.setText(task.getDueDate().getMonth());
            dayNumber.setText(task.getDueDate().getDayNumber());
            dayName.setText(task.getDueDate().getDayName());
            dueTime.setText(task.getDueDate().getHour());
            if (task.isCompleted()){
                setTaskCompleted.setImageResource(R.drawable.ic_task);
                status.setText("Completed");
                status.setTextColor(Color.parseColor("#4CAF50"));
            }
            else
            {
                status.setText("Not Completed");
            }
        }


    }
}
