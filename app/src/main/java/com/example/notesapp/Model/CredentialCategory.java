package com.example.notesapp.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "credential_category")
public class CredentialCategory {

    @PrimaryKey
    @ColumnInfo(name = "category_name", defaultValue = "other")
    @NonNull
    private String categoryName;

    @ColumnInfo(name = "no_of_apps")
    private int numberOfApps;

    private int categoryIcon;

    public CredentialCategory() {

    }

    public CredentialCategory(@NonNull String categoryName, int categoryIcon) {
        this.categoryName = categoryName;
        this.categoryIcon = categoryIcon;
        numberOfApps = 0;
    }


    @NonNull
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.categoryName = categoryName;
    }

    public int getNumberOfApps() {
        return numberOfApps;
    }

    public void setNumberOfApps(int numberOfApps) {
        this.numberOfApps = numberOfApps;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
