package com.example.taskapp.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Task implements Serializable{
    private String title;
    private String description;
    private int color;

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