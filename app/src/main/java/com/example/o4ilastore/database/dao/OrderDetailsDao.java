package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.o4ilastore.database.entities.OrderDetails;

import java.util.List;

@Dao
public interface OrderDetailsDao {
    @Query("SELECT * FROM order_details")
    List<OrderDetails> getAllOrderDetails();

    @Insert
    void insert(OrderDetails orderDetails);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetails> getOrderDetailsByOrderId(int orderId);
}
