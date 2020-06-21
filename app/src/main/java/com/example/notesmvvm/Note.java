package com.example.notesmvvm;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
* 1. Note Entity - Here we define structure of our Note table -
*                1. column names
*                2. constructor to add value in table
*                3. getter methods to retrieve data
* */


@Entity(tableName = "note_table") // By def table name = "Note", it helps to use custom name for table.
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String description;
    //@ColumnInfo(name = "priority_column") -  for custom column name in table
    private int priority;

    public Note(String title, String description, int priority) { // Id will be automaticaally generated, not set by us
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public void setId(int id) { // Used by Room to set Id of note obj
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }
}
