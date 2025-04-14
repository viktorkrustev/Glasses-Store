package com.example.o4ilastore.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.dao.UserDao;
import com.example.o4ilastore.database.entities.User;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        UserDao userDao = AppDatabase.getInstance(this).userDao();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Попълни всички полета", Toast.LENGTH_SHORT).show();
            } else {
                executor.execute(() -> {
                    User user = userDao.getUserByEmailAndPassword(email, password);
                    if (user != null) {
                        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        prefs.edit()
                                .putInt("userId", user.getId())
                                .putBoolean("isAdmin", user.isAdmin())
                                .apply();

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Добре дошъл, " + user.name + "!", Toast.LENGTH_SHORT).show();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isUserLoggedIn", true);
                            resultIntent.putExtra("userId", user.getId());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Невалидни данни за вход", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
