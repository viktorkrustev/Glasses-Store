package com.example.o4ilastore.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.o4ilastore.database.entities.Manufacturer;

import java.util.List;

@Dao
public interface ManufacturerDao {
    @Query("SELECT * FROM manufacturer")
    List<Manufacturer> getAllManufacturers();

    @Insert
    void insert(Manufacturer manufacturer);


    @Query("SELECT * FROM manufacturer WHERE id = :id LIMIT 1")
    Manufacturer getById(int id);
}
