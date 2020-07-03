package com.example.taskapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Collection;

@Entity
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String description;
    private int color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

        public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "<<" + title + ">>" + "\n" +
                        description + "\n" +
                        "________________________";
    }

}