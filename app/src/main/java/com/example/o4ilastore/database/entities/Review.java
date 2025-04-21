package com.example.o4ilastore.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews")
public class Review {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "productId")
    private int productId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "rating")
    private int rating;

    public Review(int productId, String text, int rating) {
        this.productId = productId;
        this.text = text;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
