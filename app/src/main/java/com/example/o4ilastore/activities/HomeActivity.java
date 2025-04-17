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

    private Button btnProducts, btnProfile, btnAbout,  btnLogin, btnRegister;
    private LinearLayout authLayout;
    private TextView welcomeText;
    private boolean isUserLoggedIn = false;

    private static final int LOGIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        setupListeners();
        updateUIBasedOnLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIBasedOnLogin();
    }

    private void initViews() {
        btnProducts = findViewById(R.id.btnProducts);
        btnProfile = findViewById(R.id.btnProfile);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        authLayout = findViewById(R.id.authLayout);
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void setupListeners() {
        btnProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductListActivity.class)));

        btnProfile.setOnClickListener(v -> {
            if (isUserLoggedIn) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("userId", getUserId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Не сте влезли. Моля, влезте в профила си.", Toast.LENGTH_SHORT).show();
            }
        });

        btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutUsActivity.class)));

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            isUserLoggedIn = data.getBooleanExtra("isUserLoggedIn", false);
            updateUIBasedOnLogin();
        }
    }

    private void updateUIBasedOnLogin() {
        int userId = getUserId();
        isUserLoggedIn = userId != -1;
        if (isUserLoggedIn) {
            welcomeText.setVisibility(View.VISIBLE);
            welcomeText.setText("Добре дошли!");
            authLayout.setVisibility(View.GONE);
        } else {
            welcomeText.setVisibility(View.GONE);
            authLayout.setVisibility(View.VISIBLE);
        }
    }

    private int getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getInt("userId", -1);
    }
}
