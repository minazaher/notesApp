package com.example.notesapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private String taskName;
    private int priority;
    private String taskSubtitle;
    private boolean isCompleted;
    private String creationDate;
    private TaskDate dueDate;
    private String completionDate;

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public TaskDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(TaskDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getTaskSubtitle() {
        return taskSubtitle;
    }

    public void setTaskSubtitle(String taskSubtitle) {
        this.taskSubtitle = taskSubtitle;
    }
}
