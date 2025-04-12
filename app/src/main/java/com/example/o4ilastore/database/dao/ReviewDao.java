package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.o4ilastore.database.entities.Review;

import java.util.List;

@Dao
public interface ReviewDao {

    @Insert
    void insert(Review review);

    @Query("SELECT * FROM reviews WHERE productId = :productId")
    List<Review> getForProduct(int productId);

    // Новият метод за изчисляване на средния рейтинг на продукта
}
