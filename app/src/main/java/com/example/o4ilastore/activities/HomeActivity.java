package com.example.o4ilastore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.o4ilastore.R;

public class HomeActivity extends AppCompatActivity {

    private Button btnProducts, btnProfile, btnAbout, btnLogout, btnLogin, btnRegister;
    private LinearLayout authLayout;
    private TextView welcomeText;
    private boolean isUserLoggedIn = false;

    private static final int LOGIN_REQUEST_CODE = 1; // Код за искане на вход

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Инициализиране на изгледите
        initViews();

        // Настройване на слушатели за бутоните
        setupListeners();

        // Обновяване на интерфейса според състоянието на входа
        updateUIBasedOnLogin();
    }

    private void initViews() {
        btnProducts = findViewById(R.id.btnProducts);
        btnProfile = findViewById(R.id.btnProfile);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        authLayout = findViewById(R.id.authLayout);
        welcomeText = findViewById(R.id.welcomeText);  // Текст за поздрав
    }

    private void setupListeners() {
        btnProducts.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            if (isUserLoggedIn) {
                // Пренасочваме към профила на потребителя
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("userId", getUserId());  // Предаваме ID на потребителя
                startActivity(intent);
            } else {
                // Ако не е влязъл, показваме съобщение
                Toast.makeText(HomeActivity.this, "Не сте влязли. Моля, влезте в профила си.", Toast.LENGTH_SHORT).show();
            }
        });

        // Свързване на бутона за "За нас"
        btnAbout.setOnClickListener(v -> {
            // Стартираме новата активност "За нас"
            Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        // Слушател за бутон за вход
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE); // Стартираме LoginActivity за резултат
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            isUserLoggedIn = false;
            // Изтриваме потребителското ID от SharedPreferences при изход
            SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("userId");
            editor.apply();
            updateUIBasedOnLogin();
            Toast.makeText(this, "Излязохте успешно", Toast.LENGTH_SHORT).show();
        });
    }

    // Получаваме резултата от LoginActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            // Ако резултатът е успешен, обновяваме статус на логин
            isUserLoggedIn = data.getBooleanExtra("isUserLoggedIn", false);
            // Записваме userId в SharedPreferences
            if (isUserLoggedIn) {
                int userId = data.getIntExtra("userId", -1);
                SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("userId", userId);
                editor.apply();
            }
            updateUIBasedOnLogin(); // Обновяваме интерфейса
        }
    }

    private void updateUIBasedOnLogin() {
        if (isUserLoggedIn) {
            // Ако потребителят е логнат, показваме поздрав и бутон за изход
            welcomeText.setVisibility(View.VISIBLE);
            welcomeText.setText("Добре дошли!");
            btnLogout.setVisibility(View.VISIBLE);
            authLayout.setVisibility(View.GONE);  // Скриваме бутоните за вход и регистрация
        } else {
            // Ако потребителят не е логнат, показваме бутоните за вход и регистрация
            welcomeText.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
            authLayout.setVisibility(View.VISIBLE);  // Показваме бутоните за вход и регистрация
        }
    }

    // Получаваме потребителското ID от SharedPreferences
    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        return sharedPreferences.getInt("userId", -1);  // Връща -1 ако не е намерено
    }
}
