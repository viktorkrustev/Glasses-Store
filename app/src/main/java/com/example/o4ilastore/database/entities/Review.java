package com.example.o4ilastore.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews") // Увери се, че името на таблицата е "reviews"
public class Review {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "productId") // Името на колоната трябва да е "productId"
    private int productId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "rating") // Ново поле за рейтинг
    private int rating;

    // Конструктор с рейтинг
    public Review(int productId, String text, int rating) {
        this.productId = productId;
        this.text = text;
        this.rating = rating;
    }

    // Гетъри и сетъри
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
