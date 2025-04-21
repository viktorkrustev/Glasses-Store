package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.o4ilastore.database.entities.Glasses;
import java.util.List;

@Dao
public interface GlassesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Glasses glasses);

    @Query("SELECT * FROM glasses")
    List<Glasses> getAll();

    @Query("SELECT * FROM glasses WHERE id = :productId LIMIT 1")
    Glasses getById(int productId);

    @Update
    void update(Glasses glasses);
    @Query("SELECT * FROM glasses WHERE id = :id")
    Glasses getGlassesById(int id);
}
