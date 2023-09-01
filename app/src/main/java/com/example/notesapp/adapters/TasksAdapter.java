package com.example.notesapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesapp.Model.Note;
import com.example.notesapp.Model.Task;
import com.example.notesapp.R;
import com.example.notesapp.Repository.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> implements Filterable {
    List<Task> tasks;
    TaskRepository taskRepository;
    onItemUpdated onItemUpdated;
    private List<Task> orig;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<Task> results = new ArrayList<Task>();
                if (orig == null)
                    orig = tasks;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final Task g : orig) {
                            if (isTaskContains(g,constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                tasks = (ArrayList<Task>) results.values;
                notifyDataSetChanged();

            }
        };

    }

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

    private boolean isTaskContains(Task task, String word){
        return task.getTaskName().toLowerCase().contains(word.toLowerCase()) ||
                task.getTaskSubtitle().toLowerCase().contains(word.toLowerCase());

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
