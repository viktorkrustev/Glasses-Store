package com.example.o4ilastore.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.dao.UserDao;
import com.example.o4ilastore.database.entities.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private ProgressBar progressBar;

    private Executor executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(ProgressBar.GONE);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Попълни всички полета", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Невалиден имейл адрес", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            progressBar.setVisibility(ProgressBar.GONE);
            Toast.makeText(this, "Грешка при обработката на паролата", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            UserDao userDao = AppDatabase.getInstance(this).userDao();
            User user = userDao.getUserByEmailAndPassword(email, hashedPassword);

            runOnUiThread(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                if (user != null) {
                    saveUserData(user);
                    Toast.makeText(this, "Добре дошъл, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
                    returnResult(user);
                    finish();
                } else {
                    Toast.makeText(this, "Невалидни данни за вход", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format(Locale.US, "%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveUserData(User user) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        prefs.edit()
                .putInt("userId", user.getId())
                .putBoolean("isAdmin", user.isAdmin())
                .apply();
    }

    private void returnResult(User user) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("isUserLoggedIn", true);
        resultIntent.putExtra("userId", user.getId());
        setResult(RESULT_OK, resultIntent);
    }
}
