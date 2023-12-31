package com.example.notesapp.Model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes" ,
        foreignKeys = @ForeignKey(entity = Category.class,
        parentColumns = "category_id",
        childColumns = "category_id",
        onDelete = ForeignKey.CASCADE))
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String title;

    @ColumnInfo(name = "date_time")
    private String dateTime;
    @ColumnInfo
    private String subtitle;
    @ColumnInfo(index = true, name = "category_id")
    private int categoryId;
    @ColumnInfo(name = "note_text")
    private String noteText;
    @ColumnInfo(name = "image_path")
    private String imagePath;
    @ColumnInfo
    private String color;
    private Boolean isArchived;
    private Boolean isFavourite;
    @ColumnInfo(name = "web_link")
    private String webLink;
    private String location;

    public Note() {
    }
@Ignore
    public Note(String noteText) {
        this.noteText = noteText;
        title = "NONE";
        dateTime = "NONE";
        subtitle = "NONE";
        categoryId = 0;
        imagePath = "NONE";
        color = "NONE";
        isArchived = false;
        isFavourite = false;
        webLink = "NONE";
        location = "NONE";
    }

    public int getId() {
        return id;
    }

    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }
    public Uri getImageUri(){
        return Uri.parse(this.imagePath);

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    @NonNull
    public String toString() {
        return title + " : " + dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
