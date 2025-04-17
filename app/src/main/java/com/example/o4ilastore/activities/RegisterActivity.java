package com.example.o4ilastore.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textViewMessage;

    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewMessage = findViewById(R.id.textViewMessage);

        UserDao userDao = AppDatabase.getInstance(this).userDao();

        buttonRegister.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                textViewMessage.setText("Моля, попълни всички полета.");
                return;
            }

            if (name.length() < 2) {
                textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                textViewMessage.setText("Името трябва да съдържа поне 2 символа.");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                textViewMessage.setText("Невалиден имейл адрес.");
                return;
            }

            if (password.length() < 6) {
                textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                textViewMessage.setText("Паролата трябва да съдържа поне 6 символа.");
                return;
            }

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                textViewMessage.setText("Грешка при обработката на паролата.");
                return;
            }

            User user = new User();
            user.name = name;
            user.email = email;
            user.password = hashedPassword;

            executor.execute(() -> {
                try {
                    userDao.insert(user);
                    runOnUiThread(() -> {
                        textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        textViewMessage.setText("Успешна регистрация!");
                        clearFields();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        textViewMessage.setText("Възникна грешка при регистрацията. Моля, опитайте отново.");
                    });
                }
            });
        });
    }

    private void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
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
}
