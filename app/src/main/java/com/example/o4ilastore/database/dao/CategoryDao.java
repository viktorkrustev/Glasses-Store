package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.o4ilastore.database.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    // Правилната анотация за извличане на всички категории
    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Insert
    void insert(Category category);
}
