package com.example.notesapp.Converters;

import androidx.room.TypeConverter;

import com.example.notesapp.Model.TaskDate;

public class TypeConverters {
        @TypeConverter
        public static String fromTaskDate(TaskDate taskDate) {
            return taskDate.getDayName() + "," + taskDate.getDayNumber() + ","
                    + taskDate.getMonth() + "," + taskDate.getHour();
        }

        @TypeConverter
        public static TaskDate toTaskDate(String data) {
            String[] parts = data.split(",");
            return new TaskDate(parts[0], parts[1], parts[2], parts[3]);
        }
}
