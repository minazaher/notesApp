package com.example.notesapp.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "credentials",
        foreignKeys = @ForeignKey(entity = CredentialCategory.class,
                parentColumns = "category_name",
                childColumns = "category_name",
                onDelete = ForeignKey.SET_DEFAULT))
public class Credential {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String email;
    @ColumnInfo(index = true, name = "category_name", defaultValue = "Others")
    private String categoryName;
    private String password;

    private String appName;
    private int icon;


    public Credential(String email, String categoryName, String password, String appName, int icon) {
        this.email = email;
        this.categoryName = categoryName;
        this.password = password;
        this.appName = appName;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
