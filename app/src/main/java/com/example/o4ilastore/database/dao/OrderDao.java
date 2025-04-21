package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.o4ilastore.database.entities.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> getOrdersByUserId(int userId);

}
