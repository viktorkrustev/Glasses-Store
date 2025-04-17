package com.example.o4ilastore.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.o4ilastore.database.dao.*;
import com.example.o4ilastore.database.entities.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Category.class,
                Glasses.class,
                Manufacturer.class,
                Order.class,
                OrderDetails.class,
                User.class,
                Review.class,
                CartItem.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "glasses_store_db"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("AppDatabase", "onCreate triggered");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase database = INSTANCE;

                                        if (database != null) {
                                            try {
                                                UserDao userDao = database.userDao();
                                                CategoryDao categoryDao = database.categoryDao();
                                                ManufacturerDao manufacturerDao = database.manufacturerDao();
                                                GlassesDao glassesDao = database.glassesDao();

                                                String adminPassword = "123456";
                                                String hashedAdminPassword;
                                                try {
                                                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                                    byte[] hashBytes = digest.digest(adminPassword.getBytes());
                                                    StringBuilder hexString = new StringBuilder();
                                                    for (byte b : hashBytes) {
                                                        hexString.append(String.format(Locale.US, "%02x", b));
                                                    }
                                                    hashedAdminPassword = hexString.toString();
                                                } catch (NoSuchAlgorithmException e) {
                                                    e.printStackTrace();
                                                    hashedAdminPassword = adminPassword;
                                                }

                                                userDao.insert(new User(1, "Admin", "admin@abv.bg", hashedAdminPassword, "admin"));

                                                categoryDao.insert(new Category(1, "Мъжки"));
                                                categoryDao.insert(new Category(2, "Женски"));
                                                categoryDao.insert(new Category(3, "Детски"));

                                                manufacturerDao.insert(new Manufacturer(1, "RayBrand"));
                                                manufacturerDao.insert(new Manufacturer(2, "Luxottica"));
                                                manufacturerDao.insert(new Manufacturer(3, "Visionary Co."));
                                                manufacturerDao.insert(new Manufacturer(4, "Elite Vision"));
                                                manufacturerDao.insert(new Manufacturer(5, "OptixLine"));

                                                Log.d("AppDatabase", "Категории: " + categoryDao.getAllCategories().size());
                                                Log.d("AppDatabase", "Производители: " + manufacturerDao.getAllManufacturers().size());

                                                glassesDao.insert(new Glasses(1, "Classic Aviator", 129.99, 1, 1, "glasses1.png", "Авиатор", 5, "Класически авиаторски модел с метална рамка и универсален стил."));
                                                glassesDao.insert(new Glasses(11, "Classic glasses", 129.99, 1, 3, "https://drive.google.com/file/d/1GrOb5x6_dODLHFXzARd8b8Hk6F6xPXoy/view?usp=drive_link", "Кръгли", 5, "Класически модел с метална рамка и универсален стил."));
                                                glassesDao.insert(new Glasses(2, "Urban Vision", 89.50, 2, 2, "glasses2.png", "Квадратни", 1, "Съвременен дизайн за градски стил с лека конструкция."));
                                                glassesDao.insert(new Glasses(3, "SunBlast Max", 149.00, 3, 3, "glasses3.png", "Диоптрични", 3, "Спортни слънчеви очила за активен начин на живот."));
                                                glassesDao.insert(new Glasses(4, "Sporty Breeze", 119.99, 1, 2, "glasses4.png", "Диоптрични", 15, "Леки и удобни – перфектни за спорт и движение."));
                                                glassesDao.insert(new Glasses(5, "Elegance Pro", 199.99, 2, 4, "glasses5.png", "Кръгли", 20, "Елегантен модел с кръгли рамки – идеален за специални поводи."));
                                                glassesDao.insert(new Glasses(6, "Golden Frames", 299.50, 3, 5, "glasses6.png", "Диоптрични", 30, "Луксозни очила със златисти рамки за изискан вид."));
                                                glassesDao.insert(new Glasses(7, "Minimalist Vision", 99.00, 2, 1, "glasses7.png", "Диоптрични", 10, "Минималистичен стил с изчистена визия."));
                                                glassesDao.insert(new Glasses(8, "Skyline Shades", 139.49, 2, 4, "glasses8.png", "Авиатор", 2, "Модерни очила с кръгли стъкла и градско вдъхновение."));
                                                glassesDao.insert(new Glasses(9, "Daily Driver", 79.99, 3, 2, "glasses9.png", "Авиатор", 10, "Универсални и практични – идеални за ежедневието."));
                                                glassesDao.insert(new Glasses(10, "Night Owl", 109.00, 1, 3, "https://drive.google.com/file/d/1sZDJTrUcjQVmBek4eMU7uWhcGMT8AB9A/view?usp=drive_link", "Квадратни", 18, "Подходящи за нощно виждане и ниска осветеност."));

                                                Log.d("AppDatabase", "Initial data inserted.");
                                            } catch (Exception e) {
                                                Log.e("AppDatabase", "Error inserting initial data", e);
                                            }
                                        }

                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract CategoryDao categoryDao();
    public abstract GlassesDao glassesDao();
    public abstract ManufacturerDao manufacturerDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailsDao orderDetailsDao();
    public abstract UserDao userDao();
    public abstract ReviewDao reviewDao();
    public abstract CartItemDao cartItemDao();
}
