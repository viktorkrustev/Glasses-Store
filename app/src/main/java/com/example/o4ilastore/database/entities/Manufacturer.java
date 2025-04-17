package com.example.o4ilastore.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "manufacturer")
public class Manufacturer {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public Manufacturer() {}

    public Manufacturer(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
