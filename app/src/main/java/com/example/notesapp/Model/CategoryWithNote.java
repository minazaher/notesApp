//package com.example.notesapp.Model;
//
//import androidx.room.Embedded;
//import androidx.room.Relation;
//
//import java.util.List;
//
//public class CategoryWithNote {
//    @Embedded public Category category;
//    @Relation(
//            parentColumn = "category_id",
//            entityColumn = "id"
//    )
//    public List<Note> notes;
//}
