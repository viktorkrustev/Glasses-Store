package com.example.o4ilastore.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id", childColumns = "userId"),
        indices = {@Index(value = "userId")})
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
