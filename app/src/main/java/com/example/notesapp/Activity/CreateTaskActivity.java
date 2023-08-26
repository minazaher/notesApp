package com.example.notesapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        binding = ActivityCreateTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textTaskDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM, yyyy HH:mm a ", Locale.getDefault())
                .format(new Date()));

        binding.etPickDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog dialog =
//                new DatePickerDialog(CreateTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        binding.etPickDueDate.setText(MessageFormat.format("{0}/{1}/{2}", year, month, day));
//                    }
//                }, 2022,8,25);
//                dialog.show();
                final Calendar c = Calendar.getInstance();


                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this,
                        (TimePickerDialog.OnTimeSetListener) (view1, hourOfDay, minute) -> {
                            // Display the selected time
                            String time = hourOfDay + ":" + minute;
                            binding.etPickDueDate.setText(time);
                            System.out.println("Time is : " + time);
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
    }
}