package com.example.o4ilastore.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;

public class AddProductActivity extends AppCompatActivity {

    private EditText editName, editPrice, editDescription, editForm, editImageUrl;
    private Spinner spinnerCategory, spinnerManufacturer;
    private Button btnSave, btnPreviewImage;
    private ImageView selectedImageView;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        db = AppDatabase.getInstance(this);

        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        editDescription = findViewById(R.id.editDescription);
        editForm = findViewById(R.id.editForm);
        editImageUrl = findViewById(R.id.editImageUrl);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerManufacturer = findViewById(R.id.spinnerManufacturer);
        btnSave = findViewById(R.id.btnSave);
        btnPreviewImage = findViewById(R.id.btnPreviewImage);
        selectedImageView = findViewById(R.id.selectedImageView);

        String[] categories = {
                "Мъжки",
                "Женски",
                "Детски"
        };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        String[] manufacturers = {
                "RayBrand",
                "Luxottica",
                "Visionary Co.",
                "Elite Vision",
                "OptixLine"
        };
        ArrayAdapter<String> manufacturerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, manufacturers);
        manufacturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerManufacturer.setAdapter(manufacturerAdapter);

        //
        btnPreviewImage.setOnClickListener(v -> previewImage());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void previewImage() {
        String imageUrl = editImageUrl.getText().toString().trim();
        if (!imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.photo1)
                    .error(R.drawable.photo3)
                    .into(selectedImageView);
        } else {
            Toast.makeText(this, "Въведете валиден URL", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String form = editForm.getText().toString().trim();
        String imageUrl = editImageUrl.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || form.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Моля, попълнете всички полета", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(editPrice.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Моля, въведете валидна цена", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryId = spinnerCategory.getSelectedItemPosition() + 1;
        int manufacturerId = spinnerManufacturer.getSelectedItemPosition() + 1;

        Glasses product = new Glasses();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setForm(form);
        product.setImageUrl(imageUrl);
        product.setCategoryId(categoryId);
        product.setManufacturerId(manufacturerId);
        product.setInCart(0);
        product.setQuantity(3);

        new Thread(() -> {
            db.glassesDao().insert(product);
            runOnUiThread(() -> {
                Toast.makeText(this, "Продуктът е добавен успешно", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
