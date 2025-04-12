package com.example.o4ilastore.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.dao.UserDao;
import com.example.o4ilastore.database.entities.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textViewMessage;

    private Executor executor = Executors.newSingleThreadExecutor(); // Executor за работа в фонов нишка

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
            } else {
                // Може да добавиш проверка дали email вече съществува
                User user = new User();
                user.name = name;
                user.email = email;
                user.password = password;

                // Извършваме операцията в фонов нишка с Executor
                executor.execute(() -> {
                    userDao.insert(user); // Вмъкваме потребителя в базата
                    runOnUiThread(() -> {
                        textViewMessage.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        textViewMessage.setText("Успешна регистрация!");
                        clearFields();
                    });
                });
            }
        });
    }

    private void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextPassword.setText("");
    }
}
