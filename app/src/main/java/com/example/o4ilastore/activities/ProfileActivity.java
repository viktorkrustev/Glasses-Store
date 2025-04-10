package com.example.o4ilastore.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;
import com.example.o4ilastore.database.entities.Order;
import com.example.o4ilastore.database.entities.OrderDetails;
import com.example.o4ilastore.database.entities.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail;
    private LinearLayout ordersList;
    private ImageView profileImage;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        ordersList = findViewById(R.id.ordersList);
        profileImage = findViewById(R.id.profileImage);

        // Заявка за разрешение, ако е необходимо (Android 6+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Получаване на userId
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            userId = prefs.getInt("userId", -1);
        }

        Log.d("ProfileActivity", "User ID: " + userId);

        // Зареждане на профилна снимка с Glide
        String savedUri = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("profileImageUri", null);
        if (savedUri != null) {
            Glide.with(this).load(Uri.parse(savedUri)).into(profileImage);
        }

        // Смяна на снимка
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        if (userId != -1) {
            loadUserData(userId);
            loadUserOrders(userId);
        } else {
            Toast.makeText(this, "Не е намерен потребител.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData(int userId) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserById(userId);

            runOnUiThread(() -> {
                if (user != null) {
                    tvName.setText("Име: " + user.getName());
                    tvEmail.setText("Имейл: " + user.getEmail());
                } else {
                    Toast.makeText(this, "Не намерихме потребителя.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadUserOrders(int userId) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Order> orders = db.orderDao().getOrdersByUserId(userId);

            runOnUiThread(() -> ordersList.removeAllViews());

            if (orders != null && !orders.isEmpty()) {
                Log.d("ProfileActivity", "Намерени поръчки: " + orders.size());

                for (Order order : orders) {
                    List<OrderDetails> orderDetailsList = db.orderDetailsDao().getOrderDetailsByOrderId(order.getId());
                    Log.d("ProfileActivity", "Поръчка ID: " + order.getId() + " съдържа " + orderDetailsList.size() + " детайли.");

                    for (OrderDetails details : orderDetailsList) {
                        Glasses glasses = db.glassesDao().getGlassesById(details.getGlassesId());

                        if (glasses != null) {
                            runOnUiThread(() -> {
                                LinearLayout item = new LinearLayout(ProfileActivity.this);
                                item.setOrientation(LinearLayout.VERTICAL);
                                item.setPadding(0, 0, 0, 32);

                                // Продукт
                                TextView name = new TextView(ProfileActivity.this);
                                name.setText("Продукт: " + glasses.getName());
                                name.setTextSize(16f);
                                item.addView(name);

                                // Снимка
                                if (glasses.getImageUrl() != null && !glasses.getImageUrl().isEmpty()) {
                                    ImageView image = new ImageView(ProfileActivity.this);
                                    image.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    Glide.with(ProfileActivity.this).load(glasses.getImageUrl()).into(image);
                                    item.addView(image);
                                }

                                // Дата
                                TextView date = new TextView(ProfileActivity.this);
                                date.setText("Дата на поръчка: " + (order.date != null ? order.date : "неизвестна"));
                                item.addView(date);

                                ordersList.addView(item);
                            });
                        } else {
                            Log.w("ProfileActivity", "Не намерихме очила с ID: " + details.getGlassesId());
                        }
                    }
                }
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Няма намерени поръчки за този потребител.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ProfileActivity", "onActivityResult извикан: " + requestCode + ", result: " + resultCode);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.d("ProfileActivity", "Избрана снимка: " + selectedImage);

            Glide.with(this).load(selectedImage).into(profileImage);

            getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    .edit()
                    .putString("profileImageUri", selectedImage.toString())
                    .apply();
        }
    }
}
