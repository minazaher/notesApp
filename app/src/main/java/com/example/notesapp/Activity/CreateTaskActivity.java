package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notesapp.Database.NotesDatabase;
import com.example.notesapp.Model.Task;
import com.example.notesapp.Model.TaskDate;
import com.example.notesapp.R;
import com.example.notesapp.databinding.ActivityCreateNoteBinding;
import com.example.notesapp.databinding.ActivityCreateTaskBinding;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {
    ActivityCreateTaskBinding binding ;
    final Calendar c = Calendar.getInstance();
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    TaskDate taskDue ;
    String creationDate, taskTitle, taskSubtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        creationDate= new SimpleDateFormat("EEEE, dd MMMM, yyyy HH:mm a ", Locale.getDefault())
                .format(new Date());
        binding.textTaskDateTime.setText(creationDate);

        binding.etPickDueDate.setOnClickListener(view -> showPickDateDialog());
        binding.imageSaveTask.setOnClickListener(view -> {
            saveTask();
            Toast.makeText(CreateTaskActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        });
    }


    void showPickDateDialog(){
        taskDue = new TaskDate();
        @SuppressLint("SimpleDateFormat") DatePickerDialog dialog =
                new DatePickerDialog(CreateTaskActivity.this,
                        (datePicker, year, month, day) -> {
                            Calendar s = Calendar.getInstance();
                            s.set(year, month, day);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM");
                            String[] dateParts = simpleDateFormat.format(s.getTime()).split(", ");
                            String dayName = dateParts[0];
                            String monthName = dateParts[1];
                            taskDue.setDayName(dayName.substring(0,3));
                            taskDue.setDayNumber(String.valueOf(day));
                            taskDue.setMonth(monthName.substring(0,3));
                            showPickTimeDialog();
                        },
                          2022,8,25);
        dialog.show();

    }

    void showPickTimeDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this,
                (view1, hourOfDay, minute) -> {
                    String time = hourOfDay + ":" + minute;
                    taskDue.setHour(time);
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    void getTaskData(){
        taskTitle = binding.etTaskTitle.getText().toString();
        taskSubtitle = "subtitle";
        int taskPriority = 1;
    }

    void saveTask(){
        getTaskData();
        Task task = new Task();
        task.setCompletionDate(taskDue);
        task.setCompleted(false);
        task.setTaskSubtitle(taskSubtitle);
        task.setCreationDate(creationDate);
        task.setTaskName(taskTitle);
        task.setPriority(1);

        new Thread(() -> NotesDatabase.getInstance(getApplicationContext()).taskDao().insertTask(task)).start();

    }
}