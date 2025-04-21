package com.example.o4ilastore.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "cart_items",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Glasses.class,
                        parentColumns = "id",
                        childColumns = "glassesId",
                        onDelete = ForeignKey.CASCADE)
        })
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private int glassesId;


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

    public int getGlassesId() {
        return glassesId;
    }

    public void setGlassesId(int glassesId) {
        this.glassesId = glassesId;
    }

}
