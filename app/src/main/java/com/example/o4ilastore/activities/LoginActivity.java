package com.example.o4ilastore.activities;

import android.content.Intent;
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

    private Executor executor = Executors.newSingleThreadExecutor(); // Използване на executor за работа в фонов нишка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        // Вземаме достъп до базата данни
        UserDao userDao = AppDatabase.getInstance(this).userDao();

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Проверяваме дали потребителят е попълнил полетата
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Попълни всички полета", Toast.LENGTH_SHORT).show();
            } else {
                // Извършваме проверката за потребителя в работещ нишка
                executor.execute(() -> {
                    User user = userDao.getUserByEmailAndPassword(email, password);
                    if (user != null) {
                        // Ако потребителят е намерен в базата данни
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Добре дошъл, " + user.name + "!", Toast.LENGTH_SHORT).show();

                            // Изпращаме резултат в HomeActivity, за да актуализираме интерфейса
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isUserLoggedIn", true);
                            resultIntent.putExtra("userId", user.getId()); // 👉 Предаваме userId!
                            setResult(RESULT_OK, resultIntent);
                            finish();

                        });
                    } else {
                        // Ако няма съвпадение в базата данни
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Невалидни данни за вход", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });

        // Пренасочване към регистрационната страница
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
