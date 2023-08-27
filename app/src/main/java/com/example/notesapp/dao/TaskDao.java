package com.example.notesapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notesapp.Model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Query("Select * from tasks where isCompleted is 0 Order by priority Desc")
    List<Task> getUnfinishedTasks();

    @Query("Select * from tasks where isCompleted is 1")
    List<Task> getFinishedTasks();

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);



}
