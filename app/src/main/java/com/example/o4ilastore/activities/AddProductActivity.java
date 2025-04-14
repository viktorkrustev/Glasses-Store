package com.example.o4ilastore.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.o4ilastore.R;
import com.example.o4ilastore.database.AppDatabase;
import com.example.o4ilastore.database.entities.Glasses;

public class AddProductActivity extends AppCompatActivity {

    private EditText editName, editPrice, editDescription, editForm, editCategoryId, editManufacturerId, editImageUrl;
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
        editCategoryId = findViewById(R.id.editCategoryId);
        editManufacturerId = findViewById(R.id.editManufacturerId);
        editImageUrl = findViewById(R.id.editImageUrl); // Ново поле за URL

        btnSave = findViewById(R.id.btnSave);
        btnPreviewImage = findViewById(R.id.btnPreviewImage); // Бутон за преглед
        selectedImageView = findViewById(R.id.selectedImageView);

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
        int categoryId, manufacturerId;

        try {
            price = Double.parseDouble(editPrice.getText().toString());
            categoryId = Integer.parseInt(editCategoryId.getText().toString());
            manufacturerId = Integer.parseInt(editManufacturerId.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Моля, въведете валидни стойности", Toast.LENGTH_SHORT).show();
            return;
        }

        Glasses product = new Glasses();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setForm(form);
        product.setImageUrl(imageUrl); // Записваме URL-то
        product.setCategoryId(categoryId);
        product.setManufacturerId(manufacturerId);
        product.setInCart(0);
        product.setQuantity(0);

        new Thread(() -> {
            db.glassesDao().insert(product);
            runOnUiThread(() -> {
                Toast.makeText(this, "Продуктът е добавен успешно", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
