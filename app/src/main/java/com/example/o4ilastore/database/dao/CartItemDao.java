package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.o4ilastore.database.entities.CartItem;
import com.example.o4ilastore.database.entities.Glasses;

import java.util.List;

@Dao
public interface CartItemDao {

    @Insert
    void insert(CartItem cartItem);

    @Query("DELETE FROM cart_items WHERE userId = :userId AND glassesId = :glassesId")
    void deleteItem(int userId, int glassesId);

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    void clearCart(int userId);

    @Query("SELECT g.* FROM glasses g " +
            "INNER JOIN cart_items c ON g.id = c.glassesId " +
            "WHERE c.userId = :userId")
    List<Glasses> getGlassesInCart(int userId);


}
