package com.example.o4ilastore.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details",
        foreignKeys = {
                @ForeignKey(entity = Order.class, parentColumns = "id", childColumns = "orderId"),
                @ForeignKey(entity = Glasses.class, parentColumns = "id", childColumns = "glassesId")
        },
        indices = {@Index(value = "orderId"), @Index(value = "glassesId")})  // Добавяме индекси
public class OrderDetails {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int orderId;
    public int glassesId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getGlassesId() {
        return glassesId;
    }

    public void setGlassesId(int glassesId) {
        this.glassesId = glassesId;
    }
}
