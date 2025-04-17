package com.example.o4ilastore.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "glasses",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Manufacturer.class,
                        parentColumns = "id",
                        childColumns = "manufacturerId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("categoryId"),
                @Index("manufacturerId")
        }
)
public class Glasses {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double price;
    public int categoryId;
    public int manufacturerId;
    public String imageUrl;
    public int quantity;
    public String description;

    public String form;
    public int inCart;

    public Glasses() {}

    public Glasses(int id, String name, double price, int categoryId, int manufacturerId, String imageUrl, String form, int quantity, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.manufacturerId = manufacturerId;
        this.imageUrl = imageUrl;
        this.form = form;
        this.quantity = quantity;
        this.inCart = 0;
        this.description = description;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public int getInCart() {
        return inCart;
    }

    public void setInCart(int inCart) {
        this.inCart = inCart;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
